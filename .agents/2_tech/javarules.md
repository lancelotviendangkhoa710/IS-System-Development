# QUY TẮC JAVA — Chi tiết kỹ thuật
> Core rules (stack, MVP, naming, null guard) đã có trong `core_compact.md`. File này bổ sung phần **đặc thù kỹ thuật** chưa có.

## JavaFX — Kỹ thuật UI
- **FXML & Controller:** Tách riêng `.fxml` + Controller. Dùng `@FXML` bind component và sự kiện.
- **Data Binding:** Ưu tiên `ObservableList`, `StringProperty`, `IntegerProperty` để tự động cập nhật TableView/ListView.
- **Format:** `NumberFormat` (tiền tệ) · `DateTimeFormatter` (ngày tháng).
- **Thread:** Tác vụ DB/API nặng → `Task` hoặc `Service` JavaFX (tránh UI Freeze). Update UI từ thread phụ → `Platform.runLater()`.

## DAO — Pattern xử lý Null từ ResultSet
```java
// Timestamp / Date
if (rs.getTimestamp("THOIDIEM") != null) {
    dto.setThoiDiem(rs.getTimestamp("THOIDIEM").toLocalDateTime());
}

// Wrapper class (Integer, Double)
int val = rs.getInt("COT");
if (!rs.wasNull()) {
    dto.setGiaTri(val);
}
```

## Naming — Ngoại lệ hợp lệ (bổ sung core_compact)
- **Prefix động từ Anh được phép:** `get`, `set`, `is`, `add`, `update`, `delete`, `create`, `find` đi kèm tên Việt.  
  VD: `getSanPham()` ✅ · `updateKhachHang()` ✅
- **Tên File/Class hệ thống:** File thuộc tầng kiến trúc ĐƯỢC phép tiếng Anh để giữ chuẩn framework.  
  VD: `LoginViewFXMLController` ✅ · `AuthService` ✅