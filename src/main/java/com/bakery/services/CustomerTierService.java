package com.bakery.services;

import com.bakery.models.dao.HangThanhVienDAO;
import com.bakery.models.dto.HangThanhVienDTO;

import java.sql.SQLException;
import java.util.List;

public class CustomerTierService {

	private final HangThanhVienDAO tierDAO;
	private final com.bakery.models.dao.KhachHangDAO customerDAO;

	public CustomerTierService() {
		this.tierDAO = new HangThanhVienDAO();
		this.customerDAO = new com.bakery.models.dao.KhachHangDAO();
	}

	// Kiểm tra dữ liệu đầu vào trước khi gọi DAO.
	public void validateTierInput(HangThanhVienDTO tier) throws SQLException {
		if (tier == null) {
			throw new SQLException("Dữ liệu hạng thành viên không hợp lệ");
		}

		String tierName = tier.getTenHang() != null ? tier.getTenHang().trim() : "";
		tier.setTenHang(tierName);

		if (tierName.isEmpty()) {
			throw new SQLException("Tên hạng thành viên không được để trống");
		}

		if (tierName.length() > 50) {
			throw new SQLException("Tên hạng thành viên tối đa 50 ký tự");
		}

		if (tier.getDiemToiThieu() < 0) {
			throw new SQLException("Điểm tối thiểu phải >= 0");
		}

		if (tier.getPhanTramGiamGia() < 0 || tier.getPhanTramGiamGia() > 100) {
			throw new SQLException("Phần trăm giảm giá phải trong khoảng 0-100");
		}
	}

	// Lấy danh sách hạng thành viên đang hoạt động.
	public List<HangThanhVienDTO> getAllTiers() throws SQLException {
		return tierDAO.getAllTiers();
	}

	// Lấy danh sách hạng thành viên đã xóa mềm.
	public List<HangThanhVienDTO> getDeletedTiers() throws SQLException {
		return tierDAO.getAllDeletedTiers();
	}

	// Lấy thông tin một hạng thành viên theo mã.
	public HangThanhVienDTO getTierById(int tierId) throws SQLException {
		if (tierId <= 0) {
			throw new SQLException("Mã hạng thành viên không hợp lệ");
		}

		HangThanhVienDTO tier = tierDAO.getTierById(tierId);
		if (tier == null) {
			throw new SQLException("Không tìm thấy hạng thành viên");
		}

		return tier;
	}

	// Tìm hạng thành viên theo tên trong danh sách đang hoạt động.
	public HangThanhVienDTO getTierByName(String tierName) {
		return tierDAO.findActiveTierByName(tierName);
	}

	// Lấy hạng phù hợp theo số điểm tích lũy.
	public HangThanhVienDTO getTierByPoints(int points) {
		if (points < 0) {
			return null;
		}
		return tierDAO.getTierByPoints(points);
	}

	// Đếm tổng số hạng thành viên đang hoạt động.
	public int countActiveTiers() {
		return tierDAO.countActiveTiers();
	}

	// Tạo hạng thành viên mới.
	public int createTier(HangThanhVienDTO tier) throws SQLException {
		validateTierInput(tier);

		HangThanhVienDTO existingActive = tierDAO.findActiveTierByName(tier.getTenHang());
		if (existingActive != null) {
			throw new SQLException("Tên hạng thành viên đã tồn tại trong hệ thống");
		}

		HangThanhVienDTO existingDeleted = tierDAO.findDeletedTierByName(tier.getTenHang());
		if (existingDeleted != null) {
			throw new SQLException("Tên hạng thành viên đã tồn tại trong thùng rác. Hãy khôi phục thay vì tạo mới.");
		}

		int newTierId = tierDAO.createTier(tier);
		customerDAO.syncAllCustomerTiers();
		return newTierId;
	}

	// Cập nhật hạng thành viên.
	public void updateTier(HangThanhVienDTO tier) throws SQLException {
		if (tier == null || tier.getMaHang() <= 0) {
			throw new SQLException("Mã hạng thành viên không hợp lệ");
		}

		validateTierInput(tier);

		HangThanhVienDTO existing = tierDAO.getTierById(tier.getMaHang());
		if (existing == null) {
			throw new SQLException("Không tìm thấy hạng thành viên để cập nhật");
		}

		HangThanhVienDTO sameNameActive = tierDAO.findActiveTierByName(tier.getTenHang());
		if (sameNameActive != null && sameNameActive.getMaHang() != tier.getMaHang()) {
			throw new SQLException("Tên hạng thành viên đã tồn tại trong hệ thống");
		}

		tierDAO.updateTier(tier);
		customerDAO.syncAllCustomerTiers();
	}

	// Xóa mềm hạng thành viên.
	public void softDeleteTier(int tierId, int deletedByEmployeeId) throws SQLException {
		if (tierId <= 0 || deletedByEmployeeId <= 0) {
			throw new SQLException("Dữ liệu không hợp lệ");
		}
		tierDAO.softDeleteTier(tierId, deletedByEmployeeId);
		customerDAO.syncAllCustomerTiers();
	}

	// Khôi phục hạng thành viên.
	public void restoreTier(int tierId) throws SQLException {
		if (tierId <= 0) {
			throw new SQLException("Mã hạng thành viên không hợp lệ");
		}
		tierDAO.restoreTier(tierId);
		customerDAO.syncAllCustomerTiers();
	}
}
