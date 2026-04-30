package com.bakery.services;

import com.bakery.model.dao.NhaCungCapDAO;
import com.bakery.model.dto.NhaCungCapDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class NhaCungCapService extends BaseService {

    private final NhaCungCapDAO nhaCungCapDAO;

    public NhaCungCapService() {
        this.nhaCungCapDAO = new NhaCungCapDAO();
    }

    public List<NhaCungCapDTO> layDanhSachNhaCungCap() throws Exception {
        return nhaCungCapDAO.layDanhSachNhaCungCap();
    }

    public List<NhaCungCapDTO> timKiemNhaCungCap(String tuKhoa) throws Exception {
        List<NhaCungCapDTO> tatCa = layDanhSachNhaCungCap();
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return tatCa;
        }
        
        String tuKhoaLower = tuKhoa.toLowerCase().trim();
        return tatCa.stream()
                .filter(ncc -> (ncc.getTenNCC() != null && ncc.getTenNCC().toLowerCase().contains(tuKhoaLower)) ||
                               (ncc.getSdt() != null && ncc.getSdt().contains(tuKhoaLower)))
                .collect(Collectors.toList());
    }

    public void themNhaCungCap(NhaCungCapDTO ncc) throws Exception {
        validate(ncc);
        try {
            nhaCungCapDAO.themNhaCungCap(ncc);
        } catch (Exception ex) {
            handleServiceException(ex);
        }
    }

    public void suaNhaCungCap(NhaCungCapDTO ncc) throws Exception {
        validate(ncc);
        try {
            nhaCungCapDAO.suaNhaCungCap(ncc);
        } catch (Exception ex) {
            handleServiceException(ex);
        }
    }

    public void xoaNhaCungCap(int maNCC, int maNVCapNhat) throws Exception {
        try {
            nhaCungCapDAO.xoaNhaCungCap(maNCC, maNVCapNhat);
        } catch (Exception ex) {
            handleServiceException(ex);
        }
    }

    private void validate(NhaCungCapDTO ncc) throws Exception {
        if (ncc.getTenNCC() == null || ncc.getTenNCC().trim().isEmpty()) {
            throw new Exception("Tên nhà cung cấp không được để trống.");
        }
    }

    private void handleServiceException(Exception ex) throws Exception {
        if (ex instanceof SQLException sqlEx) {
            if (sqlEx.getErrorCode() == 20318) {
                throw new Exception("Số điện thoại đã tồn tại trong hệ thống.");
            } else if (sqlEx.getErrorCode() == 20314 || sqlEx.getErrorCode() == 20316) {
                throw new Exception("Nhà cung cấp không tồn tại hoặc đã bị ngừng giao dịch.");
            }
        }
        throw ex;
    }
}
