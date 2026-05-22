# CHƯƠNG 4: XỬ LÝ TRUY XUẤT ĐỒNG THỜI

Trong hệ thống quản lý tiệm bánh, nhiều nhân viên (thu ngân, quản lý kho, thợ bánh) có thể đồng thời thao tác trên cùng một tập dữ liệu — từ việc bán hàng, cập nhật giá, nhập kho đến điều chỉnh thông tin nguyên liệu. Khi nhiều giao dịch (*transaction*) truy xuất đồng thời mà thiếu cơ chế kiểm soát thích hợp, hệ thống có thể rơi vào các hiện tượng bất thường gây mất tính toàn vẹn dữ liệu.

Chương này phân tích bốn hiện tượng lỗi phổ biến nhất trong xử lý giao dịch đồng thời — *Lost Update*, *Non-repeatable Read*, *Phantom Read* và *Deadlock* — dưới góc nhìn lý thuyết *Serializability* (phân lịch tuần tự). Kịch bản minh họa thực thi đầy đủ (bao gồm SQL từng phiên và Java demo) được trình bày trong file [`chapter4_demo.md`](chapter4_demo.md).

**Môi trường công nghệ:**
- Cơ sở dữ liệu: Oracle Database 12c trở lên
- Mức cô lập mặc định của Oracle: READ COMMITTED

---

## 4.1. Lost Update (Mất cập nhật)

### 4.1.1. Định nghĩa

Lost Update là một vấn đề về tính toàn vẹn dữ liệu xảy ra khi hai hoặc nhiều transaction đồng thời đọc cùng một mục dữ liệu, thực hiện quyết định nghiệp vụ dựa trên giá trị đã đọc, rồi ghi lại kết quả. Do mỗi transaction ra quyết định trên một snapshot đã lỗi thời, hệ thống xử lý đồng thời nhiều thao tác mà không phát hiện được rằng tổng tài nguyên tiêu hao đã vượt quá lượng tồn tại.

Xét dưới góc nhìn lý thuyết Conflict Serializability, tồn tại cặp xung đột **WW** (*write-write conflict*) giữa các transaction — lịch thực thi kết quả không tương đương với bất kỳ lịch tuần tự nào, vi phạm tính khả tuần tự hóa.

Ở mức cô lập mặc định READ COMMITTED của Oracle, mỗi câu lệnh SELECT chỉ nhìn thấy dữ liệu đã được commit tại thời điểm câu lệnh bắt đầu, nhưng không giữ khóa đọc (*read lock*) trên các dòng đã đọc. Điều này có nghĩa là: dù câu lệnh UPDATE có sử dụng công thức delta (`SOLUONGTON = SOLUONGTON - n`), quyết định nghiệp vụ dựa trên kết quả SELECT trước đó vẫn có thể đã lỗi thời khi lần lượt mỗi transaction được phép ghi.

### 4.1.2. Tình huống minh họa hiện tượng lỗi

Tiệm bánh có hai quầy thu ngân hoạt động song song. Cuối giờ sáng, tồn kho chỉ còn **5 cái Bánh Bông Lan Trứng** (MASP = 1001). Cùng một lúc:

- **Thu ngân A (Quầy 1)** nhận đơn hàng khách muốn mua **3 cái**.
- **Thu ngân B (Quầy 2)** nhận đơn hàng khách khác cũng muốn mua **3 cái**.

Phần mềm tại mỗi quầy thực hiện hai bước: (1) đọc tồn kho hiện tại để kiểm tra xem có đủ hàng không, (2) nếu đủ thì thực hiện UPDATE trừ số lượng đã bán.

Vì cả hai thu ngân thao tác gần như cùng lúc, cả hai đều đọc được **5 bánh** và đều kết luận là đủ hàng để bán. Thu ngân A bán xong, kho còn 2 bánh. Thu ngân B tiếp tục thực thi, áp dụng delta lên giá trị **hiện tại** (2 bánh), kết quả: 2 - 3 = **-1 bánh** — tồn kho **âm**. Hệ thống bán vượt quá số hàng thực có mà không cảnh báo.

### 4.1.3. Cách khắc phục và giải pháp

#### 4.1.3.1. Giải pháp lý thuyết & Cấu hình: Mức cô lập SERIALIZABLE

Để ngăn chặn Lost Update, giải pháp được áp dụng là nâng mức cô lập giao dịch lên **SERIALIZABLE** thông qua lệnh:

