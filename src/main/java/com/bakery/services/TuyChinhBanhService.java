package com.bakery.services;

import com.bakery.model.dao.CotBanhDAO;
import com.bakery.model.dao.KichCoBanhDAO;
import com.bakery.model.dao.KieuTrangTriDAO;
import com.bakery.model.dao.NhanBanhDAO;
import com.bakery.model.dto.CotBanhDTO;
import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.model.dto.KieuTrangTriDTO;
import com.bakery.model.dto.NhanBanhDTO;

import java.util.List;

/**
 * Service duy nhất chịu trách nhiệm cung cấp dữ liệu
 * các tùy chọn phụ phí cho bánh tùy chỉnh:
 * Kích cỡ, Cốt bánh, Nhân bánh, Kiểu trang trí.
 * (SRP – Single Responsibility Principle)
 */
public class TuyChinhBanhService {

    private final KichCoBanhDAO kichCoBanhDAO;
    private final CotBanhDAO cotBanhDAO;
    private final NhanBanhDAO nhanBanhDAO;
    private final KieuTrangTriDAO kieuTrangTriDAO;

    public TuyChinhBanhService() {
        this.kichCoBanhDAO = new KichCoBanhDAO();
        this.cotBanhDAO = new CotBanhDAO();
        this.nhanBanhDAO = new NhanBanhDAO();
        this.kieuTrangTriDAO = new KieuTrangTriDAO();
    }

    public TuyChinhBanhService(KichCoBanhDAO kichCoBanhDAO, CotBanhDAO cotBanhDAO,
            NhanBanhDAO nhanBanhDAO, KieuTrangTriDAO kieuTrangTriDAO) {
        this.kichCoBanhDAO = kichCoBanhDAO;
        this.cotBanhDAO = cotBanhDAO;
        this.nhanBanhDAO = nhanBanhDAO;
        this.kieuTrangTriDAO = kieuTrangTriDAO;
    }

    /** Lấy danh sách kích cỡ bánh kèm phụ phí. */
    public List<KichCoBanhDTO> layDanhSachKichCo() {
        return kichCoBanhDAO.layDanhSachPhuPhi();
    }

    /** Lấy danh sách loại cốt bánh kèm phụ phí. */
    public List<CotBanhDTO> layDanhSachCotBanh() {
        return cotBanhDAO.layDanhSachPhuPhi();
    }

    /** Lấy danh sách loại nhân bánh kèm phụ phí. */
    public List<NhanBanhDTO> layDanhSachNhanBanh() {
        return nhanBanhDAO.layDanhSachPhuPhi();
    }

    /** Lấy danh sách kiểu trang trí bánh kèm phụ phí. */
    public List<KieuTrangTriDTO> layDanhSachKieuTrangTri() {
        return kieuTrangTriDAO.layDanhSachPhuPhi();
    }
}
