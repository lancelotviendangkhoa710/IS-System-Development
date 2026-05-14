package com.bakery.model.dao.hethong;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.hethong.KhoiPhucDuLieuDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO thực hiện tra cứu bản ghi đã xóa mềm và gọi
 * PROC_KHOIPHUCDULIEU để khôi phục.
 */
public class KhoiPhucDuLieuDAO extends BaseDAO {

    /**
     * Cấu hình bảng hỗ trợ khôi phục.
     * Mỗi phần tử: { tenBang, tenCotPK, tenCotTen, loaiHienThi }
     */
    private static final String[][] CAU_HINH_BANG = {
        { "DANHMUCSP",      "MADM",         "TENDM",        "Danh mục sản phẩm" },
        { "SANPHAM",        "MASP",         "TENSP",        "Sản phẩm"          },
        { "KICHCOBANH",     "MAKC",         "TENKC",        "Kích cỡ bánh"      },
        { "COTBANH",        "MACOT",        "TENCOT",       "Cốt bánh"          },
        { "NHANBANH",       "MANHAN",       "TENNHAN",      "Nhân bánh"         },
        { "KIEUTRANGTRI",   "MATRANGTRI",   "TENTRANGTRI",  "Kiểu trang trí"   },
        { "DONVITINH",      "MADVT",        "TENDVT",       "Đơn vị tính"       },
        { "NGUYENLIEU",     "MANL",         "TENNL",        "Nguyên liệu"       },
        { "NHACUNGCAP",     "MANCC",        "TENNCC",       "Nhà cung cấp"      },
        { "HANGTHANHVIEN",  "MAHANG",       "TENHANG",      "Hạng thành viên"   },
        { "KHACHHANG",      "MAKH",         "HOTEN",        "Khách hàng"        },
        { "LOAITHUCHI",     "MALOAITHUCHI", "TENLOAITHUCHI","Loại thu chi"      },
        { "PHUONGTHUCTT",   "MAPTTT",       "TENPTTT",      "Phương thức TT"    },
        { "VAITRO",         "MAVAITRO",     "TENVAITRO",    "Vai trò"           },
    };

    /**
     * Lấy toàn bộ bản ghi đã xóa mềm từ tất cả bảng được hỗ trợ,
     * lọc thêm theo loại nếu được chỉ định.
     *
     * @param loaiDoiTuong null hoặc "" = tất cả; khác = lọc theo tên bảng/label
     * @return Danh sách bản ghi đã xóa
     */
    public List<KhoiPhucDuLieuDTO> layDanhSachDaXoa(String loaiDoiTuong) throws Exception {
        List<KhoiPhucDuLieuDTO> ketQua = new ArrayList<>();

        for (String[] cfg : CAU_HINH_BANG) {
            String tenBang  = cfg[0];
            String tenCotPK = cfg[1];
            String tenCotTen = cfg[2];
            String loaiLabel = cfg[3];

            // Lọc theo loại nếu user chọn
            if (loaiDoiTuong != null && !loaiDoiTuong.isBlank()
                    && !loaiLabel.equalsIgnoreCase(loaiDoiTuong)) {
                continue;
            }

            // Tất cả bảng hỗ trợ đều có THOIDIEMXOA + MANX (FK → NHANVIEN)
            String sql = "SELECT t." + tenCotPK + ", t." + tenCotTen
                    + ", t.THOIDIEMXOA"
                    + ", nv.HOTEN AS TENNHANVIEN"
                    + " FROM " + tenBang + " t"
                    + " LEFT JOIN NHANVIEN nv ON nv.MANV = t.MANX"
                    + " WHERE t.THOIDIEMXOA IS NOT NULL"
                    + " ORDER BY t.THOIDIEMXOA DESC";

            try (Connection con = moKetNoi();
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    KhoiPhucDuLieuDTO dto = new KhoiPhucDuLieuDTO();
                    dto.setMaDoiTuong(String.valueOf(rs.getInt(tenCotPK)));
                    dto.setTenDoiTuong(rs.getString(tenCotTen));
                    dto.setLoaiDoiTuong(loaiLabel);
                    dto.setTenBang(tenBang);
                    dto.setTenCotXoa(tenCotPK);

                    Timestamp ts = rs.getTimestamp("THOIDIEMXOA");
                    if (ts != null) {
                        dto.setThoiDiemXoa(ts.toLocalDateTime());
                    }
                    dto.setTenNhanVienXoa(rs.getString("TENNHANVIEN"));

                    ketQua.add(dto);
                }
            } catch (Exception e) {
                // Log và tiếp tục bảng kế tiếp — không để 1 bảng lỗi chặn toàn bộ
                System.err.println("[KhoiPhucDuLieuDAO] Lỗi đọc bảng " + tenBang + ": " + e.getMessage());
            }
        }

        return ketQua;
    }

    /**
     * Gọi PROC_KHOIPHUCDULIEU để khôi phục bản ghi + ghi lịch sử.
     *
     * @param tenBang   Tên bảng Oracle (VD: "SANPHAM")
     * @param tenCotPK  Tên cột PK (VD: "MASP")
     * @param maId      Giá trị PK dạng chuỗi
     * @param maNv      Mã nhân viên thực hiện (ghi audit log)
     */
    public void khoiPhuc(String tenBang, String tenCotPK, String maId, int maNv) throws Exception {
        String sql = "{ CALL PROC_KHOIPHUCDULIEU(?, ?, ?, ?) }";
        try (Connection con = moKetNoi();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, tenBang);
            cs.setString(2, tenCotPK);
            cs.setString(3, maId);
            cs.setInt(4, maNv);
            cs.execute();
        } catch (Exception e) {
            handleException("khoiPhuc", e);
        }
    }

    /**
     * Xóa vĩnh viễn tất cả bản ghi đã soft-delete quá {@code soNgay} ngày
     * trên mọi bảng được hỗ trợ bằng cách gọi PROC_XOAVINHVIEN_QUAHAN.
     *
     * @param soNgay Ngưỡng ngày (mặc định nghiệp vụ: 120)
     * @param maNv   Mã nhân viên thực hiện (ghi audit log)
     * @return Tổng số bản ghi đã xóa vĩnh viễn trên toàn bảng
     */
    public int xoaVinhVienQuaHan(int soNgay, int maNv) throws Exception {
        int tongSoDong = 0;
        String sql = "{ CALL PROC_XOAVINHVIEN_QUAHAN(?, ?, ?, ?, ?) }";

        for (String[] cfg : CAU_HINH_BANG) {
            String tenBang  = cfg[0];
            String tenCotPK = cfg[1];

            try (Connection con = moKetNoi();
                 CallableStatement cs = con.prepareCall(sql)) {
                cs.setString(1, tenBang);
                cs.setString(2, tenCotPK);
                cs.setInt(3, soNgay);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.setInt(5, maNv);
                cs.execute();
                int soDong = cs.getInt(4);
                tongSoDong += soDong;
            } catch (Exception e) {
                System.err.println("[KhoiPhucDuLieuDAO] Lỗi purge bảng " + tenBang + ": " + e.getMessage());
            }
        }

        return tongSoDong;
    }

    /**
     * Trả về danh sách label loại đối tượng để populate ComboBox.
     */
    public List<String> layDanhSachLoai() {
        List<String> loai = new ArrayList<>();
        loai.add("Tất cả");
        for (String[] cfg : CAU_HINH_BANG) {
            loai.add(cfg[3]);
        }
        return loai;
    }
}