```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

Ở mức cô lập SERIALIZABLE, Oracle triển khai cơ chế **Snapshot Isolation**: mỗi giao dịch đọc dữ liệu từ một snapshot nhất quán được chụp tại **thời điểm giao dịch bắt đầu**. Khi một giao dịch cố gắng thực thi lệnh UPDATE trên một dòng đã bị giao dịch khác sửa đổi và commit **sau** khi snapshot được chụp, Oracle phát hiện xung đột và ném ngoại lệ:

```
ORA-08177: can't serialize access for this transaction
```

Giao dịch nhận lỗi buộc phải `ROLLBACK` và thực hiện lại (*retry*) toàn bộ giao dịch. Lần retry sẽ đọc được giá trị mới nhất đã commit, tái kiểm tra điều kiện nghiệp vụ với dữ liệu đúng đắn — loại bỏ hoàn toàn hiện tượng bán vượt tồn kho.

---

## 4.2. Non-repeatable Read (Đọc không lặp lại)

### 4.2.1. Định nghĩa

Non-repeatable Read là một vấn đề về tính nhất quán dữ liệu xảy ra khi một transaction đọc cùng một dòng dữ liệu hai hoặc nhiều lần nhưng lại nhận được các giá trị khác nhau trong mỗi lần đọc.

Ở mức cô lập READ COMMITTED (mặc định Oracle), mỗi câu lệnh SELECT tạo snapshot tại thời điểm **câu lệnh** bắt đầu chứ không phải tại thời điểm **giao dịch** bắt đầu. Điều này có nghĩa là: hai câu lệnh SELECT thực hiện trong cùng một giao dịch có thể nhìn thấy các phiên bản dữ liệu khác nhau nếu có giao dịch khác commit thay đổi ở giữa.

### 4.2.2. Tình huống minh họa hiện tượng lỗi

Một khách hàng (tạm gọi là Khách A) đang đặt mua **Bánh Tuyển Chọn Bơ Sữa** (MASP = 1001). Màn hình tại quầy thu ngân hiển thị giá: **150.000đ**. Khách A đồng ý mức giá này và bắt đầu điền thông tin đặt hàng.

Trong lúc khách đang điền thông tin, quản lý tiệm cập nhật giá bánh từ **150.000đ** lên **180.000đ** và commit ngay lập tức.

Khi Khách A bấm **"Xác nhận thanh toán"**, hệ thống thực hiện lần đọc thứ hai trong cùng giao dịch để lấy giá tính tiền. Lần này nhận được **180.000đ**. Khách bị trừ **180.000đ** trong khi đã xem và đồng ý với giá **150.000đ** — thiệt hại **30.000đ**.

### 4.2.3. Cách khắc phục và giải pháp

#### 4.2.3.1. Giải pháp lý thuyết & Cấu hình: Mức cô lập SERIALIZABLE

Để ngăn chặn Non-repeatable Read, giải pháp được áp dụng là nâng mức cô lập giao dịch lên **SERIALIZABLE**:

```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

Ở mức cô lập SERIALIZABLE, Oracle triển khai cơ chế **Snapshot Isolation**: mỗi giao dịch sẽ đọc dữ liệu từ một snapshot nhất quán được chụp tại **thời điểm giao dịch bắt đầu**. Mọi thay đổi do các giao dịch khác commit sau thời điểm chụp sẽ **không hiển thị** đối với giao dịch hiện tại — dù có bao nhiêu câu SELECT được thực thi trong giao dịch đó.

---

## 4.3. Phantom Read (Đọc bóng ma)

### 4.3.1. Định nghĩa

Phantom Read là một vấn đề về tính nhất quán dữ liệu xảy ra khi một transaction thực hiện cùng một truy vấn có điều kiện hai hoặc nhiều lần, nhưng tập kết quả trả về giữa các lần truy vấn lại khác nhau do các dòng mới được thêm vào hoặc bị xóa bởi một transaction khác đã commit trong khoảng thời gian giữa hai lần truy vấn.

Các dòng dữ liệu bất ngờ xuất hiện hoặc biến mất trong tập kết quả được gọi là *phantom rows*. Phantom Read khác với Non-repeatable Read: Non-repeatable Read là **giá trị của dòng đã tồn tại thay đổi**, còn Phantom Read là **số lượng dòng trong tập kết quả thay đổi**.

### 4.3.2. Tình huống minh họa hiện tượng lỗi

Quản lý kho thực hiện **kiểm kê tổng tồn kho nguyên liệu bơ sữa** (MANL = 5) để lập báo cáo. Quản lý truy vấn tổng tồn và ghi vào đầu báo cáo: *"Tồn đầu kỳ: 100 kg"*.

Trong lúc quản lý đang soạn phần còn lại của báo cáo, nhân viên kho nhập thêm **lô bơ sữa mới 20 kg** vào hệ thống và commit.

Khi quản lý truy vấn lại để điền số cuối báo cáo, hệ thống trả về **120 kg**. Báo cáo có hai con số mâu thuẫn (100 và 120) cho cùng một mục tồn kho trong cùng một lần kiểm kê — mất tính tin cậy.

### 4.3.3. Cách khắc phục và giải pháp

#### 4.3.3.1. Giải pháp lý thuyết & Cấu hình: Mức cô lập SERIALIZABLE

Để ngăn chặn Phantom Read, giải pháp được áp dụng là nâng mức cô lập giao dịch lên **SERIALIZABLE**:

```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
```

