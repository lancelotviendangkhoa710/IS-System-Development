package com.bakery.presenters.khachhang;

import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.services.khachhang.KhachHangService;
import com.bakery.views.interfaces.khachhang.KhachHangView;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class KhachHangPresenter {

    private static final int SO_DONG_MOI_TRANG = 10;
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final KhachHangView view;
    private final KhachHangService KhachHangService;

    private List<KhachHangDTO> duLieuGoc;
    private List<KhachHangDTO> duLieuSauTimKiem;
    private List<KhachHangDTO> duLieuSauLoc;
    private String tuKhoa;
    private String tenHangLoc;
    private LocalDate tuNgay;
    private LocalDate denNgay;
    private int trangHienTai;
    private int tongTrang;
    private boolean cheDoThungRac;
    private int tongKhachHangHoatDong;
    private int tongKhachMoiTrongThang;

    public KhachHangPresenter(KhachHangView view) {
        this.view = view;
        this.KhachHangService = new KhachHangService();
        this.duLieuGoc = List.of();
        this.duLieuSauTimKiem = List.of();
        this.duLieuSauLoc = List.of();
        this.tuKhoa = "";
        this.trangHienTai = 1;
        this.tongTrang = 1;
        this.cheDoThungRac = false;
    }

    public void chuyenCheDoThungRac(boolean cheDoThungRac) {
        this.cheDoThungRac = cheDoThungRac;
        this.trangHienTai = 1;
        view.capNhatCheDoThungRac(cheDoThungRac);
        taiDuLieu();
    }

    public void taiDuLieu() {
        view.batTatTrangThaiBan(true);

        Task<List<KhachHangDTO>> task = new Task<>() {
            @Override
            protected List<KhachHangDTO> call() throws SQLException {
                if (cheDoThungRac) {
                    return new ArrayList<>(KhachHangService.getDeletedCustomers());
                }
                return new ArrayList<>(KhachHangService.getActiveCustomers());
            }
        };

        task.setOnSucceeded(event -> {
            duLieuGoc = task.getValue();
            if (tuKhoa == null || tuKhoa.isBlank()) {
                duLieuSauTimKiem = new ArrayList<>(duLieuGoc);
            }
            taiThongTinTongQuan();
            apDungBoLocNoiBo();
            view.batTatTrangThaiBan(false);
        });

        task.setOnFailed(event -> {
            view.batTatTrangThaiBan(false);
            String thongDiep = task.getException() == null ? "Lỗi không xác định." : task.getException().getMessage();
            view.hienThiLoi("Lỗi", thongDiep);
        });

        Thread thread = new Thread(task, "khach-hang-tai-du-lieu");
        thread.setDaemon(true);
        thread.start();
    }

    public void timKiem(String tuKhoa) {
        this.tuKhoa = tuKhoa == null ? "" : tuKhoa.trim();
        this.trangHienTai = 1;

        if (this.tuKhoa.isBlank()) {
            duLieuSauTimKiem = new ArrayList<>(duLieuGoc);
            apDungBoLocNoiBo();
            return;
        }

        view.batTatTrangThaiBan(true);
        Task<List<KhachHangDTO>> task = new Task<>() {
            @Override
            protected List<KhachHangDTO> call() throws SQLException {
                if (cheDoThungRac) {
                    return duLieuGoc.stream()
                            .filter(item -> khopTuKhoa(item, KhachHangPresenter.this.tuKhoa))
                            .toList();
                }
                return new ArrayList<>(KhachHangService.searchCustomers(KhachHangPresenter.this.tuKhoa));
            }
        };

        task.setOnSucceeded(event -> {
            duLieuSauTimKiem = task.getValue();
            apDungBoLocNoiBo();
            view.batTatTrangThaiBan(false);
        });

        task.setOnFailed(event -> {
            view.batTatTrangThaiBan(false);
            String thongDiep = task.getException() == null ? "Lỗi không xác định." : task.getException().getMessage();
            view.hienThiLoi("Lỗi", thongDiep);
        });

        Thread thread = new Thread(task, "khach-hang-tim-kiem");
        thread.setDaemon(true);
        thread.start();
    }

    public void loc(LocalDate tuNgay, LocalDate denNgay, String tenHangLoc) {
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
        this.tenHangLoc = tenHangLoc == null || tenHangLoc.isBlank() ? null : tenHangLoc.trim();
        this.trangHienTai = 1;
        apDungBoLocNoiBo();
    }

    public void chuyenTrang(int trang) {
        if (trang < 1) {
            trangHienTai = 1;
        } else if (trang > tongTrang) {
            trangHienTai = tongTrang;
        } else {
            trangHienTai = trang;
        }
        capNhatTrangHienTai();
    }

    public void themKhachHang(String hoTen, String sdt, String diaChi) {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setHoTen(hoTen);
        dto.setSdt(sdt);
        dto.setDiaChi(diaChi);
        dto.setNgayDangKy(LocalDate.now());
        dto.setDiemTichLuy(0);
        xuLyLuuKhachHang(dto, true);
    }

    public void capNhatKhachHang(int maKhachHang, String hoTen, String sdt, String diaChi) {
        Task<KhachHangDTO> task = new Task<>() {
            @Override
            protected KhachHangDTO call() throws Exception {
                KhachHangDTO existing = KhachHangService.getCustomerById(maKhachHang);
                existing.setHoTen(hoTen);
                existing.setSdt(sdt);
                existing.setDiaChi(diaChi);
                return existing;
            }
        };

        task.setOnSucceeded(event -> xuLyLuuKhachHang(task.getValue(), false));
        task.setOnFailed(event -> {
            String thongDiep = task.getException() == null ? "Không lấy được khách hàng." : task.getException().getMessage();
            view.hienThiLoi("Lỗi", thongDiep);
        });

        Thread thread = new Thread(task, "khach-hang-tai-thong-tin-sua");
        thread.setDaemon(true);
        thread.start();
    }

    public void xoaKhachHang(int maKhachHang, int maNhanVien) {
        view.batTatTrangThaiBan(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                KhachHangService.softDeleteCustomer(maKhachHang, maNhanVien);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.hienThiThanhCong("Thành công", "Đã xóa khách hàng.");
            taiDuLieu();
        });
        task.setOnFailed(event -> {
            view.batTatTrangThaiBan(false);
            String thongDiep = task.getException() == null ? "Lỗi không xác định." : task.getException().getMessage();
            view.hienThiLoi("Lỗi", thongDiep);
        });

        Thread thread = new Thread(task, "khach-hang-xoa");
        thread.setDaemon(true);
        thread.start();
    }

    public void khoiPhucKhachHang(int maKhachHang) {
        view.batTatTrangThaiBan(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                KhachHangService.restoreCustomer(maKhachHang);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.hienThiThanhCong("Thành công", "Đã khôi phục khách hàng.");
            taiDuLieu();
        });
        task.setOnFailed(event -> {
            view.batTatTrangThaiBan(false);
            String thongDiep = task.getException() == null ? "Lỗi không xác định." : task.getException().getMessage();
            view.hienThiLoi("Lỗi", thongDiep);
        });

        Thread thread = new Thread(task, "khach-hang-khoi-phuc");
        thread.setDaemon(true);
        thread.start();
    }

    public void xuatExcel(File tepTin) {
        if (duLieuSauLoc.isEmpty()) {
            view.hienThiThongTin("Thông báo", "Không có dữ liệu để xuất.");
            return;
        }

        view.batTatTrangThaiBan(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                ghiExcel(tepTin);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.batTatTrangThaiBan(false);
            view.hienThiThanhCong("Thành công", "Đã xuất Excel.");
        });
        task.setOnFailed(event -> {
            view.batTatTrangThaiBan(false);
            String thongDiep = task.getException() == null ? "Lỗi không xác định." : task.getException().getMessage();
            view.hienThiLoi("Lỗi", thongDiep);
        });

        Thread thread = new Thread(task, "khach-hang-xuat-excel");
        thread.setDaemon(true);
        thread.start();
    }

    private void xuLyLuuKhachHang(KhachHangDTO dto, boolean laThemMoi) {
        view.batTatTrangThaiBan(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (laThemMoi) {
                    KhachHangService.createCustomer(dto);
                } else {
                    KhachHangService.updateCustomer(dto);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.hienThiThanhCong("Thành công", laThemMoi ? "Đã thêm khách hàng." : "Đã cập nhật khách hàng.");
            taiDuLieu();
        });
        task.setOnFailed(event -> {
            view.batTatTrangThaiBan(false);
            String thongDiep = task.getException() == null ? "Lỗi không xác định." : task.getException().getMessage();
            view.hienThiLoi("Lỗi", thongDiep);
        });

        Thread thread = new Thread(task, laThemMoi ? "khach-hang-them" : "khach-hang-cap-nhat");
        thread.setDaemon(true);
        thread.start();
    }

    private void apDungBoLocNoiBo() {
        duLieuSauLoc = duLieuSauTimKiem.stream()
                .filter(this::khopLocNgay)
                .filter(this::khopLocHang)
                .toList();
        capNhatTrangHienTai();
    }

    private void capNhatTrangHienTai() {
        if (duLieuSauLoc.isEmpty()) {
            tongTrang = 1;
            trangHienTai = 1;
            view.hienThiDanhSachKhachHang(List.of());
            view.capNhatThongTinPhanTrang("Hiển thị 0-0 của 0");
            view.capNhatDieuKhienPhanTrang(1, 1);
            view.capNhatTongKhachHang(tongKhachHangHoatDong);
            view.capNhatKhachHangMoiTrongThang(tongKhachMoiTrongThang);
            return;
        }

        tongTrang = (int) Math.ceil((double) duLieuSauLoc.size() / SO_DONG_MOI_TRANG);
        if (trangHienTai > tongTrang) {
            trangHienTai = tongTrang;
        }
        if (trangHienTai < 1) {
            trangHienTai = 1;
        }

        int tuChiSo = (trangHienTai - 1) * SO_DONG_MOI_TRANG;
        int denChiSo = Math.min(tuChiSo + SO_DONG_MOI_TRANG, duLieuSauLoc.size());
        List<KhachHangDTO> trang = duLieuSauLoc.subList(tuChiSo, denChiSo);

        view.hienThiDanhSachKhachHang(trang);
        view.capNhatThongTinPhanTrang(String.format("Hiển thị %d-%d của %d", tuChiSo + 1, denChiSo, duLieuSauLoc.size()));
        view.capNhatDieuKhienPhanTrang(trangHienTai, tongTrang);
        view.capNhatTongKhachHang(tongKhachHangHoatDong);
        view.capNhatKhachHangMoiTrongThang(tongKhachMoiTrongThang);
    }

    private void taiThongTinTongQuan() {
        try {
            tongKhachHangHoatDong = KhachHangService.countActiveCustomers();
        } catch (Exception ex) {
            tongKhachHangHoatDong = 0;
        }
        try {
            LocalDate now = LocalDate.now();
            tongKhachMoiTrongThang = KhachHangService.countNewCustomersInMonth(now.getYear(), now.getMonthValue());
        } catch (Exception ex) {
            tongKhachMoiTrongThang = 0;
        }
    }

    private boolean khopLocNgay(KhachHangDTO khachHang) {
        LocalDate ngayDangKy = khachHang.getNgayDangKy();
        if (ngayDangKy == null) {
            return tuNgay == null && denNgay == null;
        }
        if (tuNgay != null && ngayDangKy.isBefore(tuNgay)) {
            return false;
        }
        return denNgay == null || !ngayDangKy.isAfter(denNgay);
    }

    private boolean khopLocHang(KhachHangDTO khachHang) {
        if (tenHangLoc == null || tenHangLoc.isBlank()) {
            return true;
        }
        String tenHang = khachHang.getTenHang() == null ? "" : khachHang.getTenHang();
        return tenHang.equalsIgnoreCase(tenHangLoc);
    }

    private boolean khopTuKhoa(KhachHangDTO khachHang, String tuKhoa) {
        String lower = tuKhoa.toLowerCase();
        return chua(khachHang.getHoTen(), lower)
                || chua(khachHang.getSdt(), lower)
                || chua(khachHang.getDiaChi(), lower)
                || String.valueOf(khachHang.getMaKH()).contains(lower);
    }

    private boolean chua(String nguon, String mau) {
        return nguon != null && nguon.toLowerCase().contains(mau);
    }

    private void ghiExcel(File tepTin) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(tepTin)) {
            Sheet sheet = workbook.createSheet("KhachHang");
            String[] tieuDe = {"Mã KH", "Họ tên", "SĐT", "Địa chỉ", "Ngày đăng ký", "Điểm", "Hạng"};

            Row dongTieuDe = sheet.createRow(0);
            for (int i = 0; i < tieuDe.length; i++) {
                dongTieuDe.createCell(i).setCellValue(tieuDe[i]);
            }

            int dong = 1;
            for (KhachHangDTO kh : duLieuSauLoc) {
                Row row = sheet.createRow(dong++);
                row.createCell(0).setCellValue(kh.getMaKH());
                row.createCell(1).setCellValue(macDinhRong(kh.getHoTen()));
                row.createCell(2).setCellValue(macDinhRong(kh.getSdt()));
                row.createCell(3).setCellValue(macDinhRong(kh.getDiaChi()));
                row.createCell(4).setCellValue(kh.getNgayDangKy() == null ? "" : kh.getNgayDangKy().format(DINH_DANG_NGAY));
                row.createCell(5).setCellValue(kh.getDiemTichLuy() == null ? 0 : kh.getDiemTichLuy());
                row.createCell(6).setCellValue(macDinhRong(kh.getTenHang()));
            }

            for (int i = 0; i < tieuDe.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }

    private String macDinhRong(String value) {
        return value == null ? "" : value;
    }
}
