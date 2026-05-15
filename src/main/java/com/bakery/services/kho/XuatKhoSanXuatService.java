package com.bakery.services.kho;

import com.bakery.model.dao.kho.CongThucDAO;
import com.bakery.model.dao.kho.PhieuXuatKhoDAO;
import com.bakery.model.dao.hethong.CauHinhGioiHanDAO;
import com.bakery.model.dto.hethong.CauHinhGioiHanDTO;
import com.bakery.services.BaseService;
import java.time.LocalDate;

/**
 * Service quản lý luồng Xuất kho Sản xuất (UC41).
 * Thợ bếp yêu cầu làm thêm X cái bánh → hệ thống truy ngược công thức
 * → xuất đúng nguyên liệu theo FIFO → cập nhật tồn kho tự động.
 */
public class XuatKhoSanXuatService extends BaseService {

    private final PhieuXuatKhoDAO phieuXuatKhoDAO;
    private final CongThucDAO congThucDAO;
    private final CauHinhGioiHanDAO cauHinhGioiHanDAO;

    public XuatKhoSanXuatService() {
        this.phieuXuatKhoDAO  = new PhieuXuatKhoDAO();
        this.congThucDAO       = new CongThucDAO();
        this.cauHinhGioiHanDAO = new CauHinhGioiHanDAO();
    }

    /**
     * Yêu cầu làm thêm bánh: kiểm tra điều kiện → gọi DB procedure.
     * DB tự lo: kiểm tra tồn kho (Pessimistic Lock) → tạo phiếu xuất → xuất FIFO lô.
     *
     * @param maSP           mã sản phẩm cần sản xuất
     * @param soLuong        số lượng bánh cần làm
     * @param maNV           mã thợ bếp thực hiện
     */
    public void yeuCauSanXuat(int maSP, double soLuong, int maNV) throws Exception {
        // 1. Validate đầu vào
        if (maSP <= 0) throw new Exception("Mã sản phẩm không hợp lệ.");
        if (soLuong <= 0) throw new Exception("Số lượng sản xuất phải lớn hơn 0.");
        if (maNV <= 0) throw new Exception("Mã nhân viên không hợp lệ.");

        // 2. Kiểm tra sản phẩm có công thức chưa (fail-fast trước khi gọi DB nặng)
        boolean coCongThuc = congThucDAO.coCongThuc(maSP);
        if (!coCongThuc) {
            throw new Exception("Sản phẩm chưa có công thức nguyên liệu. Vui lòng thiết lập công thức trước khi sản xuất.");
        }

        // 3. Gọi DB Procedure — DB tự kiểm tra tồn kho và xuất theo FIFO
        // Nếu thiếu nguyên liệu, DB sẽ ném ORA-20xxx với tên NL còn thiếu
        phieuXuatKhoDAO.xuatKhoSanXuat(maSP, soLuong, maNV);
    }

    /**
     * Tính số lượng bánh tối đa có thể làm được với tồn kho hiện tại.
     * Gọi FUNC_SOLUONGKHADUNG (đã có trên DB).
     */
    public double tinhSoLuongKhaDung(int maSP) throws Exception {
        if (maSP <= 0) return 0;
        return congThucDAO.tinhSoLuongKhaDung(maSP);
    }

    /**
     * Lấy giới hạn sản xuất và số bánh đã làm trong ngày chỉ định.
     * @param ngay ngày cần kiểm tra (thường là LocalDate.now())
     * @return int[]{gioiHanSoBanh, soBanhDaNhan};
     *         nếu chưa có cấu hình → {Integer.MAX_VALUE, 0} (đồng nghĩa không giới hạn)
     */
    public int[] layGioiHanVaDaLam(LocalDate ngay) throws Exception {
        CauHinhGioiHanDTO dto = cauHinhGioiHanDAO.layTheoNgay(ngay);
        if (dto == null) return new int[]{Integer.MAX_VALUE, 0};
        return new int[]{dto.getGioiHanSoBanh(), dto.getSoBanhDaNhan()};
    }
}
