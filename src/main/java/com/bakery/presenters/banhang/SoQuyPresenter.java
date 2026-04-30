package com.bakery.presenters.banhang;

import com.bakery.model.dto.hethong.LoaiThuChiDTO;
import com.bakery.model.dto.hethong.PhieuThuChiDTO;
import com.bakery.services.hethong.SoQuyService;
import com.bakery.utils.SessionContext;
import com.bakery.views.interfaces.hethong.ISoQuyView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SoQuyPresenter {

    private static final int MANAGER          = 44;
    private static final int TRANG_KICH_THUOC = 20;

    private final ISoQuyView   view;
    private final SoQuyService service;

    // ── State giao dịch ──────────────────────────────────────────────────────
    private List<PhieuThuChiDTO> tatCaGiaoDich = new ArrayList<>();
    private List<LoaiThuChiDTO>  loaiDangHoatDong = new ArrayList<>();
    private List<PhieuThuChiDTO> giaoDichDaLoc = new ArrayList<>();

    private String boLocPhanLoai = "ALL";
    private String tuKhoa        = "";
    private int    trangHienTai  = 0;

    // ── State danh mục ────────────────────────────────────────────────────────
    private List<LoaiThuChiDTO> tatCaLoai     = new ArrayList<>();
    private int                 trangLoai     = 0;

    public SoQuyPresenter(ISoQuyView view, SoQuyService service) {
        this.view    = view;
        this.service = service;
    }

    // ── Khởi tạo ─────────────────────────────────────────────────────────────

    public void onInitialize() {
        boolean laQuanLy = SessionContext.getInstance().getMaVaiTro() == MANAGER;
        view.setTabCauHinhVisible(laQuanLy);
        taiGiaoDich();
        taiLoaiDangHoatDong();
    }

    // ── Tải dữ liệu ──────────────────────────────────────────────────────────

    private void taiGiaoDich() {
        SessionContext session = SessionContext.getInstance();
        int maCa = session.getMaCa();
        if (maCa == 0) {
            tatCaGiaoDich = new ArrayList<>();
            capNhatTongQuan();
            apDungBoLoc();
            return;
        }
        view.setLoadingLichSu(true);
        Thread t = new Thread(() -> {
            try {
                tatCaGiaoDich = service.layGiaoDich(maCa);
                capNhatTongQuan();
                apDungBoLoc();
            } catch (Exception e) {
                view.hienThiLoi("Lỗi tải giao dịch: " + e.getMessage());
            } finally {
                view.setLoadingLichSu(false);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void taiLoaiDangHoatDong() {
        Thread t = new Thread(() -> {
            try {
                loaiDangHoatDong = service.layDanhSachLoai();
                view.setLoaiOptions(loaiDangHoatDong);
            } catch (Exception e) {
                System.err.println("[SoQuy] Loi tai giao dich dang hoat dong: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void capNhatTongQuan() {
        BigDecimal tongThu = service.tinhTongThu(tatCaGiaoDich);
        BigDecimal tongChi = service.tinhTongChi(tatCaGiaoDich);
        BigDecimal soDu    = tongThu.subtract(tongChi);
        view.hienThiTongQuan(tongThu, tongChi, soDu);

        BigDecimal tong = tongThu.add(tongChi);
        if (tong.compareTo(BigDecimal.ZERO) > 0) {
            double tyLeThu = tongThu.divide(tong, 4, RoundingMode.HALF_UP).doubleValue();
            double tyLeChi = tongChi.divide(tong, 4, RoundingMode.HALF_UP).doubleValue();
            view.hienThiTyTrong(tyLeThu, tyLeChi);
        } else {
            view.hienThiTyTrong(0, 0);
        }
    }

    // ── Lọc & phân trang giao dịch ────────────────────────────────────────────

    public void onBoLocThayDoi(String phanLoai, String tuKhoaInput) {
        this.boLocPhanLoai = (phanLoai == null) ? "ALL" : phanLoai;
        this.tuKhoa        = (tuKhoaInput == null) ? "" : tuKhoaInput.trim().toLowerCase();
        this.trangHienTai  = 0;
        apDungBoLoc();
    }

    private void apDungBoLoc() {
        giaoDichDaLoc = tatCaGiaoDich.stream()
                .filter(p -> {
                    if ("Thu".equals(boLocPhanLoai) && !"Thu".equals(p.getPhanLoai())) return false;
                    if ("Chi".equals(boLocPhanLoai) && !"Chi".equals(p.getPhanLoai())) return false;
                    if (!tuKhoa.isEmpty()) {
                        String ten = p.getTenLoaiThuChi() == null ? "" : p.getTenLoaiThuChi().toLowerCase();
                        String ghi = p.getGhiChu()        == null ? "" : p.getGhiChu().toLowerCase();
                        return ten.contains(tuKhoa) || ghi.contains(tuKhoa);
                    }
                    return true;
                })
                .collect(Collectors.toList());
        hienThiTrangGiaoDich();
    }

    private void hienThiTrangGiaoDich() {
        int tong = giaoDichDaLoc.size();
        int tongTrang = Math.max(1, (int) Math.ceil((double) tong / TRANG_KICH_THUOC));
        if (trangHienTai >= tongTrang) trangHienTai = tongTrang - 1;

        int tu  = trangHienTai * TRANG_KICH_THUOC;
        int den = Math.min(tu + TRANG_KICH_THUOC, tong);

        view.hienThiDanhSachGiaoDich(new ArrayList<>(giaoDichDaLoc.subList(tu, den)));
        view.hienThiFooterGiaoDich(tu + 1, den, tong);
        view.setNutPhanTrangEnabled(trangHienTai > 0, trangHienTai < tongTrang - 1);
    }

    public void onTrangTruoc() {
        if (trangHienTai > 0) { trangHienTai--; hienThiTrangGiaoDich(); }
    }

    public void onTrangSau() {
        int tongTrang = Math.max(1, (int) Math.ceil((double) giaoDichDaLoc.size() / TRANG_KICH_THUOC));
        if (trangHienTai < tongTrang - 1) { trangHienTai++; hienThiTrangGiaoDich(); }
    }

    // ── Thao tác giao dịch ────────────────────────────────────────────────────

    public void onYeuCauThemGiaoDich() {
        view.hienThiDialogThemGiaoDich(new ArrayList<>(loaiDangHoatDong));
    }

    public void onXacNhanThemGiaoDich(int maLoaiThuChi, BigDecimal soTien, String ghiChu) {
        SessionContext session = SessionContext.getInstance();
        if (!session.isCaoDangMo() || session.getMaCa() == 0) {
            view.hienThiLoi("Chưa mở ca làm việc. Vui lòng mở ca trước khi lập phiếu.");
            return;
        }
        Thread t = new Thread(() -> {
            try {
                service.themGiaoDich(session.getMaCa(), session.getMaNV(),
                        maLoaiThuChi, soTien, ghiChu);
                taiGiaoDich();
                taiLoaiDangHoatDong();
                view.hienThiThongBao("Đã lập phiếu thành công.");
            } catch (Exception e) {
                view.hienThiLoi("Lỗi lập phiếu: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void onHuyGiaoDich(int maPhieuTC) {
        String tenPhieu = String.format("PTC%02d", maPhieuTC);
        Optional<String> lyDo = view.hienThiDialogXacNhanHuy(tenPhieu);
        if (lyDo.isEmpty()) return;

        Thread t = new Thread(() -> {
            try {
                service.huyGiaoDich(maPhieuTC, lyDo.get());
                tatCaGiaoDich.stream()
                        .filter(p -> p.getMaPhieuTC() == maPhieuTC)
                        .findFirst()
                        .ifPresent(p -> p.setTrangThai("cancelled"));
                capNhatTongQuan();
                apDungBoLoc();
                view.hienThiThongBao("Đã huỷ giao dịch thành công.");
            } catch (Exception e) {
                view.hienThiLoi("Lỗi huỷ giao dịch: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ── Danh mục ──────────────────────────────────────────────────────────────

    public void onTaiDanhSachLoai() {
        view.setLoadingCauHinh(true);
        Thread t = new Thread(() -> {
            try {
                tatCaLoai = service.layTatCaDanhSachLoai();
                trangLoai = 0;
                hienThiTrangLoai();
                // refresh loai dropdown với chỉ loại đang hoạt động
                loaiDangHoatDong = service.layDanhSachLoai();
                view.setLoaiOptions(loaiDangHoatDong);
            } catch (Exception e) {
                view.hienThiLoi("Lỗi tải danh mục: " + e.getMessage());
            } finally {
                view.setLoadingCauHinh(false);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void hienThiTrangLoai() {
        int tong = tatCaLoai.size();
        int tongTrang = Math.max(1, (int) Math.ceil((double) tong / TRANG_KICH_THUOC));
        if (trangLoai >= tongTrang) trangLoai = tongTrang - 1;

        int tu  = trangLoai * TRANG_KICH_THUOC;
        int den = Math.min(tu + TRANG_KICH_THUOC, tong);

        view.hienThiDanhSachLoai(new ArrayList<>(tatCaLoai.subList(tu, den)));
        view.hienThiFooterLoai(tu + 1, den, tong);
        view.setNutPhanTrangLoaiEnabled(trangLoai > 0, trangLoai < tongTrang - 1);
    }

    public void onTrangLoaiTruoc() {
        if (trangLoai > 0) { trangLoai--; hienThiTrangLoai(); }
    }

    public void onTrangLoaiSau() {
        int tongTrang = Math.max(1, (int) Math.ceil((double) tatCaLoai.size() / TRANG_KICH_THUOC));
        if (trangLoai < tongTrang - 1) { trangLoai++; hienThiTrangLoai(); }
    }

    public void onThemLoai(String ten, String phanLoai) {
        if (ten == null || ten.isBlank()) { view.hienThiLoi("Tên loại không được để trống."); return; }
        if (!"Thu".equals(phanLoai) && !"Chi".equals(phanLoai)) { view.hienThiLoi("Phân loại phải là Thu hoặc Chi."); return; }
        Thread t = new Thread(() -> {
            try {
                service.themLoai(ten.trim(), phanLoai);
                onTaiDanhSachLoai();
                view.hienThiThongBao("Đã thêm danh mục \"" + ten.trim() + "\".");
            } catch (Exception e) {
                view.hienThiLoi("Lỗi thêm danh mục: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void onSuaLoai(int ma, String ten, String phanLoai) {
        if (ten == null || ten.isBlank()) { view.hienThiLoi("Tên loại không được để trống."); return; }
        Thread t = new Thread(() -> {
            try {
                service.suaLoai(ma, ten.trim(), phanLoai);
                onTaiDanhSachLoai();
                view.hienThiThongBao("Đã cập nhật danh mục.");
            } catch (Exception e) {
                view.hienThiLoi("Lỗi cập nhật danh mục: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void onKhoaLoai(int ma) {
        Thread t = new Thread(() -> {
            try {
                service.xoaLoai(ma);
                onTaiDanhSachLoai();
                view.hienThiThongBao("Đã khoá danh mục.");
            } catch (Exception e) {
                view.hienThiLoi("Lỗi khoá danh mục: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void onMoKhoaLoai(int ma) {
        Thread t = new Thread(() -> {
            try {
                service.moKhoaLoai(ma);
                onTaiDanhSachLoai();
                view.hienThiThongBao("Đã mở khoá danh mục.");
            } catch (Exception e) {
                view.hienThiLoi("Lỗi mở khoá danh mục: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
