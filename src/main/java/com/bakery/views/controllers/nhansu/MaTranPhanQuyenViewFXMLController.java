package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.views.controllers.BaseController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller cho MaTranPhanQuyenView.
 * Mọi dữ liệu được lấy từ DB. Không có Mock Data.
 */
public class MaTranPhanQuyenViewFXMLController extends BaseController {

    @FXML private ScrollPane scrollMatrix;
    @FXML private Label lblThongBao;

    private final NhanVienService nhanVienService = new NhanVienService();
    private List<NhanVienDTO> dsNhanVien;
    private List<Integer> dsMaVaiTroHeader;
    private Map<String, List<CheckBox>> matrixMap;

    @FXML
    public void initialize() {
        lblThongBao.setText("Đang tải dữ liệu...");
        new Thread(() -> {
            try {
                loadMatrixData();
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> lblThongBao.setText("Lỗi tải dữ liệu: " + e.getMessage()));
            }
        }).start();
    }

    private void loadMatrixData() throws Exception {
        dsNhanVien = nhanVienService.layTatCaNhanVien();
        Map<Integer, String> roleMap = nhanVienService.layDanhSachVaiTro();

        Platform.runLater(() -> {
            if (dsNhanVien == null || dsNhanVien.isEmpty()) {
                lblThongBao.setText("Chưa có nhân viên nào trong hệ thống.");
                scrollMatrix.setContent(new Label("Không có dữ liệu."));
                return;
            }
            if (roleMap == null || roleMap.isEmpty()) {
                lblThongBao.setText("Chưa có vai trò nào trong hệ thống.");
                scrollMatrix.setContent(new Label("Không có dữ liệu vai trò."));
                return;
            }
            buildGrid(roleMap);
            lblThongBao.setText("Đã tải " + dsNhanVien.size() + " tài khoản từ cơ sở dữ liệu.");
        });
    }

    private void buildGrid(Map<Integer, String> roleMap) {
        dsMaVaiTroHeader = new ArrayList<>(roleMap.keySet());
        matrixMap = new java.util.HashMap<>();

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: white;");

        // Header
        Label lblHeader = new Label("NHÂN VIÊN / VAI TRÒ");
        lblHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #92400E; -fx-font-size: 14px;");
        grid.add(lblHeader, 0, 0);

        int col = 1;
        for (Integer maVT : dsMaVaiTroHeader) {
            Label lblRole = new Label(roleMap.get(maVT));
            lblRole.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-min-width: 100; -fx-text-fill: #1F2937;");
            grid.add(lblRole, col++, 0);
        }

        // Rows
        for (int row = 0; row < dsNhanVien.size(); row++) {
            NhanVienDTO nv = dsNhanVien.get(row);

            Label lblName = new Label(nv.getHoTen());
            lblName.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
            grid.add(lblName, 0, row + 1);

            List<CheckBox> checkBoxes = new ArrayList<>();
            for (int i = 0; i < dsMaVaiTroHeader.size(); i++) {
                int maVT = dsMaVaiTroHeader.get(i);
                CheckBox cb = new CheckBox();
                cb.setSelected(nv.getDanhSachMaVaiTro().contains(maVT));

                VBox cell = new VBox(cb);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-border-color: #F3F4F6; -fx-border-width: 0 0 1 0; -fx-padding: 5;");

                grid.add(cell, i + 1, row + 1);
                checkBoxes.add(cb);
            }
            matrixMap.put(String.valueOf(nv.getMaNV()), checkBoxes);
        }

        scrollMatrix.setContent(grid);
    }

    @FXML
    public void onLuuTatCa() {
        if (dsNhanVien == null || dsNhanVien.isEmpty()) {
            lblThongBao.setText("Không có dữ liệu để lưu.");
            return;
        }
        new Thread(() -> {
            try {
                for (NhanVienDTO nv : dsNhanVien) {
                    List<CheckBox> checkBoxes = matrixMap.get(String.valueOf(nv.getMaNV()));
                    if (checkBoxes == null) continue;
                    List<Integer> selectedRoleIds = new ArrayList<>();
                    for (int i = 0; i < dsMaVaiTroHeader.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            selectedRoleIds.add(dsMaVaiTroHeader.get(i));
                        }
                    }
                    nhanVienService.capNhatVaiTro(nv.getMaNV(), selectedRoleIds);
                }
                Platform.runLater(() -> lblThongBao.setText("Đã cập nhật phân quyền vào Cơ sở dữ liệu!"));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> lblThongBao.setText("Lỗi khi lưu: " + e.getMessage()));
            }
        }).start();
    }
}
