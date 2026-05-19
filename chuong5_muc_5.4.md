# 5.4 Minh họa đồ án ở mức chương trình

> **Ghi chú:** Trong báo cáo chính thức, đây là mục **4.4**.

---

## 5.4.1 Lost update

### Tình huống

- Thu ngân 1 thực hiện tạo đơn hàng bán 3 cái sản phẩm X (tồn kho hiện tại là 20 cái).
- Ngay sau đó, thu ngân 2 thực hiện tạo đơn hàng bán 5 cái cùng sản phẩm X, trong khi giao dịch được thực hiện bởi thu ngân 1 chưa được commit.
- Thu ngân 1 thực hiện thành công giao dịch bán hàng.
- Thu ngân 2 thực hiện thành công giao dịch bán hàng.
- Tồn kho của sản phẩm X lúc này chỉ còn 15 cái chứ không phải 12 cái (20 − 3 − 5 = 12).

### Các bước giả lập

- **Bước 1:** Mở file `database/05_procedures/cud/proc_order_cud.sql`, tạm thời bỏ mệnh đề `FOR UPDATE` trong câu lệnh `SELECT SOLUONGTON` (line 93) và thêm dòng lệnh `DBMS_LOCK.SLEEP(10);` trước lệnh `COMMIT` (line 142) để giả lập thời gian xử lý kéo dài.
- **Bước 2:** Kiểm tra tồn kho sản phẩm X trong cơ sở dữ liệu, xác nhận giá trị ban đầu là 20 cái.
- **Bước 3:** Đăng nhập vào tài khoản thu ngân 1 và chuyển đến giao diện Tạo đơn hàng.
- **Bước 4:** Mở thêm một instance ứng dụng, đăng nhập vào tài khoản thu ngân 2 và chuyển đến giao diện Tạo đơn hàng.
- **Bước 5:** Thu ngân 1 chọn sản phẩm X, nhập số lượng 3, bấm thanh toán.
- **Bước 6:** Trong khoảng 10 giây procedure đang sleep (giao dịch của thu ngân 1 chưa commit), thu ngân 2 chọn sản phẩm X, nhập số lượng 5, bấm thanh toán.

### Thực hiện

- Tồn kho ban đầu của sản phẩm X là 20 cái.

- Thực hiện đăng nhập vào tài khoản thu ngân thứ 1.
    - Vào giao diện **Tạo đơn hàng**, chọn sản phẩm X, nhập số lượng 3 cái.

- Thực hiện đăng nhập vào tài khoản thu ngân thứ 2 (instance 2 của ứng dụng).
    - Vào giao diện **Tạo đơn hàng**, chọn cùng sản phẩm X, nhập số lượng 5 cái.

- Thu ngân 1 bấm thanh toán (procedure `PROC_TAODONHANG` thực thi, đọc tồn kho = 20, trừ 3, đang sleep 10 giây).

- Thu ngân 2 bấm thanh toán (procedure cũng đọc tồn kho = 20 do không có `FOR UPDATE`, kiểm tra đủ hàng, trừ 5).

- Trong cơ sở dữ liệu có lưu 2 đơn hàng, bảng `CTDONHANG` ghi nhận bán 3 cái và 5 cái sản phẩm X.

- Kiểm tra tồn kho trong bảng `SANPHAM`: `SOLUONGTON` = 15.

- _Vấn đề:_ Tồn kho sản phẩm X đúng phải là 20 − 3 − 5 = 12 cái, tuy nhiên thực tế chỉ còn 15 cái. Giao dịch trừ kho của thu ngân 1 đã bị ghi đè bởi thu ngân 2, gây ra tình trạng lost update. Hệ thống ghi nhận bán tổng cộng 8 cái nhưng kho chỉ trừ 5 cái, dẫn đến sai lệch dữ liệu.

### Giải pháp: Chuyển mức cô lập thành Serializable trong Oracle

Để khắc phục vấn đề lost update, ta sử dụng câu lệnh `SET TRANSACTION ISOLATION LEVEL SERIALIZABLE` thay cho mức cô lập mặc định `READ COMMITTED`. Cụ thể, thêm câu lệnh này vào đầu procedure `PROC_TAODONHANG`:

