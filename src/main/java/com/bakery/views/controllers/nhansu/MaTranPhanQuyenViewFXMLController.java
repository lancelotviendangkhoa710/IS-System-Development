package com.bakery.views.controllers.nhansu;

import com.bakery.model.dao.nhansu.PhanQuyenDAO;
import com.bakery.model.dao.nhansu.VaiTroDAO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class MaTranPhanQuyenViewFXMLController {

    @FXML private ScrollPane scrollMatrix;
    @FXML private Label lblThongBao;

    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();
    private final VaiTroDAO vaiTroDAO = new VaiTroDAO();

    @FXML
    public void initialize() {
        try {
            loadMatrix();
        } catch (Exception e) {
            e.printStackTrace();
            lblThongBao.setText("Lỗi khi tải ma trận phân quyền!");
        }
    }

    private void loadMatrix() throws Exception {
        List<VaiTroDTO> dsVaiTro = vaiTroDAO.layDanhSachVaiTroDangHoatDong();
        List<ChucNangDTO> dsChucNang = phanQuyenDAO.layToanBoChucNang();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getStyleClass().add("grid-matrix");

        // 1. Header Row: Role Names
        grid.add(new Label("CHỨC NĂNG / VAI TRÒ"), 0, 0);
        for (int i = 0; i < dsVaiTro.size(); i++) {
            Label lblRole = new Label(dsVaiTro.get(i).getTenVaiTro());
            lblRole.getStyleClass().add("lbl-matrix-header");
            grid.add(lblRole, i + 1, 0);
        }

        // 2. Data Rows: Function -> Role Flags
        for (int row = 0; row < dsChucNang.size(); row++) {
            ChucNangDTO cn = dsChucNang.get(row);
            grid.add(new Label(cn.getTenChucNang()), 0, row + 1);

            for (int col = 0; col < dsVaiTro.size(); col++) {
                VaiTroDTO vt = dsVaiTro.get(col);
                
                // Fetch current flags for this pair
                PhanQuyenDAO.RolePermissionInfo info = phanQuyenDAO.layThongTinPhanQuyenTheoVaiTro(vt.getMaVaiTro());
                ChucNangDTO currentPerm = info.getDanhSachChucNang().stream()
                        .filter(c -> c.getMaChucNang() == cn.getMaChucNang())
                        .findFirst()
                        .orElse(new ChucNangDTO());

                grid.add(createPermissionBox(vt.getMaVaiTro(), cn.getMaChucNang(), currentPerm), col + 1, row + 1);
            }
        }

        scrollMatrix.setContent(grid);
    }

    private VBox createPermissionBox(int maVT, int maCN, ChucNangDTO perm) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("matrix-cell");

        CheckBox cbV = new CheckBox("Xem");
        cbV.setSelected(perm.isCanView());
        CheckBox cbA = new CheckBox("Thêm");
        cbA.setSelected(perm.isCanAdd());
        CheckBox cbE = new CheckBox("Sửa");
        cbE.setSelected(perm.isCanEdit());
        CheckBox cbD = new CheckBox("Xóa");
        cbD.setSelected(perm.isCanDelete());
        CheckBox cbDL = new CheckBox("Tải");
        cbDL.setSelected(perm.isCanDownload());

        // Action to save on change
        Runnable saveAction = () -> {
            try {
                phanQuyenDAO.capNhatQuyenChiTiet(maVT, maCN, 
                    cbV.isSelected(), cbA.isSelected(), cbE.isSelected(), cbD.isSelected(), cbDL.isSelected());
                lblThongBao.setText("Đã cập nhật quyền cho " + vtName(maVT) + " - " + cnName(maCN));
            } catch (Exception e) {
                lblThongBao.setText("Lỗi cập nhật!");
            }
        };

        cbV.setOnAction(e -> saveAction.run());
        cbA.setOnAction(e -> saveAction.run());
        cbE.setOnAction(e -> saveAction.run());
        cbD.setOnAction(e -> saveAction.run());
        cbDL.setOnAction(e -> saveAction.run());

        box.getChildren().addAll(cbV, cbA, cbE, cbD, cbDL);
        return box;
    }

    private String vtName(int id) { return "Vai trò " + id; } // Placeholder
    private String cnName(int id) { return "Chức năng " + id; } // Placeholder

    @FXML
    public void onLuuTatCa() {
        lblThongBao.setText("Hệ thống tự động lưu khi thay đổi.");
    }
}
