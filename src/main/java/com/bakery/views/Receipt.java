package com.bakery.views;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Man hinh preview hoa don theo phong cach hien dai (JavaFX).
 * Giu nguyen constructor + setVisible(boolean) de tuong thich call site hien tai.
 */
public class Receipt {

    private final String tieuDe;
    private final String maDonStr;
    private final String maHoaDonStr;
    private final String ngayLapHoaDon;
    private final String khachHang;
    private final List<CTDonHangDTO> cart;
    private final List<SanPhamDTO> data;
    private final String tienGiamGia;
    private final String tongTien;
    private final String daThu;
    private final String tienKhachDua;
    private final String tienThua;

    private static final NumberFormat FORMAT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    static {
        FORMAT_TIEN.setMaximumFractionDigits(0);
    }

    public Receipt(String tieuDe, String maDonStr, String maHoaDonStr, String ngayLapHoaDon, String khachHang,
                   List<CTDonHangDTO> cart, List<SanPhamDTO> data,
                   String tienGiamGia, String tongTien, String daThu,
                   String tienKhachDua, String tienThua, Double customQrAmount) {
        this.tieuDe = tieuDe;
        this.maDonStr = maDonStr;
        this.maHoaDonStr = maHoaDonStr;
        this.ngayLapHoaDon = ngayLapHoaDon;
        this.khachHang = khachHang;
        this.cart = cart;
        this.data = data;
        this.tienGiamGia = tienGiamGia;
        this.tongTien = tongTien;
        this.daThu = daThu;
        this.tienKhachDua = tienKhachDua;
        this.tienThua = tienThua;
    }

    public void setVisible(boolean visible) {
        if (!visible) {
            return;
        }

        Runnable showTask = this::hienThiManHinhHoaDon;

        if (Platform.isFxApplicationThread()) {
            showTask.run();
        } else {
            Platform.runLater(showTask);
        }
    }

    private void hienThiManHinhHoaDon() {
        Stage stage = new Stage();
        stage.setTitle("Invoice - H3K Bakery");

        VBox invoiceCard = taoInvoiceCard();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #fcf9f5;");
        root.setTop(taoToolbar(stage));

        ScrollPane scrollPane = new ScrollPane(invoiceCard);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox centerWrap = new VBox(scrollPane);
        centerWrap.setStyle("-fx-padding: 28 24 28 24;");
        root.setCenter(centerWrap);

        Scene scene = new Scene(root, 600, 800);
        stage.setScene(scene);
        stage.show();
    }

    private HBox taoToolbar(Stage stage) {
        Label lblBrand = new Label("H3K Bakery");
        lblBrand.setStyle(
                "-fx-text-fill: #92400E; -fx-font-size: 24px; -fx-font-weight: 800; -fx-letter-spacing: 0.5px;");

        Button btnPrint = taoActionButton("In");
        btnPrint.setOnAction(event -> thongBaoThongTin("Ban co the dung Ctrl+P de in tu cua so nay."));

        Button btnDownload = taoActionButton("Tai PDF");
        btnDownload.setOnAction(event -> thongBaoThongTin("Tinh nang tai PDF se duoc bo sung o buoc tiep theo."));

        Button btnShare = taoActionButton("Chia se");
        btnShare.setOnAction(event -> thongBaoThongTin("Tinh nang chia se dang duoc xu ly."));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(12, btnPrint, btnDownload, btnShare);
        HBox wrapper = new HBox(16, lblBrand, spacer, actions);
        wrapper.setStyle("-fx-background-color: #FDFCF0; -fx-border-color: #92400E1A; -fx-border-width: 0 0 1 0;"
                + "-fx-padding: 18 24 18 24; -fx-alignment: center-left;");
        return wrapper;
    }

