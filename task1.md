# Yêu cầu chỉnh sửa module Sản phẩm

## Mục tiêu

Gom các chức năng:

- Quản lý SP
- Danh mục SP
- Công thức
- Thành phần bánh tùy chỉnh

thành một module duy nhất tên là **"Sản phẩm"** trên Main Layout.

---

# Yêu cầu giao diện

## Main Layout

- Xóa các nút riêng lẻ:
  - Quản lý SP
  - Danh mục SP
  - Công thức
  - Thành phần bánh tùy chỉnh

- Thay bằng duy nhất 1 nút:
  - **Sản phẩm**

---

# Khi click nút "Sản phẩm"

Load một file FXML mới chứa `TabPane`.

Ví dụ:

```java
SanPhamTongQuanView.fxml