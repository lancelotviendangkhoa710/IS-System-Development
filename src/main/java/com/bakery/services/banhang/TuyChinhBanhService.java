package com.bakery.services.banhang;

import com.bakery.model.dao.kho.CotBanhDAO;
import com.bakery.model.dao.kho.KichCoBanhDAO;
import com.bakery.model.dao.kho.KieuTrangTriDAO;
import com.bakery.model.dao.kho.NhanBanhDAO;
import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.kho.KichCoBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;

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

    /** Lấy danh sách kích cỡ bánh kèm phụ phí. */
    public List<KichCoBanhDTO> layDanhSachKichCo() throws Exception {
        return kichCoBanhDAO.layDanhSachPhuPhi();
    }

    /** Lấy danh sách loại cốt bánh kèm phụ phí. */
    public List<CotBanhDTO> layDanhSachCotBanh() throws Exception {
        return cotBanhDAO.layDanhSachPhuPhi();
    }

    /** Lấy danh sách loại nhân bánh kèm phụ phí. */
    public List<NhanBanhDTO> layDanhSachNhanBanh() throws Exception {
        return nhanBanhDAO.layDanhSachPhuPhi();
    }

    /** Lấy danh sách kiểu trang trí bánh kèm phụ phí. */
    public List<KieuTrangTriDTO> layDanhSachKieuTrangTri() throws Exception {
        return kieuTrangTriDAO.layDanhSachPhuPhi();
    }
}
