package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.views.controllers.BaseController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Controller cho MaTranPhanQuyenView.
 * Ma trận checkbox Nhân viên × Vai trò — gán vai trò cho từng nhân viên.
 * Mọi dữ liệu đọc/ghi từ DB. Không có Mock Data.
 */
public class MaTranPhanQuyenViewFXMLController extends BaseController {

    // ── FXML fields ──────────────────────────────────────────────────────
    @FXML private ScrollPane scrollMatrix;
    @FXML private TextField  txtTimNhanVien;
    @FXML private Label      lblStatusVaiTro;

    // ── State ────────────────────────────────────────────────────────────
    private final NhanVienService nhanVienService = new NhanVienService();

    private List<NhanVienDTO> cachedNhanVien = new ArrayList<>();
    private List<VaiTroDTO>   cachedVaiTro   = new ArrayList<>();
    /** matrixMap: maNV → list of CheckBox theo thứ tự cachedVaiTro. */
    private final Map<Integer, List<CheckBox>> matrixMap = new LinkedHashMap<>();

    // ── Initialize ────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        taiDuLieuNenTang();
    }

    private void taiDuLieuNenTang() {
        lblStatusVaiTro.setText("Đang tải dữ liệu...");
        Thread t = new Thread(() -> {
            try {
                List<NhanVienDTO> dsNV = nhanVienService.layTatCaNhanVien();
                Map<Integer, String> roleMap = nhanVienService.layDanhSachVaiTro();
                List<VaiTroDTO> dsVT = new ArrayList<>();
                for (Map.Entry<Integer, String> e : roleMap.entrySet()) {
                    VaiTroDTO vt = new VaiTroDTO();
                    vt.setMaVaiTro(e.getKey());
                    vt.setTenVaiTro(e.getValue());
                    dsVT.add(vt);
                }
                Platform.runLater(() -> {
                    cachedNhanVien = dsNV;
                    cachedVaiTro   = dsVT;
                    xayDungMatrixVaiTro(dsNV, dsVT);
                    lblStatusVaiTro.setText(
                            "Đã tải " + dsNV.size() + " nhân viên · " + dsVT.size() + " vai trò từ DB.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblStatusVaiTro.setText("Lỗi tải dữ liệu: " + e.getMessage()));
            }
        }, "phan-quyen-init");
        t.setDaemon(true);
        t.start();
    }

    // ── Ma trận Nhân viên × Vai trò ──────────────────────────────────────

    private void xayDungMatrixVaiTro(List<NhanVienDTO> dsNV, List<VaiTroDTO> dsVT) {
        matrixMap.clear();

        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));
        grid.setStyle("-fx-background-color: white;");

        // Header: tên vai trò
        Label lblHeaderNV = new Label("NHÂN VIÊN");
        lblHeaderNV.setStyle("-fx-font-weight: bold; -fx-text-fill: #92400E; -fx-font-size: 13px;");
        grid.add(lblHeaderNV, 0, 0);

        for (int c = 0; c < dsVT.size(); c++) {
            Label lblVT = new Label(dsVT.get(c).getTenVaiTro());
            lblVT.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2937; -fx-min-width: 90; -fx-alignment: CENTER;");
            grid.add(lblVT, c + 1, 0);
        }

        // Rows: nhân viên
        for (int row = 0; row < dsNV.size(); row++) {
            NhanVienDTO nv = dsNV.get(row);
            Label lblNV = new Label(nv.getHoTen() + "\n(" + nv.getTenDangNhap() + ")");
            lblNV.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px;");
            grid.add(lblNV, 0, row + 1);

            List<CheckBox> cbs = new ArrayList<>();
            for (int c = 0; c < dsVT.size(); c++) {
                int maVT = dsVT.get(c).getMaVaiTro();
                CheckBox cb = new CheckBox();
                cb.setSelected(nv.getDanhSachMaVaiTro() != null && nv.getDanhSachMaVaiTro().contains(maVT));

                VBox cell = new VBox(cb);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-padding: 4;");
                grid.add(cell, c + 1, row + 1);
                cbs.add(cb);
            }
            matrixMap.put(nv.getMaNV(), cbs);
        }

        scrollMatrix.setContent(grid);
    }

    @FXML
    private void onTimKiemNhanVien() {
        String kw = txtTimNhanVien.getText().trim().toLowerCase();
        List<NhanVienDTO> filtered = kw.isEmpty() ? cachedNhanVien
                : cachedNhanVien.stream()
                        .filter(nv -> (nv.getHoTen() != null && nv.getHoTen().toLowerCase().contains(kw))
                                || (nv.getTenDangNhap() != null && nv.getTenDangNhap().toLowerCase().contains(kw)))
                        .toList();
        xayDungMatrixVaiTro(filtered, cachedVaiTro);
        lblStatusVaiTro.setText("Hiển thị " + filtered.size() + " / " + cachedNhanVien.size() + " nhân viên.");
    }

    @FXML
    private void onLuuPhanVaiTro() {
        lblStatusVaiTro.setText("Đang lưu...");
        Thread t = new Thread(() -> {
            try {
                int count = 0;
                for (NhanVienDTO nv : cachedNhanVien) {
                    List<CheckBox> cbs = matrixMap.get(nv.getMaNV());
                    if (cbs == null) continue;
                    List<Integer> selectedIds = new ArrayList<>();
                    for (int i = 0; i < cachedVaiTro.size(); i++) {
                        if (cbs.get(i).isSelected()) {
                            selectedIds.add(cachedVaiTro.get(i).getMaVaiTro());
                        }
                    }
                    nhanVienService.capNhatVaiTro(nv.getMaNV(), selectedIds);
                    count++;
                }
                final int saved = count;
                Platform.runLater(() -> lblStatusVaiTro
                        .setText("✅ Đã lưu phân vai trò cho " + saved + " nhân viên vào DB."));
            } catch (Exception e) {
                Platform.runLater(() -> lblStatusVaiTro.setText("Lỗi lưu: " + e.getMessage()));
            }
        }, "phan-quyen-luu-vaitro");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onRefresh() {
        taiDuLieuNenTang();
    }
}
