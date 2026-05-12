# Use Case Index — Bakery Management System
> Danh sách chính thức. Khi implement: đọc file UC tương ứng trong `use_cases/`.
> Cần phân quyền chi tiết hoặc mô tả đầy đủ: đọc `agent_use_cases.md`.

## I. Quản trị & Nhân sự
| UC | Tên | Mô tả ngắn |
|---|---|---|
| UC01 | Đăng nhập | Xác thực danh tính, điều hướng màn hình |
| UC02 | Đăng xuất | Kết thúc phiên làm việc |
| UC03 | Đổi mật khẩu | Nhân viên tự đổi mật khẩu cá nhân |
| UC04 | Thêm nhân sự | Tạo hồ sơ + cấp tài khoản nhân viên mới |
| UC05 | Sửa nhân sự | Cập nhật thông tin, vị trí, chức vụ |
| UC06 | Cập nhật trạng thái nhân sự | Vô hiệu hóa / kích hoạt tài khoản |
| UC07 | Tra cứu nhân sự | Tìm kiếm thông tin nhân viên |
| UC08 | Phân quyền | Cấp / giới hạn quyền theo vai trò |
| UC09 | Xem nhật ký hoạt động | Lịch sử thao tác nhạy cảm (ai, khi nào, nội dung) |
| UC58 | Tối ưu đăng xuất thu ngân & RBAC | Ép đóng ca trước logout và ẩn module nhạy cảm theo vai trò |

## II. Quản lý Khách hàng
| UC | Tên | Mô tả ngắn |
|---|---|---|
| UC10 | Thêm khách hàng | Ghi nhận thành viên mới (tên, SĐT) |
| UC11 | Sửa khách hàng | Cập nhật thông tin liên lạc |
| UC12 | Cập nhật trạng thái KH | Vô hiệu hóa hồ sơ, giữ lịch sử |
| UC13 | Tra cứu khách hàng | Tìm theo tên hoặc SĐT |
| UC14 | Lịch sử mua hàng | Xem danh sách hóa đơn của khách |
| UC15 | Cấu hình hạng thành viên | Thiết lập tỷ lệ tích điểm, quy tắc nâng hạng |

## III. Bán hàng & Đơn hàng
| UC | Tên | Mô tả ngắn |
|---|---|---|
| UC16 | Lập hóa đơn bán lẻ | Chọn món, tính tiền, áp KM, in hóa đơn tại quầy |
| UC17 | Hủy hóa đơn bán lẻ | Hủy HĐ đã TT, nhập lý do, hoàn kho, cập nhật doanh thu |
| UC18 | Lập đơn bánh tùy chỉnh | Chọn mẫu, phụ kiện, tính TG chuẩn bị, thu cọc |
| UC19 | Cập nhật trạng thái đơn | Chuyển trạng thái đơn hàng |
| UC20 | Tra cứu danh sách đơn | Lọc theo ngày giao hoặc trạng thái |
| UC21 | Hủy đơn và hoàn cọc | Hủy trước ngày giao, giải phóng năng lực, hoàn cọc |
| UC22 | Mở ca làm việc | Ghi nhận tiền mặt đầu ca |
| UC23 | Đóng ca và đối soát | Đếm tiền thực tế, đối chiếu doanh thu, chốt sổ |

