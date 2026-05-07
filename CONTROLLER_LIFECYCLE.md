# 🔄 VÒNG ĐỜI VÀ GIAO TIẾP CONTROLLER TRONG APPSHELL

Kiến trúc AppShell sử dụng một mô hình phân tầng để quản lý giao diện:

## 1. Phân tầng Controller
1. **AppShellController (Gốc):** Quản lý toàn bộ cửa sổ ứng dụng và việc nạp các module.
2. **MainMenuViewFXMLController (Vỏ):** Quản lý Sidebar và Header (được nhúng vào AppShell).
3. **Child Controllers (Module):** Quản lý nội dung nghiệp vụ cụ thể (POS, Kho, Nhân sự...).

## 2. Luồng nạp View
Khi một mục menu được click:
1. `MainMenuViewFXMLController` gọi `loadView(path)`.
2. `FXMLLoaderUtil` nạp tệp FXML và khởi tạo Controller của module đó.
3. Nội dung (Node) được đưa vào `contentArea` (StackPane) của Shell.
4. Controller cũ của module trước đó sẽ được JavaFX giải phóng (GC) vì không còn nằm trong Scene Graph.

## 3. Giao tiếp giữa các Controller
- **Module con -> Shell:** Sử dụng `AppShellController.getInstance()`.
- **Shell -> Module con:** Hiếm khi cần. Nếu cần, có thể lấy controller từ `FXMLLoader` trong `FXMLLoaderUtil`.
- **Dữ liệu dùng chung:** Sử dụng `UserSession` hoặc các Service lớp Singleton.

## 4. Lưu ý về Memory
- Không giữ các tham chiếu tĩnh (static) đến các Node hoặc Controller của module con để tránh memory leak.
- Các listener trên Stage/Scene nên được dọn dẹp trong phương thức cleanup (nếu module con có cơ chế này).
