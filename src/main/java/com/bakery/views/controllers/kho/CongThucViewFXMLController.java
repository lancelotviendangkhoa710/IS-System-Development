package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.CongThucDTO;
import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.presenters.kho.CongThucPresenter;
import com.bakery.utils.DialogHelper;
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
    @FXML private TableColumn<CongThucDTO, String>  colDVT;
    @FXML private TableColumn<CongThucDTO, Double>  colDonGia;
    @FXML private TableColumn<CongThucDTO, Double>  colThanhTien;

    @FXML private ComboBox<SanPhamDTO>    cmbChonSanPham;  // autocomplete chọn SP
    @FXML private Label                   lblTongGiaVon;
    @FXML private Label                   lblSanPhamDangCauHinh; // hiện tên SP đang cấu hình

    @FXML private Button                  btnSua;
    @FXML private Button                  btnXoa;

    private final ObservableList<CongThucDTO> dsCongThuc = FXCollections.observableArrayList();
    private CongThucPresenter presenter;

    /** Danh sách gốc — giữ để reset filter autocomplete. */
    private List<SanPhamDTO>    dsSanPhamGoc       = new ArrayList<>();
    private List<NguyenLieuDTO> cachedDsNguyenLieu = new ArrayList<>();
    private List<DonViTinhDTO>  cachedDsDVT        = new ArrayList<>();
    private List<NhaCungCapDTO> cachedDsNCC        = new ArrayList<>();

    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    @FXML
    public void initialize() {
        setupTable();
        presenter = new CongThucPresenter(this);
        presenter.khoiTao();
        // Autocomplete setup sau khi presenter tạo xong (dữ liệu load async)
        setupComboChonSanPham();
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void setupTable() {
        colTenNguyenLieu.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNguyenLieu()));
        colDinhMuc.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSoLuongTieuHao()).asObject());
        // Cột ĐV Tính — hiển thị đơn vị để biết "0.25" là 0.25 gram hay 0.25 kg
        if (colDVT != null) {
            colDVT.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenDVT()));
        }
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
                .addListener((obs, o, n) -> {
                    presenter.chonCongThuc(n);
                    boolean coChon = n != null;
                    if (btnSua != null) btnSua.setDisable(!coChon);
                    if (btnXoa != null) btnXoa.setDisable(!coChon);
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
    }

    @Override
    public void hienThiDanhSachSanPham(List<SanPhamDTO> dsSP) {
        dsSanPhamGoc = dsSP != null ? dsSP : new ArrayList<>();
        cmbChonSanPham.setItems(FXCollections.observableArrayList(dsSanPhamGoc));
    }

    @Override
    public void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT) {
        cachedDsDVT = dsDVT != null ? dsDVT : new ArrayList<>();
    }

    @Override
    public void napDanhSachNhaCungCap(List<NhaCungCapDTO> dsNCC) {
        cachedDsNCC = dsNCC != null ? dsNCC : new ArrayList<>();
    }

    @Override
    public void hienThiChiTiet(CongThucDTO ct) {
        // No-op vì không còn form inline bên phải, SelectionListener tự xử lý bật/tắt nút
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
        tblCongThuc.getSelectionModel().clearSelection();
        if (btnSua != null) btnSua.setDisable(true);
        if (btnXoa != null) btnXoa.setDisable(true);
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

        // ── Chọn luồng: NL có sẵn (ưu tiên) hoặc tạo NL mới ──────────────
        ButtonType btnChonCo  = new ButtonType("📋 Chọn nguyên liệu có sẵn");
        ButtonType btnThemMoi = new ButtonType("➕ Tạo nguyên liệu mới");
        Alert chonLuong = new Alert(Alert.AlertType.NONE,
                "Bạn muốn dùng nguyên liệu đã có trong hệ thống\nhay tạo mới hoàn toàn?",
                btnChonCo, btnThemMoi, ButtonType.CANCEL);
        chonLuong.setTitle("Thêm nguyên liệu vào công thức");
        chonLuong.setHeaderText("Chọn cách thêm nguyên liệu");
        DialogHelper.applyBakeryTheme(chonLuong);

        ButtonType chon = chonLuong.showAndWait().orElse(ButtonType.CANCEL);
        if (chon == ButtonType.CANCEL) return;

        if (chon == btnChonCo) {
            moDialogChonNguyenLieuCo();
        } else {
            moDialogThemNguyenLieuMoi();
        }
    }

    /** Luồng 1 (ưu tiên): Chọn nguyên liệu đã có trong DB → chỉ nhập định mức. */
    private void moDialogChonNguyenLieuCo() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/kho/ChonNguyenLieuCuDialog.fxml"));
            Parent root = loader.load();

            ChonNguyenLieuCuDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTao(cachedDsNguyenLieu);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chọn nguyên liệu có sẵn vào công thức");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(tblCongThuc.getScene().getWindow());

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (dialogCtrl.isConfirmed()) {
                presenter.luuCongThuc(dialogCtrl.getMaNL(), dialogCtrl.getDinhMuc());
            }
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog chọn nguyên liệu: " + e.getMessage());
        }
    }

    /** Luồng 2: Tạo nguyên liệu hoàn toàn mới + nhập kho lần đầu + định mức. */
    private void moDialogThemNguyenLieuMoi() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/kho/ThemCongThucDialog.fxml"));
            Parent root = loader.load();

            ThemCongThucDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTao(cachedDsDVT, cachedDsNCC);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm nguyên liệu mới vào công thức");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(tblCongThuc.getScene().getWindow());

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (dialogCtrl.isConfirmed()) {
                int maDVT = dialogCtrl.getDonViTinh()  != null ? dialogCtrl.getDonViTinh().getMaDVT()  : 0;
                int maNCC = dialogCtrl.getNhaCungCap() != null ? dialogCtrl.getNhaCungCap().getMaNCC() : 0;
                presenter.themNguyenLieuMoiVaoCongThuc(
                        dialogCtrl.getTenNL(),
                        dialogCtrl.getXuatXu(),
                        maDVT,
                        maNCC,
                        dialogCtrl.getSoLuong(),
                        dialogCtrl.getDonGia(),
                        dialogCtrl.getDinhMuc());
            }
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog thêm công thức: " + e.getMessage());
        }
    }

    @FXML
    private void onSuaAction() {
        CongThucDTO selected = getSelectedCongThuc();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getSoLuongTieuHao()));
        dialog.setTitle("Sửa định mức nguyên liệu");
        dialog.setHeaderText("Chỉnh sửa định mức cho " + selected.getTenNguyenLieu());
        dialog.setContentText("Định mức tiêu hao (" + selected.getTenDVT() + "):");
        dialog.getDialogPane().getStyleClass().add("bg-app");

        try {
            dialog.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/css/bakery.css").toExternalForm());
        } catch (Exception ignored) {}

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (btnOk != null) btnOk.getStyleClass().add("btn-primary");
        Button btnCancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (btnCancel != null) btnCancel.getStyleClass().add("btn-secondary");

        dialog.showAndWait().ifPresent(val -> {
            try {
                double dinhMucMoi = Double.parseDouble(val.trim());
                if (dinhMucMoi <= 0) {
                    hienThiLoiLabel("⚠ Định mức tiêu hao phải là số dương.");
                    return;
                }
                presenter.luuCongThuc(selected.getMaNL(), dinhMucMoi);
            } catch (NumberFormatException e) {
                hienThiLoiLabel("⚠ Định mức không hợp lệ.");
            }
        });
    }

    @FXML
    private void onXoaAction() {
        CongThucDTO selected = getSelectedCongThuc();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc chắn muốn xóa nguyên liệu \"" + selected.getTenNguyenLieu() + "\" khỏi công thức?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.getDialogPane().getStyleClass().add("bg-app");
        DialogHelper.applyBakeryTheme(alert);

        Button btnOk = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (btnOk != null) btnOk.getStyleClass().add("btn-danger");
        Button btnCancel = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (btnCancel != null) btnCancel.getStyleClass().add("btn-secondary");

        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                presenter.xoaCongThuc();
            }
        });
    }

    @FXML private void onLamMoi() { presenter.taiCongThuc(); }
}