    private Button taoActionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #92400E; -fx-font-size: 13px;"
                + "-fx-font-weight: 600; -fx-border-color: #92400E33; -fx-border-width: 1;"
                + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 14 8 14;");
        return button;
    }

    private VBox taoInvoiceCard() {
        VBox card = new VBox(28);
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #92400E1A; -fx-border-width: 1;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 14, 0.2, 0, 2);"
                + "-fx-padding: 34 38 34 38;");

        card.getChildren().add(taoHeaderSection());
        card.getChildren().add(taoKhachHangSection());
        card.getChildren().add(taoBangSanPhamSection());
        card.getChildren().add(taoNoteVaTongKetSection());
        card.getChildren().add(taoFooterSection());
        return card;
    }

    private HBox taoHeaderSection() {
        VBox left = new VBox(6);
        Label lblTitle = new Label(tieuDe == null || tieuDe.isBlank() ? "HOA DON" : tieuDe.toUpperCase(Locale.ROOT));
        lblTitle.setStyle("-fx-text-fill: #92400E; -fx-font-size: 36px; -fx-font-weight: 800;");
        Label lblMaHoaDon = new Label("Ma hoa don: " + safeText(maHoaDonStr));
        lblMaHoaDon.setStyle("-fx-text-fill: #5c5c57; -fx-font-size: 14px;");
        Label lblNgay = new Label("Ngay: " + safeText(ngayLapHoaDon));
        lblNgay.setStyle("-fx-text-fill: #5c5c57; -fx-font-size: 14px;");
        left.getChildren().addAll(lblTitle, lblMaHoaDon, lblNgay);

        VBox right = new VBox(4);
        right.setStyle("-fx-alignment: center-right;");
        Label lblShop = new Label("H3K Bakery");
        lblShop.setStyle("-fx-text-fill: #D85A30; -fx-font-size: 18px; -fx-font-weight: 700;");
        Label lblAddress = new Label("123 Duong Suong Nguyet Anh, Quan 1");
        lblAddress.setStyle("-fx-text-fill: #5c5c57; -fx-font-size: 12px;");
        Label lblPhone = new Label("028 9999 8888");
        lblPhone.setStyle("-fx-text-fill: #5c5c57; -fx-font-size: 12px;");
        right.getChildren().addAll(lblShop, lblAddress, lblPhone);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return new HBox(16, left, spacer, right);
    }

    private VBox taoKhachHangSection() {
        VBox section = new VBox(6);
        section.setStyle("-fx-background-color: #f6f3f0; -fx-border-color: #92400E10; -fx-border-width: 1;"
                + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 16 18 16 18;");

        Label lblHeader = new Label("KHACH HANG");
        lblHeader.setStyle("-fx-text-fill: #92400E; -fx-font-size: 12px; -fx-font-weight: 700; -fx-letter-spacing: 1px;");
        Label lblCustomer = new Label(safeText(khachHang));
        lblCustomer.setStyle("-fx-text-fill: #1b1c1a; -fx-font-size: 22px; -fx-font-weight: 700;");
        Label lblOrderCode = new Label("Ma don: " + safeText(maDonStr));
        lblOrderCode.setStyle("-fx-text-fill: #5c5c57; -fx-font-size: 13px;");

        section.getChildren().addAll(lblHeader, lblCustomer, lblOrderCode);
        return section;
    }

    private VBox taoBangSanPhamSection() {
        VBox table = new VBox(0);
        table.setStyle("-fx-border-color: #92400E1A; -fx-border-width: 1; -fx-border-radius: 8;");

        table.getChildren().add(taoDongHeaderBang());

        List<CTDonHangDTO> items = safeCart();
        for (int i = 0; i < items.size(); i++) {
            table.getChildren().add(taoDongItem(i + 1, items.get(i), i == items.size() - 1));
        }

        if (items.isEmpty()) {
            HBox emptyRow = new HBox(new Label("Khong co san pham"));
            emptyRow.setStyle("-fx-padding: 14 16 14 16;");
            table.getChildren().add(emptyRow);
        }

        return table;
    }

    private HBox taoDongHeaderBang() {
        HBox row = new HBox();
        row.setStyle("-fx-background-color: #f0edea; -fx-border-color: #92400E1A; -fx-border-width: 0 0 1 0;");
        row.getChildren().addAll(
                taoCell("STT", 56, "-fx-font-weight: 700; -fx-text-fill: #1b1c1a;"),
                taoCell("Ten san pham", 220, "-fx-font-weight: 700; -fx-text-fill: #1b1c1a;"),
                taoCell("SL", 72, "-fx-font-weight: 700; -fx-text-fill: #1b1c1a; -fx-alignment: center;"),
                taoCell("Don gia", 100, "-fx-font-weight: 700; -fx-text-fill: #1b1c1a; -fx-alignment: center-right;"),
                taoCell("Thanh tien", 110, "-fx-font-weight: 700; -fx-text-fill: #1b1c1a; -fx-alignment: center-right;")
        );
        return row;
    }

    private HBox taoDongItem(int stt, CTDonHangDTO item, boolean isLast) {
        HBox row = new HBox();
        String borderBottom = isLast ? "0" : "1";
        row.setStyle("-fx-border-color: #92400E0F; -fx-border-width: 0 0 " + borderBottom + " 0;");

        double thanhTien = item.getDonGia() * item.getSoLuong();
        row.getChildren().addAll(
                taoCell(String.format("%02d", stt), 56, "-fx-text-fill: #5c5c57;"),
                taoCell(layTenSanPham(item.getMaSP()), 220, "-fx-font-weight: 600; -fx-text-fill: #1b1c1a;"),
                taoCell(String.valueOf(item.getSoLuong()), 72, "-fx-alignment: center;"),
                taoCell(dinhDangTien(item.getDonGia()), 100, "-fx-alignment: center-right;"),
                taoCell(dinhDangTien(thanhTien), 110, "-fx-alignment: center-right; -fx-font-weight: 700; -fx-text-fill: #92400E;")
        );
        return row;
    }

    private Label taoCell(String text, double width, String customStyle) {
        Label label = new Label(text);
        label.setPrefWidth(width);
        label.setMinWidth(width);
        label.setMaxWidth(width);
        label.setStyle("-fx-padding: 12 10 12 10; -fx-font-size: 13px;" + customStyle);
        return label;
    }

    private HBox taoNoteVaTongKetSection() {
        VBox noteBox = new VBox(6);
        noteBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #92400E33; -fx-border-style: segments(6, 4);"
                + "-fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 12 14 12 14;");
        Label lblNoteHeader = new Label("GHI CHU");
        lblNoteHeader.setStyle("-fx-text-fill: #92400E; -fx-font-size: 12px; -fx-font-weight: 700;");
        Label lblNote = new Label("Cam on quy khach da su dung dich vu. Vui long kiem tra hoa don truoc khi roi quay.");
        lblNote.setWrapText(true);
        lblNote.setStyle("-fx-text-fill: #5c5c57; -fx-font-size: 13px;");
        noteBox.getChildren().addAll(lblNoteHeader, lblNote);
        HBox.setHgrow(noteBox, Priority.ALWAYS);

        VBox tongKetBox = new VBox(8);
        tongKetBox.setPrefWidth(300);
        tongKetBox.setStyle("-fx-background-color: #ffffff;");

        long tongCart = tinhTongCart();
        tongKetBox.getChildren().add(taoDongTongKet("Tam tinh:", dinhDangTien(tongCart), false));
        if (coTienHopLe(tienGiamGia)) {
            tongKetBox.getChildren().add(taoDongTongKet("Giam gia:", "-" + tienGiamGia.trim(), false));
        }
        tongKetBox.getChildren().add(taoDongTongKet("Tong cong:", safeTien(tongTien), true));
        tongKetBox.getChildren().add(taoDongTongKet("Da thu:", safeTien(daThu), false));

        if (coTienHopLe(tienKhachDua)) {
            tongKetBox.getChildren().add(taoDongTongKet("Khach dua:", tienKhachDua.trim(), false));
        }
        if (coTienHopLe(tienThua)) {
            tongKetBox.getChildren().add(taoDongTongKet("Tien thua:", tienThua.trim(), false));
        } else {
            long conLai = parseTien(safeTien(tongTien)) - parseTien(safeTien(daThu));
            if (conLai > 0) {
                tongKetBox.getChildren().add(taoDongTongKet("Con lai:", dinhDangTien(conLai), false));
            }
        }

        HBox wrapper = new HBox(18, noteBox, tongKetBox);
        return wrapper;
    }

    private HBox taoDongTongKet(String nhan, String giaTri, boolean highlight) {
        Label lblNhan = new Label(nhan);
        Label lblGiaTri = new Label(giaTri);

        if (highlight) {
            lblNhan.setStyle("-fx-text-fill: #D85A30; -fx-font-size: 14px; -fx-font-weight: 700;");
            lblGiaTri.setStyle("-fx-text-fill: #D85A30; -fx-font-size: 26px; -fx-font-weight: 800;");
        } else {
            lblNhan.setStyle("-fx-text-fill: #5c5c57; -fx-font-size: 13px;");
            lblGiaTri.setStyle("-fx-text-fill: #1b1c1a; -fx-font-size: 14px; -fx-font-weight: 600;");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(10, lblNhan, spacer, lblGiaTri);
        row.setStyle("-fx-alignment: center-left;");
        return row;
    }

    private VBox taoFooterSection() {
        VBox footer = new VBox(8);
        footer.setStyle("-fx-alignment: center; -fx-padding: 18 0 0 0; -fx-border-color: #92400E1A; -fx-border-width: 1 0 0 0;");

        Label thanks = new Label("Cam on ban da lua chon Amitie!");
        thanks.setStyle("-fx-text-fill: #92400E; -fx-font-size: 22px; -fx-font-weight: 700;");
        Label sub = new Label("Moi chiec banh la mot loi tri an chan thanh tu bep cua chung toi.");
        sub.setStyle("-fx-text-fill: #5c5c57; -fx-font-size: 13px;");

        footer.getChildren().addAll(thanks, sub);
        return footer;
    }

    private String layTenSanPham(int maSP) {
        if (data == null) {
            return "SP #" + maSP;
        }
        for (SanPhamDTO sanPham : data) {
            if (sanPham.getMaSP() == maSP) {
                return sanPham.getTenSP();
            }
        }
        return "SP #" + maSP;
    }

    private List<CTDonHangDTO> safeCart() {
        return cart == null ? Collections.emptyList() : new ArrayList<>(cart);
    }

    private long tinhTongCart() {
        long tong = 0;
        for (CTDonHangDTO item : safeCart()) {
            tong += Math.round(item.getDonGia() * item.getSoLuong());
        }
        return tong;
    }

    private boolean coTienHopLe(String value) {
        return value != null && !value.isBlank() && parseTien(value) > 0;
    }

    private String safeTien(String value) {
        if (value == null || value.isBlank()) {
            return dinhDangTien(0);
        }
        return value.trim();
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }

    private long parseTien(String value) {
        if (value == null) {
            return 0;
        }
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String dinhDangTien(double amount) {
        return FORMAT_TIEN.format(Math.round(amount)) + "d";
    }

    private void thongBaoThongTin(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thong tin");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
