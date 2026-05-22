# CHƯƠNG 4 — Bảng kịch bản minh họa

---

## 4.1. Lost Update — Kịch bản lỗi

| Phiên 1 (Thu ngân A — bán 3 cái) | Phiên 2 (Thu ngân B — bán 3 cái) | Giải thích |
|---|---|---|
| `SELECT MASP, SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`MASP  SOLUONGTON`<br>`----  ----------`<br>`1001  5` | `SELECT MASP, SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`MASP  SOLUONGTON`<br>`----  ----------`<br>`1001  5` | **Trạng thái ban đầu.** Tồn kho = **5 bánh**. Cả hai phiên cùng nhìn thấy giá trị này trước khi bắt đầu giao dịch. |
| `SET TRANSACTION ISOLATION LEVEL READ COMMITTED;` | Không có hành động | Phiên 1 bắt đầu giao dịch. READ COMMITTED là mặc định của Oracle nhưng được khai báo tường minh để làm rõ cơ chế. |
| Không có hành động | `SET TRANSACTION ISOLATION LEVEL READ COMMITTED;` | Phiên 2 bắt đầu giao dịch cũng ở mức READ COMMITTED. |
| `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`5` | Không có hành động | Phiên 1 đọc tồn kho = **5 bánh**. Điều kiện 5 ≥ 3 → cho phép bán. Procedure bắt đầu delay. |
| Không có hành động | `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`5` | Phiên 2 đọc tồn kho = **5 bánh** (Phiên 1 chưa commit). READ COMMITTED không giữ read lock → Phiên 2 đọc được cùng giá trị. Điều kiện 5 ≥ 3 → cho phép bán. |
| `UPDATE SANPHAM SET SOLUONGTON = SOLUONGTON - 3 WHERE MASP = 1001;`<br>`COMMIT;`<br><br>Output:<br>`1 row updated.` | Không có hành động | Phiên 1 ghi delta: `SOLUONGTON = 5 - 3 = 2`. Commit, lock dòng giải phóng. |
| Không có hành động | `UPDATE SANPHAM SET SOLUONGTON = SOLUONGTON - 3 WHERE MASP = 1001;`<br>`COMMIT;`<br><br>Output:<br>`1 row updated.` | Phiên 2 ghi delta lên giá trị hiện tại (đã bị Phiên 1 trừ): `SOLUONGTON = 2 - 3 = -1`. Commit thành công, **không có lỗi nào ném ra**. |
| `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`-1` | `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`-1` | **Kiểm chứng kết quả: → BUG.** Tồn kho âm. Hệ thống bán 6 bánh trong khi chỉ có 5. |

---

## 4.1. Lost Update — Kịch bản đã khắc phục

