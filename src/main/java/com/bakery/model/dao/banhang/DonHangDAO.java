package com.bakery.model.dao.banhang;
import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.banhang.CTDonHangDTO;
import com.bakery.model.dto.banhang.CTDonTuyChinhDTO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.banhang.TrangThaiDonDTO;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DonHangDAO extends BaseDAO {

    /**
     * Tạo đơn hàng mới — standalone transaction.
     * Dùng explicit setAutoCommit(false) để đảm bảo PROC_TAODONHANG
     * có transaction context thực sự: SELECT FOR UPDATE bên trong proc
     * sẽ block đúng các connection khác, ngăn Lost Update khi 2 thu ngân
     * cùng bán sản phẩm có tồn kho ít.
     */
    public int taoDonHang(DonDatHangDTO donDatHang, List<CTDonHangDTO> dsCtDonHang, List<CTDonTuyChinhDTO> dsCtTuyChinh) throws Exception {
        Connection conn = null;
        try {
            conn = moKetNoi();
            conn.setAutoCommit(false);   // Bắt đầu explicit transaction
            int maDon = taoDonHangWithConn(conn, donDatHang, dsCtDonHang, dsCtTuyChinh);
            conn.commit();
            return maDon;
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (Exception ignored) {} }
            handleException("taoDonHang", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {} }
        }
        return -1;
    }

    /**
     * Overload dùng trong Distributed Transaction — nhận Connection từ tầng Service.
     * Không đóng Connection; không COMMIT (Service chịu trách nhiệm).
     */
    public int taoDonHang(Connection conn, DonDatHangDTO donDatHang, List<CTDonHangDTO> dsCtDonHang, List<CTDonTuyChinhDTO> dsCtTuyChinh) throws Exception {
        try {
            return taoDonHangWithConn(conn, donDatHang, dsCtDonHang, dsCtTuyChinh);
        } catch (SQLException e) {
            handleException("taoDonHang[tx]", e);
        }
        return -1;
    }

    private int taoDonHangWithConn(Connection conn, DonDatHangDTO donDatHang, List<CTDonHangDTO> dsCtDonHang, List<CTDonTuyChinhDTO> dsCtTuyChinh) throws SQLException {
        String sql = "{CALL PROC_TAODONHANG(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setTimestamp(1, Timestamp.valueOf(donDatHang.getNgayGioNhanBanh()));
            if (donDatHang.getMaKH() != null) cstmt.setInt(2, donDatHang.getMaKH()); else cstmt.setNull(2, Types.NUMERIC);
            cstmt.setInt(3, donDatHang.getMaNVLap());
            cstmt.setInt(4, donDatHang.getMaTrangThai());
            cstmt.setBigDecimal(5, donDatHang.getTienDaCoc());
            if (donDatHang.getHinhThucNhan() != null) cstmt.setInt(6, donDatHang.getHinhThucNhan()); else cstmt.setNull(6, Types.NUMERIC);
            if (donDatHang.getDiaChiGiao() != null && !donDatHang.getDiaChiGiao().trim().isEmpty()) cstmt.setString(7, donDatHang.getDiaChiGiao().trim()); else cstmt.setNull(7, Types.NVARCHAR);
            cstmt.setString(8, taoJsonChiTiet(dsCtDonHang, dsCtTuyChinh));
            cstmt.registerOutParameter(9, Types.NUMERIC);
            cstmt.execute();
            return cstmt.getInt(9);
        }
    }

    /**
     * Chuyển trạng thái đơn — explicit transaction.
     * PROC_CHUYENTRANGTHAIDON có SELECT FOR UPDATE để đảm bảo
     * 2 nhân viên không cùng xác nhận 1 đơn tại cùng thời điểm.
     */
    public void chuyenTrangThaiDon(int maDon, int maTrangThaiMoi, int maNvCapNhat, Integer hinhThucNhan) throws Exception {
        Connection conn = null;
        try {
            conn = moKetNoi();
            conn.setAutoCommit(false);
            String sql = "{CALL PROC_CHUYENTRANGTHAIDON(?, ?, ?, ?)}";
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.setInt(1, maDon);
                cstmt.setInt(2, maTrangThaiMoi);
                cstmt.setInt(3, maNvCapNhat);
                if (hinhThucNhan != null) cstmt.setInt(4, hinhThucNhan); else cstmt.setNull(4, Types.NUMERIC);
                cstmt.execute();
            }
            conn.commit();
        } catch (Exception e) {
            if (conn != null) { try { conn.rollback(); } catch (Exception ignored) {} }
            handleException("chuyenTrangThaiDon", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {} }
        }
    }

    public void huyDonVaHoanKho(int maDon, String lyDoHuy, int maNvCapNhat, double refundAmount, int maCa) throws Exception {
        String sql = "{CALL PROC_HUYDON_HOANCOC(?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi()) {
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.setInt(1, maDon);
                cstmt.setString(2, lyDoHuy);
                cstmt.setInt(3, maNvCapNhat);
                cstmt.setDouble(4, refundAmount);
                cstmt.setInt(5, maCa);
                cstmt.execute();
            }
        } catch (SQLException e) {
            handleException("huyDonVaHoanKho", e);
        }
    }

    /** Hủy hóa đơn bán lẻ đã hoàn thành — hoàn kho, không hoàn tiền mặt. */
    public void huyHoaDonBanLe(int maDon, String lyDoHuy, int maNvCapNhat) throws Exception {
        String sql = "{CALL PROC_HUYHOADONBANLE(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maDon);
            cstmt.setString(2, lyDoHuy);
            cstmt.setInt(3, maNvCapNhat);
            cstmt.execute();
        } catch (SQLException e) {
            handleException("huyHoaDonBanLe", e);
            throw e;
        }
    }

    public boolean tonTaiDonHang(int maDon) throws Exception {
        String sql = "SELECT COUNT(*) AS TOTAL FROM DONDATHANG WHERE MADON = ?";
        try (Connection conn = moKetNoi()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getInt("TOTAL") > 0;
                }
            }
        } catch (SQLException e) {
            handleException("tonTaiDonHang", e);
        }
        return false;
    }

    public String layTenTrangThaiDon(int maDon) throws Exception {
        String sql = "SELECT TT.TENTRANGTHAI FROM DONDATHANG DDH " +
                "JOIN TRANGTHAIDON TT ON DDH.MATRANGTHAI = TT.MATRANGTHAI " +
                "WHERE DDH.MADON = ?";
        try (Connection conn = moKetNoi()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getString("TENTRANGTHAI");
                }
            }
        } catch (SQLException e) {
            handleException("layTenTrangThaiDon", e);
        }
        return null;
    }

    public DonDatHangDTO layTomTatDonHang(int maDon) throws Exception {
        String sql = "SELECT * FROM VW_DanhSachDonHang WHERE MADON = ?";
        try (Connection conn = moKetNoi()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        DonDatHangDTO dto = new DonDatHangDTO();
                        dto.setMaDon(rs.getInt("MADON"));
                        int maKH = rs.getInt("MAKH");
                        if (!rs.wasNull()) dto.setMaKH(maKH);
                        dto.setMaTrangThai(rs.getInt("MATRANGTHAI"));
                        dto.setTenTrangThai(rs.getString("TENTRANGTHAI"));
                        if (rs.getTimestamp("NGAYGIONHANBANH") != null) {
                            dto.setNgayGioNhanBanh(rs.getTimestamp("NGAYGIONHANBANH").toLocalDateTime());
                        }
                        dto.setTongTienHDBan(rs.getBigDecimal("TONGTIENHDBAN"));
                        dto.setTienDaCoc(rs.getBigDecimal("TIENDACOC"));
                        int hinhThucNhan = rs.getInt("HINHTHUCNHAN");
                        if (!rs.wasNull()) dto.setHinhThucNhan(hinhThucNhan);
                        return dto;
                    }
                }
            }
        } catch (SQLException e) {
            handleException("layTomTatDonHang", e);
        }
        return null;
    }

    public List<DonDatHangDTO> layDanhSachDonTheoDoi(String maDonSearch, String tenKhachSearch, LocalDate ngayNhan, LocalTime gioTu, LocalTime gioDen, String trangThaiFilter) throws Exception {
        List<DonDatHangDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT MADON, MAKH, TENKHACHHANG, MATRANGTHAI, TENTRANGTHAI, NGAYGIONHANBANH, TONGTIENHDBAN " +
                "FROM VW_DanhSachDonHang " +
                "WHERE 1 = 1");

        if ("COMPLETED".equalsIgnoreCase(trangThaiFilter)) {
            sql.append(" AND UPPER(TENTRANGTHAI) = UPPER(N'Hoàn thành')");
        } else if ("NOT_COMPLETED".equalsIgnoreCase(trangThaiFilter)) {
            sql.append(" AND UPPER(TENTRANGTHAI) <> UPPER(N'Hoàn thành')");
        }

        if (maDonSearch != null && !maDonSearch.trim().isEmpty()) {
            sql.append(" AND MADON = ?");
        }
        if (tenKhachSearch != null && !tenKhachSearch.trim().isEmpty()) {
            sql.append(" AND UPPER(TENKHACHHANG) LIKE UPPER(?)");
        }
        if (ngayNhan != null) {
            sql.append(" AND TRUNC(NGAYGIONHANBANH) = ?");
        }
        if (gioTu != null) {
            sql.append(" AND TO_CHAR(NGAYGIONHANBANH, 'HH24:MI') >= ?");
        }
        if (gioDen != null) {
            sql.append(" AND TO_CHAR(NGAYGIONHANBANH, 'HH24:MI') <= ?");
        }
        sql.append(" ORDER BY NGAYGIONHANBANH ASC, MADON ASC");

        try (Connection conn = moKetNoi()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                if (maDonSearch != null && !maDonSearch.trim().isEmpty()) {
                    pstmt.setInt(paramIndex++, Integer.parseInt(maDonSearch.trim()));
                }
                if (tenKhachSearch != null && !tenKhachSearch.trim().isEmpty()) {
                    pstmt.setString(paramIndex++, "%" + tenKhachSearch.trim() + "%");
                }
                if (ngayNhan != null) {
                    pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(ngayNhan.atStartOfDay()));
                }
                if (gioTu != null) {
                    pstmt.setString(paramIndex++, gioTu.toString());
                }
                if (gioDen != null) {
                    pstmt.setString(paramIndex++, gioDen.toString());
                }
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        DonDatHangDTO dto = new DonDatHangDTO();
                        dto.setMaDon(rs.getInt("MADON"));
                        int maKH = rs.getInt("MAKH");
                        if (!rs.wasNull()) dto.setMaKH(maKH);
                        dto.setMaTrangThai(rs.getInt("MATRANGTHAI"));
                        dto.setTenTrangThai(rs.getString("TENTRANGTHAI"));
                        if (rs.getTimestamp("NGAYGIONHANBANH") != null) {
                            dto.setNgayGioNhanBanh(rs.getTimestamp("NGAYGIONHANBANH").toLocalDateTime());
                        }
                        dto.setTongTienHDBan(rs.getBigDecimal("TONGTIENHDBAN"));
                        list.add(dto);
                    }
                }
            }
        } catch (SQLException e) {
            handleException("layDanhSachDonTheoDoi", e);
        }
        return list;
    }

    public List<TrangThaiDonDTO> layDanhSachTrangThaiDon() throws Exception {
        List<TrangThaiDonDTO> list = new ArrayList<>();
        String sql = "SELECT MATRANGTHAI, TENTRANGTHAI FROM TRANGTHAIDON ORDER BY MATRANGTHAI";
        try (Connection conn = moKetNoi()) {
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
        } catch (SQLException e) {
            handleException("layDanhSachTrangThaiDon", e);
        }
        return list;
    }

    private String taoJsonChiTiet(List<CTDonHangDTO> dsCtDonHang, List<CTDonTuyChinhDTO> dsCtTuyChinh) {
        StringBuilder json = new StringBuilder("[");
        boolean hasItem = false;

        if (dsCtDonHang != null) {
            for (CTDonHangDTO item : dsCtDonHang) {
                if (hasItem) json.append(",");
                hasItem = true;
                // Null-guard: donGia null sẽ khiến Oracle SUM = NULL → TONGTIENHDBAN = 0 → vi phạm CK_DON_THANHTOAN
                java.math.BigDecimal donGia = item.getDonGia() != null ? item.getDonGia() : java.math.BigDecimal.ZERO;
                json.append("{")
                        .append("\"maSP\":").append(item.getMaSP()).append(",")
                        .append("\"soLuong\":").append(item.getSoLuong()).append(",")
                        .append("\"donGia\":").append(donGia.toPlainString()).append(",")
                        .append("\"isCustom\":\"false\"")
                        .append("}");
            }
        }

        if (dsCtTuyChinh != null) {
            for (CTDonTuyChinhDTO item : dsCtTuyChinh) {
                if (hasItem) json.append(",");
                hasItem = true;
                java.math.BigDecimal donGia = item.getDonGia() != null ? item.getDonGia() : java.math.BigDecimal.ZERO;
                json.append("{")
                        .append("\"maSP\":").append(item.getMaSP()).append(",")
                        .append("\"soLuong\":").append(item.getSoLuong()).append(",")
                        .append("\"donGia\":").append(donGia.toPlainString()).append(",")
                        .append("\"isCustom\":\"true\",")
                        .append("\"ghiChu\":\"").append(thoatKyTuJson(item.getLoiChucTrenBanh())).append("\",")
                        .append("\"phuKien\":\"").append(thoatKyTuJson(item.getGhiChuThoBanh())).append("\",")
                        .append("\"maKC\":").append(item.getMaKC() != null ? item.getMaKC() : "null").append(",")
                        .append("\"maCot\":").append(item.getMaCot() != null ? item.getMaCot() : "null").append(",")
                        .append("\"maNhan\":").append(item.getMaNhan() != null ? item.getMaNhan() : "null").append(",")
                        .append("\"maTrangTri\":").append(item.getMaTrangTri() != null ? item.getMaTrangTri() : "null")
                        .append("}");
            }
        }

        json.append("]");
        return json.toString();
    }

    public List<CTDonHangDTO> layChiTietDonHang(int maDon) throws Exception {
        List<CTDonHangDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM CTDONHANG WHERE MADON = ?";
        try (Connection conn = moKetNoi()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        CTDonHangDTO item = new CTDonHangDTO();
                        item.setMaCTHD(rs.getInt("MACTHD"));
                        item.setMaDon(rs.getInt("MADON"));
                        item.setMaSP(rs.getInt("MASP"));
                        item.setSoLuong(rs.getInt("SOLUONG"));
                        item.setDonGia(rs.getBigDecimal("DONGIA"));
                        item.setDonGiaVon(rs.getBigDecimal("DONGIAVON"));
                        item.setPhanTramGiam(rs.getDouble("PHANTRAMGIAM"));
                        list.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            handleException("layChiTietDonHang", e);
        }
        return list;
    }

    public List<CTDonTuyChinhDTO> layChiTietTuyChinh(int maDon) throws Exception {
        List<CTDonTuyChinhDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM CTDON_TUYCHINH WHERE MADON = ?";
        try (Connection conn = moKetNoi()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        CTDonTuyChinhDTO item = new CTDonTuyChinhDTO();
                        item.setMaCTTC(rs.getInt("MACTTC"));
                        item.setMaDon(rs.getInt("MADON"));
                        item.setMaSP(rs.getInt("MASP"));
                        item.setSoLuong(rs.getInt("SOLUONG"));
                        item.setDonGia(rs.getBigDecimal("DONGIA"));
                        item.setDonGiaVon(rs.getBigDecimal("DONGIAVON"));
                        item.setMaKC(rs.getInt("MAKC"));
                        item.setMaCot(rs.getInt("MACOT"));
                        item.setMaNhan(rs.getInt("MANHAN"));
                        item.setMaTrangTri(rs.getInt("MATRANGTRI"));
                        item.setLoiChucTrenBanh(rs.getString("LOICHUCTRENBANH"));
                        item.setGhiChuThoBanh(rs.getString("GHICHUTHOBANH"));
                        list.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            handleException("layChiTietTuyChinh", e);
        }
        return list;
    }

    private String thoatKyTuJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t");
    }
}
