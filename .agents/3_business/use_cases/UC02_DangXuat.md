# UC02 — Đăng xuất (Logout)

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Đăng xuất |
| **Mô tả Use-case** | Nhân viên kết thúc phiên làm việc. Hệ thống thu hồi token trong `ACCOUNT_TOKEN` (`IS_REVOKED = 'Y'`), xóa dữ liệu phiên khỏi bộ nhớ, và điều hướng về màn hình đăng nhập. |
| **Actors** | Nhân viên (mọi vai trò đang đăng nhập) |
| **Tiền điều kiện** | Nhân viên đã đăng nhập, `UserSession.currentToken` khác null, `SessionContext` đang có phiên hợp lệ |
| **Hậu điều kiện** | `ACCOUNT_TOKEN.IS_REVOKED = 'Y'`, `UserSession` và `SessionContext` đã bị xóa, ứng dụng trở về `DangNhapView` |

---

## Luồng sự kiện chính

| Bước | Actor | Hành động |
|:-----|:------|:----------|
| 1 | Nhân viên | Bấm nút **Đăng xuất** trên sidebar |
| 2 | Hệ thống | `MainMenuViewFXMLController xử lý` dừng `SessionWatchdogService` ngay lập tức |
| 3 | Hệ thống | `lblThongBao = "Đang đăng xuất..."` — hiển thị trạng thái loading |
| 4 | Hệ thống | Tạo `Task<Void>` và khởi chạy trên background thread |
| 5 | Hệ thống | `XacThucService xử lý` lấy `token` từ `UserSession xử lý` |
| 6 | Hệ thống | `AccountTokenDAO xử lý` → `UPDATE ACCOUNT_TOKEN SET IS_REVOKED = 'Y' WHERE TOKEN_VALUE = ?` |
| 7 | Hệ thống | `UserSession xử lý` + `SessionContext xử lý` — xóa toàn bộ phiên in-memory |
| 8 | Hệ thống | `Platform xử lý` → `chuyenVeDangNhap()` |
| 9 | Hệ thống | Load `DangNhapView.fxml`, gọi `setLoginInfo("Bạn đã đăng xuất thành công.")` |
| 10 | Hệ thống | `Stage` chuyển cảnh về màn hình đăng nhập |

---

## Luồng sự kiện phụ

**2a — Watchdog phát hiện token bị revoke từ bên ngoài (force logout):**

| Bước | Hành động |
|:-----|:----------|
| 2a.1 | `SessionWatchdogService` check mỗi 30s → `AccountTokenDAO xử lý` trả về `IS_REVOKED = 'Y'` hoặc `null` |
| 2a.2 | `succeeded()` callback: `Platform xử lý` → `SessionContext xử lý` + `UserSession xử lý` |
| 2a.3 | Hiện `Alert.WARNING` — "Phiên đăng nhập đã bị thu hồi..." |
| 2a.4 | Watchdog tự hủy (`cancel()`), load `DangNhapView.fxml` |

---

## Luồng sự kiện lỗi

| Bước lỗi | Mô tả | Xử lý |
|:---------|:------|:------|
| 6 (DB lỗi) | `AccountTokenDAO xử lý` ném exception (mất kết nối) | `taskDangXuat xử lý` vẫn gọi `UserSession xử lý` + `SessionContext xử lý` + `chuyenVeDangNhap()` (**fail-safe**) |
| 5 (token null) | `UserSession xử lý` = null (session lỗi) | `XacThucService xử lý` bỏ qua bước revoke DB, vẫn xóa session local |
| 9 (FXML lỗi) | Không tìm thấy `DangNhapView.fxml` | `lblThongBao` hiện thông báo lỗi, ứng dụng không treo |

---

## Activity Diagram

```mermaid
flowchart TD
    subgraph Nguoi_Dung["Người dùng"]
        A([Bấm Đăng xuất])
    end

    subgraph FX_Thread["Hệ thống"]
        B[Dừng SessionWatchdog]
        C[lblThongBao = Đang đăng xuất...]
        D[Tạo Task background]
        K{Task thành công?}
        L[Platform.runLater → chuyenVeDangNhap]
        M[UserSession.clear + SessionContext.clear]
        N[chuyenVeDangNhap - fail-safe]
        P([Load DangNhapView\nsetLoginInfo thành công])
        Q([lblThongBao = Lỗi đăng xuất])
    end

    subgraph BG_Thread["Hệ thống"]
        E[XacThucService.dangXuat]
        F{Token khác null?}
        G[AccountTokenDAO.revokeToken]
        H[UserSession.clear]
        I[SessionContext.clear]
    end

    subgraph CSDL["CSDL — ACCOUNT_TOKEN"]
        J[(UPDATE IS_REVOKED = 'Y'\nWHERE TOKEN_VALUE = ?)]
    end

    A --> B --> C --> D --> E
    E --> F
    F -->|Có| G --> J --> H --> I
    F -->|Không| H
    I --> K
    K -->|Thành công| L --> P
    K -->|Thất bại| M --> N --> P
```

---


