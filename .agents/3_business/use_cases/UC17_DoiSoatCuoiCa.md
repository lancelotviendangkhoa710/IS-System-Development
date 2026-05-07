# UC17: Đối soát tiền cuối ca

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Đối soát tiền cuối ca |
| **Mô tả Use-case** | Thu ngân thực hiện kiểm đếm tiền mặt thực tế tại quầy và đối chiếu với doanh thu lý thuyết trên hệ thống trước khi đóng ca làm việc. |
| **Actors** | Thu ngân, Quản lý cửa hàng |
| **Tiền điều kiện** | Thu ngân đang trong ca làm việc đã được mở. |
| **Hậu điều kiện** | Ca làm việc được đóng, thông tin chênh lệch (nếu có) được ghi nhận và lưu vào sổ quỹ. |
| **Luồng sự kiện chính** | 1. Thu ngân chọn chức năng "Kết ca/Đóng ca".<br>2. Hệ thống gọi `FUNC_TinhTienMatLyTuong` để tính số tiền dựa trên (Tiền đầu ca + Thu - Chi).<br>3. Hệ thống yêu cầu Thu ngân nhập số tiền mặt thực tế đang có.<br>4. Thu ngân nhập số tiền và nhấn "Xác nhận".<br>5. Hệ thống so sánh tiền thực tế và tiền lý tưởng.<br>6. Nếu khớp, hệ thống tiến hành đóng ca.<br>7. Hệ thống cập nhật trạng thái ca làm việc và ghi log vào sổ quỹ. |
| **Luồng sự kiện phụ** | 5a. Tiền thực tế lệch so với hệ thống: Hệ thống yêu cầu nhập lý do giải trình.<br>5b. Thu ngân xem lại lịch sử các giao dịch thu chi trong ca trước khi đóng. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 5a1. Thu ngân không nhập lý do giải trình khi lệch tiền: Hệ thống chặn đóng ca.<br>7a. Lỗi kết nối DB: Hệ thống thông báo và giữ nguyên trạng thái ca. |

## Activity Diagram

```mermaid
activityDiagram
    autonumber
    swimlane Người dùng
    start
    :Chọn chức năng Đóng ca;
    
    swimlane Hệ thống
    :Tính toán doanh thu lý thuyết (Ẩn);
    :Yêu cầu nhập tiền thực tế;
    
    swimlane Người dùng
    :Nhập số tiền mặt thực tế;
    :Nhấn Xác nhận;
    
    swimlane Hệ thống
    :So sánh tiền thực tế vs Lý thuyết;
    if (Tiền khớp?) then (Có)
        :Tiến hành đóng ca;
    else (Không)
        swimlane Người dùng
        :Nhập lý do giải trình chênh lệch;
        swimlane Hệ thống
        :Ghi nhận lý do;
        :Tiến hành đóng ca;
    endif
    
    swimlane CSDL
    :Cập nhật bảng CALAMVIEC (TrangThai=0);
    :Lưu giao dịch đối soát vào SOQUY;
    return Thành công;
    
    swimlane Hệ thống
    :Thông báo kết ca thành công;
    stop
```
