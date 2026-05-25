# Demo Phantom Read — Báo cáo kiểm kê phiếu nhập kho

Tài liệu này mô phỏng hiện tượng Phantom Read xảy ra khi lập báo cáo
kiểm kê phiếu nhập kho bằng JasperReports.

Giả sử tại thời điểm bắt đầu demo, cơ sở dữ liệu đang có đúng 5 phiếu nhập kho.

---

## Bối cảnh tình huống

Đây là ngày cuối tháng tại cửa hàng bánh. Chị Lan — quản lý kho — ngồi vào máy tính lúc
8 giờ tối và bấm nút Lập báo cáo kiểm kê phiếu nhập kho để chuẩn bị số liệu gửi ban
giám đốc vào sáng hôm sau. Hệ thống hỏi: Tìm thấy 5 phiếu nhập kho trong tháng. Bạn
có muốn lập báo cáo không? Chị bấm OK.

Cùng lúc đó ở phòng kho bên cạnh, anh Minh — nhân viên kho — đang nhập liệu chuyến
hàng bột mì về muộn. Anh hoàn thành và bấm xác nhận lúc 8 giờ 15 phút, ngay trong khi
hệ thống của chị Lan vẫn đang chạy báo cáo ở bước xử lý nặng.

Khi báo cáo xuất ra, chị Lan ngạc nhiên: tiêu đề ghi Tổng 5 phiếu nhưng bảng chi tiết
lại liệt kê 6 dòng. Chị không hiểu phiếu thứ 6 từ đâu ra vì lúc chị bấm OK thì nó
chưa tồn tại. Đây chính là hiện tượng Phantom Read: một bản ghi bất ngờ xuất hiện
giữa hai lần đọc trong cùng một giao dịch, làm cho báo cáo mâu thuẫn với chính nó
ngay từ khi vừa xuất ra.

---

## Kịch bản 1 — Có Lỗi (Isolation Level: READ COMMITTED)

Ở mức READ COMMITTED, mỗi lần Oracle thực thi một câu lệnh SELECT, nó sẽ tạo
ra một bản chụp dữ liệu mới tại đúng thời điểm câu lệnh đó chạy, chứ không
phải tại thời điểm giao dịch bắt đầu. Điều này có nghĩa là trong cùng một
giao dịch, hai lần SELECT chạy ở hai thời điểm khác nhau có thể trả về kết
quả khác nhau nếu có ai đó commit dữ liệu mới ở giữa.

