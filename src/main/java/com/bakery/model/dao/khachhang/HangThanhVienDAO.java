package com.bakery.model.dao.khachhang;

import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import com.bakery.utils.DBConnect;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HangThanhVienDAO {

	private static final String TIER_SELECT_SQL = """
			SELECT MAHANG, TENHANG, DIEMTOITHIEU, PHANTRAMGIAMGIA, THOIDIEMXOA, MANX
			FROM HANGTHANHVIEN
			""";

	// Lay danh sach hang thanh vien dang hoat dong.
	public List<HangThanhVienDTO> getAllTiers() throws SQLException {
		String sql = TIER_SELECT_SQL + " WHERE THOIDIEMXOA IS NULL ORDER BY DIEMTOITHIEU ASC, MAHANG ASC";
		List<HangThanhVienDTO> tiers = new ArrayList<>();

		try (Connection conn = DBConnect.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				tiers.add(mapRowToTier(rs));
			}
		} catch (SQLException e) {
			System.err.println("DAO error - getAllTiers: " + e.getMessage());
			throw e;
		}

		return tiers;
	}

	// Lay hang thanh vien dang hoat dong theo ma.
	public HangThanhVienDTO getTierById(int tierId) {
		String sql = TIER_SELECT_SQL + " WHERE MAHANG = ? AND THOIDIEMXOA IS NULL";

		try (Connection conn = DBConnect.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, tierId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapRowToTier(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO error - getTierById: " + e.getMessage());
		}

		return null;
	}

	// Tim hang thanh vien dang hoat dong theo ten.
	public HangThanhVienDTO findActiveTierByName(String tierName) {
		String sql = TIER_SELECT_SQL + " WHERE LOWER(TENHANG) = LOWER(?) AND THOIDIEMXOA IS NULL";

		try (Connection conn = DBConnect.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, tierName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapRowToTier(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO error - findActiveTierByName: " + e.getMessage());
		}

		return null;
	}

	// Tim hang thanh vien da xoa mem theo ten.
	public HangThanhVienDTO findDeletedTierByName(String tierName) {
		String sql = TIER_SELECT_SQL + " WHERE LOWER(TENHANG) = LOWER(?) AND THOIDIEMXOA IS NOT NULL";

		try (Connection conn = DBConnect.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, tierName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapRowToTier(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO error - findDeletedTierByName: " + e.getMessage());
		}

		return null;
	}

	// Lay danh sach hang thanh vien da xoa mem.
	public List<HangThanhVienDTO> getAllDeletedTiers() throws SQLException {
		String sql = TIER_SELECT_SQL + " WHERE THOIDIEMXOA IS NOT NULL ORDER BY THOIDIEMXOA DESC, MAHANG DESC";
		List<HangThanhVienDTO> tiers = new ArrayList<>();

		try (Connection conn = DBConnect.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()) {
				tiers.add(mapRowToTier(rs));
			}
		} catch (SQLException e) {
			System.err.println("DAO error - getAllDeletedTiers: " + e.getMessage());
			throw e;
		}

		return tiers;
	}

	// Tao hang thanh vien moi qua procedure.
	public int createTier(HangThanhVienDTO tier) throws SQLException {
		String sql = "{ CALL PROC_THEM_HANGTHANHVIEN(?, ?, ?, ?) }";

		try (Connection conn = DBConnect.getConnection();
			 CallableStatement cstmt = conn.prepareCall(sql)) {
			cstmt.setString(1, tier.getTenHang());
			cstmt.setInt(2, tier.getDiemToiThieu());
			cstmt.setBigDecimal(3, tier.getPhanTramGiamGia());
			cstmt.registerOutParameter(4, Types.NUMERIC);
			cstmt.execute();

			Object outValue = cstmt.getObject(4);
			if (outValue instanceof Number) {
				return ((Number) outValue).intValue();
			}

			if (outValue != null) {
				try {
					return Integer.parseInt(outValue.toString());
				} catch (NumberFormatException ignored) {
					// Fallback to explicit error below.
				}
			}

			throw new SQLException("Khong nhan duoc ma hang thanh vien moi");
		} catch (SQLException e) {
			String errorMsg = mapProcedureErrorToMessage(e);
			System.err.println("DAO error - createTier: " + errorMsg);
			throw new SQLException(errorMsg, e);
		}
	}

	// Cap nhat hang thanh vien qua procedure.
	public void updateTier(HangThanhVienDTO tier) throws SQLException {
		String sql = "{ CALL PROC_SUA_HANGTHANHVIEN(?, ?, ?, ?) }";

		try (Connection conn = DBConnect.getConnection();
			 CallableStatement cstmt = conn.prepareCall(sql)) {
			cstmt.setInt(1, tier.getMaHang());
			cstmt.setString(2, tier.getTenHang() != null ? tier.getTenHang() : "");
			cstmt.setInt(3, tier.getDiemToiThieu());
			cstmt.setBigDecimal(4, tier.getPhanTramGiamGia());
			cstmt.execute();
		} catch (SQLException e) {
			String errorMsg = mapProcedureErrorToMessage(e);
			System.err.println("DAO error - updateTier: " + errorMsg);
			throw new SQLException(errorMsg, e);
		}
	}

	// Xoa mem hang thanh vien qua procedure.
	public void softDeleteTier(int tierId, int deletedByEmployeeId) throws SQLException {
		String sql = "{ CALL PROC_XOA_HANGTHANHVIEN(?, ?) }";

		try (Connection conn = DBConnect.getConnection();
			 CallableStatement cstmt = conn.prepareCall(sql)) {
			cstmt.setInt(1, tierId);
			cstmt.setInt(2, deletedByEmployeeId);
			cstmt.execute();
		} catch (SQLException e) {
			String errorMsg = mapProcedureErrorToMessage(e);
			System.err.println("DAO error - softDeleteTier: " + errorMsg);
			throw new SQLException(errorMsg, e);
		}
	}

	// Khoi phuc hang thanh vien da xoa mem.
	public void restoreTier(int tierId) throws SQLException {
		String sql = "UPDATE HANGTHANHVIEN SET THOIDIEMXOA = NULL, MANX = NULL WHERE MAHANG = ?";

		try (Connection conn = DBConnect.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, tierId);
			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated == 0) {
				throw new SQLException("Khong tim thay hang thanh vien de khoi phuc");
			}
		} catch (SQLException e) {
			System.err.println("DAO error - restoreTier: " + e.getMessage());
			throw e;
		}
	}

	// Dem tong so hang thanh vien dang hoat dong.
	public int countActiveTiers() {
		String sql = "SELECT COUNT(*) FROM HANGTHANHVIEN WHERE THOIDIEMXOA IS NULL";

		try (Connection conn = DBConnect.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("DAO error - countActiveTiers: " + e.getMessage());
		}

		return 0;
	}

	// Lay hang mac dinh co ten "Khong".
	public HangThanhVienDTO getDefaultTier() {
		HangThanhVienDTO defaultTier = findActiveTierByName("Không");
		if (defaultTier != null) {
			return defaultTier;
		}
		return findActiveTierByName("Khong");
	}

	// Lay hang phu hop theo diem (hang co DIEMTOITHIEU lon nhat <= points).
	public HangThanhVienDTO getTierByPoints(int points) {
		String sql = """
				SELECT MAHANG, TENHANG, DIEMTOITHIEU, PHANTRAMGIAMGIA, THOIDIEMXOA, MANX
				FROM HANGTHANHVIEN
				WHERE THOIDIEMXOA IS NULL
				  AND DIEMTOITHIEU <= ?
				ORDER BY DIEMTOITHIEU DESC
				FETCH FIRST 1 ROW ONLY
				""";

		try (Connection conn = DBConnect.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, points);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return mapRowToTier(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("DAO error - getTierByPoints: " + e.getMessage());
		}

		return null;
	}

	private HangThanhVienDTO mapRowToTier(ResultSet rs) throws SQLException {
		HangThanhVienDTO tier = new HangThanhVienDTO();
		tier.setMaHang(rs.getInt("MAHANG"));
		tier.setTenHang(rs.getString("TENHANG"));
		tier.setDiemToiThieu(rs.getInt("DIEMTOITHIEU"));
		tier.setPhanTramGiamGia(rs.getBigDecimal("PHANTRAMGIAMGIA"));

		if (rs.getTimestamp("THOIDIEMXOA") != null) {
			tier.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
		}

		tier.setMaNX(rs.getInt("MANX"));
		return tier;
	}

	// Chuyen ma loi DB thanh thong diep de Service hien thi.
	private String mapProcedureErrorToMessage(SQLException e) {
		int errorCode = e.getErrorCode();
		return switch (errorCode) {
			case -20114 -> "Loi he thong khi them hang thanh vien";
			case -20115 -> "Khong tim thay hang thanh vien de cap nhat";
			case -20116 -> "Loi he thong khi cap nhat hang thanh vien";
			case -20117 -> "Khong tim thay hang thanh vien de xoa";
			case -20118 -> "Loi he thong khi xoa hang thanh vien";
			default -> "Loi co so du lieu: " + e.getMessage();
		};
	}
}
