# Demo Deadlock — Xuất kho sản xuất (2 thợ bếp cùng lúc)

Tài liệu này mô phỏng hiện tượng Deadlock thông qua 2 kịch bản thực tế:
hai thợ bếp cùng lúc xuất kho nguyên liệu để làm hai loại bánh khác nhau
nhưng có chung nguyên liệu.

Giả sử công thức như sau:

- Bánh bông lan cần: Đường cát trắng (MANL = 5, lượng 0.5 kg) và Bột mì số 8 (MANL = 3, lượng 0.1 kg)
- Bánh mì gối cần: Bột mì số 8 (MANL = 3, lượng 0.5 kg) và Đường cát trắng (MANL = 5, lượng 0.1 kg)

Tồn kho ban đầu: Đường cát trắng còn 100 kg, Bột mì số 8 còn 80 kg.

---

## Bối cảnh tình huống

Đây là buổi sáng bận rộn ở xưởng bánh. Anh Hùng và chị Mai — hai thợ bếp — cùng lúc
mở hệ thống để xuất kho nguyên liệu: anh Hùng làm bánh bông lan, chị Mai làm
bánh mì gối. Cả hai loại bánh đều cần Đường cát trắng và Bột mì số 8, chỉ khác
nhau ở lượng dùng.

Hệ thống xử lý xuất kho bằng cách khóa từng nguyên liệu theo thứ tự lượng tiêu
hao giảm dần. Với bánh bông lan, Đường (0.5 kg) nhiều hơn nên khóa Đường trước.
Với bánh mì gối thì ngược lại — Bột mì (0.5 kg) nhiều hơn nên khóa Bột mì trước.

Hai giao dịch chạy song song: anh Hùng đang giữ Đường và chờ Bột mì, chị
Mai đang giữ Bột mì và chờ Đường. Không ai chịu nhường. Sau 5 giây, cả hai
cùng nhận thông báo lỗi và cả hai đều mất công — không mật khẩu nào được
xuất, không mặt hàng nào được trừ. Đây là Deadlock — khóa chết — khi
hai giao dịch cùng chờ nhau theo vòng tròn và không ai tự thoát ra được.

---

## Kịch bản 1 — Có Lỗi (ORDER BY SOLUONGTIEUHAO DESC + FOR UPDATE WAIT 5)

Procedure sắp xếp nguyên liệu theo lượng tiêu hao giảm dần nên thứ tự khóa
của hai phiên ngược chiều nhau. Sau khi sleep xong, cả hai phiên cùng thức
dậy và cùng cần khóa nguyên liệu thứ hai. Do thứ tự ngược nhau, mỗi phiên
đang nắm giữ chính thứ mà phiên kia đang cần. **Phiên nào bị block trước sẽ
đếm ngược 5 giây và nhận LOCK_TIMEOUT, sau đó ROLLBACK.** Khi phiên đó rollback
và nhả lock, phiên còn lại được gỡ block và tiếp tục đến khi thành công.

| Bước | Phiên 1 (Thợ A — Bánh bông lan) | Phiên 2 (Thợ B — Bánh mì gối) | Giải thích |
|------|----------------------------------|--------------------------------|------------|
| 1 | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 5`<br>`FOR UPDATE WAIT 5;`<br><br>Output:<br>`SOLUONGTONTONG`<br>`--------------`<br>`        100.00`<br>`1 row selected.`<br><br>`DBMS_SESSION.SLEEP(6);` | Không có hành động. | Phiên 1 khóa Đường (MANL=5) thành công — đây là nguyên liệu có lượng lớn nhất (0.5 kg) của bánh bông lan theo ORDER BY SOLUONGTIEUHAO DESC. Procedure ngủ 6 giây để Phiên 2 kịp bắt đầu và tạo ra tình huống deadlock. |
| 2 | Đang chờ (sleep 6 giây). | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 3`<br>`FOR UPDATE WAIT 5;`<br><br>Output:<br>`SOLUONGTONTONG`<br>`--------------`<br>`         80.00`<br>`1 row selected.`<br><br>`DBMS_SESSION.SLEEP(6);` | Phiên 2 khóa Bột mì (MANL=3) thành công — đây là nguyên liệu lớn nhất (0.5 kg) của bánh mì gối theo ORDER BY DESC. Tại đây: Phiên 1 giữ Đường, Phiên 2 giữ Bột mì. Mỗi phiên đang ôm đúng thứ mà phiên kia cần. |
| 3 | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 3`<br>`FOR UPDATE WAIT 5;`<br><br>Output:<br>`Bị CHẶN.`<br>`Bắt đầu đếm ngược 5 giây...` | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 5`<br>`FOR UPDATE WAIT 5;`<br><br>Output:<br>`Bị CHẶN.`<br>`Đang đợi Phiên 1 nhả Đường...` | Sau 6 giây ngủ, cả hai cùng thức dậy và yêu cầu khóa nguyên liệu thứ hai. Phiên 1 cần Bột mì (P2 đang giữ), Phiên 2 cần Đường (P1 đang giữ). **Vòng chờ tròn** hình thành — phiên nào bị block trước sẽ đếm ngược 5 giây. |
| 4 | Output:<br>`ERROR:`<br>`ORA-30006: WAIT timeout expired`<br><br>`ROLLBACK;` → Nhả khóa Đường (MANL=5)<br><br>App hiển thị: **LOCK\_TIMEOUT** trên Bột mì số 8. | Phiên 2 được gỡ block (Phiên 1 vừa nhả Đường)<br>→ `FOR UPDATE WAIT 5` lock Đường thành công<br>→ Kiểm tra tồn kho ✔<br>→ Xử lý lô hàng<br>→ `COMMIT;`<br><br>App hiển thị: **Xuất kho thành công** ✔ | **Phiên 1 timeout** sau 5 giây chờ → ORA-30006 → ROLLBACK → nhả khóa Đường. **Phiên 2 ngay lập tức được Oracle gỡ block** → tiếp tục đến khi COMMIT. Kết quả: **1 thất bại (Outcome A)** — không phải cả 2 đều fail. |

