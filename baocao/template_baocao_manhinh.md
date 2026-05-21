# KHUÔN MẪU — Báo cáo mô tả màn hình

> **Mục đích:** Template chuẩn cho báo cáo đồ án. Dùng cho mọi conversation sau.  
> **Lưu trữ:** Tất cả báo cáo màn hình lưu tại `D:\Clone\baocao\`

---

## QUY TẮC BẮT BUỘC

1. **Chỉ mô tả những gì ĐANG HIỂN THỊ trên màn hình.** Không liệt kê panel ẩn, sub-panel ẩn, hay các trạng thái khác của màn hình.
2. **Agent đánh số STT trước** → User chụp screenshot và đánh số `(1), (2), (3)...` lên hình theo bảng.
3. **Bảng phẳng** — không phân nhóm, không header phụ. Tất cả đối tượng nằm chung 1 bảng.
4. **Ràng buộc** — để **trống** khi không có. Chỉ điền quy tắc nghiệp vụ tự nhiên (VD: "Không được để trống", "Phải trùng khớp mật khẩu mới").
5. **Mô tả ngắn gọn** — cụm danh từ hoặc 1 câu ngắn. Không viết dài.
6. **Biến cố** — dùng mẫu câu: `Chọn button [tên]`, `Khởi tạo màn hình`, `Chọn dòng trên [tên bảng]`.
7. **Xử lý** — ngôn ngữ tự nhiên. Nếu có if/else, gạch đầu dòng với `Nếu [điều kiện] →`.
8. **Lưu file** — Tên file: `baocao_[tên màn hình].md`, lưu vào `D:\Clone\baocao\`.
9. **Không ghi các dòng metadata không cần thiết ở đầu tệp** — loại bỏ hoàn toàn các dòng như Màn hình, File FXML chính, File FXML tab, Controller, Truy cập từ... Tệp báo cáo bắt đầu trực tiếp bằng Tiêu đề chính dạng `# Màn hình [Tên màn hình]` và đi thẳng vào các bảng dữ liệu.
10. **Không sử dụng icon hoặc emoji** — loại bỏ hoàn toàn các biểu tượng cảm xúc, ký hiệu đồ họa hoặc icon trang trí trong toàn bộ nội dung tệp báo cáo.

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `[fx:id]` | [Kiểu] | | [Mô tả ngắn] |
| ... | ... | ... | ... | ... |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Thiết lập các control về trạng thái mặc định. | |
| 2 | Chọn button `[btnTen]` | [Xử lý].<br>- Nếu [điều kiện] → [xử lý]. | |
| ... | ... | ... | ... |
