package com.bakery.services.hethong;
import com.bakery.services.BaseService;

import com.bakery.model.dao.hethong.CaLamViecDAO;
import com.bakery.model.dto.hethong.CaLamViecDTO;

import java.math.BigDecimal;

public class CaLamViecService extends BaseService {

    private final CaLamViecDAO dao = new CaLamViecDAO();

    public CaLamViecDTO layCaHienTai(int maNV) throws Exception {
        return dao.layCaHienTai(maNV);
    }

    public int moCa(int maNV, String maMayPOS, BigDecimal tienDauCa) throws Exception {
        if (maMayPOS == null || maMayPOS.isBlank())
            throw new Exception("Vui lòng chọn máy POS.");
        if (tienDauCa == null || tienDauCa.compareTo(BigDecimal.ZERO) < 0)
            throw new Exception("Số tiền đầu ca không hợp lệ.");
        return dao.moCa(maMayPOS, tienDauCa, maNV);
    }
}