Khi giao dịch chạy ở mức SERIALIZABLE, Oracle triển khai **Snapshot Isolation**: toàn bộ các truy vấn trong giao dịch đọc từ snapshot chụp tại **thời điểm giao dịch bắt đầu**. Các dòng mới do giao dịch khác INSERT và commit sau thời điểm đó sẽ **không xuất hiện** trong tập kết quả — *phantom rows* bị loại bỏ hoàn toàn.

---

## 4.4. Deadlock (Tắc nghẽn)

### 4.4.1. Định nghĩa

Deadlock là tình trạng tắc nghẽn xảy ra khi hai hoặc nhiều transaction rơi vào trạng thái chờ đợi vòng tròn: mỗi transaction đang giữ khóa trên một tập tài nguyên trong khi đồng thời chờ tài nguyên đang bị transaction khác giữ khóa — tạo thành chu trình không thể tự phá vỡ.

Deadlock được hình thức hóa thông qua **Wait-for Graph**: mỗi transaction là một đỉnh, mỗi cạnh có hướng T₁ → T₂ biểu diễn "T₁ đang chờ T₂ giải phóng tài nguyên". Deadlock xảy ra **khi và chỉ khi** đồ thị chờ chứa **chu trình**.

Deadlock là hệ quả tất yếu khi bốn điều kiện Coffman đồng thời xảy ra:
1. **Mutual Exclusion**: Tài nguyên chỉ được một transaction giữ khóa độc quyền tại một thời điểm.
2. **Hold and Wait**: Transaction đang giữ ít nhất một khóa và tiếp tục chờ thêm khóa khác.
3. **No Preemption**: Khóa chỉ được giải phóng tự nguyện bởi transaction đang giữ.
4. **Circular Wait**: Tồn tại chuỗi T₁ → T₂ → ... → Tₙ → T₁ chờ đợi lẫn nhau.

Khi phát hiện chu trình, Oracle tự động chọn một transaction làm "nạn nhân" và ném ngoại lệ `ORA-00060`. Transaction nhận lỗi có trách nhiệm tự `ROLLBACK` để giải phóng khóa.

### 4.4.2. Tình huống minh họa hiện tượng lỗi

Ban quản lý quyết định tăng mức tồn an toàn (MUCTONANTOAN) của hai nguyên liệu: **Bột mì đa dụng** (MANL = 1001) và **Đường trắng** (MANL = 1002). Hai nhân viên kho cập nhật đồng thời nhưng theo thứ tự ngược nhau:

- **Nhân viên A**: cập nhật 1001 trước → rồi 1002.
- **Nhân viên B**: cập nhật 1002 trước → rồi 1001.

Kết quả: A khóa dòng 1001 và chờ dòng 1002 (B đang giữ); B khóa dòng 1002 và chờ dòng 1001 (A đang giữ). Chu trình hình thành → Deadlock.

### 4.4.3. Cách khắc phục và giải pháp

#### 4.4.3.1. Giải pháp lý thuyết & Cấu hình: Chuẩn hóa thứ tự truy cập tài nguyên (Resource Ordering)

Để ngăn chặn Deadlock, giải pháp là **chuẩn hóa thứ tự truy cập tài nguyên** (*Resource Ordering*): tất cả các transaction bắt buộc phải truy cập tài nguyên theo **cùng thứ tự cố định** — cụ thể là theo giá trị khóa chính tăng dần (MANL nhỏ hơn trước).

Giải pháp này phá vỡ điều kiện **Circular Wait**: khi mọi transaction đều tuân thủ cùng thứ tự, đồ thị chờ chỉ có cạnh một chiều — không thể hình thành chu trình.

---

## Bảng tổng kết Chương 4

| Hiện tượng | Nguyên nhân gốc | Cơ chế lỗi | Giải pháp | Tầng khắc phục |
|:----------:|:---------------:|:-----------:|:---------:|:--------------:|
| **Lost Update** | Quyết định nghiệp vụ dựa trên snapshot đã lỗi thời | Hai transaction đều được phép thực thi dựa trên cùng dữ liệu cũ | `SERIALIZABLE` + retry ORA-08177 | Cấu hình session DB |
| **Non-repeatable Read** | Snapshot tạo theo từng câu lệnh (READ COMMITTED) | Giao dịch khác commit thay đổi giữa hai lần đọc trong cùng giao dịch | `SERIALIZABLE` | Cấu hình session DB |
| **Phantom Read** | Snapshot tạo theo từng câu lệnh (READ COMMITTED) | Giao dịch khác INSERT/DELETE làm thay đổi tập kết quả truy vấn | `SERIALIZABLE` | Cấu hình session DB |
| **Deadlock** | Truy cập tài nguyên theo thứ tự ngược → chu trình Wait-for Graph | Circular Wait: mỗi bên đợi tài nguyên của bên kia | Resource Ordering (khóa chính tăng dần) | Logic thứ tự xử lý nghiệp vụ |
