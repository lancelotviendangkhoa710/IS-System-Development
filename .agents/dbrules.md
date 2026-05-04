# 3. QUY TẮC DATABASE (ORACLE PL/SQL) BẮT BUỘC TUÂN THỦ

## 3.1. QUY TẮC ĐẶT TÊN (NAMING CONVENTIONS)
- **Tên File (Vật lý):** Đặt bằng tiếng Anh, tối đa 3 từ, không khoảng trắng, chữ thường. Tiền tố: `func_`, `trg_`, `proc_` (VD: `func_calculate_stock.sql`).
- **Tên Object (Bên trong CSDL):** Viết HOA toàn bộ, tiếng Việt KHÔNG dấu, viết liền. Tiền tố: `FUNC_`, `PROC_`, `TRG_` (VD: `PROC_HUYDONVAHOANKHO`, `FUNC_SOLUONGKHADUNG`).
- **Tham số đầu vào (Parameters):** Bắt buộc có tiền tố `P_` (VD: `P_MASP`, `P_MADON`).
- **Biến cục bộ (Local Variables):** Bắt buộc có tiền tố `V_` (VD: `V_MAX_CAKES`, `V_MADON`).
- **Biến con trỏ/vòng lặp (Cursor/Loop):** Bắt buộc có tiền tố `ROW_` (VD: `FOR ROW_CT IN...`).
- **Ràng buộc (Constraints):** Đặt theo format `CK_[TÊN BẢNG VIẾT TẮT]_[TÊN CỘT/LOGIC]` (VD: `CK_KH_DIEM`).

## 3.2. ĐỊNH DẠNG VÀ PHONG CÁCH CODE (FORMATTING & COMMENTING)
- **Casing:** Tất cả các từ khóa SQL/PLSQL (`SELECT`, `INTO`, `UPDATE`, `BEGIN`, `IF`), tên bảng, tên cột đều phải viết **IN HOA (UPPERCASE)**.
- **Header Comment:** Ngay phía trên dòng `CREATE OR REPLACE...`, bắt buộc có 1 dòng comment giải thích ngắn gọn bằng tiếng Việt có dấu (VD: `-- Procedure Xử lý rủi ro Hủy/Bom hàng và hoàn kho`).
- **Step Comments:** Trong thân PL/SQL, logic phải được chia thành các bước rõ ràng, đánh số thứ tự (VD: `-- 1. Kiểm tra...`, `-- 2. Chốt án tử...`, `-- 3. Quét & Hoàn kho...`).
- **Thư mục `./01_tables`:**
  + CẤM viết bất kỳ ghi chú (comment) nào trong tất cả các file của thư mục này.
  + Mọi thay đổi thuộc tính bằng lệnh `ALTER TABLE` BẮT BUỘC phải viết gom xuống phía dưới cùng của file.
  + Tuân thủ tuyệt đối bố cục các bảng đã tạo trước đó.
  + Tất cả Khóa chính (Primary Key) phải dùng cú pháp: `NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY`.

## 3.3. LOGIC NGHIỆP VỤ & BEST PRACTICES
- **CẤM Hardcode ID:** Tuyệt đối không gán cứng các ID trạng thái hoặc danh mục (VD: `MATRANGTHAI = 5`). Bắt buộc phải query động dựa trên tên (VD: `SELECT MATRANGTHAI INTO V_MATT_HUY FROM TRANGTHAIDON WHERE UPPER(TENTRANGTHAI) = 'HỦY';`).
- **Sử dụng `RETURNING INTO`:** Thay vì dùng `INSERT/UPDATE` rồi `SELECT` lại, hãy dùng mệnh đề `RETURNING [CỘT] INTO [BIẾN V_]` để tối ưu hiệu năng.
- **An toàn dữ liệu Null:** Luôn bọc các phép toán tính toán hoặc hàm `SUM`, `MIN`, `MAX` bằng hàm `NVL(..., 0)` để tránh lỗi do giá trị NULL.
- **Xử lý Trigger:** Khi viết Trigger, luôn dùng các khối `IF INSERTING THEN... ELSIF UPDATING THEN... ELSIF DELETING THEN...` để phân tách hành động rõ ràng.

## 3.4. QUY TẮC NÂNG CAO (GIAO TIẾP JAVA & BẮT LỖI)
- **Master-Detail & CUD Phức tạp:** BẮT BUỘC đóng gói bằng Stored Procedure. CẤM để logic lặp Insert rớt xuống tầng Java.
- **Truyền danh sách (List):** Giao tiếp List từ Java xuống Oracle phải qua định dạng chuỗi JSON và parse bằng hàm `JSON_TABLE`.
- **Quản lý Giao dịch (Transaction):** 
  + Chỉ gọi `COMMIT;` khi hoàn tất tất cả luồng chạy (ngay trước EXCEPTION).
  + Mọi Procedure thao tác DML phải có khối bẫy lỗi ở cuối:
    ```sql
    EXCEPTION WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20xxx, 'Thông báo lỗi tiếng Việt có dấu: ' || SQLERRM);
    ```
- **Thông báo lỗi (Error Messages):** Đoạn text ném ra trong `RAISE_APPLICATION_ERROR` phải là tiếng Việt **CÓ DẤU**.