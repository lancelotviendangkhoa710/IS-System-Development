# UC10 — Xem Lịch Sử Mua Hàng Và Xuất Báo Cáo

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Xem lịch sử mua hàng và xuất báo cáo |
| **Mô tả Use-case** | Nhân viên quản lý xem toàn bộ lịch sử đơn hàng của một khách hàng cụ thể, với thống kê tổng chi tiêu và xuất báo cáo ra định dạng Excel hoặc PDF |
| **Actors** | Nhân viên quản lý khách hàng |
| **Tiền điều kiện** | Đã đăng nhập hệ thống; màn hình Quản lý Khách hàng đang mở; có ít nhất một khách hàng trong danh sách |
| **Hậu điều kiện** | Dialog lịch sử hiển thị đầy đủ thông tin đơn hàng; nếu xuất file thì file Excel/PDF được lưu tại vị trí người dùng chọn |
| **Luồng sự kiện chính** | 1. Nhân viên nhấn nút **📋 Lịch sử** trên dòng khách hàng trong bảng danh sách <br>2. Hệ thống gọi Presenter → Service → DAO truy vấn `VW_DanhSachDonHang` theo `MAKH` <br>3. Dialog `LichSuMuaHangDialog` mở ra (900×560, modal) hiển thị: <br>&nbsp;&nbsp;- Thẻ thống kê: Tổng đơn, Tổng chi tiêu, Đã cọc <br>&nbsp;&nbsp;- Bảng 7 cột: Mã đơn, Ngày nhận, Trạng thái, Hình thức, Tổng tiền, Đã cọc, Còn lại <br>4a. Nhân viên nhấn **📥 Xuất Excel** → Hộp thoại lưu file → lưu `.xlsx` <br>4b. Nhân viên nhấn **📄 Xuất PDF** → Hộp thoại lưu file → lưu `.pdf` <br>5. Nhân viên nhấn **✕ Đóng** |
| **Luồng sự kiện phụ** | 2a. Khách hàng chưa có đơn nào → bảng hiển thị placeholder "Khách hàng chưa có lịch sử giao dịch." <br>4a-1. Người dùng hủy hộp thoại lưu → không thực hiện xuất |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 2b. Lỗi kết nối DB → Presenter bắt exception, gọi `view xử lý`, label `lblThongBao` hiển thị thông báo đỏ <br>4c. Lỗi ghi file (quyền, đĩa đầy) → label `lblThongBao` trong dialog hiển thị "❌ Lỗi xuất file: ..." |

---

## Activity Diagram

```mermaid
flowchart TD
    subgraph User["Người dùng"]
        A([Nhấn nút Lịch sử]) --> E{Chọn hành động}
        E --> |Xuất Excel| G([Chọn đường dẫn .xlsx])
        E --> |Xuất PDF| H([Chọn đường dẫn .pdf])
        E --> |Đóng| Z([Kết thúc])
        G --> G2([Lưu file])
        H --> H2([Lưu file])
    end

    subgraph System["Hệ thống"]
        A --> B{Khách hàng hợp lệ?}
        B --> |Không| ERR1([Hiển thị lỗi])
        B --> |Có| C[Tải danh sách đơn - background Task]
        C --> D{Có dữ liệu?}
        D --> |Không| EMPTY([Hiển thị placeholder])
        D --> |Có| F[Mở dialog - tính thống kê]
        F --> E
        G --> |onXuatExcel| EXCEL[Ghi .xlsx bằng Apache POI]
        H --> |onXuatPdf| PDF[Ghi .pdf bằng PDFBox]
        EXCEL --> |Thành công| OK1([lblThongBao: Đã xuất Excel])
        PDF --> |Thành công| OK2([lblThongBao: Đã xuất PDF])
        EXCEL --> |Lỗi| ERR2([lblThongBao: Lỗi xuất file])
        PDF --> |Lỗi| ERR2
    end

    subgraph DB["CSDL"]
        C --> |SELECT từ VW_DanhSachDonHang WHERE MAKH=?| DB1[(Oracle DB)]
        DB1 --> C
    end
```
