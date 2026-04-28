package com.bakery.views.controllers;

import com.bakery.model.dto.SanPhamDTO;
import com.bakery.presenters.SanPhamPresenter;
import com.bakery.utils.UserSession;
import com.bakery.views.interfaces.ISanPhamView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

public class SanPhamViewFXMLController implements ISanPhamView {

    @FXML private TableView<SanPhamDTO> tblSanPham;
    @FXML private TableColumn<SanPhamDTO, Integer> colMaSP;
    @FXML private TableColumn<SanPhamDTO, String> colTenSP;
    @FXML private TableColumn<SanPhamDTO, String> colDanhMuc;
    @FXML private TableColumn<SanPhamDTO, Double> colGiaBan;
    @FXML private TableColumn<SanPhamDTO, Double> colTonKho;

    @FXML private ComboBox<Map.Entry<Integer, String>> cmbDanhMuc;
    @FXML private TextField txtTenSP;
    @FXML private TextField txtGiaBan;
    @FXML private CheckBox chkTuyChinh;
    @FXML private TextField txtTGBaoQuan;
    @FXML private TextField txtTGChuanBi;
    @FXML private ImageView imgSanPham;

    @FXML private Button btnThemMoi;
    @FXML private Button btnLuuThayDoi;
    @FXML private Button btnXoa;
    @FXML private Label lblThongBao;

    private final ObservableList<SanPhamDTO> masterData = FXCollections.observableArrayList();
    private SanPhamPresenter presenter;
    private Map<Integer, String> currentDanhMucMap;
    private String hinhAnhName = "default.jpg";

    @FXML
    public void initialize() {
        setupTable();
        int maNV = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getMaNV() : 1;
        presenter = new SanPhamPresenter(this, maNV);
        setupSelectionListener();
        lamMoiForm();
        presenter.taiDuLieuBanDau();
    }

