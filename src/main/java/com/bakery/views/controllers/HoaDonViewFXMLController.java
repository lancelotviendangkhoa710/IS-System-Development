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

public class HoaDonViewFXMLController extends BaseController {

    @FXML
    private VBox receiptContainer;
    @FXML
    private Label lblBakeryName;
    @FXML
    private Label lblTieuDe;
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
    private Label lblGiamGiaLabel;
    @FXML
    private Label lblDaThu;
    @FXML
    private Label lblPayLabel;
    @FXML
    private Label lblTienKhachDua;
    @FXML
    private Label lblTienThua;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnClose;

    private static final NumberFormat FORMAT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    public void setReceiptData(String tieuDe, HoaDonDTO hoaDon, DonDatHangDTO donHang,
            List<CTDonHangDTO> cart,
            List<SanPhamDTO> originData,
            String tenKhach, double khachDua, double tienThua, double soTienGiamGia, boolean laDonCoc) {

        if (lblTieuDe != null && tieuDe != null) {
            lblTieuDe.setText(tieuDe);
        }

        Integer maDon = (donHang != null) ? donHang.getMaDon() : hoaDon.getMaDon();
        lblMaDon.setText(maDon != null ? "#ORD-" + maDon : "N/A");
        lblMaHoaDon.setText("#INV-" + hoaDon.getMaHD());
        lblNgayLap.setText(hoaDon.getNgayXuatHd() != null
                ? hoaDon.getNgayXuatHd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblTenKhach.setText(tenKhach != null ? tenKhach : "N/A");

        if (laDonCoc) {
            lblPayLabel.setText("TIỀN CỌC:");
            lblGiamGiaLabel.setText("KHẤU TRỪ:");
        } else {
            lblPayLabel.setText("THANH TOÁN:");
            lblGiamGiaLabel.setText("GIẢM GIÁ:");
        }

        double tongTienHD = hoaDon.getTongTienThanhToan() != null ? hoaDon.getTongTienThanhToan().doubleValue() : 0.0;

        // Tính toán lại tổng tiền hàng thực tế từ giỏ hàng để đảm bảo khớp hiển thị
        double tongHangThucTe = 0;
        if (cart != null) {
            for (CTDonHangDTO item : cart) {
                double dg = item.getDonGia() != null ? item.getDonGia().doubleValue() : 0.0;
                tongHangThucTe += item.getSoLuong() * dg;
            }
        }

        // Nếu là đơn cọc, soTienGiamGia truyền vào có thể bị sai lệch do tính trên số
        // tiền cọc
        // Ta nên tính lại dựa trên tổng hàng thực tế và tổng tiền thanh toán của ĐƠN
        // HÀNG (nếu có)
        double tongPhaiTraCuaDon = (donHang != null && donHang.getTongTienHDBan() != null)
                ? donHang.getTongTienHDBan().doubleValue()
                : (laDonCoc ? tongHangThucTe : tongTienHD);

        double giamGiaChuan = Math.max(0, tongHangThucTe - tongPhaiTraCuaDon);

        lblTongTien.setText(FORMAT_TIEN.format(tongHangThucTe) + " đ");
        lblGiamGia.setText("-" + FORMAT_TIEN.format(giamGiaChuan) + " đ");
        lblDaThu.setText(FORMAT_TIEN.format(tongTienHD) + " đ");
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
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblTen = new Label(tenSP);
        lblTen.setPrefWidth(180);
        lblTen.setWrapText(true);
        lblTen.getStyleClass().add("receipt-item-name");

        Label lblSL = new Label("x" + item.getSoLuong());
        lblSL.setPrefWidth(40);
        lblSL.setAlignment(Pos.CENTER);
        lblSL.getStyleClass().add("lbl-body");

        double donGia = item.getDonGia() != null ? item.getDonGia().doubleValue() : 0.0;
        Label lblGia = new Label(FORMAT_TIEN.format(item.getSoLuong() * donGia) + " đ");
        lblGia.setPrefWidth(100);
        lblGia.setAlignment(Pos.CENTER_RIGHT);
        lblGia.getStyleClass().add("receipt-item-price");

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

                hienThiThongTin("Thành công", "Hóa đơn đã được lưu tại thư mục 'hoadon'!");
            }

            handleClose();
        } catch (Exception e) {
            System.err.println("[Receipt] Lỗi lưu PDF: " + e.getMessage());
            hienThiThongBaoLoi("Lỗi", "Không thể lưu PDF: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
