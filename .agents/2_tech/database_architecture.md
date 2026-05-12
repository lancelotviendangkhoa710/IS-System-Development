# KIẾN TRÚC CƠ SỞ DỮ LIỆU

Oracle Database — xử lý nặng tại tầng Data. Java chỉ gọi lệnh và hiển thị.

## 5 Cụm Bảng Chính
1. **Nhân sự & Khách hàng:** `VAITRO`, `CHUCNANG`, `NHANVIEN`, `KHACHHANG`, `HANGTHANHVIEN`
2. **Sản phẩm:** `DANHMUCSP`, `SANPHAM`, `KICHCOBANH`, `COTBANH` và các bảng cấu hình. Thuộc tính `PhienBan` dùng Optimistic Locking.
3. **Kho:** `NGUYENLIEU`, `CONGTHUC`, `NHACUNGCAP`, `PHIEUNHAPKHO`, `CTPHIEUNHAP`, `PHIEUXUATKHO`, `CTPHIEUXUAT_NL/TP`. Tồn kho chia nhỏ theo `MaLo`.
4. **Đơn hàng:** `TRANGTHAIDON`, `DONDATHANG`, `CTDONHANG`, `CTDONTUYCHINH`
5. **Tài chính:** `CALAMVIEC`, `DOISOAT`, `HOADON`, `PHIEUTHUCHI`

## Trigger (tự động — không can thiệp từ Java)
- `NGUYENLIEU.SoLuongTonTong` và `GiaVonTrungBinh` tự cập nhật khi có phiếu nhập kho.
- `HOADON` và `PHIEUTHUCHI` bị cấm xóa vật lý (INSTEAD OF DELETE).
- Tự đóng băng giá vốn sản phẩm vào hóa đơn tại thời điểm bán.

## Stored Procedure bắt buộc cho CUD
Mọi thao tác CUD từ Java BẮT BUỘC qua Procedure. CẤM `INSERT/UPDATE` thuần trong DAO.

| Thao tác | Procedure |
|---|---|
| Chốt đơn & Thăng hạng | `PROC_ThanhToanVaThangHang` |
| Hủy đơn / Bom hàng | `PROC_HuyDonVaHoanKho` |
| Chốt xuất xưởng / Mẻ nướng | `PROC_XuatKhoSanXuat` (Pessimistic Locking) |