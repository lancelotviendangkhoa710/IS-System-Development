package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller cho MaTranPhanQuyenView.
 * Đã refactor sang 'Nhân viên vs Vai trò' theo yêu cầu.
 */
public class MaTranPhanQuyenViewFXMLController extends BaseController {

    @FXML private ScrollPane scrollMatrix;
    @FXML private Label lblThongBao;

    private final NhanVienService nhanVienService = new NhanVienService();
    private List<NhanVienDTO> dsNhanVien;
    private List<Integer> dsMaVaiTroHeader;
    private Map<String, List<CheckBox>> matrixMap; // Key: maNV, Value: list of role checkboxes

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
        dsNhanVien = nhanVienService.layTatCaNhanVien();
        Map<Integer, String> roleMap = nhanVienService.layDanhSachVaiTro();
        
        dsMaVaiTroHeader = new ArrayList<>(roleMap.keySet());
        matrixMap = new HashMap<>();

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.getStyleClass().add("grid-matrix");
        grid.setAlignment(Pos.TOP_LEFT);

        // 1. Header Row
        Label lblHeader = new Label("NHÂN VIÊN / VAI TRÒ");
        lblHeader.getStyleClass().add("lbl-matrix-header-first");
        grid.add(lblHeader, 0, 0);

        int col = 1;
        for (Integer maVT : dsMaVaiTroHeader) {
            Label lblRole = new Label(roleMap.get(maVT));
            lblRole.getStyleClass().add("lbl-matrix-header");
            lblRole.setMinWidth(100);
            lblRole.setAlignment(Pos.CENTER);
            grid.add(lblRole, col++, 0);
        }

        // 2. Data Rows
        for (int row = 0; row < dsNhanVien.size(); row++) {
            NhanVienDTO nv = dsNhanVien.get(row);
            
            Label lblName = new Label(nv.getHoTen());
            lblName.getStyleClass().add("lbl-matrix-row-name");
            grid.add(lblName, 0, row + 1);

            List<CheckBox> checkBoxes = new ArrayList<>();
            for (int i = 0; i < dsMaVaiTroHeader.size(); i++) {
                int maVT = dsMaVaiTroHeader.get(i);
                CheckBox cb = new CheckBox();
                cb.setSelected(nv.getDanhSachMaVaiTro().contains(maVT));
                cb.setAlignment(Pos.CENTER);
                
                VBox cell = new VBox(cb);
                cell.setAlignment(Pos.CENTER);
                cell.getStyleClass().add("matrix-cell");
                
                grid.add(cell, i + 1, row + 1);
                checkBoxes.add(cb);
            }
            matrixMap.put(String.valueOf(nv.getMaNV()), checkBoxes);
        }

        scrollMatrix.setContent(grid);
        lblThongBao.setText("Đã tải " + dsNhanVien.size() + " nhân viên.");
    }

    @FXML
    public void onLuuTatCa() {
        try {
            int count = 0;
            for (NhanVienDTO nv : dsNhanVien) {
                List<CheckBox> checkBoxes = matrixMap.get(String.valueOf(nv.getMaNV()));
                List<Integer> selectedRoleIds = new ArrayList<>();
                
                for (int i = 0; i < dsMaVaiTroHeader.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        selectedRoleIds.add(dsMaVaiTroHeader.get(i));
                    }
                }
                
                // Cập nhật nếu có thay đổi (optional optimization, but let's just save for simplicity)
                nhanVienService.capNhatVaiTro(nv.getMaNV(), selectedRoleIds);
                count++;
            }
            lblThongBao.setText("Lưu thành công phân quyền cho " + count + " tài khoản!");
        } catch (Exception e) {
            e.printStackTrace();
            lblThongBao.setText("Lỗi khi lưu: " + e.getMessage());
        }
    }
}
