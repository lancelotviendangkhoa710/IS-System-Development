package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.utils.ReportPathUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Controller dialog lịch sử mua hàng.
 * Hiển thị tổng đơn, tổng chi tiêu, bảng chi tiết và xuất PDF.
 */
public class LichSuMuaHangDialogViewFXMLController {

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    @FXML private Label  lblTenKhachHang;
    @FXML private Label  lblTongDon;
    @FXML private Label  lblTongChiTieu;
    @FXML private Label  lblThongBao;
    @FXML private Button btnXuatPDF;

    @FXML private TableView<DonDatHangDTO>           tableDoanhSach;
    @FXML private TableColumn<DonDatHangDTO, String> colMaDon;
    @FXML private TableColumn<DonDatHangDTO, String> colNgayDat;
    @FXML private TableColumn<DonDatHangDTO, String> colTongTien;
    @FXML private TableColumn<DonDatHangDTO, String> colTrangThai;

    // Lưu maKH để đặt tên file PDF
    private int maKH = 0;

    @FXML
    public void initialize() {
        // Bind columns
        colMaDon.setCellValueFactory(c ->
                new SimpleStringProperty("#ORD-" + c.getValue().getMaDon()));
        colNgayDat.setCellValueFactory(c -> {
            if (c.getValue().getNgayLap() != null)
                return new SimpleStringProperty(c.getValue().getNgayLap().format(FMT_NGAY));
            return new SimpleStringProperty("—");
        });
        colTongTien.setCellValueFactory(c -> {
            BigDecimal tien = c.getValue().getTongTienHDBan();
            if (tien == null) tien = BigDecimal.ZERO;
            return new SimpleStringProperty(FMT_TIEN.format(tien) + " đ");
        });
        colTrangThai.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTenTrangThai() != null
                        ? c.getValue().getTenTrangThai() : "—"));

        // Tô màu theo trạng thái
        tableDoanhSach.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DonDatHangDTO item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-done", "row-cancelled", "row-processing");
                if (!empty && item != null && item.getTenTrangThai() != null) {
                    switch (item.getTenTrangThai()) {
                        case "Hoàn thành" -> getStyleClass().add("row-done");
                        case "Đã hủy"     -> getStyleClass().add("row-cancelled");
                        case "Đang xử lý" -> getStyleClass().add("row-processing");
                    }
                }
            }
        });
    }

    /** Nạp dữ liệu khách hàng và danh sách đơn vào dialog. */
    public void khoiTao(KhachHangDTO kh, List<DonDatHangDTO> dsDon) {
        maKH = kh.getMaKH();
        lblTenKhachHang.setText(kh.getHoTen() + " | " + kh.getSdt());

        List<DonDatHangDTO> ds = (dsDon != null) ? dsDon : List.of();
        lblTongDon.setText(String.valueOf(ds.size()));

        BigDecimal tongTien = ds.stream()
                .map(d -> d.getTongTienHDBan() != null ? d.getTongTienHDBan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTongChiTieu.setText(FMT_TIEN.format(tongTien) + " đ");

        tableDoanhSach.setItems(FXCollections.observableArrayList(ds));

        if (ds.isEmpty()) {
            lblThongBao.setText("Khách hàng chưa có lịch sử giao dịch.");
            if (btnXuatPDF != null) btnXuatPDF.setDisable(true);
        }
    }

    @FXML
    private void onXuatPDF() {
        if (btnXuatPDF != null) btnXuatPDF.setDisable(true);
        lblThongBao.setText("Đang xuất PDF...");

        javafx.application.Platform.runLater(() -> {
            try {
                // Snapshot toàn bộ tableDoanhSach
                VBox container = (VBox) tableDoanhSach.getParent();
                WritableImage snapshot = container.snapshot(
                        new javafx.scene.SnapshotParameters(), null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

                File outputFile = ReportPathUtils.buildPdfPath("LichSuMuaHang", "KH-" + maKH);

                new Thread(() -> {
                    try (PDDocument doc = new PDDocument()) {
                        float w = (float) snapshot.getWidth();
                        float h = (float) snapshot.getHeight();
                        PDPage page = new PDPage(new PDRectangle(w, h));
                        doc.addPage(page);
                        PDImageXObject img = LosslessFactory.createFromImage(doc, bufferedImage);
                        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                            cs.drawImage(img, 0, 0, w, h);
                        }
                        doc.save(outputFile);
                        javafx.application.Platform.runLater(() -> {
                            lblThongBao.setText("✅ Đã lưu PDF tại: " + outputFile.getAbsolutePath());
                            if (btnXuatPDF != null) btnXuatPDF.setDisable(false);
                        });
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> {
                            lblThongBao.setText("❌ Lỗi xuất PDF: " + ex.getMessage());
                            if (btnXuatPDF != null) btnXuatPDF.setDisable(false);
                        });
                    }
                }, "lichsu-pdf-writer").start();

            } catch (Exception ex) {
                lblThongBao.setText("❌ Lỗi snapshot: " + ex.getMessage());
                if (btnXuatPDF != null) btnXuatPDF.setDisable(false);
            }
        });
    }

    @FXML
    private void onDong() {
        Stage stage = (Stage) lblTongDon.getScene().getWindow();
        stage.close();
    }
}
