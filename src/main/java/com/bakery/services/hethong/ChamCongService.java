package com.bakery.services.hethong;

import com.bakery.model.dao.hethong.CaLamViecDAO;
import com.bakery.model.dto.hethong.CaLamViecDTO;
import com.bakery.services.BaseService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

/**
 * Service nghiệp vụ chấm công — tái sử dụng bảng CALAMVIEC.
 * - Thu ngân:  moCa() (dùng PROC_MOCA, có MAMAYPOS)
 * - NV thường: checkIn() (INSERT trực tiếp, MAMAYPOS = NULL)
 * - Tất cả:    checkOut(maCa) để đóng ca
 */
public class ChamCongService extends BaseService {

    private final CaLamViecDAO dao = new CaLamViecDAO();

    // ─── Trạng thái ca hiện tại ──────────────────────────────────────────────

    /** @return Ca đang mở của NV, null nếu chưa check-in */
    public CaLamViecDTO layCaHienTai(int maNV) throws Exception {
        return dao.layCaHienTai(maNV);
    }

    public boolean dangTrongCa(int maNV) throws Exception {
        return dao.kiemTraNvDangMoCa(maNV);
    }

    // ─── Check-in (NV thường, không có POS) ─────────────────────────────────

    /**
     * Check-in cho nhân viên không phải thu ngân (QL, Thủ kho, Bếp…).
     * MAMAYPOS = NULL — không liên quan đến đối soát tiền.
     *
     * @throws Exception nếu NV đang trong ca chưa đóng
     */
    public int checkIn(int maNV) throws Exception {
        if (dao.kiemTraNvDangMoCa(maNV))
            throw new Exception("Bạn đang có ca chưa đóng. Vui lòng đóng ca trước khi check-in.");
        return dao.checkIn(maNV);
    }

    // ─── Check-in cho Thu ngân (dùng PROC_MOCA) ─────────────────────────────

    /**
     * Mở ca POS cho thu ngân — gọi PROC_MOCA để INSERT cả DOISOAT.
     */
    public int moCaPOS(int maNV, String maMayPOS, BigDecimal tienDauCa) throws Exception {
        if (maMayPOS == null || maMayPOS.isBlank())
            throw new Exception("Vui lòng chọn máy POS.");
        if (tienDauCa == null || tienDaCoc(tienDauCa))
            throw new Exception("Số tiền đầu ca không hợp lệ (phải >= 0).");
        if (dao.kiemTraNvDangMoCa(maNV))
            throw new Exception("Bạn đang có ca chưa đóng.");
        return dao.moCa(maMayPOS, tienDauCa, maNV);
    }

    // ─── Check-out ───────────────────────────────────────────────────────────

    /**
     * Check-out: đóng ca hiện tại của nhân viên.
     *
     * @throws Exception nếu NV không có ca đang mở
     */
    public void checkOut(int maNV) throws Exception {
        CaLamViecDTO ca = dao.layCaHienTai(maNV);
        if (ca == null)
            throw new Exception("Bạn chưa check-in. Không có ca nào đang mở.");
        dao.checkOut(ca.getMaCa());
    }

    // ─── Lịch sử & Thống kê ─────────────────────────────────────────────────

    /**
     * Lấy lịch sử chấm công theo tháng.
     */
    public List<CaLamViecDTO> layLichSu(int maNV, int thang, int nam) throws Exception {
        if (thang < 1 || thang > 12) throw new Exception("Tháng không hợp lệ.");
        if (nam < 2020 || nam > 2100) throw new Exception("Năm không hợp lệ.");
        return dao.layLichSuChamCong(maNV, thang, nam);
    }

    /**
     * Tính tổng giờ làm trong tháng từ danh sách ca đã lấy.
     * Chỉ tính các ca đã đóng (có THOIGIANDONGCA).
     *
     * @return Chuỗi hiển thị "X giờ Y phút"
     */
    public String tinhTongGioLam(List<CaLamViecDTO> dsCa) {
        long tongPhut = dsCa.stream()
                .filter(ca -> ca.getThoiGianDongCa() != null && ca.getThoiGianMoCa() != null)
                .mapToLong(ca -> Duration.between(ca.getThoiGianMoCa(), ca.getThoiGianDongCa()).toMinutes())
                .filter(p -> p > 0)
                .sum();
        long gio = tongPhut / 60;
        long phut = tongPhut % 60;
        return gio + " giờ " + phut + " phút";
    }

    /**
     * Đếm số ngày có ít nhất 1 ca chấm công (đã đóng) trong tháng.
     */
    public long demNgayLam(List<CaLamViecDTO> dsCa) {
        return dsCa.stream()
                .filter(ca -> ca.getThoiGianDongCa() != null)
                .map(ca -> ca.getThoiGianMoCa().toLocalDate())
                .distinct()
                .count();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private boolean tienDaCoc(BigDecimal v) {
        return v.compareTo(BigDecimal.ZERO) < 0;
    }
}
