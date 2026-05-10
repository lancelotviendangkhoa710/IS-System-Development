package com.bakery.services.hethong;
import com.bakery.services.BaseService;

import com.bakery.model.dao.hethong.CaLamViecDAO;
import com.bakery.model.dao.hethong.DoiSoatDAO;
import com.bakery.model.dto.hethong.CaLamViecDTO;
import com.bakery.model.dto.hethong.DoiSoatInfoDTO;
import com.bakery.utils.SessionContext;

import java.math.BigDecimal;

/**
 * Xử lý toàn bộ logic đối soát ca: mở ca, tính tiền lý tưởng (bí mật),
 * tính chênh lệch và đóng ca.
 * KHÔNG chứa SQL — chỉ điều phối DAO và bảo vệ bất biến nghiệp vụ.
 */
public class DoiSoatService extends BaseService {

    private final CaLamViecDAO caLamViecDAO = new CaLamViecDAO();
    private final DoiSoatDAO doiSoatDAO = new DoiSoatDAO();
    // Phải gọi tinhTienMatLyTuong() trước khi gọi tinhChenhLech().
    private BigDecimal tienMatLyTuong;

    /**
     * Tải toàn bộ thông tin cần thiết cho dialog Đối soát Đóng ca.
     * Đồng thời cache tienMatLyTuong để tinhChenhLech() dùng sau.
     */
    public DoiSoatInfoDTO layThongTinDoiSoat(int maCa, int maNV) throws Exception {
        CaLamViecDTO ca = caLamViecDAO.layCaHienTai(maNV);
        String maMayPOS = (ca != null && ca.getMaMayPOS() != null) ? ca.getMaMayPOS() : "—";

        BigDecimal tienKhaiBaoDauCa = doiSoatDAO.layTienKhaiBaoDauCa(maCa);

        // Cache tienMatLyTuong — tinhChenhLech() dùng lại không cần gọi DB lần nữa
        this.tienMatLyTuong = doiSoatDAO.tinhTienMatLyTuong(maCa, tienKhaiBaoDauCa);
        BigDecimal doanhThu = this.tienMatLyTuong.subtract(tienKhaiBaoDauCa);

        return new DoiSoatInfoDTO(maCa, maMayPOS, tienKhaiBaoDauCa, doanhThu);
    }

    /**
     * Tính tiền mặt lý tưởng từ DB và lưu nội bộ — cơ chế đối soát mù.
     * Thu ngân PHẢI tự đếm tiền và nhập vào trước khi hệ thống tiết lộ con số này.
     * Gọi hàm này trước khi gọi tinhChenhLech().
     */
    public void tinhTienMatLyTuong(int maCa, BigDecimal tienKhaiBao) throws Exception {
        this.tienMatLyTuong = doiSoatDAO.tinhTienMatLyTuong(maCa, tienKhaiBao);
    }

    /**
     * Tính chênh lệch giữa tiền thu ngân đếm thực tế và tiền hệ thống tính.
     * Phải gọi tinhTienMatLyTuong() trước — nếu chưa gọi sẽ ném RuntimeException.
     *
     * @return chênh lệch = tienThucTeDem − tienMatLyTuong (âm = thiếu, dương =
     *         thừa)
     */
    public BigDecimal tinhChenhLech(BigDecimal tienThucTeDem) {
        if (this.tienMatLyTuong == null) {
            throw new RuntimeException("Chưa tính tiền mặt lý tưởng. Gọi tinhTienMatLyTuong() trước.");
        }
        return tienThucTeDem.subtract(this.tienMatLyTuong);
    }

    /**
     * Đóng ca và ghi kết quả đối soát.
     * Nếu có chênh lệch mà không có lý do → chặn luồng.
     *
     * @throws RuntimeException nếu chênh lệch != 0 mà lyDo để trống
     */
    public void dongCaDoiSoat(int maCa, BigDecimal tienThucTeDem, String lyDo) throws Exception {
        BigDecimal chenhLech = tinhChenhLech(tienThucTeDem);

        boolean amTien = chenhLech.compareTo(BigDecimal.ZERO) < 0;
        boolean thieulLyDo = lyDo == null || lyDo.isBlank();

        // Chỉ bắt buộc lý do khi thiếu tiền; thừa tiền không cần giải trình
        if (amTien && thieulLyDo) {
            throw new Exception(
                    "Tiền thực tế thiếu " + chenhLech.abs() + "đ — vui lòng nhập lý do trước khi đóng ca.");
        }

        doiSoatDAO.dongCaDoiSoat(maCa, tienThucTeDem, chenhLech, amTien ? lyDo : null);
        caLamViecDAO.dongCa(maCa);
        SessionContext.getInstance().dongCa();

        // Reset trạng thái nội bộ sau khi đóng ca xong
        this.tienMatLyTuong = null;
    }
}