| Bước | Phiên 1 (Quản lý kho — lập báo cáo) | Phiên 2 (Nhân viên kho — nhập hàng) | Giải thích |
|------|--------------------------------------|--------------------------------------|------------|
| 1 | `SELECT COUNT(*) FROM PHIEUNHAPKHO;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`       5`<br>`1 row selected.` | `SELECT COUNT(*) FROM PHIEUNHAPKHO;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`       5`<br>`1 row selected.` | Trạng thái ban đầu. Cả hai phiên đều nhìn thấy cùng 5 phiếu nhập kho. Đây là điểm xuất phát chung trước khi bất kỳ ai làm gì. |
| 2 | `SET TRANSACTION ISOLATION LEVEL READ COMMITTED;`<br><br>Output:<br>`Transaction set.` | Không có hành động. | Phiên 1 bắt đầu giao dịch ở mức READ COMMITTED. Đây là mức mặc định của Oracle. Hệ quả là mỗi câu SELECT sẽ tự tạo snapshot riêng của nó — không có snapshot chung cho cả giao dịch. |
| 3 | `SELECT COUNT(*) FROM PHIEUNHAPKHO;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`       5`<br>`1 row selected.`<br><br>Hệ thống hiện dialog: "Tìm thấy 5 phiếu nhập kho. Bạn có muốn lập báo cáo không?" Người quản lý bấm OK. | Không có hành động. | Đây là lần đọc thứ nhất. Phiên 1 đếm được 5 phiếu và lưu con số này lại để ghi vào tiêu đề của báo cáo PDF sau này. Sau khi người dùng bấm OK, hệ thống tiến vào bước xử lý nặng. |
| 4 | `DBMS_SESSION.SLEEP(20);`<br><br>Đang tạm dừng 20 giây... Chưa đọc danh sách phiếu. | Không có hành động. | Phiên 1 đang trong trạng thái chờ. Đây là khoảng thời gian trống để Phiên 2 có thể thêm dữ liệu mới vào cơ sở dữ liệu mà Phiên 1 không hề biết. |
| 5 | Đang chờ (vẫn trong sleep). | `INSERT INTO PHIEUNHAPKHO (MAPHIEU, NGAYNHAP, MANCC, MANV)`<br>`  VALUES ('PNK006', SYSDATE, 1, 3);`<br><br>Output:<br>`1 row created.`<br><br>`COMMIT;`<br><br>Output:<br>`Commit complete.` | Phiên 2 tạo thành công phiếu nhập kho thứ 6 và commit. Dữ liệu mới này đã chính thức tồn tại trong cơ sở dữ liệu. Vì Phiên 1 đang ở mức READ COMMITTED, bất kỳ câu SELECT nào chạy sau thời điểm này đều có thể thấy phiếu mới này. |
| 6 | `OPEN P_CURSOR_OUT FOR`<br>`  SELECT MAPHIEU, NGAYNHAP, TONGTIEN`<br>`  FROM PHIEUNHAPKHO ORDER BY NGAYNHAP;`<br><br>Output:<br>`MAPHIEU  NGAYNHAP    TONGTIEN`<br>`-------  ----------  ----------`<br>`PNK001   01/05/2025     1500000`<br>`PNK002   05/05/2025     2000000`<br>`PNK003   10/05/2025     1800000`<br>`PNK004   15/05/2025     2500000`<br>`PNK005   20/05/2025     1200000`<br>`PNK006   25/05/2025     3000000`<br>`6 rows selected.` | Xong việc, không có hành động thêm. | Đây là lần đọc thứ hai trong cùng một giao dịch. Vì mức READ COMMITTED tạo snapshot mới tại thời điểm câu SELECT này chạy, Phiên 1 nhìn thấy dữ liệu mới nhất — bao gồm phiếu vừa được Phiên 2 commit. Kết quả trả về 6 dòng thay vì 5 dòng như lần đầu. |
| 7 | Hệ thống so sánh: soPhieuDaDem = 5, danhSach.size() = 6. Số liệu không khớp.<br><br>Thông báo lỗi: "Phát hiện Phantom Read! Đã đếm 5 phiếu nhưng danh sách báo cáo có 6 phiếu."<br><br>File PDF được tạo ra:<br>Tiêu đề ghi: "Tổng số phiếu kiểm kê: 5"<br>Bảng chi tiết có: 6 dòng (bao gồm PNK006 là phiếu bóng ma) | Không có hành động. | Đây chính là lỗi Phantom Read. Trong cùng một giao dịch, hai lần đọc dữ liệu trả về kết quả không nhất quán. Báo cáo PDF bị sai ngay từ khi xuất ra: dòng tiêu đề nói 5 phiếu nhưng bảng liệt kê lại có 6 phiếu. Phiếu PNK006 xuất hiện như một bóng ma — người quản lý không biết nó đến từ đâu. |

**Hậu quả cụ thể trong file PDF:**

- Dòng tiêu đề (header) ghi: Tổng số phiếu kiểm kê = 5
- Bảng chi tiết (detail list) có: 6 dòng dữ liệu
- Phiếu PNK006 là bóng ma — xuất hiện trong danh sách mà người quản lý không hề biết, vì lúc họ bấm OK thì nó chưa tồn tại.

---

## Kịch bản 2 — Đã Sửa (Isolation Level: SERIALIZABLE)

Ở mức SERIALIZABLE, ngay khi giao dịch bắt đầu, Oracle chụp lại toàn bộ
trạng thái cơ sở dữ liệu tại thời điểm đó. Tất cả các câu SELECT trong cùng
giao dịch đều chỉ đọc từ bản chụp này — bất kể có bao nhiêu phiên khác commit
dữ liệu mới trong khoảng thời gian đó, Phiên 1 đều không thấy.

| Bước | Phiên 1 (Quản lý kho — lập báo cáo) | Phiên 2 (Nhân viên kho — nhập hàng) | Giải thích |
|------|--------------------------------------|--------------------------------------|------------|
| 1 | `SELECT COUNT(*) FROM PHIEUNHAPKHO;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`       5`<br>`1 row selected.` | `SELECT COUNT(*) FROM PHIEUNHAPKHO;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`       5`<br>`1 row selected.` | Trạng thái ban đầu giống hệt kịch bản 1. Cả hai phiên cùng thấy 5 phiếu. Đây là điểm xuất phát chung. |
| 2 | `SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;`<br><br>Output:<br>`Transaction set.` | Không có hành động. | Đây là điểm khác biệt then chốt. Ngay khi câu lệnh này được thực thi, Oracle đóng băng dữ liệu mà Phiên 1 được phép nhìn thấy. Bất kỳ thay đổi nào từ phiên khác commit sau thời điểm này đều bị Phiên 1 bỏ qua hoàn toàn. |
| 3 | `SELECT COUNT(*) FROM PHIEUNHAPKHO;`<br><br>Output:<br>`COUNT(*)`<br>`--------`<br>`       5`<br>`1 row selected.`<br><br>Hệ thống hiện dialog: "Tìm thấy 5 phiếu nhập kho. Bạn có muốn lập báo cáo không?" Người quản lý bấm OK. | Không có hành động. | Lần đọc thứ nhất. Phiên 1 đếm được 5 phiếu từ bản chụp. Con số 5 này sẽ được ghi vào tiêu đề báo cáo. Bản chụp vẫn đang được Oracle gìn giữ cho đến khi giao dịch kết thúc. |
| 4 | `DBMS_SESSION.SLEEP(20);`<br><br>Đang tạm dừng 20 giây... Bản chụp vẫn được giữ nguyên. | Không có hành động. | Phiên 1 đang chờ. Bản chụp của Oracle không bị ảnh hưởng bởi việc Phiên 1 đang ngủ. Bản chụp chỉ bị xóa khi giao dịch kết thúc bằng COMMIT hoặc ROLLBACK. |
| 5 | Đang chờ (vẫn trong sleep). | `INSERT INTO PHIEUNHAPKHO (MAPHIEU, NGAYNHAP, MANCC, MANV)`<br>`  VALUES ('PNK006', SYSDATE, 1, 3);`<br><br>Output:<br>`1 row created.`<br><br>`COMMIT;`<br><br>Output:<br>`Commit complete.` | Phiên 2 tạo và commit thành công phiếu thứ 6. Trong cơ sở dữ liệu thật, giờ có 6 phiếu. Nhưng bản chụp của Phiên 1 vẫn chỉ có 5 phiếu — sự kiện này xảy ra hoàn toàn ngoài tầm nhìn của Phiên 1. |
| 6 | `OPEN P_CURSOR_OUT FOR`<br>`  SELECT MAPHIEU, NGAYNHAP, TONGTIEN`<br>`  FROM PHIEUNHAPKHO ORDER BY NGAYNHAP;`<br><br>Output:<br>`MAPHIEU  NGAYNHAP    TONGTIEN`<br>`-------  ----------  ----------`<br>`PNK001   01/05/2025     1500000`<br>`PNK002   05/05/2025     2000000`<br>`PNK003   10/05/2025     1800000`<br>`PNK004   15/05/2025     2500000`<br>`PNK005   20/05/2025     1200000`<br>`5 rows selected.` | Xong việc, không có hành động thêm. | Lần đọc thứ hai trong cùng giao dịch. Mặc dù phiếu PNK006 đã tồn tại trong cơ sở dữ liệu thật, Phiên 1 vẫn chỉ đọc từ bản chụp của mình — bản chụp được tạo ra trước khi Phiên 2 commit. Kết quả vẫn là 5 dòng. Phantom Read đã bị ngăn chặn. |
| 7 | Hệ thống so sánh: soPhieuDaDem = 5, danhSach.size() = 5. Số liệu khớp.<br><br>Thông báo thành công: "Báo cáo đồng nhất! 5 phiếu, không phát hiện Phantom Read."<br><br>File PDF được tạo ra:<br>Tiêu đề ghi: "Tổng số phiếu kiểm kê: 5"<br>Bảng chi tiết có: 5 dòng (không có PNK006) | Không có hành động. | Báo cáo hoàn toàn chính xác và nhất quán. Tiêu đề và bảng chi tiết cùng hiển thị con số 5. Người quản lý có thể tin tưởng vào báo cáo này vì toàn bộ dữ liệu được đọc từ cùng một thời điểm. |

**Kết quả cụ thể trong file PDF:**

- Dòng tiêu đề (header) ghi: Tổng số phiếu kiểm kê = 5
- Bảng chi tiết (detail list) có: 5 dòng dữ liệu
- Phiếu PNK006 mà Phiên 2 vừa thêm không xuất hiện — đúng như mong muốn.

---

## So sánh hai mức Isolation

| Tiêu chí | READ COMMITTED — có lỗi | SERIALIZABLE — đã sửa |
|----------|-------------------------|----------------------|
| Bản chụp dữ liệu được tạo khi nào | Mỗi câu SELECT tự tạo một bản chụp mới tại thời điểm nó chạy | Một bản chụp duy nhất được tạo ngay khi giao dịch bắt đầu |
| Phiếu PNK006 có xuất hiện trong báo cáo không | Có, vì Phiên 2 đã commit trước khi câu SELECT thứ 2 chạy | Không, vì bản chụp đã bị đóng băng từ trước |
| Tiêu đề PDF ghi bao nhiêu | 5 phiếu (con số đếm lần đầu) | 5 phiếu (chính xác) |
| Bảng chi tiết có bao nhiêu dòng | 6 dòng (lệch 1 dòng so với tiêu đề) | 5 dòng (khớp với tiêu đề) |
| Kết luận | Phantom Read xảy ra — báo cáo bị sai | Phantom Read bị ngăn chặn — báo cáo đúng |

---

## Tại sao gọi là Phantom Read (Đọc Bóng Ma)?

Tên gọi "Phantom Read" xuất phát từ hình ảnh: một hàng dữ liệu bất ngờ
xuất hiện trong kết quả trả về của một giao dịch, dù giao dịch đó chưa hề
thêm nó vào, giống như một bóng ma hiện ra.

Cụ thể trong ví dụ này:

- Phiên 1 bấm OK với thông báo tìm thấy 5 phiếu.
- Phiên 1 chưa kết thúc giao dịch, nhưng Phiên 2 đã len vào và thêm phiếu thứ 6.
- Khi Phiên 1 đọc lại, phiếu PNK006 xuất hiện như bóng ma — người dùng không
  biết nó đến từ đâu, và nó làm cho báo cáo bị sai ngay trước mặt họ.

Mức SERIALIZABLE loại bỏ bóng ma bằng cách đảm bảo rằng mọi thứ giao dịch thấy
đều nhất quán với thời điểm nó bắt đầu — như đang đọc từ một bản chụp ảnh
được đóng khung ngay từ đầu và không ai được phép thay đổi.
