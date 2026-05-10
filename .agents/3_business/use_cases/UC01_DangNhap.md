# UC01 — Đăng nhập (Login)

> **Liên quan:** Xem [UC02_DangXuat.md](UC02_DangXuat.md) cho luồng đăng xuất.

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Đăng nhập |
| **Mô tả** | Nhân viên xác thực thông tin, hệ thống tạo token trong bảng `ACCOUNT_TOKEN` (hiệu lực 12h) và khởi động Watchdog giám sát phiên. |
| **Actors** | Nhân viên (mọi vai trò) |
| **Tiền điều kiện** | Tài khoản tồn tại trong `TAIKHOAN`, trạng thái hoạt động (`TRANGTHAITK = 1`, `TRANGTHAILAMVIEC = 1`) |
| **Hậu điều kiện** | Bản ghi `ACCOUNT_TOKEN` được tạo, `UserSession.currentToken` cập nhật, `SessionContext.AuthSession` active, Watchdog chạy |

## Luồng sự kiện chính — Đăng nhập

1. Nhân viên nhập tên đăng nhập và mật khẩu → bấm **Đăng nhập**.
2. `DangNhapViewFXMLController xử lý` tạo `Task<NhanVienDTO>` chạy background.
3. `XacThucService xử lý` kiểm tra:
   - Tên đăng nhập & mật khẩu không rỗng.
   - `NHANVIEN.TRANGTHAILAMVIEC = 1` và `TAIKHOAN.TRANGTHAITK = 1`.
   - bcrypt verify mật khẩu.
   - Lấy phân quyền hợp nhất qua `PhanQuyenDAO`.
4. Tạo `SessionContext.AuthSession` (in-memory).
5. Resolve `MATAIKHOAN` từ `NhanVienDAO xử lý`.
6. `AccountTokenDAO xử lý` — dọn token cũ.
7. `AccountTokenDAO xử lý` — tạo `ACCOUNT_TOKEN` mới, hết hạn +1 ngày (≥12h ca dài nhất).
8. Lưu `token` vào `UserSession.currentToken` và `NhanVienDTO` vào `UserSession.currentUser`.
9. UI chuyển về `MainMenuView` (hoặc `MoCaView` nếu là Thu ngân chưa mở ca).
10. `SessionWatchdogService` bắt đầu giám sát mỗi 30 giây.

## Luồng sự kiện chính — Đăng xuất

1. Nhân viên bấm **Đăng xuất** trên sidebar.
2. `MainMenuViewFXMLController xử lý`:
   - Dừng Watchdog ngay lập tức.
   - Hiển thị loading `lblThongBao = "Đang đăng xuất..."`.
   - Tạo `Task<Void>` chạy background → `XacThucService xử lý`.
3. `XacThucService xử lý`:
   - `AccountTokenDAO xử lý` → `UPDATE ACCOUNT_TOKEN SET IS_REVOKED='Y' WHERE TOKEN_VALUE=?`
   - `UserSession xử lý` + `SessionContext xử lý`.
4. `Platform xử lý` → `chuyenVeDangNhap()` load lại `DangNhapView.fxml`.

## Luồng lỗi

| Bước | Lỗi | Xử lý |
|:---|:---|:---|
| 3 | Sai tên/mật khẩu | `NgoaiLeXacThuc.THONG_TIN_DANG_NHAP_SAI` → hiển thị label lỗi |
| 3 | Tài khoản bị khóa | `NgoaiLeXacThuc.TAI_KHOAN_BI_KHOA` → hiển thị label lỗi |
| 7 | DB lỗi insertToken | Exception propagate → View hiển thị lỗi hệ thống |
| 3 (Logout) | DB lỗi revokeToken | Vẫn xóa session local + về màn đăng nhập (fail-safe) |

## Luồng Watchdog

- Mỗi 30s: `AccountTokenDAO xử lý` → check `IS_REVOKED = 'N'` và `EXPIRES_AT >= SYSDATE`.
- Nếu token bị revoke từ ngoài hoặc hết hạn: `SessionContext xử lý` + `UserSession xử lý` + Alert + về màn đăng nhập.

```mermaid
flowchart TD
    subgraph "Người dùng"
        A[Nhập tên/mật khẩu] --> B[Bấm Đăng nhập]
        Z[Bấm Đăng xuất]
    end

    subgraph "Hệ thống - FX Thread"
        B --> C{Validate rỗng?}
        C -->|Có| E[Hiện lỗi label]
        C -->|Không| D[Tạo Task bg]
        D --> F[setOnSucceeded / setOnFailed]
        Z --> Z1[Dừng Watchdog]
        Z1 --> Z2[lblThongBao = Loading...]
        Z2 --> Z3[Tạo Task bg logout]
        Z3 --> Z4[Platform.runLater → chuyenVeDangNhap]
    end

    subgraph "Background Thread"
        D --> G[XacThucService.dangNhap]
        G --> G2{Xác thực OK?}
        G2 -->|Không| H[Ném Exception]
        G2 -->|Có| I[AccountTokenDAO.insertToken]
        I --> J[UserSession.setCurrentToken]
        Z3 --> L[XacThucService.dangXuat]
        L --> M[AccountTokenDAO.revokeToken]
        M --> N[UserSession.clear]
    end

    subgraph "CSDL - ACCOUNT_TOKEN"
        I --> DB1[(INSERT TOKEN_VALUE, EXPIRES_AT, IS_REVOKED='N')]
        M --> DB2[(UPDATE IS_REVOKED='Y')]
    end
```


