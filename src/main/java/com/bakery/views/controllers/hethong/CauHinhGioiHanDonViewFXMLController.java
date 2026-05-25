package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.hethong.CauHinhGioiHanDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.presenters.hethong.CauHinhGioiHanPresenter;
import com.bakery.views.interfaces.hethong.ICauHinhGioiHanView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller màn hình Cấu hình giới hạn nhận đơn.
 * Cho phép Quản lý thiết lập số lượng bánh tối đa bếp nhận/ngày.
 */
public class CauHinhGioiHanDonViewFXMLController implements ICauHinhGioiHanView {

    private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TableView<CauHinhGioiHanDTO> tblCauHinh;
    @FXML private TableColumn<CauHinhGioiHanDTO, String>  colLoai;
    @FXML private TableColumn<CauHinhGioiHanDTO, Integer> colGioiHan;
    @FXML private TableColumn<CauHinhGioiHanDTO, String>  colMoTa;
    @FXML private TableColumn<CauHinhGioiHanDTO, String>  colCapNhat;

    @FXML private TextField txtGioiHanTuyChinhMoi;
    // Task 5: Đổi generic type sang SanPhamDTO để có thể binding đúng đối tượng
    @FXML private ComboBox<SanPhamDTO> cmbSanPhamBanLe;
    @FXML private TextField txtGioiHanSanPham;
    @FXML private Button btnLuuCauHinh;
    @FXML private Button btnHuy;
    @FXML private Label lblThongBao;

    private CauHinhGioiHanPresenter presenter;

    @FXML
    public void initialize() {
        presenter = new CauHinhGioiHanPresenter(this);
        khoiTaoCot();
        presenter.taiDuLieu();
    }

    // --- ICauHinhGioiHanView ---

    @Override
    public void hienThiDanhSachCauHinh(List<CauHinhGioiHanDTO> dsCauHinh) {
        tblCauHinh.setItems(FXCollections.observableArrayList(dsCauHinh));
        lblThongBao.getStyleClass().setAll("lbl-small-bold");
        lblThongBao.setText("Hiển thị " + dsCauHinh.size() + " bản ghi.");
    }

    @Override
    public void hienThiThongBao(String msg) {
        lblThongBao.getStyleClass().setAll("lbl-small-bold");
        lblThongBao.setText(msg);
    }

    @Override
    public void hienThiLoi(String msg) {
        lblThongBao.getStyleClass().setAll("lbl-danger");
        lblThongBao.setText(msg);
    }

    /**
     * Task 5: Nhận danh sách sản phẩm từ Presenter, populate ComboBox với StringConverter.
     */
    @Override
    public void napDanhSachSanPhamBanLe(List<SanPhamDTO> dsSanPham) {
        cmbSanPhamBanLe.setConverter(new StringConverter<>() {
            @Override public String toString(SanPhamDTO sp) {
                return sp == null ? "" : sp.getTenSP();
            }
            @Override public SanPhamDTO fromString(String s) { return null; }
        });
        cmbSanPhamBanLe.setItems(FXCollections.observableArrayList(dsSanPham));
    }

    @Override
    public void lamMoiForm() {
        txtGioiHanTuyChinhMoi.clear();
        txtGioiHanSanPham.clear();
        cmbSanPhamBanLe.getSelectionModel().clearSelection();
    }

    // --- FXML handlers ---

    /**
     * Task 6: Tách logic Tùy chỉnh vs Có sẵn trong cùng 1 handler.
     * - Nếu txtGioiHanTuyChinhMoi có giá trị → path Tùy chỉnh.
     * - Nếu cmbSanPhamBanLe + txtGioiHanSanPham đều có giá trị → path Có sẵn.
     * - Không được điền đồng thời cả hai.
     */
    @FXML
    private void onLuuCauHinh() {
        String gioiHanTuyChinh = txtGioiHanTuyChinhMoi.getText().trim();
        if (gioiHanTuyChinh.isBlank()) {
            hienThiLoi("Vui lòng nhập giới hạn bánh tùy chỉnh.");
            return;
        }
        presenter.luuCauHinhTuyChinh(gioiHanTuyChinh);
    }

    @FXML
    private void onHuy() {
        lamMoiForm();
        lblThongBao.setText("");
    }

    @FXML
    private void onLamMoi() {
        lamMoiForm();
        presenter.taiDuLieu();
    }

    // --- private helpers ---

    private void khoiTaoCot() {
        // Cột "Loại đơn" — hiển thị ngày sản xuất
        colLoai.setCellValueFactory(cell -> {
            CauHinhGioiHanDTO dto = cell.getValue();
            String val = dto.getNgaySanXuat() != null ? dto.getNgaySanXuat().format(FMT_NGAY) : "N/A";
            return new SimpleStringProperty(val);
        });

        // Cột "Giới hạn (đơn/ngày)"
        colGioiHan.setCellValueFactory(cell ->
                new SimpleIntegerProperty(cell.getValue().getGioiHanSoBanh()).asObject());

        // Cột "Mô tả" — đã nhận / giới hạn
        colMoTa.setCellValueFactory(cell -> {
            CauHinhGioiHanDTO dto = cell.getValue();
            return new SimpleStringProperty(dto.getSoBanhDaNhan() + " / " + dto.getGioiHanSoBanh() + " bánh");
        });

        // Cột "Cập nhật lần cuối" — dùng tạm ngày sản xuất
        colCapNhat.setCellValueFactory(cell -> {
            CauHinhGioiHanDTO dto = cell.getValue();
            String val = dto.getNgaySanXuat() != null ? dto.getNgaySanXuat().format(FMT_NGAY) : "";
            return new SimpleStringProperty(val);
        });
    }
}
