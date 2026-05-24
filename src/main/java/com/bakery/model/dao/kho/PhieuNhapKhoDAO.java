package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.KetQuaKiemKeDTO;
import com.bakery.model.dto.kho.PhieuNhapKhoDTO;
import com.bakery.model.dto.kho.CTPhieuNhapDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                    dto.setMaPN(maPN); // dùng tham số method — MAPN không có trong SELECT
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
     * Tạo phiếu nhập kho qua PROC_TAOPHIEUNHAPKHO.
     * @param maNV        mã nhân viên thực hiện
     * @param maNCC       mã nhà cung cấp
     * @param jsonChiTiet JSON array chi tiết lô hàng
     * @param maCa        mã ca làm việc đang mở (dùng cho phiếu chi tự động)
     * @return mã phiếu nhập vừa tạo
     */
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

    /**
     * Hủy phiếu nhập kho qua PROC_HUYPHIEUNHAPKHO.
     */
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

    /**
     * Demo Phantom Read §4.3 — lập báo cáo kiểm kê phiếu nhập kho.
     *
     * <p>Gọi {@code PROC_LAPBAOCAOPHIEUNHAP} — procedure tự quyết định
     * isolation level thông qua {@code EXECUTE IMMEDIATE 'SET TRANSACTION ...'}.
     *
     * <p>Kịch bản BUG/FIX được toggle bằng comment/uncomment TRONG procedure:
     * <ul>
     *   <li><b>BUG</b> (mặc định): dòng SET TRANSACTION bị comment → READ COMMITTED
     *       → Phase 3 cursor thấy phiếu mới → N+1 dòng → phantom read</li>
     *   <li><b>FIX</b>: bỏ comment dòng SET TRANSACTION → SERIALIZABLE
     *       → Phase 3 cursor dùng snapshot cũ → đúng N dòng</li>
     * </ul>
     *
     * @return KetQuaKiemKeDTO { soPhieuDaDem, danhSachPhieu }
     * @throws Exception nếu DB lỗi
     */
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
                        } catch (Exception ignored) { /* giữ null */ }
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
