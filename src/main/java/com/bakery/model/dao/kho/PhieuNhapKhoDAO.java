package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.KetQuaKiemKeDTO;
import com.bakery.model.dto.kho.PhieuNhapKhoDTO;
import com.bakery.model.dto.kho.CTPhieuNhapDTO;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO cho PHIEUNHAPKHO và CTPHIEUNHAP.
 * Gọi PROC_TAOPHIEUNHAPKHO với payload JSON.
 */
public class PhieuNhapKhoDAO extends BaseDAO {

    /** Lấy danh sách phiếu nhập gần đây (50 phiếu mới nhất). */
    public List<PhieuNhapKhoDTO> layDanhSachPhieuNhap() throws Exception {
        List<PhieuNhapKhoDTO> list = new ArrayList<>();
        String sql = "SELECT PN.MAPN, PN.NGAYNHAP, PN.TONGTIENNHAP, " +
                "NV.HOTEN AS TENNV, NCC.TENNCC " +
                "FROM PHIEUNHAPKHO PN " +
                "LEFT JOIN NHANVIEN NV ON PN.MANV = NV.MANV " +
                "LEFT JOIN NHACUNGCAP NCC ON PN.MANCC = NCC.MANCC " +
                "ORDER BY PN.MAPN DESC " +
                "FETCH FIRST 50 ROWS ONLY";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuNhapKhoDTO dto = new PhieuNhapKhoDTO();
                dto.setMaPN(rs.getInt("MAPN"));
                dto.setNgayNhap(rs.getTimestamp("NGAYNHAP") != null
                        ? rs.getTimestamp("NGAYNHAP").toLocalDateTime() : null);
                dto.setTongTienNhap(rs.getBigDecimal("TONGTIENNHAP"));
                dto.setTenNhanVien(rs.getString("TENNV"));
                dto.setTenNhaCungCap(rs.getString("TENNCC"));
                list.add(dto);
            }
        } catch (SQLException e) {
            handleException("layDanhSachPhieuNhap", e);
        }
        return list;
    }

    /** Lấy chi tiết các lô trong một phiếu nhập. */
    public List<CTPhieuNhapDTO> layChiTietPhieuNhap(int maPN) throws Exception {
        List<CTPhieuNhapDTO> list = new ArrayList<>();
        String sql = "SELECT CT.MALO, CT.MANL, NL.TENNL, CT.SOLUONG, CT.DONGIA, " +
                "CT.SOLUONGCONLAI, CT.NGAYSANXUAT, CT.HANSUDUNG " +
                "FROM CTPHIEUNHAP CT " +
                "JOIN NGUYENLIEU NL ON CT.MANL = NL.MANL " +
                "WHERE CT.MAPN = ? ORDER BY CT.MALO";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPN);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CTPhieuNhapDTO dto = new CTPhieuNhapDTO();
                    dto.setMaLo(rs.getInt("MALO"));
                    dto.setMaPN(maPN);
                    dto.setMaNL(rs.getInt("MANL"));
                    dto.setTenNL(rs.getString("TENNL"));
                    dto.setSoLuong(rs.getDouble("SOLUONG"));
                    dto.setDonGia(rs.getBigDecimal("DONGIA"));
                    dto.setSoLuongConLai(rs.getDouble("SOLUONGCONLAI"));
                    if (rs.getDate("NGAYSANXUAT") != null)
                        dto.setNgaySanXuat(rs.getDate("NGAYSANXUAT").toLocalDate());
                    if (rs.getDate("HANSUDUNG") != null)
                        dto.setHanSuDung(rs.getDate("HANSUDUNG").toLocalDate());
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            handleException("layChiTietPhieuNhap", e);
        }
        return list;
    }

    /**
     * Lấy chi tiết CTPHIEUNHAP cho nhiều phiếu cùng lúc (batch query).
     * Kết quả nhóm theo MAPN, sắp xếp theo tên nguyên liệu.
     *
     * @param maPhieus danh sách mã phiếu
     * @return Map&lt;MAPN, List&lt;String[]{TENNL, soLuong, TENDVT, donGia_raw, thanhTien_raw}&gt;&gt;
     */
    public Map<Integer, List<String[]>> layChiTietNhieuPhieuNhap(List<Integer> maPhieus)
            throws Exception {
        Map<Integer, List<String[]>> result = new LinkedHashMap<>();
        if (maPhieus == null || maPhieus.isEmpty()) return result;

        String inClause = maPhieus.stream()
                .map(String::valueOf).collect(Collectors.joining(","));
        String sql =
            "SELECT ctp.MAPN, nl.TENNL, ctp.SOLUONG, dvt.TENDVT, ctp.DONGIA, " +
            "       ROUND(ctp.SOLUONG * ctp.DONGIA, 0) AS THANHTIEN " +
            "FROM CTPHIEUNHAP ctp " +
            "JOIN NGUYENLIEU nl ON nl.MANL = ctp.MANL " +
            "JOIN DONVITINH dvt ON dvt.MADVT = nl.MADVT " +
            "WHERE ctp.MAPN IN (" + inClause + ") " +
            "ORDER BY ctp.MAPN, nl.TENNL";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int maPN = rs.getInt("MAPN");
                double soLuong = rs.getDouble("SOLUONG");
                String slStr = soLuong % 1 == 0
                        ? String.valueOf((long) soLuong) : String.valueOf(soLuong);
                result.computeIfAbsent(maPN, k -> new ArrayList<>())
                      .add(new String[]{
                          rs.getString("TENNL"),
                          slStr,
                          rs.getString("TENDVT"),
                          String.valueOf(rs.getLong("DONGIA")),
                          String.valueOf(rs.getLong("THANHTIEN"))
                      });
            }
        } catch (SQLException e) {
            handleException("layChiTietNhieuPhieuNhap", e);
            throw e;
        }
        return result;
    }

    /** Tạo phiếu nhập kho qua PROC_TAOPHIEUNHAPKHO. */
    public int taoPhieuNhap(int maNV, int maNCC, String jsonChiTiet, int maCa) throws Exception {
        String sql = "{CALL PROC_TAOPHIEUNHAPKHO(?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maNV);
            cs.setInt(2, maNCC);
            cs.setClob(3, new java.io.StringReader(jsonChiTiet));
            cs.setInt(4, maCa);
            cs.registerOutParameter(5, Types.NUMERIC);
            cs.execute();
            return cs.getInt(5);
        } catch (SQLException e) {
            handleException("taoPhieuNhap", e);
            throw e;
        }
    }

    /** Hủy phiếu nhập kho qua PROC_HUYPHIEUNHAPKHO. */
    public void huyPhieuNhap(int maPN) throws Exception {
        String sql = "{CALL PROC_HUYPHIEUNHAPKHO(?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maPN);
            cs.execute();
        } catch (SQLException e) {
            handleException("huyPhieuNhap", e);
            throw e;
        }
    }

    /** Lập báo cáo phiếu nhập kho — gọi PROC_LAPBAOCAOPHIEUNHAP. */
    public KetQuaKiemKeDTO lapBaoCaoPhieuNhap() throws Exception {
        String sql = "{CALL PROC_LAPBAOCAOPHIEUNHAP(?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.NUMERIC);
            cs.registerOutParameter(2, -10); // OracleTypes.CURSOR
            cs.execute();

            int soPhieu = cs.getInt(1);
            List<PhieuNhapKhoDTO> danhSach = new ArrayList<>();

            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs != null && rs.next()) {
                    PhieuNhapKhoDTO dto = new PhieuNhapKhoDTO();
                    dto.setMaPN(rs.getInt("MAPN"));
                    dto.setTenNhaCungCap(rs.getString("TENNCC"));
                    dto.setTenNhanVien(rs.getString("TENNV"));
                    dto.setTongTienNhap(rs.getBigDecimal("TONGTIENNHAP"));
                    String ngayStr = rs.getString("NGAYNHAP_STR");
                    if (ngayStr != null) {
                        try {
                            dto.setNgayNhap(java.time.LocalDateTime.parse(ngayStr,
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                        } catch (Exception ignored) {}
                    }
                    danhSach.add(dto);
                }
            }

            return new KetQuaKiemKeDTO(soPhieu, danhSach);

        } catch (SQLException e) {
            handleException("lapBaoCaoPhieuNhap", e);
            throw e;
        }
    }
}
