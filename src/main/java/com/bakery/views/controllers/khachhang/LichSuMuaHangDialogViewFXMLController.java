package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/** Controller dialog lịch sử mua hàng — hiện tổng đơn và tổng chi tiêu. */
public class LichSuMuaHangDialogViewFXMLController {

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    @FXML private Label lblTenKhachHang;
    @FXML private Label lblTongDon;
    @FXML private Label lblTongChiTieu;
    @FXML private Label lblThongBao;

    /** Nạp dữ liệu khách hàng và danh sách đơn vào dialog. */
    public void khoiTao(KhachHangDTO kh, List<DonDatHangDTO> dsDon) {
        lblTenKhachHang.setText(kh.getHoTen() + " | " + kh.getSdt());

        List<DonDatHangDTO> ds = (dsDon != null) ? dsDon : List.of();

        lblTongDon.setText(String.valueOf(ds.size()));

        BigDecimal tongTien = ds.stream()
                .map(d -> d.getTongTienHDBan() != null ? d.getTongTienHDBan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTongChiTieu.setText(FMT_TIEN.format(tongTien) + " đ");

        if (ds.isEmpty()) {
            lblThongBao.setText("Khách hàng chưa có lịch sử giao dịch.");
        }
    }

    @FXML
    private void onDong() {
        Stage stage = (Stage) lblTongDon.getScene().getWindow();
        stage.close();
    }
}
