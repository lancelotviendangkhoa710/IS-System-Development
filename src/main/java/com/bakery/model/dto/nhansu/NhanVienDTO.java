package com.bakery.model.dto.nhansu;
import com.bakery.model.dto.BaseDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDTO extends BaseDTO {
    private int maNV;
    private List<Integer> danhSachMaVaiTro = new ArrayList<>();
    private List<String> danhSachTenVaiTro = new ArrayList<>();
    private String hoTen;
    private LocalDate ngaySinh;
    private String sdt;
    private String diaChi;
    private String tenDangNhap;
    private String matKhau;
    private int trangThaiLamViec;
    private int trangThaiTK; // 1: Tài khoản hoạt động, 0: Bị khóa
    private transient boolean canDoiMatKhau; // true nếu mật khẩu chưa được hash (lần đăng nhập đầu)

    public NhanVienDTO() {}

    public NhanVienDTO(int maNV, String hoTen, LocalDate ngaySinh, String sdt, String diaChi, String tenDangNhap, String matKhau, int trangThaiLamViec) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.trangThaiLamViec = trangThaiLamViec;
    }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public List<Integer> getDanhSachMaVaiTro() { return danhSachMaVaiTro; }
    public void setDanhSachMaVaiTro(List<Integer> danhSachMaVaiTro) { this.danhSachMaVaiTro = danhSachMaVaiTro; }

    public List<String> getDanhSachTenVaiTro() { return danhSachTenVaiTro; }
    public void setDanhSachTenVaiTro(List<String> danhSachTenVaiTro) { this.danhSachTenVaiTro = danhSachTenVaiTro; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public int getTrangThaiLamViec() { return trangThaiLamViec; }
    public void setTrangThaiLamViec(int trangThaiLamViec) { this.trangThaiLamViec = trangThaiLamViec; }

    public int getTrangThaiTK() { return trangThaiTK; }
    public void setTrangThaiTK(int trangThaiTK) { this.trangThaiTK = trangThaiTK; }

    public boolean isCanDoiMatKhau() { return canDoiMatKhau; }
    public void setCanDoiMatKhau(boolean canDoiMatKhau) { this.canDoiMatKhau = canDoiMatKhau; }

    /** Helper để lấy tên vai trò chính hoặc danh sách chuỗi */
    public String getTenVaiTroHienThi() {
        if (danhSachTenVaiTro == null || danhSachTenVaiTro.isEmpty()) return "N/A";
        return String.join(", ", danhSachTenVaiTro);
    }

    // --- Legacy Compatibility ---
    public int getMaVaiTro() {
        return (danhSachMaVaiTro == null || danhSachMaVaiTro.isEmpty()) ? 0 : danhSachMaVaiTro.get(0);
    }
    
    public void setMaVaiTro(int maVaiTro) {
        if (this.danhSachMaVaiTro == null) this.danhSachMaVaiTro = new ArrayList<>();
        if (!this.danhSachMaVaiTro.contains(maVaiTro)) {
            this.danhSachMaVaiTro.add(maVaiTro);
        }
    }

    public String getTenVaiTro() {
        return (danhSachTenVaiTro == null || danhSachTenVaiTro.isEmpty()) ? "" : danhSachTenVaiTro.get(0);
    }

    public void setTenVaiTro(String tenVaiTro) {
        if (this.danhSachTenVaiTro == null) this.danhSachTenVaiTro = new ArrayList<>();
        if (!this.danhSachTenVaiTro.contains(tenVaiTro)) {
            this.danhSachTenVaiTro.add(tenVaiTro);
        }
    }
}
