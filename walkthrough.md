# Walkthrough - Thay thế Mã nguyên liệu bằng Số lượng tồn trên Màn hình kiểm kê

Tôi đã hoàn thành việc sửa đổi giao diện màn hình Kiểm kê kho (Quản lý nguyên liệu), thay thế hoàn toàn cột "Mã nguyên liệu" bằng cột "Số lượng tồn" thực tế định dạng kèm đơn vị tính tương ứng.

Dưới đây là chi tiết các thay đổi, kết quả biên dịch và báo cáo hậu kiểm.

---

## Các Thay đổi đã Thực hiện (Changes Made)

### 1. Giao diện FXML
#### [MODIFY] [NguyenLieuView.fxml](file:///d:/Clone/src/main/resources/fxml/kho/NguyenLieuView.fxml)
- Loại bỏ khai báo cột cũ: `<TableColumn fx:id="colMaNL" text="Mã" prefWidth="55"/>`.
- Thêm cột mới: `<TableColumn fx:id="colSoLuongTon" text="Số lượng tồn" prefWidth="110"/>`.

### 2. Tầng View (Java Controller)
#### [MODIFY] [NguyenLieuViewFXMLController.java](file:///d:/Clone/src/main/java/com/bakery/views/controllers/kho/NguyenLieuViewFXMLController.java)
- Khai báo cột mới thay cho cột cũ:
  ```java
  @FXML private TableColumn<NguyenLieuDTO, Double> colSoLuongTon;
  ```
- Định cấu hình liên kết dữ liệu và hiển thị định dạng số lượng kèm theo đơn vị tính (UoM) trong hàm `setupTable()`:
  ```java
  colSoLuongTon.setCellValueFactory(c ->
          new SimpleDoubleProperty(c.getValue().getSoLuongTonTong() != null ? c.getValue().getSoLuongTonTong() : 0.0).asObject());
  colSoLuongTon.setCellFactory(tc -> new TableCell<>() {
      @Override
      protected void updateItem(Double val, boolean empty) {
          super.updateItem(val, empty);
          if (empty || val == null) { setText(null); return; }
          NguyenLieuDTO nl = getTableRow() != null
                  ? (NguyenLieuDTO) getTableRow().getItem() : null;
          String dvt = (nl != null && !nl.getTenDVT().isEmpty())
                  ? " " + nl.getTenDVT() : "";
          setText(val % 1 == 0
                  ? String.valueOf((long)(double)val) + dvt
                  : val + dvt);
      }
  });
  ```

### 3. Tài liệu Báo cáo màn hình
#### [MODIFY] [baocao_kiem_ke_kho_nguyen_lieu.md](file:///d:/Clone/baocao/baocao_kiem_ke_kho_nguyen_lieu.md)
- Cập nhật mục **STT 5** trong bảng đối tượng từ `colMaNL` thành `colSoLuongTon` (Cột số lượng tồn của nguyên liệu).
#### [MODIFY] [baocao_nguyenlieu.md](file:///d:/Clone/baocao/baocao_nguyenlieu.md)
- Cập nhật bảng đối tượng từ `colMaNL` thành `colSoLuongTon` tương ứng để đồng bộ tài liệu đặc tả.

---

## Kết quả Hậu kiểm & Xác minh (Validation & Testing)

### 1. Biên dịch dự án (Maven Compile)
- Đã chạy thành công lệnh `mvn compile` mà không gặp bất kỳ lỗi hay cảnh báo nào:
  ```bash
  mvn compile
  # BUILD SUCCESS
  ```

### 2. Quét thay đổi (GitNexus Detect Changes)
- Chạy `gitnexus_detect_changes()` xác nhận chỉ có 4 file liên quan đến màn hình nguyên liệu và báo cáo bị ảnh hưởng trực tiếp, rủi ro đánh giá ở mức **LOW** đối với các symbol hệ thống.

---

## ✅ REVIEW AGENT REPORT

- **Task**: Thay thế Mã nguyên liệu bằng Số lượng tồn trên Màn hình kiểm kê
- **Files checked**:
  - `src/main/resources/fxml/kho/NguyenLieuView.fxml`
  - `src/main/java/com/bakery/views/controllers/kho/NguyenLieuViewFXMLController.java`
  - `baocao/baocao_kiem_ke_kho_nguyen_lieu.md`
  - `baocao/baocao_nguyenlieu.md`

- **Lỗi đã tự fix**: Không có (mã nguồn tuân thủ tốt các quy chuẩn MVP, không có style inline hoặc vi phạm kiến trúc).
- **Cảnh báo (cần User xác nhận)**: Không có.
- **GitNexus Risk**: LOW
- **Changed symbols**:
  - Touched: `NguyenLieuViewFXMLController`
  - Removed: `colMaNL`
  - Added: `colSoLuongTon`
- **Affected processes**: Không có luồng xử lý (execution flows) nào bị phá vỡ vì đây hoàn toàn là thay đổi hiển thị ở tầng View.

**Status: ✅ CLEAN**
