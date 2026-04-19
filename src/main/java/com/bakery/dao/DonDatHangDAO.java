package com.bakery.dao;

import com.bakery.dto.CTDonHangDTO;
import com.bakery.dto.CTDonTuyChinhDTO;
import com.bakery.dto.DonDatHangDTO;
import com.bakery.dto.TrangThaiDonDTO;
import com.bakery.utils.DBConnect;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DonDatHangDAO {

    public int taoDonHang(DonDatHangDTO donDatHang, List<CTDonHangDTO> dsCtDonHang, List<CTDonTuyChinhDTO> dsCtTuyChinh) throws SQLException {
        String sql = "{CALL PROC_TAODONHANG(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) throw new SQLException("Khong the ket noi CSDL.");

            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.setTimestamp(1, Timestamp.valueOf(donDatHang.getNgayGioNhanBanh()));
                if (donDatHang.getMaKH() != null) cstmt.setInt(2, donDatHang.getMaKH()); else cstmt.setNull(2, Types.NUMERIC);
                cstmt.setInt(3, donDatHang.getMaNVLap());
                cstmt.setInt(4, donDatHang.getMaTrangThai());
                cstmt.setDouble(5, donDatHang.getTienDaCoc());
                if (donDatHang.getHinhThucNhan() != null) cstmt.setInt(6, donDatHang.getHinhThucNhan()); else cstmt.setNull(6, Types.NUMERIC);
                if (donDatHang.getDiaChiGiao() != null && !donDatHang.getDiaChiGiao().trim().isEmpty()) cstmt.setString(7, donDatHang.getDiaChiGiao().trim()); else cstmt.setNull(7, Types.NVARCHAR);

                cstmt.setString(8, taoJsonChiTiet(dsCtDonHang, dsCtTuyChinh));
                cstmt.registerOutParameter(9, Types.NUMERIC);
                cstmt.execute();

                int maDonMoi = cstmt.getInt(9);
                return maDonMoi;
            }
        } catch (SQLException e) { throw e; }
    }

    public void chuyenTrangThaiDon(int maDon, int maTrangThaiMoi, int maNvCapNhat, Integer hinhThucNhan) throws SQLException {
        String sql = "{CALL PROC_CHUYENTRANGTHAIDON(?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) throw new SQLException("Khong the ket noi CSDL.");

            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.setInt(1, maDon);
                cstmt.setInt(2, maTrangThaiMoi);
                cstmt.setInt(3, maNvCapNhat);
                if (hinhThucNhan != null) cstmt.setInt(4, hinhThucNhan); else cstmt.setNull(4, Types.NUMERIC);
                cstmt.execute();
            }
        } catch (SQLException e) { throw e; }
    }

    public void huyDonVaHoanKho(int maDon, String lyDoHuy, int maNvCapNhat) throws SQLException {
        String sql = "{CALL PROC_HUYDONVAHOANKHO(?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) throw new SQLException("Khong the ket noi CSDL.");

            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.setInt(1, maDon);
                cstmt.setString(2, lyDoHuy);
                cstmt.setInt(3, maNvCapNhat);
                cstmt.execute();
            }
        } catch (SQLException e) { throw e; }
    }

    public boolean tonTaiDonHang(int maDon) throws SQLException {
        String sql = "SELECT COUNT(*) AS TOTAL FROM DONDATHANG WHERE MADON = ?";
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) throw new SQLException("Khong the ket noi CSDL.");
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getInt("TOTAL") > 0;
                }
            }
        } catch (SQLException e) { throw e; }
        return false;
    }

    public String layTenTrangThaiDon(int maDon) throws SQLException {
        String sql = "SELECT TT.TENTRANGTHAI FROM DONDATHANG DDH JOIN TRANGTHAIDON TT ON DDH.MATRANGTHAI = TT.MATRANGTHAI WHERE DDH.MADON = ?";
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) throw new SQLException("Khong the ket noi CSDL.");
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getString("TENTRANGTHAI");
                }
            }
        } catch (SQLException e) { throw e; }
        return null;
    }

    public DonDatHangDTO layTomTatDonHang(int maDon) throws SQLException {
        // Bổ sung TIENDACOC vào truy vấn
        String sql = "SELECT MADON, MAKH, MATRANGTHAI, TONGTIENHDBAN, TIENDACOC FROM DONDATHANG WHERE MADON = ?";
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) throw new SQLException("Khong the ket noi CSDL.");
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        DonDatHangDTO dto = new DonDatHangDTO();
                        dto.setMaDon(rs.getInt("MADON"));
                        int maKH = rs.getInt("MAKH");
                        if (!rs.wasNull()) dto.setMaKH(maKH);
                        dto.setMaTrangThai(rs.getInt("MATRANGTHAI"));
                        dto.setTongTienHDBan(rs.getDouble("TONGTIENHDBAN"));
                        dto.setTienDaCoc(rs.getDouble("TIENDACOC")); // Lấy giá trị cọc
                        return dto;
                    }
                }
            }
        } catch (SQLException e) { throw e; }
        return null;
    }

    public List<TrangThaiDonDTO> layDanhSachTrangThaiDon() throws SQLException {
        List<TrangThaiDonDTO> list = new ArrayList<>();
        String sql = "SELECT MATRANGTHAI, TENTRANGTHAI FROM TRANGTHAIDON ORDER BY MATRANGTHAI";
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) throw new SQLException("Khong the ket noi CSDL.");
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        TrangThaiDonDTO dto = new TrangThaiDonDTO();
                        dto.setMaTrangThai(rs.getInt("MATRANGTHAI"));
                        dto.setTenTrangThai(rs.getString("TENTRANGTHAI"));
                        list.add(dto);
                    }
                }
            }
        } catch (SQLException e) { throw e; }
        return list;
    }

    private String taoJsonChiTiet(List<CTDonHangDTO> dsCtDonHang, List<CTDonTuyChinhDTO> dsCtTuyChinh) {
        StringBuilder json = new StringBuilder("[");
        boolean hasItem = false;

        if (dsCtDonHang != null) {
            for (CTDonHangDTO item : dsCtDonHang) {
                if (hasItem) json.append(",");
                hasItem = true;
                json.append("{")
                        .append("\"maSP\":").append(item.getMaSP()).append(",")
                        .append("\"soLuong\":").append(item.getSoLuong()).append(",")
                        .append("\"donGia\":").append(item.getDonGia()).append(",")
                        .append("\"isCustom\":\"false\"")
                        .append("}");
            }
        }

        if (dsCtTuyChinh != null) {
            for (CTDonTuyChinhDTO item : dsCtTuyChinh) {
                if (hasItem) json.append(",");
                hasItem = true;
                json.append("{")
                        .append("\"maSP\":").append(item.getMaSP()).append(",")
                        .append("\"soLuong\":").append(item.getSoLuong()).append(",")
                        .append("\"donGia\":").append(item.getDonGia()).append(",")
                        .append("\"isCustom\":\"true\",")
                        .append("\"ghiChu\":\"").append(thoatKyTuJson(item.getLoiChucTrenBanh())).append("\",")
                        .append("\"phuKien\":\"").append(thoatKyTuJson(item.getGhiChuThoBanh())).append("\"")
                        .append("}");
            }
        }

        json.append("]");
        return json.toString();
    }

    private String thoatKyTuJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
    }
}