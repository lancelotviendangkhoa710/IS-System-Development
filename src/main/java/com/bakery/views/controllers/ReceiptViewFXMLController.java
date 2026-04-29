package com.bakery.views.controllers;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.HoaDonDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReceiptViewFXMLController {

    @FXML
    private VBox receiptContainer;
    @FXML
    private Label lblMaDon;
    @FXML
    private Label lblMaHoaDon;
    @FXML
    private Label lblNgayLap;
    @FXML
    private Label lblTenKhach;
    @FXML
    private VBox vboxItems;
    @FXML
    private Label lblTongTien;
    @FXML
    private Label lblGiamGia;
    @FXML
    private Label lblDaThu;
    @FXML
    private Label lblTienKhachDua;
    @FXML
    private Label lblTienThua;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnClose;

    private static final NumberFormat FORMAT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    public void setReceiptData(HoaDonDTO hoaDon, DonDatHangDTO donHang,
            List<CTDonHangDTO> cart,
            List<SanPhamDTO> originData,
            String tenKhach, double khachDua, double tienThua) {

        lblMaDon.setText(donHang != null ? "#ORD-" + donHang.getMaDon() : "N/A");
        lblMaHoaDon.setText("#INV-" + hoaDon.getMaHD());
        lblNgayLap.setText(hoaDon.getNgayXuatHd() != null
                ? hoaDon.getNgayXuatHd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblTenKhach.setText(tenKhach != null ? tenKhach : "Khách vãng lai");

        double tongTien = hoaDon.getTongTienThanhToan() != null ? hoaDon.getTongTienThanhToan().doubleValue() : 0.0;
        lblTongTien.setText(FORMAT_TIEN.format(tongTien) + " đ");
        lblGiamGia.setText("0 đ");
        lblDaThu.setText(FORMAT_TIEN.format(tongTien) + " đ");
        lblTienKhachDua.setText(FORMAT_TIEN.format(khachDua) + " đ");
        lblTienThua.setText(FORMAT_TIEN.format(tienThua) + " đ");

        vboxItems.getChildren().clear();
        if (cart != null) {
            for (CTDonHangDTO item : cart) {
                vboxItems.getChildren().add(createItemRow(item, originData));
            }
        }
    }

    private HBox createItemRow(CTDonHangDTO item, List<SanPhamDTO> originData) {
        String tenSP = "Sản phẩm #" + item.getMaSP();
        if (originData != null) {
            for (SanPhamDTO sp : originData) {
                if (sp.getMaSP() == item.getMaSP()) {
                    tenSP = sp.getTenSP();
                    break;
                }
            }
        }

        HBox row = new HBox();
        row.setSpacing(5);

        Label lblTen = new Label(tenSP);
        lblTen.setPrefWidth(180);
        lblTen.setWrapText(true);

        Label lblSL = new Label("x" + item.getSoLuong());
        lblSL.setPrefWidth(40);
        lblSL.setAlignment(Pos.CENTER);

        double donGia = item.getDonGia() != null ? item.getDonGia().doubleValue() : 0.0;
        Label lblGia = new Label(FORMAT_TIEN.format(item.getSoLuong() * donGia) + " đ");
        lblGia.setPrefWidth(100);
        lblGia.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(lblTen, lblSL, lblGia);
        return row;
    }

    @FXML
    private void handlePrint() {
        try {

            File folder = new File("hoadon");
            if (!folder.exists()) {
                folder.mkdir();
            }

            WritableImage snapshot = receiptContainer.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            String fileName = "hoadon/HoaDon_" + lblMaHoaDon.getText().replace("#", "") + ".pdf";
            try (PDDocument doc = new PDDocument()) {

                float width = (float) snapshot.getWidth();
                float height = (float) snapshot.getHeight();
                PDPage page = new PDPage(new PDRectangle(width, height));
                doc.addPage(page);

                PDImageXObject pdImage = LosslessFactory.createFromImage(doc, bufferedImage);
                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                    contents.drawImage(pdImage, 0, 0, width, height);
                }

                doc.save(fileName);
                System.out.println("Đã lưu hóa đơn tại: " + new File(fileName).getAbsolutePath());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Hóa đơn đã được lưu tại thư mục 'hoadon'!");
                alert.showAndWait();
            }

            handleClose();
        } catch (Exception e) {
            System.err.println("[Receipt] Lỗi lưu PDF: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setContentText("Không thể lưu PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
