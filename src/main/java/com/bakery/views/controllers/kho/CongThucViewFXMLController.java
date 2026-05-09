package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.CongThucDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.presenters.kho.CongThucPresenter;
import com.bakery.views.interfaces.kho.ICongThucView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Controller Tab Công thức — Quản lý BOM nguyên liệu. */
public class CongThucViewFXMLController extends BaseController implements ICongThucView {

    @FXML private TableView<CongThucDTO> tblCongThuc;
    @FXML private TableColumn<CongThucDTO, String> colTenNguyenLieu;
    @FXML private TableColumn<CongThucDTO, Double> colDinhMuc;
    @FXML private TableColumn<CongThucDTO, Double> colDonGia;
    @FXML private TableColumn<CongThucDTO, Double> colThanhTien;

    @FXML private ComboBox<NguyenLieuDTO> cmbNguyenLieu;
    @FXML private TextField txtDinhMuc;
    @FXML private Label lblTenSPDangChon;
    @FXML private Label lblTongGiaVon;

    private final ObservableList<CongThucDTO> dsCongThuc = FXCollections.observableArrayList();
    private CongThucPresenter presenter;
    /** Cache danh sách nguyên liệu để inject vào Dialog "Thêm". */
    private List<NguyenLieuDTO> cachedDsNguyenLieu = new ArrayList<>();

    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    @FXML
    public void initialize() {
        setupTable();
        setupComboBox();
        presenter = new CongThucPresenter(this);
        presenter.khoiTao();
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void setupTable() {
        colTenNguyenLieu.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNguyenLieu()));
        colDinhMuc.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSoLuongTieuHao()).asObject());
        colDonGia.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getDonGia()).asObject());
        colDonGia.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : NF.format(v) + " đ");
            }
        });
        colThanhTien.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().tinhThanhTien()).asObject());
        colThanhTien.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : NF.format(v) + " đ");
            }
        });

        tblCongThuc.setItems(dsCongThuc);
        // Chọn dòng trong bảng → điền vào form bên phải để sửa định mức
        tblCongThuc.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> presenter.chonCongThuc(n));
    }

    private void setupComboBox() {
        cmbNguyenLieu.setConverter(new StringConverter<>() {
            @Override public String toString(NguyenLieuDTO nl) { return nl != null ? nl.getTenNL() : ""; }
            @Override public NguyenLieuDTO fromString(String s) { return null; }
        });
    }

    // ── ICongThucView ─────────────────────────────────────────────────────────

    @Override
    public void hienThiDanhSachCongThuc(List<CongThucDTO> ds) {
        dsCongThuc.setAll(ds != null ? ds : List.of());
    }

    @Override
    public void hienThiDanhSachNguyenLieu(List<NguyenLieuDTO> dsNL) {
        cachedDsNguyenLieu = dsNL != null ? dsNL : new ArrayList<>();
        // Nạp vào ComboBox form bên phải (dùng khi sửa định mức)
        cmbNguyenLieu.setItems(FXCollections.observableArrayList(cachedDsNguyenLieu));
    }

    @Override
    public void hienThiChiTiet(CongThucDTO ct) {
        if (ct == null) return;
        // Điền form bên phải để user sửa định mức
        cmbNguyenLieu.getItems().stream()
                .filter(nl -> nl.getMaNL() == ct.getMaNL())
                .findFirst()
                .ifPresent(cmbNguyenLieu::setValue);
        txtDinhMuc.setText(String.valueOf(ct.getSoLuongTieuHao()));
    }

    @Override public void hienThiLoi(String thongBao)      { hienThiLoiLabel(thongBao); }

    @Override
    public void hienThiThanhCong(String thongBao) {
        if (thongBao != null && thongBao.startsWith("Giá vốn BOM:")) {
            if (lblTongGiaVon != null) lblTongGiaVon.setText(thongBao.replace("Giá vốn BOM: ", ""));
        } else {
            hienThiThanhCongLabel(thongBao);
        }
    }

    @Override
    public void lamMoiForm() {
        cmbNguyenLieu.setValue(null);
        txtDinhMuc.clear();
        tblCongThuc.getSelectionModel().clearSelection();
        if (lblThongBao != null) lblThongBao.setText("");
    }

    @Override
    public CongThucDTO getSelectedCongThuc() {
        return tblCongThuc.getSelectionModel().getSelectedItem();
    }

    // ── API cho QuanLySanPhamViewFXMLController ───────────────────────────────

    /** Được gọi khi user chọn sản phẩm ở Tab Sản phẩm — tự động load công thức. */
    public void capNhatSanPhamDangChon(SanPhamDTO sp) {
        if (sp == null) {
            if (lblTenSPDangChon != null)
                lblTenSPDangChon.setText("(Chưa chọn — hãy chọn sản phẩm ở Tab Sản phẩm)");
            presenter.chonSanPham(-1);
        } else {
            if (lblTenSPDangChon != null)
                lblTenSPDangChon.setText(sp.getTenSP() + " (Mã: " + sp.getMaSP() + ")");
            presenter.chonSanPham(sp.getMaSP());
        }
    }

    // ── FXML Events ───────────────────────────────────────────────────────────

    /** Nút "➕ Thêm nguyên liệu" → mở Dialog chọn NL + nhập định mức. */
    @FXML
    private void onThemCongThuc() {
        if (presenter == null) return;
        if (presenter.getMaSPDangChon() <= 0) {
            hienThiLoiLabel("Vui lòng chọn sản phẩm ở Tab Sản phẩm trước.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ThemCongThucDialog.fxml"));
            Parent root = loader.load();

            ThemCongThucDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTaoDanhSachNguyenLieu(cachedDsNguyenLieu);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm nguyên liệu vào công thức");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(tblCongThuc.getScene().getWindow());

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (dialogCtrl.isConfirmed()) {
                presenter.luuCongThuc(dialogCtrl.getNguyenLieu().getMaNL(), dialogCtrl.getDinhMuc());
            }
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog thêm công thức: " + e.getMessage());
        }
    }

    /** Nút "💾 Lưu thay đổi" — sửa định mức dòng đang chọn trong bảng. */
    @FXML
    private void onLuuCongThuc() {
        NguyenLieuDTO nlChon = cmbNguyenLieu.getValue();
        if (nlChon == null) { hienThiLoiLabel("Vui lòng chọn dòng nguyên liệu cần sửa từ bảng."); return; }
        double dinhMuc;
        try {
            dinhMuc = Double.parseDouble(txtDinhMuc.getText().trim());
            if (dinhMuc <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            hienThiLoiLabel("Định mức phải là số dương hợp lệ.");
            return;
        }
        presenter.luuCongThuc(nlChon.getMaNL(), dinhMuc);
    }

    @FXML private void onXoaCongThuc() { presenter.xoaCongThuc(); }

    @FXML private void onLamMoi() { presenter.taiCongThuc(); }
}