**Hậu quả cụ thể:**

- **Phiên bị block trước** (Thợ A hoặc B): LOCK_TIMEOUT sau 5 giây. ROLLBACK. Tồn kho không thay đổi. Phải làm lại.
- **Phiên còn lại**: Sau khi phiên kia rollback và nhả lock, phiên này được gỡ block và tiếp tục thành công.
- Tồn kho: Bị trừ **1 lần** (chỉ phiên thành công) — phiên thất bại đã rollback sạch.
- Nguyên nhân: Thứ tự khóa ngược chiều khiến một giao dịch bị hủy ngẫu nhiên — không dự đoán được phiên nào sẽ thất bại.

---

## Kịch bản 2 — Đã Sửa (ORDER BY MANL ASC — Lock Ordering)

Procedure sắp xếp nguyên liệu cần khóa theo mã nguyên liệu tăng dần (MANL ASC).
Cả hai loại bánh đều có Bột mì (MANL = 3) và Đường (MANL = 5). Hai phiên
cùng khóa theo thứ tự MANL tăng dần — không thể hình thành vòng chờ tròn.

| Bước | Phiên 1 (Thợ A — Bánh bông lan) | Phiên 2 (Thợ B — Bánh mì gối) | Giải thích |
|------|----------------------------------|--------------------------------|------------|
| 1 | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 3`<br>`FOR UPDATE;`<br><br>Output:<br>`SOLUONGTONTONG`<br>`--------------`<br>`         80.00`<br>`1 row selected.`<br><br>`DBMS_SESSION.SLEEP(6);` | Không có hành động. | Với ORDER BY MANL ASC, Bột mì (MANL=3) luôn được khóa trước Đường (MANL=5) — bất kể là bánh nào. Phiên 1 khóa Bột mì thành công và bắt đầu sleep. Ở FIX mode dùng FOR UPDATE (không có WAIT) vì không cần timeout khi không có deadlock. |
| 2 | Đang chờ (sleep 6 giây). | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 3`<br>`FOR UPDATE;`<br><br>Output:<br>`Bị CHẶN.`<br>`Bột mì đang bị Phiên 1 giữ.`<br>`Xếp hàng chờ...` | Phiên 2 cũng cần Bột mì trước tiên nhưng bị chặn. Điểm then chốt: Phiên 2 chưa giữ bất kỳ khóa nào. Không có vòng chờ tròn vì Phiên 2 không giữ thứ mà Phiên 1 cần. |
| 3 | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 5`<br>`FOR UPDATE;`<br><br>Output:<br>`SOLUONGTONTONG`<br>`--------------`<br>`        100.00`<br>`1 row selected.`<br><br>`COMMIT;`<br><br>Output:<br>`Commit complete.` | Đang chờ (chờ Bột mì được giải phóng). | Phiên 1 thức dậy và cần Đường — không ai giữ nên khóa thành công ngay. Phiên 1 commit và giải phóng cả hai khóa. Xuất kho bánh bông lan thành công. |
| 4 | Kết thúc. Thành công. | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 3`<br>`FOR UPDATE;`<br><br>Output:<br>`SOLUONGTONTONG`<br>`--------------`<br>`         80.00`<br>`1 row selected.`<br><br>`DBMS_SESSION.SLEEP(6);` | Bột mì được giải phóng ngay sau khi Phiên 1 commit. Phiên 2 nhận khóa Bột mì thành công và tiếp tục. Không có lỗi, không có rollback. |
| 5 | Kết thúc. Thành công. | `SELECT SOLUONGTONTONG`<br>`FROM NGUYENLIEU`<br>`WHERE MANL = 5`<br>`FOR UPDATE;`<br><br>Output:<br>`SOLUONGTONTONG`<br>`--------------`<br>`        100.00`<br>`1 row selected.`<br><br>`COMMIT;`<br><br>Output:<br>`Commit complete.` | Phiên 2 khóa Đường thành công vì Phiên 1 đã commit. Phiên 2 hoàn tất xuất kho. Cả hai thợ bếp đều thành công. Tồn kho được trừ đúng 2 lần. |