| Phiên 1 (Thu ngân A — bán 3 cái) | Phiên 2 (Thu ngân B — bán 3 cái) | Giải thích |
|---|---|---|
| `SELECT MASP, SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`MASP  SOLUONGTON`<br>`----  ----------`<br>`1001  5` | `SELECT MASP, SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`MASP  SOLUONGTON`<br>`----  ----------`<br>`1001  5` | **Trạng thái ban đầu.** Tồn kho = **5 bánh**. |
| `SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;` | Không có hành động | Phiên 1 bắt đầu SERIALIZABLE — **snapshot được chụp tại đây**. |
| Không có hành động | `SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;` | Phiên 2 bắt đầu SERIALIZABLE với **snapshot riêng** tại thời điểm này. |
| `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`5` | Không có hành động | Phiên 1 đọc từ snapshot = **5**. Điều kiện thỏa → tiến hành bán. Delay bắt đầu. |
| Không có hành động | `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`5` | Phiên 2 đọc từ snapshot riêng = **5**. Điều kiện thỏa → tạm thời cho phép bán. |
| `UPDATE SANPHAM SET SOLUONGTON = SOLUONGTON - 3 WHERE MASP = 1001;`<br>`COMMIT;`<br><br>Output:<br>`1 row updated.` | Không có hành động | Phiên 1 ghi `5 - 3 = 2` và commit. SCN của dòng tăng sau snapshot của Phiên 2. |
| Không có hành động | `UPDATE SANPHAM SET SOLUONGTON = SOLUONGTON - 3 WHERE MASP = 1001;`<br><br>Output:<br>`ORA-08177: can't serialize access for this transaction`<br><br>`ROLLBACK;` | Oracle phát hiện dòng MASP=1001 bị sửa sau snapshot của Phiên 2 → ném **ORA-08177**. Phiên 2 rollback. |
| Không có hành động | *(Phiên 2 retry — giao dịch mới)*<br>`SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;`<br>`SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`2`<br><br>Điều kiện: 2 < 3 → **từ chối bán**. | Phiên 2 retry với snapshot mới. Đọc được `SOLUONGTON = 2` → không đủ hàng → hủy giao dịch. |
| `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`2` | `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`SOLUONGTON`<br>`----------`<br>`2` | **Kiểm chứng kết quả: → OK.** Tồn kho = 2. Chỉ Phiên 1 thành công. Phiên 2 bị từ chối đúng. |

---

## 4.2. Non-repeatable Read — Kịch bản lỗi

| Phiên 1 (Thu ngân — tạo đơn hàng) | Phiên 2 (Quản lý — cập nhật giá) | Giải thích |
|---|---|---|
| `SELECT MASP, GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`MASP  GIABAN`<br>`----  ----------`<br>`1001  150000.00` | `SELECT MASP, GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`MASP  GIABAN`<br>`----  ----------`<br>`1001  150000.00` | **Trạng thái ban đầu.** Giá bánh = **150.000đ**. |
| `SET TRANSACTION ISOLATION LEVEL READ COMMITTED;` | Không có hành động | Phiên 1 bắt đầu giao dịch tạo đơn hàng ở mức READ COMMITTED. |
| `SELECT GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`GIABAN`<br>`----------`<br>`150000.00` | Không có hành động | **Lần đọc 1:** Phiên 1 đọc giá = **150.000đ**, hiển thị cho khách. Khách đồng ý. Phiên 1 bắt đầu delay (khách điền thông tin). |
| Không có hành động | `UPDATE SANPHAM SET GIABAN = 180000 WHERE MASP = 1001;`<br>`COMMIT;`<br><br>Output:<br>`1 row updated.` | Quản lý cập nhật giá từ 150.000đ → **180.000đ** và commit. |
| `SELECT GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`GIABAN`<br>`----------`<br>`180000.00` | Không có hành động | **Lần đọc 2** (cùng giao dịch): READ COMMITTED tạo snapshot mới → thấy giá đã commit = **180.000đ**. Hệ thống tính tiền theo giá này. |
| `COMMIT;` | Không có hành động | Hệ thống tính tiền = **180.000đ**. |
| `SELECT GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`GIABAN`<br>`----------`<br>`180000.00`<br><br>*(Giá lần 1 đã hiển thị: 150.000đ)*<br>*(Giá lần 2 dùng để tính tiền: 180.000đ)* | `SELECT GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`GIABAN`<br>`----------`<br>`180000.00` | **Kiểm chứng kết quả: → BUG.** Hai lần đọc trong cùng giao dịch trả về giá trị khác nhau. Khách bị tính dư **30.000đ**. |

---

## 4.2. Non-repeatable Read — Kịch bản đã khắc phục

| Phiên 1 (Thu ngân — tạo đơn hàng) | Phiên 2 (Quản lý — cập nhật giá) | Giải thích |
|---|---|---|
| `SELECT MASP, GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`MASP  GIABAN`<br>`----  ----------`<br>`1001  150000.00` | `SELECT MASP, GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`MASP  GIABAN`<br>`----  ----------`<br>`1001  150000.00` | **Trạng thái ban đầu.** Giá bánh = **150.000đ**. |
| `SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;` | Không có hành động | Phiên 1 bắt đầu SERIALIZABLE — **snapshot chụp tại thời điểm này**. Mọi SELECT trong giao dịch dùng cùng snapshot này. |
| `SELECT GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`GIABAN`<br>`----------`<br>`150000.00` | Không có hành động | **Lần đọc 1:** Đọc từ snapshot = **150.000đ**. Hiển thị cho khách. Delay bắt đầu. |
| Không có hành động | `UPDATE SANPHAM SET GIABAN = 180000 WHERE MASP = 1001;`<br>`COMMIT;`<br><br>Output:<br>`1 row updated.` | Quản lý cập nhật giá và commit. Thay đổi xảy ra **sau** snapshot của Phiên 1. |
| `SELECT GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`GIABAN`<br>`----------`<br>`150000.00` | Không có hành động | **Lần đọc 2** (cùng giao dịch): SERIALIZABLE dùng **snapshot cũ** → vẫn thấy **150.000đ** mặc dù DB thực tế đã là 180.000đ. |
| `COMMIT;` | Không có hành động | Hệ thống tính tiền = **150.000đ**. Đúng với giá khách đã xem và đồng ý. |
| `SELECT GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`GIABAN`<br>`----------`<br>`180000.00`<br><br>*(Giá lần 1: 150.000đ — Giá lần 2: 150.000đ)* | `SELECT GIABAN FROM SANPHAM WHERE MASP = 1001;`<br><br>Output:<br>`GIABAN`<br>`----------`<br>`180000.00` | **Kiểm chứng kết quả: → OK.** Cả hai lần đọc trong Phiên 1 đều trả về **150.000đ**. Khách được tính đúng giá. |

