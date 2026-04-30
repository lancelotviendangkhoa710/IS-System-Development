package com.bakery.services.khachhang;

import com.bakery.model.dao.khachhang.HangThanhVienDAO;
import com.bakery.model.dto.khachhang.HangThanhVienDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CustomerTierService {

	private final HangThanhVienDAO tierDAO;
	private final com.bakery.model.dao.KhachHangDAO customerDAO;

	public CustomerTierService() {
		this.tierDAO = new HangThanhVienDAO();
		this.customerDAO = new com.bakery.model.dao.KhachHangDAO();
	}

	// Kiem tra du lieu dau vao truoc khi goi DAO.
	public void validateTierInput(HangThanhVienDTO tier) throws SQLException {
		if (tier == null) {
			throw new SQLException("Du lieu hang thanh vien khong hop le");
		}

		String tierName = tier.getTenHang() != null ? tier.getTenHang().trim() : "";
		tier.setTenHang(tierName);

		if (tierName.isEmpty()) {
			throw new SQLException("Ten hang thanh vien khong duoc de trong");
		}

		if (tierName.length() > 50) {
			throw new SQLException("Ten hang thanh vien toi da 50 ky tu");
		}

		if (tier.getDiemToiThieu() < 0) {
			throw new SQLException("Diem toi thieu phai >= 0");
		}

		if (tier.getPhanTramGiamGia() == null) {
			throw new SQLException("Phan tram giam gia khong duoc de trong");
		}

		if (tier.getPhanTramGiamGia().compareTo(BigDecimal.valueOf(0)) < 0
				|| tier.getPhanTramGiamGia().compareTo(BigDecimal.valueOf(100)) > 0) {
			throw new SQLException("Phan tram giam gia phai trong khoang 0-100");
		}
	}

	// Lay danh sach hang thanh vien dang hoat dong.
	public List<HangThanhVienDTO> getAllTiers() throws SQLException {
		return tierDAO.getAllTiers();
	}

	// Lay danh sach hang thanh vien da xoa mem.
	public List<HangThanhVienDTO> getDeletedTiers() throws SQLException {
		return tierDAO.getAllDeletedTiers();
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

	// Tim hang thanh vien theo ten trong danh sach dang hoat dong.
	public HangThanhVienDTO getTierByName(String tierName) {
		return tierDAO.findActiveTierByName(tierName);
	}

	// Lay hang phu hop theo so diem tich luy.
	public HangThanhVienDTO getTierByPoints(int points) {
		if (points < 0) {
			return null;
		}
		return tierDAO.getTierByPoints(points);
	}

	// Dem tong so hang thanh vien dang hoat dong.
	public int countActiveTiers() {
		return tierDAO.countActiveTiers();
	}

	// Tao hang thanh vien moi.
	public int createTier(HangThanhVienDTO tier) throws SQLException {
		validateTierInput(tier);

		HangThanhVienDTO existingActive = tierDAO.findActiveTierByName(tier.getTenHang());
		if (existingActive != null) {
			throw new SQLException("Ten hang thanh vien da ton tai trong he thong");
		}

		HangThanhVienDTO existingDeleted = tierDAO.findDeletedTierByName(tier.getTenHang());
		if (existingDeleted != null) {
			throw new SQLException("Ten hang thanh vien da ton tai trong thung rac. Hay khoi phuc thay vi tao moi.");
		}

		int newTierId = tierDAO.createTier(tier);
		try {
			customerDAO.syncAllCustomerTiers();
		} catch (Exception e) {
			throw new SQLException("Loi dong bo hang thanh vien khach hang", e);
		}
		return newTierId;
	}

	// Cap nhat hang thanh vien.
	public void updateTier(HangThanhVienDTO tier) throws SQLException {
		if (tier == null || tier.getMaHang() <= 0) {
			throw new SQLException("Ma hang thanh vien khong hop le");
		}

		validateTierInput(tier);

		HangThanhVienDTO existing = tierDAO.getTierById(tier.getMaHang());
		if (existing == null) {
			throw new SQLException("Khong tim thay hang thanh vien de cap nhat");
		}

		HangThanhVienDTO sameNameActive = tierDAO.findActiveTierByName(tier.getTenHang());
		if (sameNameActive != null && sameNameActive.getMaHang() != tier.getMaHang()) {
			throw new SQLException("Ten hang thanh vien da ton tai trong he thong");
		}

		tierDAO.updateTier(tier);
		try {
			customerDAO.syncAllCustomerTiers();
		} catch (Exception e) {
			throw new SQLException("Loi dong bo hang thanh vien khach hang", e);
		}
	}

	// Xoa mem hang thanh vien.
	public void softDeleteTier(int tierId, int deletedByEmployeeId) throws SQLException {
		if (tierId <= 0 || deletedByEmployeeId <= 0) {
			throw new SQLException("Du lieu khong hop le");
		}
		tierDAO.softDeleteTier(tierId, deletedByEmployeeId);
		try {
			customerDAO.syncAllCustomerTiers();
		} catch (Exception e) {
			throw new SQLException("Loi dong bo hang thanh vien khach hang", e);
		}
	}

	// Khoi phuc hang thanh vien.
	public void restoreTier(int tierId) throws SQLException {
		if (tierId <= 0) {
			throw new SQLException("Ma hang thanh vien khong hop le");
		}
		tierDAO.restoreTier(tierId);
		try {
			customerDAO.syncAllCustomerTiers();
		} catch (Exception e) {
			throw new SQLException("Loi dong bo hang thanh vien khach hang", e);
		}
	}
}