**Kết quả cụ thể:**

- Phiên 1 (Thợ A — Bánh bông lan): Thành công. Tồn kho trừ đúng.
- Phiên 2 (Thợ B — Bánh mì gối): Thành công sau khi xếp hàng chờ đúng thứ tự.
- Tổng cộng: Tồn kho bị trừ 2 lần — đúng với 2 yêu cầu xuất kho.
- Phiên 2 phải chờ Phiên 1 xong thì mới tiếp tục — đây là hành vi đúng đắn.

---

## So sánh hai chế độ

| Tiêu chí | ORDER BY SOLUONGTIEUHAO DESC — có lỗi | ORDER BY MANL ASC — đã sửa |
|----------|---------------------------------------|---------------------------|
| Thứ tự khóa của Phiên 1 | Đường trước (0.5 kg lớn nhất), rồi Bột mì | Bột mì trước (MANL = 3 nhỏ nhất), rồi Đường |
| Thứ tự khóa của Phiên 2 | Bột mì trước (0.5 kg lớn nhất), rồi Đường | Bột mì trước (MANL = 3 nhỏ nhất), rồi Đường |
| Hai phiên cùng chiều không | Không — ngược chiều nhau | Có — cùng thứ tự khóa |
| Vòng chờ tròn có hình thành không | Có — phiên nào bị block trước sẽ chờ phiên kia | Không — Phiên 2 chưa giữ gì khi chờ |
| Kết quả Phiên bị block trước | Thất bại — LOCK_TIMEOUT sau 5 giây, ROLLBACK | Thành công |
| Kết quả Phiên còn lại | Thành công sau khi phiên kia rollback và nhả lock | Thành công sau khi xếp hàng chờ đúng thứ tự |
| Tồn kho bị trừ mấy lần | 1 lần — chỉ phiên thành công, phiên thất bại đã rollback | 2 lần — cả hai thành công |

---

## Tại sao 1 phiên thất bại, 1 phiên thành công trong kịch bản BUG?

Khi hai phiên chạy gần nhất lúc nhưng không đồng thời hoàn toàn:

1. **Phiên nào lock nguyên liệu thứ hai trước sẽ bị block ngay** vì phiên kia đang giữ.
2. **Đếm ngược 5 giây bắt đầu**. Sau 5 giây không giải phóng được lock, Oracle trả `ORA-30006` (LOCK_TIMEOUT).
3. **Phiên bị timeout ROLLBACK** — toàn bộ giao dịch hủy, lock được nhả.
4. **Phiên kia được gỡ block** — nhận lock vừa được nhả, tiếp tục xử lý và thành công.

Đây vẫn là **lỗi** vì:
- **Một giao dịch bị hủy ngẫu nhiên** — không dự đoán được phiên nào thất bại.
- **Thợ bếp phải làm lại từ đầu** — mất thời gian và công sức không cần thiết.
- **Hệ thống không có cơ chế tự retry** — người dùng phải tự nhấn lại.
- **Với tải cao** (nhiều phiên cùng lúc), tỷ lệ thất bại tăng theo và hiệu năng hệ thống giảm đáng kể.

---

## Tại sao gọi là Deadlock (Khóa Chết)?

Tên gọi "Deadlock" xuất phát từ hình ảnh hai người đang đối mặt nhau ở một
cửa hẹp: mỗi người giữ một nửa cánh cửa và cần nửa kia của người đối diện để
đi qua, nhưng không ai chịu nhường. Kết quả là cả hai đều đứng im mãi mãi.

Cụ thể trong hệ thống này:

- Thợ A đang giữ khóa Đường và cần Bột mì của Thợ B.
- Thợ B đang giữ khóa Bột mì và cần Đường của Thợ A.
- Không ai nhả khóa của mình ra trước khi có được khóa của người kia.
- Sau 5 giây không ai tiến được, hệ thống tự hủy cả hai giao dịch.

Giải pháp Lock Ordering phá vỡ tình huống này bằng cách đảm bảo tất cả mọi
người đều tiếp cận tài nguyên theo cùng một thứ tự cố định. Không còn ai
đi ngược chiều nữa, nên vòng chờ tròn không thể hình thành.
