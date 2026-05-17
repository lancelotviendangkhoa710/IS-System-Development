package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dao.kho.SanPhamDAO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller Kiểm Kê Kho.
 * Hiển thị tổng hợp tồn kho thực tế từ DB:
 *  - Nguyên liệu (NGUYENLIEU.SOLUONGTONTONG)
 *  - Thành phẩm  (SANPHAM.SOLUONGTON)
 * Không gọi mock data.
 */
public class KiemKeKhoViewFXMLController extends BaseController {

    @FXML private Label lblTitle;
    @FXML private TableView<TonKhoRow> tblData;
    @FXML private TableColumn<TonKhoRow, String> colDate;    // tai dung: "Loai"
    @FXML private TableColumn<TonKhoRow, String> colUser;    // tai dung: "Ten hang"
    @FXML private TableColumn<TonKhoRow, String> colDonVi;   // Don vi tinh
    @FXML private TableColumn<TonKhoRow, String> colContent; // tai dung: "Ton kho"
    @FXML private TableColumn<TonKhoRow, String> colStatus;  // "Trang thai"

    private static final NumberFormat FMT = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    static { FMT.setMaximumFractionDigits(2); }

    /** Row hien thi tong hop ton kho — gop NL lan SP. */
    public record TonKhoRow(String loai, String ten, String donViTinh, String tonKho, String trangThai) {}

    private final NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final ObservableList<TonKhoRow> rows = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lblTitle.setText("KIỂM KÊ KHO");
        setupTable();
        taiDuLieu();
    }

    private void setupTable() {
        colDate.setText("Loại");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().loai()));

        colUser.setText("Tên hàng");
        colUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ten()));

        colDonVi.setText("Đơn vị tính");
        colDonVi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().donViTinh()));

        colContent.setText("Tồn kho");
        colContent.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tonKho()));

        colStatus.setText("Trạng thái");
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().trangThai()));

        tblData.setItems(rows);
        tblData.setPlaceholder(new Label("Đang tải dữ liệu kho..."));
    }

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            List<TonKhoRow> data = new ArrayList<>();
            try {
                // --- Nguyên liệu ---
                List<NguyenLieuDTO> dsNL = nguyenLieuDAO.layTatCaNguyenLieu();
                for (NguyenLieuDTO nl : dsNL) {
                    double ton = nl.getSoLuongTonTong();
                    String trang = ton <= 0 ? "\u26D4 Hết hàng"
                            : ton <= nl.getMucTonAnToan() ? "\u26A0 Sắp hết" : "\u2705 Đủ hàng";
                    String dvt = nl.getTenDVT().isBlank() ? "---" : nl.getTenDVT();
                    data.add(new TonKhoRow("Nguyên liệu", nl.getTenNL(),
                            dvt, FMT.format(ton), trang));
                }

                // --- Thành phẩm ---
                List<SanPhamDTO> dsSP = sanPhamDAO.layTatCaSanPhamQuanLy();
                for (SanPhamDTO sp : dsSP) {
                    double ton = sp.getSoLuongTon();
                    String trang = ton <= 0 ? "\u26D4 Hết hàng"
                            : ton < 5 ? "\u26A0 Sắp hết" : "\u2705 Đủ hàng";
                    data.add(new TonKhoRow("Thành phẩm", sp.getTenSP(),
                            "cái", FMT.format(ton), trang));
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tải dữ liệu kho: " + e.getMessage()));
                return;
            }
            final List<TonKhoRow> finalData = data;
            javafx.application.Platform.runLater(() -> {
                rows.setAll(finalData);
                if (finalData.isEmpty()) {
                    tblData.setPlaceholder(new Label("Kho chưa có hàng."));
                }
            });
        }, "kiem-ke-tai-du-lieu");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onAction() {
        taiDuLieu();
        hienThiThanhCongLabel("Đã làm mới dữ liệu kiểm kê.");
    }
}