```sql
-- Thêm vào đầu phần BEGIN của PROC_TAODONHANG
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

Khi sử dụng mức cô lập Serializable, Oracle đảm bảo rằng mỗi transaction chỉ nhìn thấy dữ liệu tại thời điểm transaction bắt đầu. Nếu hai transaction cùng cố gắng cập nhật cùng một dòng dữ liệu, transaction thực hiện sau sẽ phát hiện rằng dòng dữ liệu đã bị thay đổi kể từ khi snapshot của nó được tạo, và Oracle sẽ tự động từ chối giao dịch với lỗi ORA-08177.

### Kết quả sau khi áp dụng Serializable

Khi mô phỏng lại trường hợp trên với mức cô lập Serializable:

- Thu ngân 1 bấm thanh toán → procedure `PROC_TAODONHANG` thực thi với Serializable, đọc tồn kho = 20, trừ 3, cập nhật tồn kho thành 17, đang sleep 10 giây.

- Thu ngân 2 bấm thanh toán → procedure cũng thực thi với Serializable, đọc tồn kho = 20 (snapshot tại thời điểm bắt đầu transaction), thử cập nhật tồn kho. Tuy nhiên, do dòng `SANPHAM WHERE MASP = 5` đã bị thu ngân 1 thay đổi (nhưng chưa commit), thu ngân 2 phải chờ.

- Thu ngân 1 commit thành công. Tồn kho = 17.

- Thu ngân 2 được unblock. Oracle phát hiện dòng dữ liệu đã bị thay đổi sau snapshot của thu ngân 2 → **giao dịch của thu ngân 2 thất bại** với lỗi:

```
ORA-08177: can't serialize access for this transaction
```

- Giao dịch của thu ngân 1 thành công, tồn kho = 17. Giao dịch của thu ngân 2 bị rollback. Thu ngân 2 cần thực hiện lại giao dịch.

- Tiến hành kiểm tra: ứng dụng Java nhận được exception từ Oracle và hiển thị thông báo lỗi cho thu ngân 2. Thu ngân 2 thực hiện lại thao tác bán hàng, lần này đọc tồn kho mới nhất (17), trừ 5 → tồn kho = 12. Dữ liệu đã chính xác.

- Lịch sử đơn hàng và dữ liệu tồn kho lúc này đã nhất quán.

### Ghi chú: Giải pháp thực tế trong hệ thống

Ngoài phương pháp Serializable, hệ thống tiệm bánh còn triển khai thêm cơ chế **Pessimistic Lock** (`SELECT ... FOR UPDATE`) trực tiếp trong procedure `PROC_TAODONHANG` (line 93). Cơ chế này hoạt động như sau:

- Khi thu ngân 1 gọi procedure, câu lệnh `SELECT ... FOR UPDATE` sẽ khóa dòng sản phẩm X trong bảng `SANPHAM`.
- Khi thu ngân 2 gọi cùng procedure, câu lệnh `FOR UPDATE` sẽ **chặn (block)** cho đến khi thu ngân 1 commit.
- Sau khi lock được giải phóng, thu ngân 2 đọc lại tồn kho mới nhất (17), kiểm tra đủ hàng (17 ≥ 5), trừ tiếp → tồn kho = 12.
- Cả hai giao dịch đều thành công và dữ liệu nhất quán.

Ở tầng Java, lớp `DonHangDAO.java` (line 31–46) đảm bảo lock hoạt động đúng bằng cách sử dụng explicit transaction:

```java
// Trích từ DonHangDAO.java (line 31–46)
public int taoDonHang(...) throws Exception {
    Connection conn = null;
    try {
        conn = moKetNoi();
        conn.setAutoCommit(false); // Bắt đầu explicit transaction
        int maDon = taoDonHangWithConn(conn, ...);
        conn.commit();             // Lock giải phóng tại đây
        return maDon;
    } catch (Exception e) {
        if (conn != null) conn.rollback();
        handleException("taoDonHang", e);
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
    return -1;
}
```

Nhờ `setAutoCommit(false)`, câu lệnh `SELECT ... FOR UPDATE` bên trong procedure sẽ giữ khóa trên dòng sản phẩm cho đến khi Java gọi `conn.commit()`. Điều này đảm bảo rằng tại bất kỳ thời điểm nào, chỉ có một session được phép đọc và cập nhật tồn kho của cùng một sản phẩm, ngăn chặn triệt để vấn đề lost update.

---

## Mô hình quan hệ

**NHANVIEN** (<u>MANV</u>, HOTEN, NGAYSINH, SDT, TRANGTHAILAMVIEC)

**VAITRO** (<u>MAVAITRO</u>, TENVAITRO, MOTA)

**NHANVIEN_VAITRO** (<u>MANV</u>, <u>MAVAITRO</u>)

**TAIKHOAN** (<u>MATAIKHOAN</u>, MANV, TENDANGNHAP, MATKHAU, EMAIL, TRANGTHAITK)

**CALAMVIEC** (<u>MACA</u>, MANV, MAMAYPOS, THOIGIANMOCA, THOIGIANDONGCA, TRANGTHAI)

**CHUCNANG** (<u>MACHUCNANG</u>, TENCHUCNANG, MOTA, MODULE)

**VAITRO_CHUCNANG** (<u>MAVAITRO</u>, <u>MACHUCNANG</u>, CAN_VIEW, CAN_ADD, CAN_EDIT, CAN_DELETE, CAN_DOWNLOAD)

**KHACHHANG** (<u>MAKH</u>, HOTEN, SDT, DIACHI, NGAYDANGKY, DIEMTICHLUY, MAHANG)

**HANGTHANHVIEN** (<u>MAHANG</u>, TENHANG, DIEMTOITHIEU, PHANTRAMGIAMGIA)

**DANHMUCSP** (<u>MADM</u>, TENDM)

**SANPHAM** (<u>MASP</u>, MADM, TENSP, GIAVON, GIABAN, HINHANH, CHOPHEPTUYCHINH, THOIGIANBAOQUAN, SOLUONGTON, PHIENBAN, THOIGIANCHUANBI)

**KICHCOBANH** (<u>MAKC</u>, TENKC, PHUPHI)

**COTBANH** (<u>MACOT</u>, TENCOT, PHUPHI)

**NHANBANH** (<u>MANHAN</u>, TENNHAN, PHUPHI)

**KIEUTRANGTRI** (<u>MATRANGTRI</u>, TENTRANGTRI, PHUPHI)

**NGUYENLIEU** (<u>MANL</u>, TENNL, XUATXU, MADVT, GIAVONTRUNGBINH, MUCTONANTOAN, SOLUONGTONTONG, HESOQUYDOI, PHIENBAN)

**DONVITINH** (<u>MADVT</u>, TENDVT)

**CONGTHUC** (<u>MASP</u>, <u>MANL</u>, SOLUONGTIEUHAO)

**NHACUNGCAP** (<u>MANCC</u>, TENNCC, SDT, DIACHI)

**PHIEUNHAPKHO** (<u>MAPN</u>, NGAYNHAP, MANV, MANCC, TONGTIENNHAP)

**CTPHIEUNHAP** (<u>MALO</u>, MAPN, MANL, SOLUONG, DONGIA, SOLUONGCONLAI, NGAYSANXUAT, HANSUDUNG)

**PHIEUXUATKHO** (<u>MAPX</u>, NGAYXUAT, LYDOXUAT, MANV)

**CTPHIEUXUAT_NL** (<u>MAPX</u>, <u>MALO</u>, SOLUONG)

**CTPHIEUXUAT_TP** (<u>MAPX</u>, <u>MASP</u>, SOLUONG, DONGIAVON)

**MESANXUAT** (<u>MAME</u>, MASP, SOLUONGSANXUAT, NGAYSANXUAT, MANV, MAPX, HANSUDUNG, SOLUONGCONLAI)

**TRANGTHAIDON** (<u>MATRANGTHAI</u>, TENTRANGTHAI)

**NANGLUCSANXUAT** (<u>NGAYSANXUAT</u>, GIOIHANSOBANH, SOBANHDANHAN)

**DONDATHANG** (<u>MADON</u>, NGAYLAP, NGAYGIONHANBANH, MAKH, MANV_LAP, MATRANGTHAI, TONGTIENHDBAN, TIENDACOC, PHIENBAN, HINHTHUCNHAN, DIACHIGIAO, SDTGIAO)

**CTDONHANG** (<u>MACTHD</u>, MADON, MASP, SOLUONG, DONGIA, PHANTRAMGIAM, DONGIAVON)

**CTDONTUYCHINH** (<u>MACTTC</u>, MADON, MASP, SOLUONG, MAKC, MACOT, MANHAN, MATRANGTRI, LOICHUCTRENBANH, GHICHUTHOBANH, HINHANHTHAMKHAO, DONGIA, DONGIAVON, THOIGIANCHUANBI)

**LICHSUDONHANG** (<u>MALOG</u>, MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT, GHICHU)

**PHUONGTHUCTT** (<u>MAPTTT</u>, TENPTTT)

**HOADON** (<u>MAHD</u>, MADON, MACA, NGAYXUATHD, THUEVAT, TONGTIENTHANHTOAN, TIENHANGGOC, MAPTTT, LOAIHD, TRANGTHAI)

**DOISOAT** (<u>MACA</u>, TIENKHAIBAODAUCA, TONGTIENHETHONG, TIENTHUCTEDEM, CHENHLECH, LYDOCHENHLECH)

**LOAITHUCHI** (<u>MALOAITHUCHI</u>, TENLOAITHUCHI, PHANLOAI)

**PHIEUTHUCHI** (<u>MAPHIEUTC</u>, NGAYTAO, MALOAITHUCHI, SOTIEN, MANV, MAHD, MAPN, MACA, GHICHU, TRANGTHAI)

**HOATDONGNHANVIEN** (<u>MAHOATDONG</u>, MANV, NHOM, HANHDONG, ENTITY_ID, THOIGIAN)
