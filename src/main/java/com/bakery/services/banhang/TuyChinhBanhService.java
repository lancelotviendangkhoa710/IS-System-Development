package com.bakery.services.banhang;

import com.bakery.model.dao.kho.CotBanhDAO;
import com.bakery.model.dao.kho.KichCoBanhDAO;
import com.bakery.model.dao.kho.KieuTrangTriDAO;
import com.bakery.model.dao.kho.NhanBanhDAO;
import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.kho.KichCoBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;
import com.bakery.services.BaseService;

import java.util.List;

/**
 * Service duy nhất chịu trách nhiệm cung cấp dữ liệu
 * các tùy chọn phụ phí cho bánh tùy chỉnh:
 * Kích cỡ, Cốt bánh, Nhân bánh, Kiểu trang trí.
 * (SRP – Single Responsibility Principle)
 */
public class TuyChinhBanhService extends BaseService {

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

    // --- Cot Banh ---
    public List<CotBanhDTO> layDanhSachCotBanh() throws Exception {
        return cotBanhDAO.layDanhSachPhuPhi();
    }
    public boolean themCotBanh(CotBanhDTO item) throws Exception {
        return cotBanhDAO.them(item);
    }
    public boolean suaCotBanh(CotBanhDTO item) throws Exception {
        return cotBanhDAO.sua(item);
    }
    public boolean xoaCotBanh(int id, int maNV) throws Exception {
        return cotBanhDAO.xoa(id, maNV);
    }

    // --- Nhan Banh ---
    public List<NhanBanhDTO> layDanhSachNhanBanh() throws Exception {
        return nhanBanhDAO.layDanhSachPhuPhi();
    }
    public boolean themNhanBanh(NhanBanhDTO item) throws Exception {
        return nhanBanhDAO.them(item);
    }
    public boolean suaNhanBanh(NhanBanhDTO item) throws Exception {
        return nhanBanhDAO.sua(item);
    }
    public boolean xoaNhanBanh(int id, int maNV) throws Exception {
        return nhanBanhDAO.xoa(id, maNV);
    }

    // --- Kieu Trang Tri ---
    public List<KieuTrangTriDTO> layDanhSachKieuTrangTri() throws Exception {
        return kieuTrangTriDAO.layDanhSachPhuPhi();
    }
    public boolean themKieuTrangTri(KieuTrangTriDTO item) throws Exception {
        return kieuTrangTriDAO.them(item);
    }
    public boolean suaKieuTrangTri(KieuTrangTriDTO item) throws Exception {
        return kieuTrangTriDAO.sua(item);
    }
    public boolean xoaKieuTrangTri(int id, int maNV) throws Exception {
        return kieuTrangTriDAO.xoa(id, maNV);
    }

    // --- Kich Co ---
    public List<KichCoBanhDTO> layDanhSachKichCo() throws Exception {
        return kichCoBanhDAO.layDanhSachPhuPhi();
    }
}
