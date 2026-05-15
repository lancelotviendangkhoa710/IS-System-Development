package com.bakery.services.banhang;

import com.bakery.model.dao.banhang.DonHangDAO;
import com.bakery.model.dto.banhang.DonDatHangDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service duy nhất chịu trách nhiệm truy vấn danh sách đơn hàng
 * phục vụ màn hình Theo dõi đơn.
 * (SRP – Single Responsibility Principle)
 */
public class TheoDoiDonService {

    private final DonHangDAO donHangDAO;

    public TheoDoiDonService() {
        this.donHangDAO = new DonHangDAO();
    }

    public TheoDoiDonService(DonHangDAO donHangDAO) {
        this.donHangDAO = donHangDAO;
    }

    /**
     * Lấy danh sách đơn hàng theo bộ lọc:
     * mã đơn (tìm kiếm mờ), ngày nhận, khoảng giờ.
     */
    public List<DonDatHangDTO> layDanhSachDonTheoDoi(String maDonSearch, String tenKhachSearch, LocalDate ngayNhan,
            LocalTime gioTu, LocalTime gioDen, String trangThaiFilter) throws Exception {
        try {
            return donHangDAO.layDanhSachDonTheoDoi(maDonSearch, tenKhachSearch, ngayNhan, gioTu, gioDen, trangThaiFilter);
        } catch (SQLException e) {
            throw new Exception("Không thể tải danh sách theo dõi đơn: " + e.getMessage(), e);
        }
    }
    public List<DonDatHangDTO> layDanhSachDonTheoDoi(String maDonSearch, LocalDate ngayNhan,
            LocalTime gioTu, LocalTime gioDen) throws Exception {
        return layDanhSachDonTheoDoi(maDonSearch, null, ngayNhan, gioTu, gioDen, "NOT_COMPLETED");
    }

    /** Lấy đơn có bánh tùy chỉnh chưa hoàn thành/hủy — dùng cho màn hình bếp. */
    public List<DonDatHangDTO> layDonCoTuyChinhChuaHoanThanh() throws Exception {
        try {
            return donHangDAO.layDonCoTuyChinhChuaHoanThanh();
        } catch (SQLException e) {
            throw new Exception("Không thể tải đơn tùy chỉnh cho bếp: " + e.getMessage(), e);
        }
    }
}
