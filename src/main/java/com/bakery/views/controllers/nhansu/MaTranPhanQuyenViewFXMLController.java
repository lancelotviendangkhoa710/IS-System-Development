package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller cho MaTranPhanQuyenView.
 * Hỗ trợ hiển thị Ma trận Nhân viên - Vai trò.
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
        // Chạy load dữ liệu trong một thread riêng để tránh treo UI và dễ bắt lỗi
        new Thread(() -> {
            try {
                System.out.println("[DEBUG] Bắt đầu load ma trận phân quyền...");
                loadMatrixData();
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> lblThongBao.setText("Lỗi kết nối DB: " + e.getMessage()));
            }
        }).start();
    }

    private void loadMatrixData() throws Exception {
        dsNhanVien = nhanVienService.layTatCaNhanVien();
        Map<Integer, String> roleMap = nhanVienService.layDanhSachVaiTro();
        
        System.out.println("[DEBUG] Tìm thấy " + dsNhanVien.size() + " nhân viên và " + roleMap.size() + " vai trò.");

        Platform.runLater(() -> {
            try {
                buildGrid(roleMap);
                lblThongBao.setText("Đã tải " + dsNhanVien.size() + " tài khoản.");
            } catch (Exception e) {
                e.printStackTrace();
                lblThongBao.setText("Lỗi hiển thị: " + e.getMessage());
            }
        });
    }

    private void buildGrid(Map<Integer, String> roleMap) {
        dsMaVaiTroHeader = new ArrayList<>(roleMap.keySet());
        matrixMap = new HashMap<>();

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: white;"); // Đảm bảo nền trắng để thấy text

        // 1. Header
        Label lblHeader = new Label("NHÂN VIÊN / VAI TRÒ");
        lblHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #92400E;");
        grid.add(lblHeader, 0, 0);

        int col = 1;
        for (Integer maVT : dsMaVaiTroHeader) {
            Label lblRole = new Label(roleMap.get(maVT));
            lblRole.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-min-width: 100;");
            grid.add(lblRole, col++, 0);
        }

        // 2. Rows
        if (dsNhanVien.isEmpty()) {
            grid.add(new Label("(Danh sách nhân viên trống)"), 0, 1);
        } else {
            for (int row = 0; row < dsNhanVien.size(); row++) {
                NhanVienDTO nv = dsNhanVien.get(row);
                
                Label lblName = new Label(nv.getHoTen());
                lblName.setStyle("-fx-font-weight: bold;");
                grid.add(lblName, 0, row + 1);

                List<CheckBox> checkBoxes = new ArrayList<>();
                for (int i = 0; i < dsMaVaiTroHeader.size(); i++) {
                    int maVT = dsMaVaiTroHeader.get(i);
                    CheckBox cb = new CheckBox();
                    cb.setSelected(nv.getDanhSachMaVaiTro().contains(maVT));
                    
                    VBox cell = new VBox(cb);
                    cell.setAlignment(Pos.CENTER);
                    grid.add(cell, i + 1, row + 1);
                    checkBoxes.add(cb);
                }
                matrixMap.put(String.valueOf(nv.getMaNV()), checkBoxes);
            }
        }

        scrollMatrix.setContent(grid);
    }

    @FXML
    public void onLuuTatCa() {
        new Thread(() -> {
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
                    nhanVienService.capNhatVaiTro(nv.getMaNV(), selectedRoleIds);
                    count++;
                }
                final int finalCount = count;
                Platform.runLater(() -> lblThongBao.setText("Đã lưu phân quyền cho " + finalCount + " tài khoản!"));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> lblThongBao.setText("Lỗi khi lưu: " + e.getMessage()));
            }
        }).start();
    }
}
