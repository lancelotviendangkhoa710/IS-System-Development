# [cite_start]6. KIẾN TRÚC CƠ SỞ DỮ LIỆU [cite: 3061]
Hệ thống sử dụng Oracle Database với kiến trúc xử lý nặng tại tầng Data. Java chỉ đóng vai trò gọi lệnh và hiển thị.

## 6.1. Tổng quan 5 Cụm Bảng Chính
1. [cite_start]**Nhân sự & Khách hàng:** `VAITRO`, `CHUCNANG`, `NHANVIEN`, `KHACHHANG`, `HANGTHANHVIEN`[cite: 3062, 3077, 3083].
2. [cite_start]**Sản phẩm:** `DANHMUCSP`, `SANPHAM`, bảng cấu hình (`KICHCOBANH`, `COTBANH`, v.v.)[cite: 3094, 3099, 3109]. [cite_start]Thuộc tính `PhienBan` dùng cho Optimistic Locking[cite: 3106].
3. [cite_start]**Kho:** `NGUYENLIEU`, `CONGTHUC`, `NHACUNGCAP`, `PHIEUNHAPKHO`, `CTPHIEUNHAP`, `PHIEUXUATKHO`, `CTPHIEUXUAT_NL/TP`[cite: 3125, 3129, 3137]. Dữ liệu tồn kho được chia nhỏ quản lý theo từng `MaLo`[cite: 3149, 3150].
4. [cite_start]**Đơn hàng:** `TRANGTHAIDON`, `DONDATHANG`, `CTDONHANG`, `CTDONTUYCHINH`[cite: 3170, 3173].
5. [cite_start]**Tài chính:** `CALAMVIEC`, `DOISOAT`, `HOADON`, `PHIEUTHUCHI`[cite: 3202, 3206].

## 6.2. Cơ chế Trigger (Không cần can thiệp từ Java)
- [cite_start]`NGUYENLIEU.SoLuongTonTong` và `GiaVonTrungBinh` tự động cập nhật khi có phiếu nhập kho[cite: 3262, 3269, 3270].
- [cite_start]Bảng `HOADON` và `PHIEUTHUCHI` bị cấm xóa vật lý (INSTEAD OF DELETE)[cite: 3275].
- [cite_start]Tự đóng băng giá vốn sản phẩm vào hóa đơn tại thời điểm bán[cite: 3280].

## 6.3. [cite_start]Quy định gọi Stored Procedure [cite: 3848]
Mọi thao tác CUD (Create/Update/Delete) trên các bảng chính từ Java BẮT BUỘC dùng Procedure. [cite_start]Tuyệt đối không dùng `INSERT/UPDATE` thuần trong DAO[cite: 3862, 3863].
- [cite_start]Chốt đơn & Thăng hạng: Gọi `PROC_ThanhToanVaThangHang`[cite: 3298].
- [cite_start]Hủy đơn/Bom hàng: Gọi `PROC_HuyDonVaHoanKho`[cite: 3310].
- [cite_start]Chốt xuất xưởng/Mẻ nướng: Gọi `PROC_XuatKhoSanXuat` (Áp dụng Pessimistic Locking bằng UPDLOCK dưới DB)[cite: 3319, 3323].