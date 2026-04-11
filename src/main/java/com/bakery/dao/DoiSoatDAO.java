package com.bakery.dao;

import com.bakery.dto.DoiSoatDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoiSoatDAO {

    public List<DoiSoatDTO> layDanhSachDoiSoat() {
        List<DoiSoatDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM DOISOAT";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DoiSoatDTO dsDto = new DoiSoatDTO();
                dsDto.setMaCa(rs.getInt("MACA"));
                dsDto.setTienKhaiBaoDauCa(rs.getDouble("TIENKHAIBAODAUCA"));
                dsDto.setTongTienHeThong(rs.getDouble("TONGTIENHETHONG"));
                dsDto.setTienThucTeDem(rs.getDouble("TIENTHUCTEDEM"));
                dsDto.setChenhLech(rs.getDouble("CHENHLECH"));
                dsDto.setLyDoChenhLech(rs.getString("LYDOCHENHLECH"));

                ds.add(dsDto);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - layDanhSachDoiSoat: " + e.getMessage());
        }
        return ds;
    }

    public boolean themDoiSoatMoi(DoiSoatDTO ds) {
        String sql = "INSERT INTO DOISOAT (MACA, TIENKHAIBAODAUCA, TONGTIENHETHONG, TIENTHUCTEDEM, CHENHLECH, LYDOCHENHLECH) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ds.getMaCa());
            pstmt.setDouble(2, ds.getTienKhaiBaoDauCa());
            pstmt.setDouble(3, ds.getTongTienHeThong());
            pstmt.setDouble(4, ds.getTienThucTeDem());
            pstmt.setDouble(5, ds.getChenhLech());
            pstmt.setString(6, ds.getLyDoChenhLech());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - themDoiSoatMoi: " + e.getMessage());
        }
        return false;
    }

    public boolean capNhatDoiSoat(DoiSoatDTO ds) {
        String sql = "UPDATE DOISOAT SET TIENKHAIBAODAUCA = ?, TONGTIENHETHONG = ?, TIENTHUCTEDEM = ?, CHENHLECH = ?, LYDOCHENHLECH = ? WHERE MACA = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, ds.getTienKhaiBaoDauCa());
            pstmt.setDouble(2, ds.getTongTienHeThong());
            pstmt.setDouble(3, ds.getTienThucTeDem());
            pstmt.setDouble(4, ds.getChenhLech());
            pstmt.setString(5, ds.getLyDoChenhLech());
            pstmt.setInt(6, ds.getMaCa());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatDoiSoat: " + e.getMessage());
        }
        return false;
    }
}