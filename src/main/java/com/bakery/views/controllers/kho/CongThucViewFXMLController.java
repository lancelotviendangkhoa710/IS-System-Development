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

    @FXML private TableView<CongThucDTO>            tblCongThuc;
    @FXML private TableColumn<CongThucDTO, String>  colTenNguyenLieu;
    @FXML private TableColumn<CongThucDTO, Double>  colDinhMuc;
    @FXML private TableColumn<CongThucDTO, Double>  colDonGia;
    @FXML private TableColumn<CongThucDTO, Double>  colThanhTien;

    @FXML private ComboBox<SanPhamDTO>    cmbChonSanPham;  // autocomplete chọn SP
    @FXML private ComboBox<NguyenLieuDTO> cmbNguyenLieu;   // chọn NL khi sửa định mức
    @FXML private TextField               txtDinhMuc;
    @FXML private Label                   lblTongGiaVon;
    @FXML private Label                   lblSanPhamDangCauHinh; // hiện tên SP đang cấu hình

    private final ObservableList<CongThucDTO> dsCongThuc = FXCollections.observableArrayList();
    private CongThucPresenter presenter;

    /** Danh sách gốc — giữ để reset filter autocomplete. */
    private List<SanPhamDTO>    dsSanPhamGoc    = new ArrayList<>();
    private List<NguyenLieuDTO> cachedDsNguyenLieu = new ArrayList<>();

    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    @FXML
    public void initialize() {
        setupTable();
        setupComboNguyenLieu();
        presenter = new CongThucPresenter(this);
        presenter.khoiTao();
        // Autocomplete setup sau khi presenter tạo xong (dữ liệu load async)
        setupComboChonSanPham();
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
        tblCongThuc.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> presenter.chonCongThuc(n));
    }

    /** ComboBox nguyên liệu — hiện "Tên NL (DVT)" để user biết đơn vị khi nhập định mức. */
    private void setupComboNguyenLieu() {
        cmbNguyenLieu.setConverter(new StringConverter<>() {
            @Override public String toString(NguyenLieuDTO nl) {
                if (nl == null) return "";
                String dvt = nl.getTenDVT();
                return dvt.isEmpty() ? nl.getTenNL() : nl.getTenNL() + " (" + dvt + ")";
            }
            @Override public NguyenLieuDTO fromString(String s) { return null; }
        });
    }

    /**
     * ComboBox chọn sản phẩm ở header — hỗ trợ autocomplete.
     * Khi user gõ: filter danh sách theo tên. Khi chọn: load công thức.
     */
    private void setupComboChonSanPham() {
        cmbChonSanPham.setConverter(new StringConverter<>() {
            @Override public String toString(SanPhamDTO sp) {
                return sp != null ? sp.getTenSP() + " (Mã: " + sp.getMaSP() + ")" : "";
            }
            @Override public SanPhamDTO fromString(String s) { return null; }
        });

        // Autocomplete: lọc khi gõ
        cmbChonSanPham.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            SanPhamDTO selected = cmbChonSanPham.getValue();
            // Bỏ qua nếu text thay đổi do việc chọn item (không phải user gõ)
            if (selected != null && cmbChonSanPham.getConverter().toString(selected).equals(newVal)) return;

            String filter = newVal == null ? "" : newVal.trim().toLowerCase();
            List<SanPhamDTO> filtered = dsSanPhamGoc.stream()
                    .filter(sp -> sp.getTenSP().toLowerCase().contains(filter))
                    .toList();
            cmbChonSanPham.setItems(FXCollections.observableArrayList(filtered));
            if (!filtered.isEmpty() && !filter.isEmpty()) cmbChonSanPham.show();
        });

        // Khi user chọn sản phẩm → load công thức
        cmbChonSanPham.valueProperty().addListener((obs, oldSP, newSP) -> {
            if (newSP != null) presenter.chonSanPham(newSP.getMaSP());
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
        cmbNguyenLieu.setItems(FXCollections.observableArrayList(cachedDsNguyenLieu));
    }

    @Override
    public void hienThiDanhSachSanPham(List<SanPhamDTO> dsSP) {
        dsSanPhamGoc = dsSP != null ? dsSP : new ArrayList<>();
        cmbChonSanPham.setItems(FXCollections.observableArrayList(dsSanPhamGoc));
    }

    @Override
    public void hienThiChiTiet(CongThucDTO ct) {
        if (ct == null) return;
        cmbNguyenLieu.getItems().stream()
                .filter(nl -> nl.getMaNL() == ct.getMaNL())
                .findFirst()
                .ifPresent(cmbNguyenLieu::setValue);
        txtDinhMuc.setText(String.valueOf(ct.getSoLuongTieuHao()));
    }

    @Override public void hienThiLoi(String thongBao)     { hienThiLoiLabel(thongBao); }

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

    // ── API cho QuanLySanPhamViewFXMLController (backward-compat) ─────────────

    /**
     * Được gọi khi user chọn sản phẩm ở Tab Sản phẩm.
     * Đồng bộ luôn ComboBox và cập nhật label tên SP đang cấu hình.
     */
    public void capNhatSanPhamDangChon(SanPhamDTO sp) {
        if (sp == null) {
            cmbChonSanPham.setValue(null);
            if (lblSanPhamDangCauHinh != null) lblSanPhamDangCauHinh.setText("");
            presenter.chonSanPham(-1);
        } else {
            dsSanPhamGoc.stream()
                    .filter(s -> s.getMaSP() == sp.getMaSP())
                    .findFirst()
                    .ifPresent(found -> {
                        cmbChonSanPham.setItems(FXCollections.observableArrayList(dsSanPhamGoc));
                        cmbChonSanPham.setValue(found);
                    });
            if (lblSanPhamDangCauHinh != null)
                lblSanPhamDangCauHinh.setText("→ Đang cấu hình: " + sp.getTenSP());
            presenter.chonSanPham(sp.getMaSP());
        }
    }

    // ── FXML Events ───────────────────────────────────────────────────────────

    @FXML
    private void onThemCongThuc() {
        if (presenter == null) return;
        if (presenter.getMaSPDangChon() <= 0) {
            hienThiLoiLabel("Vui lòng chọn sản phẩm từ ComboBox trước.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/kho/ThemCongThucDialog.fxml"));
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
