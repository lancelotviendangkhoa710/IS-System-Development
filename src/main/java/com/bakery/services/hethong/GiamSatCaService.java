package com.bakery.services.hethong;

import com.bakery.model.dao.hethong.GiamSatCaDAO;
import com.bakery.model.dto.hethong.GiamSatCaDTO;

import java.util.List;

/** Service giám sát tiền mặt đóng ca — chỉ đọc, không CUD. */
public class GiamSatCaService {
    private final GiamSatCaDAO dao = new GiamSatCaDAO();

    /** Lấy 200 ca gần nhất. */
    public List<GiamSatCaDTO> layLichSuCa() throws Exception {
        return dao.layLichSuCa(200);
    }
}