## IV. Quản lý Danh mục
| UC | Tên | Mô tả ngắn |
|---|---|---|
| UC24 | Thêm danh mục SP | Tạo nhóm phân loại mới |
| UC25 | Sửa danh mục SP | Đổi tên / mô tả nhóm |
| UC26 | Cập nhật trạng thái danh mục | Khóa nhóm, giữ liên kết dữ liệu cũ |
| UC27 | Thêm sản phẩm | Đưa bánh mới vào thực đơn |
| UC28 | Sửa sản phẩm | Cập nhật giá, ảnh, mô tả |
| UC29 | Cập nhật trạng thái SP | Khóa / mở sản phẩm trên màn hình bán |
| UC30 | Tra cứu sản phẩm | Tìm kiếm giá, ảnh để tư vấn |
| UC31 | Thêm công thức | Tạo bảng định lượng nguyên liệu |
| UC32 | Sửa công thức | Điều chỉnh tỷ lệ / thành phần |
| UC33 | Cập nhật trạng thái công thức | Vô hiệu hóa công thức cũ |
| UC34 | Tra cứu công thức | Xem định lượng nguyên liệu |
| UC35 | Tính SL bánh làm ra | Dựa trên tồn kho nguyên liệu |
| UC36 | Cấu hình giới hạn nhận đơn | Cài tối đa bánh/ngày cho bếp |
| UC37 | Thêm nguyên liệu | Ghi nhận vật tư mới + đơn vị tính |
| UC38 | Sửa nguyên liệu | Cập nhật tên, quy cách, đơn vị |
| UC39 | Cập nhật trạng thái NL | Vô hiệu hóa NL không dùng |

## V. Quản lý Kho
| UC | Tên | Mô tả ngắn |
|---|---|---|
| UC40 | Lập phiếu nhập kho | Ghi nhận NL mua từ NCC |
| UC41 | Lập phiếu xuất kho | Xuất NL cho thợ bếp làm bánh |
| UC42 | Lập phiếu xuất hủy | Hủy NL hết hạn / hỏng |
| UC43 | Cảnh báo tồn kho | Tự động báo khi dưới mức an toàn / sắp hết hạn |
| UC44 | Tra cứu thẻ kho | Lịch sử biến động nhập/xuất/tồn |
| UC45 | Truy vết nguồn gốc | Dò lô hàng có vấn đề |
| UC46 | Thêm nhà cung cấp | Tạo hồ sơ đối tác cung ứng |
| UC47 | Sửa nhà cung cấp | Cập nhật địa chỉ, liên hệ |
| UC48 | Cập nhật trạng thái NCC | Khóa NCC ngừng hợp tác |
| UC49 | Tra cứu nhà cung cấp | Tìm thông tin đặt hàng / truy vết |

## VI. Thống kê & Báo cáo
| UC | Tên | Mô tả ngắn |
|---|---|---|
| UC50 | Báo cáo lợi nhuận | Giá vốn vs đơn giá bán |
| UC51 | Báo cáo doanh thu | Theo ca / ngày / tháng / năm |
| UC52 | Báo cáo tồn kho | Đầu kỳ / nhập / xuất / cuối kỳ |
| UC59 | Gộp màn hình theo Tab Báo cáo/Bếp | Nhúng Giám sát ca vào Báo cáo và Cấu hình giới hạn vào Bếp |

## VII. Sổ Quỹ
| UC | Tên | Mô tả ngắn |
|---|---|---|
| UC53 | Thêm loại thu chi | Tạo hạng mục phân loại dòng tiền |
| UC54 | Sửa loại thu chi | Cập nhật tên / mô tả hạng mục |
| UC55 | Cập nhật trạng thái loại thu chi | Vô hiệu hóa / kích hoạt danh mục |
| UC56 | Lập phiếu thu chi | Ghi nhận tiền ra/vào ngoài bán hàng |
| UC57 | Hủy phiếu thu chi | Hoàn tác dòng tiền lập sai |

---

## Mapping: Màn hình → Use-case
| Màn hình | UC cần cover |
|---|---|
| Đăng nhập | UC01 |
| Mở ca | UC22 |
| Dashboard | UC02, UC23, UC51 |
| POS | UC16, UC17, UC18 |
| Đơn hàng / KDS | UC19, UC20, UC21 |
| Kho & Nguyên liệu | UC40–UC45 |
| Sản phẩm & Công thức | UC27–UC36 |
| Danh mục SP | UC24–UC26 |
| Nguyên liệu | UC37–UC39 |
| Khách hàng | UC10–UC15 |
| Nhân sự | UC04–UC09 |
| Nhà cung cấp | UC46–UC49 |
| Báo cáo | UC50–UC52 |
| Sổ quỹ | UC53–UC57 |
| Tài khoản | UC03, UC08 |
| Nhật ký | UC09 |