    private void setupTable() {
        colMaSP.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaSP()).asObject());
        colTenSP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenSP()));
        colDanhMuc.setCellValueFactory(cellData -> {
            String tenDM = currentDanhMucMap != null ? currentDanhMucMap.get(cellData.getValue().getMaDM()) : "";
            return new SimpleStringProperty(tenDM != null ? tenDM : "N/A");
        });
        colGiaBan.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getGiaCoBan()).asObject());
        colTonKho.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSoLuongTon()).asObject());
        tblSanPham.setItems(masterData);

        cmbDanhMuc.setConverter(new StringConverter<Map.Entry<Integer, String>>() {
            @Override
            public String toString(Map.Entry<Integer, String> object) {
                return object != null ? object.getValue() : "";
            }

            @Override
            public Map.Entry<Integer, String> fromString(String string) {
                return null;
            }
        });
    }

    private void setupSelectionListener() {
        tblSanPham.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            presenter.onChonSanPham(newSelection);
        });
    }

    @Override
    public void hienThiDanhSachSanPham(List<SanPhamDTO> dsSanPham) {
        masterData.setAll(dsSanPham);
    }

    @Override
    public void hienThiDanhSachDanhMuc(Map<Integer, String> danhMucMap) {
        this.currentDanhMucMap = danhMucMap;
        ObservableList<Map.Entry<Integer, String>> dmList = FXCollections.observableArrayList(danhMucMap.entrySet());
        cmbDanhMuc.setItems(dmList);
    }

    @Override
    public void hienThiChiTiet(SanPhamDTO sp) {
        if (sp == null) return;
        txtTenSP.setText(sp.getTenSP());
        txtGiaBan.setText(String.valueOf(sp.getGiaCoBan()));
        chkTuyChinh.setSelected(sp.getChoPhepTuyChinh() == 1);
        txtTGBaoQuan.setText(String.valueOf(sp.getThoiGianBaoQuan()));
        txtTGChuanBi.setText(String.valueOf(sp.getThoiGianChuanBi()));

        for (Map.Entry<Integer, String> entry : cmbDanhMuc.getItems()) {
            if (entry.getKey() == sp.getMaDM()) {
                cmbDanhMuc.getSelectionModel().select(entry);
                break;
            }
        }

        hinhAnhName = sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty() ? sp.getHinhAnh() : "default.jpg";
        hienThiAnhHienTai();

        btnLuuThayDoi.setDisable(false);
        btnXoa.setDisable(false);
    }

    private void hienThiAnhHienTai() {
        try {
            java.net.URL imgUrl = getClass().getResource("/images/products/" + hinhAnhName);
            if (imgUrl != null) {
                imgSanPham.setImage(new Image(imgUrl.toExternalForm()));
            } else {
                imgSanPham.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Không thể load ảnh: " + hinhAnhName);
        }
    }

    @Override
    public void hienThiLoi(String msg) {
        lblThongBao.setText(msg);
        lblThongBao.getStyleClass().removeAll("lbl-success");
        lblThongBao.getStyleClass().add("lbl-danger");
    }

    @Override
    public void hienThiThanhCong(String msg) {
        lblThongBao.setText(msg);
        lblThongBao.getStyleClass().removeAll("lbl-danger");
        lblThongBao.getStyleClass().add("lbl-success");
    }

    @Override
    public void lamMoiForm() {
        txtTenSP.clear();
        txtGiaBan.clear();
        txtTGBaoQuan.clear();
        txtTGChuanBi.clear();
        chkTuyChinh.setSelected(false);
        cmbDanhMuc.getSelectionModel().clearSelection();
        tblSanPham.getSelectionModel().clearSelection();
        
        hinhAnhName = "default.jpg";
        hienThiAnhHienTai();

        btnLuuThayDoi.setDisable(true);
        btnXoa.setDisable(true);
        lblThongBao.setText("");
    }

    @Override
    public SanPhamDTO getSelectedSanPham() {
        return tblSanPham.getSelectionModel().getSelectedItem();
    }

    @Override
    public SanPhamDTO layDuLieuTuForm() {
        try {
            Map.Entry<Integer, String> selectedDM = cmbDanhMuc.getSelectionModel().getSelectedItem();
            if (selectedDM == null) {
                hienThiLoi("Vui lòng chọn danh mục.");
                return null;
            }
            
            String tenSP = txtTenSP.getText().trim();
            if (tenSP.isEmpty()) {
                hienThiLoi("Tên sản phẩm không được trống.");
                return null;
            }

            double giaBan = Double.parseDouble(txtGiaBan.getText().trim());
            int tgBaoQuan = Integer.parseInt(txtTGBaoQuan.getText().trim());
            int tgChuanBi = Integer.parseInt(txtTGChuanBi.getText().trim());

            SanPhamDTO sp = new SanPhamDTO();
            sp.setMaDM(selectedDM.getKey());
            sp.setTenSP(tenSP);
            sp.setGiaCoBan(giaBan);
            sp.setChoPhepTuyChinh(chkTuyChinh.isSelected() ? 1 : 0);
            sp.setThoiGianBaoQuan(tgBaoQuan);
            sp.setThoiGianChuanBi(tgChuanBi);
            sp.setHinhAnh(hinhAnhName);

            return sp;
        } catch (NumberFormatException e) {
            hienThiLoi("Các trường số (giá, thời gian) phải là số hợp lệ.");
            return null;
        }
    }

    @FXML private void onThemMoi() { presenter.themSanPham(); }
    @FXML private void onLuuThayDoi() { presenter.suaSanPham(); }
    @FXML private void onXoa() { presenter.xoaSanPham(); }
    @FXML private void onLamMoi() { lamMoiForm(); }

    @FXML
    private void onChonAnh() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(btnThemMoi.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Thư mục đích trong mã nguồn (để giữ file lâu dài)
                Path srcDirPath = Paths.get("src/main/resources/images/products");
                if (!Files.exists(srcDirPath)) Files.createDirectories(srcDirPath);
                Path destSrcPath = srcDirPath.resolve(selectedFile.getName());
                Files.copy(selectedFile.toPath(), destSrcPath, StandardCopyOption.REPLACE_EXISTING);

                // Thư mục đích trong target (để hiển thị ngay lúc chạy mà ko cần build lại)
                Path targetDirPath = Paths.get("target/classes/images/products");
                if (!Files.exists(targetDirPath)) Files.createDirectories(targetDirPath);
                Path destTargetPath = targetDirPath.resolve(selectedFile.getName());
                Files.copy(selectedFile.toPath(), destTargetPath, StandardCopyOption.REPLACE_EXISTING);

                // Ghi nhận tên ảnh và hiển thị tạm
                hinhAnhName = selectedFile.getName();
                imgSanPham.setImage(new Image(selectedFile.toURI().toString()));

                hienThiThanhCong("Đã tải ảnh: " + hinhAnhName);
            } catch (IOException e) {
                hienThiLoi("Lỗi khi sao chép ảnh: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onQuayLai() {
        try {
            java.net.URL fxmlUrl = getClass().getResource("/fxml/MainMenuView.fxml");
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlUrl);
            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 1366, 768);
            
            MainMenuViewFXMLController controller = loader.getController();
            controller.khoiTaoThongTinDangNhap(UserSession.getCurrentUser()); 
            
            java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            
            javafx.stage.Stage stage = (javafx.stage.Stage) btnThemMoi.getScene().getWindow();
            stage.setTitle("H3K Bakery - Dashboard");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            hienThiLoi("Không thể quay lại Menu: " + ex.getMessage());
        }
    }
}
