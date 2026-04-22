package com.bakery.services;

import com.bakery.model.dao.DonDatHangDAO;
import com.bakery.model.dto.DonDatHangDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service duy nhất chịu trách nhiệm truy vấn danh sách đơn hàng
 * phục vụ màn hình Theo dõi đơn.
 * (SRP – Single Responsibility Principle)
 */
public class TheoDoDonService {

    private final DonDatHangDAO donDatHangDAO;

    public TheoDoDonService() {
        this.donDatHangDAO = new DonDatHangDAO();
    }

    public TheoDoDonService(DonDatHangDAO donDatHangDAO) {
        this.donDatHangDAO = donDatHangDAO;
    }

    /**
     * Lấy danh sách đơn hàng theo bộ lọc:
     * mã đơn (tìm kiếm mờ), ngày nhận, khoảng giờ.
     */
    public List<DonDatHangDTO> layDanhSachDonTheoDoi(String maDonSearch, LocalDate ngayNhan,
            LocalTime gioTu, LocalTime gioDen) throws Exception {
        try {
            return donDatHangDAO.layDanhSachDonTheoDoi(maDonSearch, ngayNhan, gioTu, gioDen);
        } catch (SQLException e) {
            throw new Exception("Không thể tải danh sách theo dõi đơn: " + e.getMessage(), e);
        }
    }
}
