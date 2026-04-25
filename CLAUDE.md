# H3K Bakery Management System — Hướng dẫn cho Claude Code

## Tài liệu bắt buộc đọc trước
Trước khi code bất kỳ màn hình nào, đọc file `docs/documentation.md`.
File này mô tả layout, logic nghiệp vụ, props của từng màn hình từ bản
React gốc mà project đang chuyển sang JavaFX.

---

## Kiến trúc: MVP (Model — View — Presenter)

Đây là kiến trúc BẮT BUỘC. Không được thay đổi hoặc bỏ qua bất kỳ tầng nào.

```
Model (DTO + DAO + Service)
  ↑ ↓
Presenter  ←→  IView (interface)
                  ↑
              Controller (implements IView)
                  ↑
              View (FXML)
```

---

## Quy tắc từng tầng

### FXML (View)
- Chỉ chứa khai báo giao diện, KHÔNG có logic Java nào
- Mỗi màn hình có một file FXML riêng trong `resources/fxml/`
- Đặt tên theo màn hình: `LoginView.fxml`, `POSView.fxml`, ...

### Controller (implements IView)
Controller CHỈ được làm 3 việc:
1. Inject các thành phần UI bằng `@FXML`
2. Forward event từ UI lên Presenter
3. Implement các method của IView interface để cập nhật UI

```java
// ĐÚNG — Controller chỉ forward event
@FXML
private void onBtnLoginClicked() {
    presenter.onLoginClicked(tfUsername.getText(), pfPassword.getText());
}

// ĐÚNG — Controller chỉ implement IView để cập nhật UI
@Override
public void showErrorMessage(String message) {
    lblError.setText(message);
    lblError.setVisible(true);
}

// SAI — Controller KHÔNG ĐƯỢC chứa logic nghiệp vụ
@FXML
private void onBtnLoginClicked() {
    if (tfUsername.getText().isEmpty()) { ... }  // ← SAI, logic này phải ở Presenter
    User user = userService.login(...);           // ← SAI, gọi service phải ở Presenter
}
```

### IView (Interface)
- Mỗi màn hình có một interface riêng trong `views/interfaces/`
- Định nghĩa toàn bộ method mà Presenter dùng để điều khiển UI
- Đặt tên: `ILoginView`, `IPOSView`, `IDashboardView`, ...

```java
public interface ILoginView {
    void showErrorMessage(String message);
    void clearError();
    void navigateToDashboard(NhanVienDTO user);
    void setLoginButtonEnabled(boolean enabled);
}
```

### Presenter
- Nhận event từ View, gọi Service xử lý, rồi gọi method của IView để cập nhật UI
- KHÔNG import bất kỳ class JavaFX nào (Node, Label, Button, ...)
- KHÔNG trực tiếp thao tác UI
- Chứa toàn bộ logic nghiệp vụ của màn hình
- Đặt tên: `LoginPresenter`, `POSPresenter`, ...

```java
public class LoginPresenter {
    private final ILoginView view;
    private final NhanVienService nhanVienService;

    public LoginPresenter(ILoginView view, NhanVienService nhanVienService) {
        this.view = view;
        this.nhanVienService = nhanVienService;
    }

    public void onLoginClicked(String username, String password) {
        view.clearError();

        if (username.isBlank() || password.isBlank()) {
            view.showErrorMessage("Vui lòng nhập đầy đủ thông tin");
            return;
        }

        NhanVienDTO user = nhanVienService.login(username, password);
        if (user == null) {
            view.showErrorMessage("Sai tên đăng nhập hoặc mật khẩu");
            return;
        }

        view.navigateToDashboard(user);
    }
}
```

### Service
- Chứa logic nghiệp vụ phức tạp, điều phối giữa các DAO
- Không biết gì về UI

### DAO
- Chỉ gọi Stored Procedure hoặc truy vấn SQL
- Trả về DTO hoặc List<DTO>

### DTO
- Chỉ chứa thuộc tính + getter/setter
- Không có logic

---

## Cấu trúc thư mục chuẩn

```
src/main/java/com/bakery/
├── App.java
├── model/
│   ├── dto/          # SanPhamDTO, DonHangDTO, NhanVienDTO, ...
│   └── dao/          # SanPhamDAO, DonHangDAO, ...
├── services/         # SanPhamService, DonHangService, ...
├── presenters/       # LoginPresenter, POSPresenter, ...
├── views/
│   ├── interfaces/   # ILoginView, IPOSView, ...
│   └── controllers/  # LoginController, POSController, ...
└── utils/
    ├── DBConnect.java
    ├── AlertHelper.java
    ├── CurrencyFormatter.java
    └── Config.java

src/main/resources/
├── fxml/             # LoginView.fxml, POSView.fxml, ...
├── css/              # styles.css
├── reports/          # HoaDon.jrxml, PhieuHen.jrxml
└── images/
```

---

## Quy ước đặt tên

| Tầng | Pattern | Ví dụ |
|---|---|---|
| FXML | `{TenManHinh}View.fxml` | `POSView.fxml` |
| Interface | `I{TenManHinh}View` | `IPOSView` |
| Controller | `{TenManHinh}Controller` | `POSController` |
| Presenter | `{TenManHinh}Presenter` | `POSPresenter` |
| Service | `{TenNghiepVu}Service` | `DonHangService` |
| DAO | `{TenNghiepVu}DAO` | `DonHangDAO` |
| DTO | `{TenNghiepVu}DTO` | `DonHangDTO` |

---

## Checklist trước khi tạo một màn hình mới

Phải tạo đủ 4 file sau, theo đúng thứ tự:
- [ ] `IXxxView.java` — interface định nghĩa contract
- [ ] `XxxView.fxml` — layout giao diện
- [ ] `XxxController.java implements IXxxView` — kết nối FXML ↔ Presenter
- [ ] `XxxPresenter.java` — toàn bộ logic màn hình

Nếu thiếu bất kỳ file nào, hãy tạo đủ trước khi tiếp tục.

---

## Giao diện

- Ngôn ngữ hiển thị: Tiếng Việt
- Màu primary: `#92400e` (amber-800), `#78350f` (amber-900)
- Màu nền chính: `#FDFBF7`
- Màu thành công: `#16a34a`
- Màu nguy hiểm: `#dc2626`
- Màu cảnh báo: `#d97706`
- Màu thông tin: `#2563eb`
- Format tiền: `1.500.000 ₫` (dùng `CurrencyFormatter.format()`)

---

## Lưu ý quan trọng

1. **Presenter không import JavaFX** — nếu thấy `import javafx.*` trong Presenter là sai
2. **Controller không gọi Service** — nếu thấy `service.xxx()` trong Controller là sai
3. **Logic không nằm trong FXML** — không dùng `onAction` trực tiếp trong FXML để xử lý logic
4. **Mỗi màn hình độc lập** — Presenter của màn hình này không gọi Presenter của màn hình khác
5. **Navigation** — việc chuyển màn hình do `App.java` hoặc `MainController` xử lý, View chỉ gọi `view.navigateTo...()`
