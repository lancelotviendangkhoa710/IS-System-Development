package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.KeHoachXuatKhoDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.services.kho.CongThucService;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller Lập Kế Hoạch Sản Xuất (UC2).
 * Tính nguyên liệu cần xuất dựa trên công thức và số lượng kế hoạch.
 * Highlight đỏ các dòng thiếu kho bằng custom TableRow.
 */
public class SanXuatViewFXMLController extends BaseController {

    @FXML private ComboBox<SanPhamDTO> cmbSanPham;
    @FXML private Label lblSoLuongKhaDung;
    @FXML private TextField txtSoLuongKeHoach;
    @FXML private Button btnTinhToan;
    @FXML private Button btnXoaBang;
    @FXML private TableView<KeHoachXuatKhoDTO> tblNguyenLieu;
    @FXML private TableColumn<KeHoachXuatKhoDTO, String> colTenNL;
    @FXML private TableColumn<KeHoachXuatKhoDTO, String> colDVT;
    @FXML private TableColumn<KeHoachXuatKhoDTO, Double> colTonKho;
    @FXML private TableColumn<KeHoachXuatKhoDTO, Double> colDinhMuc;
    @FXML private TableColumn<KeHoachXuatKhoDTO, Double> colCanXuat;
    @FXML private TableColumn<KeHoachXuatKhoDTO, String> colTrangThai;
    @FXML private Label lblCanhBaoThieu;
    @FXML private Label lblThongBao;

    private final CongThucService congThucService = new CongThucService();
    private final ObservableList<KeHoachXuatKhoDTO> danhSachNL = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        taiDanhSachSanPham();
    }

    private void setupTable() {
        colTenNL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNguyenLieu()));
        colDVT.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getDonViTinh())));
        colTonKho.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSoLuongTonKho()).asObject());
        colDinhMuc.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSoLuongTieuHao()).asObject());
        colCanXuat.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSoLuongCanXuat()).asObject());
        colTrangThai.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isThieuKho() ? "⚠ Thiếu kho" : "✓ Đủ"));

        // Highlight đỏ các dòng thiếu kho bằng custom TableRow
        tblNguyenLieu.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(KeHoachXuatKhoDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    getStyleClass().removeAll("row-danger");
                } else if (item.isThieuKho()) {
                    if (!getStyleClass().contains("row-danger")) getStyleClass().add("row-danger");
                } else {
                    getStyleClass().removeAll("row-danger");
                }
            }
        });

        tblNguyenLieu.setItems(danhSachNL);
        tblNguyenLieu.setPlaceholder(new Label("Chọn sản phẩm và nhấn 'Tính nguyên liệu'."));
    }

    private void taiDanhSachSanPham() {
        Thread t = new Thread(() -> {
            try {
                List<SanPhamDTO> ds = congThucService.layDanhSachSanPham();
                javafx.application.Platform.runLater(() -> {
                    cmbSanPham.setItems(FXCollections.observableArrayList(ds));
                    cmbSanPham.setCellFactory(lv -> new ListCell<>() {
                        @Override protected void updateItem(SanPhamDTO item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getTenSP());
                        }
                    });
                    cmbSanPham.setButtonCell(new ListCell<>() {
                        @Override protected void updateItem(SanPhamDTO item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? "— Chọn sản phẩm —" : item.getTenSP());
                        }
                    });
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tải sản phẩm: " + e.getMessage()));
            }
        }, "sanxuat-tai-sp");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onSanPhamChanged() {
        SanPhamDTO sp = cmbSanPham.getValue();
        if (sp == null) { lblSoLuongKhaDung.setText("— Chọn sản phẩm —"); return; }

        Thread t = new Thread(() -> {
            try {
                double khaDung = congThucService.tinhSoLuongKhaDung(sp.getMaSP());
                javafx.application.Platform.runLater(() -> {
                    lblSoLuongKhaDung.setText(String.format("%.0f cái", khaDung));
                    danhSachNL.clear();
                    lblCanhBaoThieu.setText("");
                    lblThongBao.setText("Đã chọn: " + sp.getTenSP());
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tính số lượng khả dụng: " + e.getMessage()));
            }
        }, "sanxuat-tinh-khadung");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onTinhToan() {
        SanPhamDTO sp = cmbSanPham.getValue();
        if (sp == null) { hienThiLoiLabel("Vui lòng chọn sản phẩm."); return; }

        double soLuong;
        try {
            soLuong = Double.parseDouble(txtSoLuongKeHoach.getText().trim());
            if (soLuong <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            hienThiLoiLabel("Số lượng kế hoạch không hợp lệ — phải là số dương.");
            return;
        }

        final double khoachSoLuong = soLuong;
        Thread t = new Thread(() -> {
            try {
                List<KeHoachXuatKhoDTO> ds = congThucService.layCongThucVaTonKho(sp.getMaSP());
                ds.forEach(row -> row.tinhCanXuat(khoachSoLuong));

                long soThieu = ds.stream().filter(KeHoachXuatKhoDTO::isThieuKho).count();
                javafx.application.Platform.runLater(() -> {
                    danhSachNL.setAll(ds);
                    lblCanhBaoThieu.setText(soThieu > 0 ? "⚠ " + soThieu + " nguyên liệu thiếu kho!" : "");
                    hienThiThanhCongLabel("Đã tính xong cho " + (int) khoachSoLuong + " cái " + sp.getTenSP() + ".");
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tính toán: " + e.getMessage()));
            }
        }, "sanxuat-tinh-toan");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onXoaBang() {
        danhSachNL.clear();
        lblCanhBaoThieu.setText("");
        lblThongBao.setText("Đã xóa bảng kế hoạch.");
    }

    private static String nvl(String s) { return s != null ? s : "—"; }
}
