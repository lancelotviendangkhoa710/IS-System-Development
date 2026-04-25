package com.bakery.services;

import com.bakery.model.dao.CaLamViecDAO;
import com.bakery.model.dto.CaLamViecDTO;

import java.math.BigDecimal;

public class CaLamViecService {

    private final CaLamViecDAO dao = new CaLamViecDAO();

    public CaLamViecDTO layCaHienTai(int maNV) {
        return dao.layCaHienTai(maNV);
    }

    public int moCa(int maNV, String maMayPOS, BigDecimal tienDauCa) {
        if (maMayPOS == null || maMayPOS.isBlank())
            throw new IllegalArgumentException("Vui lòng chọn máy POS.");
        if (tienDauCa == null || tienDauCa.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Số tiền đầu ca không hợp lệ.");
        return dao.moCa(maMayPOS, tienDauCa, maNV);
    }
}
