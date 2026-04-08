package com.bakery.dao;

import com.bakery.dao.CaLamViecDAO;
import com.bakery.dao.KhachHangDAO;
import com.bakery.dao.NhanVienDAO;
import com.bakery.dao.PhanQuyenDAO;
import com.bakery.dto.CaLamViecDTO;
import com.bakery.dto.ChucNangDTO;
import com.bakery.dto.KhachHangDTO;
import com.bakery.dto.NhanVienDTO;

import java.util.List;

public class TestDAO {

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU KIỂM THỬ TẦNG DAO (NHÂN SỰ & KHÁCH HÀNG) ===\n");

        // Khởi tạo các đối tượng DAO
        NhanVienDAO nvDAO = new NhanVienDAO();
        KhachHangDAO khDAO = new KhachHangDAO();
        CaLamViecDAO caDAO = new CaLamViecDAO();
        PhanQuyenDAO pqDAO = new PhanQuyenDAO();

        // --------------------------------------------------------
        // TEST 1: Kiểm tra đăng nhập (NhanVienDAO)
        // --------------------------------------------------------
        System.out.println("1. Đang test NhanVienDAO.kiemTraDangNhap()...");
        try {
            // Thay "admin" và "123456" bằng dữ liệu thực tế có trong DB 
            NhanVienDTO nv = nvDAO.kiemTraDangNhap("admin", "123456");
            System.out.println(" ✅ Đăng nhập thành công! Xin chào: " + nv.getHoTen() + " (Mã NV: " + nv.getMaNV() + ")");
        } catch (Exception e) {
            System.out.println(" ❌ Đăng nhập thất bại: " + e.getMessage());
        }
        System.out.println("--------------------------------------------------------");

        // --------------------------------------------------------
        // TEST 2: Tìm khách hàng theo Số điện thoại (KhachHangDAO)
        // --------------------------------------------------------
        System.out.println("2. Đang test KhachHangDAO.timKhachHangBangSDT()...");
        String sdtTest = "0901234567"; // Thay bằng SĐT có thật trong DB
        KhachHangDTO kh = khDAO.timKhachHangBangSDT(sdtTest);
        if (kh != null) {
            System.out.println(" ✅ Tìm thấy khách hàng: " + kh.getHoTen() + " - Điểm tích lũy: " + kh.getDiemTichLuy());
        } else {
            System.out.println(" ❌ Không tìm thấy khách hàng với SĐT: " + sdtTest);
        }
        System.out.println("--------------------------------------------------------");

        // --------------------------------------------------------
        // TEST 3: Kiểm tra trạng thái Ca làm việc (CaLamViecDAO)
        // --------------------------------------------------------
        System.out.println("3. Đang test CaLamViecDAO.kiemTraCaDangMo()...");
        int maNVTest = 1; // Giả sử mã nhân viên là 1
        CaLamViecDTO ca = caDAO.kiemTraCaDangMo(maNVTest);
        if (ca != null) {
            System.out.println(" ✅ Nhân viên này đang trong ca làm việc. Mã ca: " + ca.getMaCa() + " tại máy POS: " + ca.getMaMayPOS());
        } else {
            System.out.println(" ℹ️ Nhân viên này hiện không có ca nào đang mở.");
        }
        System.out.println("--------------------------------------------------------");

        // --------------------------------------------------------
        // TEST 4: Lấy danh sách chức năng phân quyền (PhanQuyenDAO)
        // --------------------------------------------------------
        System.out.println("4. Đang test PhanQuyenDAO.layDanhSachChucNangTheoVaiTro()...");
        int maVaiTroTest = 1; // Giả sử 1 là vai trò Quản lý
        List<ChucNangDTO> danhSachChucNang = pqDAO.layDanhSachChucNangTheoVaiTro(maVaiTroTest);

        if (!danhSachChucNang.isEmpty()) {
            System.out.println(" ✅ Đã lấy được " + danhSachChucNang.size() + " chức năng cho vai trò này:");
            for (ChucNangDTO cn : danhSachChucNang) {
                System.out.println("    - [" + cn.getMaChucNang() + "] " + cn.getTenChucNang() + ": " + cn.getMoTa());
            }
        } else {
            System.out.println(" ❌ Không tìm thấy chức năng nào (hoặc bảng phân quyền chưa có dữ liệu).");
        }
        System.out.println("================ HOÀN TẤT KIỂM THỬ ================");
    }
}