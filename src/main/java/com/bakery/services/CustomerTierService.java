package com.bakery.services;

import com.bakery.models.dao.HangThanhVienDAO;
import com.bakery.models.dto.HangThanhVienDTO;
import java.sql.SQLException;
import java.util.List;

public class CustomerTierService {

    private final HangThanhVienDAO tierDAO;

    public CustomerTierService() {
        this.tierDAO = new HangThanhVienDAO();
    }

    // Lay danh sach hang thanh vien de do vao combobox.
    public List<HangThanhVienDTO> getAllTiers() throws SQLException {
        try {
            return tierDAO.getAllTiers();
        } catch (Exception e) {
            throw new SQLException("Loi tai danh sach hang thanh vien", e);
        }
    }

    // Lay thong tin mot hang thanh vien theo ma.
    public HangThanhVienDTO getTierById(int tierId) throws SQLException {
        if (tierId <= 0) {
            throw new SQLException("Ma hang thanh vien khong hop le");
        }

        HangThanhVienDTO tier = tierDAO.getTierById(tierId);
        if (tier == null) {
            throw new SQLException("Khong tim thay hang thanh vien");
        }

        return tier;
    }
}