---

## 4.3. Phantom Read — Kịch bản lỗi

| Phiên 1 (Quản lý kho — kiểm kê) | Phiên 2 (Nhân viên — nhập kho) | Giải thích |
|---|---|---|
| `SELECT MALO, SOLUONGCONLAI FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`MALO  SOLUONGCONLAI`<br>`----  -------------`<br>`201   60.00`<br>`202   40.00`<br><br>`SELECT SUM(SOLUONGCONLAI) FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output: `SUM = 100.00` | `SELECT SUM(SOLUONGCONLAI) FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output: `SUM = 100.00` | **Trạng thái ban đầu.** 2 lô bơ sữa, tổng = **100 kg**. |
| `SET TRANSACTION ISOLATION LEVEL READ COMMITTED;` | Không có hành động | Phiên 1 bắt đầu giao dịch kiểm kê ở mức READ COMMITTED. |
| `SELECT SUM(SOLUONGCONLAI) AS TONG_TON FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`TONG_TON`<br>`--------`<br>`100.00` | Không có hành động | **Truy vấn lần 1:** Phiên 1 đọc tổng = **100 kg** (2 lô: 60 + 40). Ghi vào đầu báo cáo. Delay bắt đầu. |
| Không có hành động | `INSERT INTO CTPHIEUNHAP (MAPN, MANL, SOLUONG, DONGIA, SOLUONGCONLAI, NGAYSANXUAT, HANSUDUNG)`<br>`VALUES (10, 5, 20, 45000, 20, DATE '2026-05-20', DATE '2026-08-20');`<br>`COMMIT;`<br><br>Output:<br>`1 row inserted.` | Nhân viên kho nhập lô bơ sữa mới **20 kg** và commit. Tổng thực tế trong DB: 120 kg. |
| `SELECT SUM(SOLUONGCONLAI) AS TONG_TON FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`TONG_TON`<br>`--------`<br>`120.00` | Không có hành động | **Truy vấn lần 2** (cùng giao dịch): READ COMMITTED tạo snapshot mới → thấy lô mới (phantom row) = **120 kg**. |
| `COMMIT;` | Không có hành động | Báo cáo ghi "Tồn đầu kỳ = 100 kg, Tồn cuối kỳ = 120 kg". Mâu thuẫn trong cùng 1 lần kiểm kê. |
| `SELECT COUNT(*) FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`3`<br><br>*(Lần truy vấn 1: TONG = 100)*<br>*(Lần truy vấn 2: TONG = 120)* | `SELECT COUNT(*) FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`3` | **Kiểm chứng kết quả: → BUG.** 3 lô trong DB (lô mới đã commit). Phiên 1 thấy 2 kết quả khác nhau trong cùng 1 giao dịch. |

---

## 4.3. Phantom Read — Kịch bản đã khắc phục

| Phiên 1 (Quản lý kho — kiểm kê) | Phiên 2 (Nhân viên — nhập kho) | Giải thích |
|---|---|---|
| `SELECT SUM(SOLUONGCONLAI) FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output: `SUM = 100.00` | `SELECT SUM(SOLUONGCONLAI) FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output: `SUM = 100.00` | **Trạng thái ban đầu.** 2 lô, tổng = **100 kg**. |
| `SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;` | Không có hành động | Phiên 1 bắt đầu SERIALIZABLE — **snapshot chụp tại đây**: chỉ thấy 2 lô (MALO 201, 202). |
| `SELECT SUM(SOLUONGCONLAI) AS TONG_TON FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`TONG_TON`<br>`--------`<br>`100.00` | Không có hành động | **Truy vấn lần 1:** Đọc từ snapshot = **100 kg**. Ghi vào báo cáo. Delay bắt đầu. |
| Không có hành động | `INSERT INTO CTPHIEUNHAP (..., MANL=5, SOLUONGCONLAI=20, ...);`<br>`COMMIT;`<br><br>Output:<br>`1 row inserted.` | Nhân viên nhập lô mới 20 kg và commit. INSERT xảy ra **sau** snapshot của Phiên 1. |
| `SELECT SUM(SOLUONGCONLAI) AS TONG_TON FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`TONG_TON`<br>`--------`<br>`100.00` | Không có hành động | **Truy vấn lần 2** (cùng giao dịch): SERIALIZABLE dùng snapshot cũ → **lô mới không xuất hiện** → vẫn **100 kg**. |
| `COMMIT;` | Không có hành động | Báo cáo nhất quán: "Tồn đầu kỳ = 100 kg, Tồn cuối kỳ = 100 kg". |
| `SELECT COUNT(*) FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`3`<br><br>*(Lần truy vấn 1 trong Phiên 1: TONG = 100)*<br>*(Lần truy vấn 2 trong Phiên 1: TONG = 100)* | `SELECT COUNT(*) FROM CTPHIEUNHAP WHERE MANL = 5;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`3` | **Kiểm chứng kết quả: → OK.** DB thực tế có 3 lô nhưng Phiên 1 nhất quán thấy 100 kg cả hai lần. Phantom bị chặn. |

---

## 4.4. Deadlock — Kịch bản lỗi

| Phiên 1 (Nhân viên A) | Phiên 2 (Nhân viên B) | Giải thích |
|---|---|---|
| `SELECT MANL, MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001, 1002);`<br><br>Output:<br>`MANL  MUCTONANTOAN`<br>`----  ------------`<br>`1001  20.00`<br>`1002  15.00` | `SELECT MANL, MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001, 1002);`<br><br>Output:<br>`MANL  MUCTONANTOAN`<br>`----  ------------`<br>`1001  20.00`<br>`1002  15.00` | **Trạng thái ban đầu.** Bột mì (1001) = 20, Đường (1002) = 15. |
| `SET AUTOCOMMIT OFF;`<br>`UPDATE NGUYENLIEU SET MUCTONANTOAN = 50 WHERE MANL = 1001;`<br><br>Output:<br>`1 row updated.` | Không có hành động | Phiên 1 cập nhật MANL=1001 trước. Oracle đặt **Exclusive Lock** trên dòng 1001. |
| Không có hành động | `SET AUTOCOMMIT OFF;`<br>`UPDATE NGUYENLIEU SET MUCTONANTOAN = 40 WHERE MANL = 1002;`<br><br>Output:<br>`1 row updated.` | Phiên 2 cập nhật MANL=1002 trước — **thứ tự ngược**. Oracle đặt **Exclusive Lock** trên dòng 1002. |
| `UPDATE NGUYENLIEU SET MUCTONANTOAN = 45 WHERE MANL = 1002;`<br><br>`-- (chờ, không trả về)` | Không có hành động | Phiên 1 cần lock MANL=1002 (Phiên 2 đang giữ) → **bị chặn**. |
| Không có hành động | `UPDATE NGUYENLIEU SET MUCTONANTOAN = 55 WHERE MANL = 1001;`<br><br>Output:<br>`ORA-00060: deadlock detected while waiting for resource`<br><br>`ROLLBACK;` | Phiên 2 cần lock MANL=1001 (Phiên 1 đang giữ). Oracle phát hiện **chu trình** → ném **ORA-00060** cho Phiên 2. Phiên 2 rollback. |
| *(được giải phóng)*<br>`1 row updated.`<br>`COMMIT;` | Không có hành động | Lock 1002 được giải phóng. Phiên 1 hoàn thành và commit. |
| `SELECT MANL, MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001, 1002);`<br><br>Output:<br>`MANL  MUCTONANTOAN`<br>`----  ------------`<br>`1001  50.00`<br>`1002  45.00` | `SELECT MANL, MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001, 1002);`<br><br>Output:<br>`MANL  MUCTONANTOAN`<br>`----  ------------`<br>`1001  50.00`<br>`1002  45.00` | **Kiểm chứng kết quả: → BUG.** Phiên 2 bị deadlock và mất toàn bộ thay đổi. Nhân viên B phải thực hiện lại từ đầu. |

---

## 4.4. Deadlock — Kịch bản đã khắc phục

**Quy tắc:** Cả hai phiên luôn cập nhật theo thứ tự MANL tăng dần (1001 trước, 1002 sau).

| Phiên 1 (Nhân viên A) | Phiên 2 (Nhân viên B) | Giải thích |
|---|---|---|
| `SELECT MANL, MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001, 1002);`<br><br>Output:<br>`MANL  MUCTONANTOAN`<br>`----  ------------`<br>`1001  20.00`<br>`1002  15.00` | `SELECT MANL, MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001, 1002);`<br><br>Output:<br>`MANL  MUCTONANTOAN`<br>`----  ------------`<br>`1001  20.00`<br>`1002  15.00` | **Trạng thái ban đầu.** Bột mì (1001) = 20, Đường (1002) = 15. |
| `SET AUTOCOMMIT OFF;`<br>`UPDATE NGUYENLIEU SET MUCTONANTOAN = 50 WHERE MANL = 1001;`<br><br>Output:<br>`1 row updated.` | Không có hành động | Phiên 1 cập nhật MANL=**1001** trước (thứ tự tăng dần). Lock MANL=1001. |
| Không có hành động | `SET AUTOCOMMIT OFF;`<br>`UPDATE NGUYENLIEU SET MUCTONANTOAN = 55 WHERE MANL = 1001;`<br><br>`-- (chờ, không trả về)` | Phiên 2 **cũng bắt đầu từ MANL=1001** (cùng thứ tự). MANL=1001 đang bị Phiên 1 lock → **Phiên 2 bị chặn tại đây**. Phiên 2 chưa giữ lock nào → không tạo chu trình. |
| `UPDATE NGUYENLIEU SET MUCTONANTOAN = 40 WHERE MANL = 1002;`<br>`COMMIT;`<br><br>Output:<br>`1 row updated.` | Không có hành động | Phiên 1 cập nhật MANL=1002, commit. Giải phóng tất cả lock. |
| Không có hành động | *(được giải phóng)*<br>`1 row updated.` (MANL=1001)<br>`UPDATE NGUYENLIEU SET MUCTONANTOAN = 45 WHERE MANL = 1002;`<br>`COMMIT;`<br><br>Output:<br>`1 row updated.` | Lock MANL=1001 được giải phóng → Phiên 2 tiếp tục. Cập nhật 1001 rồi 1002 thành công. |
| `SELECT MANL, MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001, 1002);`<br><br>Output:<br>`MANL  MUCTONANTOAN`<br>`----  ------------`<br>`1001  55.00`<br>`1002  45.00` | `SELECT MANL, MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001, 1002);`<br><br>Output:<br>`MANL  MUCTONANTOAN`<br>`----  ------------`<br>`1001  55.00`<br>`1002  45.00` | **Kiểm chứng kết quả: → OK.** Cả hai phiên hoàn thành thành công. Không Deadlock. Phiên 2 ghi đè Phiên 1 đúng như nghiệp vụ. |
