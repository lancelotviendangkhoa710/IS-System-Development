package com.bakery.services.kho;

import com.bakery.model.dao.kho.CongThucDAO;
import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dto.kho.CongThucDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.services.BaseService;

import java.util.List;

/** Service quản lý Công thức nguyên liệu (Bill of Materials). */
public class CongThucService extends BaseService {

    private final CongThucDAO congThucDAO = new CongThucDAO();
    private final NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();

    /** Lấy danh sách công thức của một sản phẩm (kèm tên và đơn giá nguyên liệu). */
    public List<CongThucDTO> layCongThucTheoSP(int maSP) throws Exception {
        if (maSP <= 0) throw new Exception("Mã sản phẩm không hợp lệ.");
        return congThucDAO.layCongThucTheoSP(maSP);
    }

    /** Lấy danh sách tất cả nguyên liệu để hiển thị trên ComboBox. */
    public List<NguyenLieuDTO> layDanhSachNguyenLieu() throws Exception {
        return nguyenLieuDAO.layTatCaNguyenLieu();
    }

    /**
     * Lưu (thêm hoặc cập nhật) một dòng nguyên liệu vào công thức sản phẩm.
     * Sau khi lưu, trigger DB sẽ tự tính lại giá vốn sản phẩm.
     */
    public void luuCongThuc(int maSP, int maNL, double soLuong) throws Exception {
        if (maSP <= 0) throw new Exception("Chưa chọn sản phẩm.");
        if (maNL <= 0) throw new Exception("Vui lòng chọn nguyên liệu.");
        if (soLuong <= 0) throw new Exception("Định mức phải lớn hơn 0.");
        congThucDAO.upsertCongThuc(maSP, maNL, soLuong);
    }

    /**
     * Xóa một dòng nguyên liệu khỏi công thức.
     * Trigger DB sẽ tự tính lại giá vốn sản phẩm sau khi xóa.
     */
    public void xoaCongThuc(int maSP, int maNL) throws Exception {
        if (maSP <= 0 || maNL <= 0) throw new Exception("Dữ liệu công thức không hợp lệ.");
        congThucDAO.xoaCongThuc(maSP, maNL);
    }

    /** Tính tổng giá vốn BOM của một sản phẩm (preview trước khi lưu). */
    public double tinhGiaVonBOM(int maSP) throws Exception {
        return congThucDAO.tinhTongGiaVon(maSP);
    }
}
