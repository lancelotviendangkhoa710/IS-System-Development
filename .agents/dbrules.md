# 3. QUY TẮC DATABASE (ORACLE PL/SQL) BẮT BUỘC TUÂN THỦ

## 3.1. QUY TẮC ĐẶT TÊN (NAMING CONVENTIONS)
- **Tên File (Vật lý):** Đặt bằng tiếng Anh, tối đa 3 từ, không khoảng trắng, chữ thường. Tiền tố: `func_`, `trg_`, `proc_` (VD: `func_calculate_stock.sql`).
- **Tên Object (Bên trong CSDL):** Viết HOA toàn bộ, tiếng Việt KHÔNG dấu, viết liền. Tiền tố: `FUNC_`, `PROC_`, `TRG_` (VD: `PROC_HUYDONVAHOANKHO`, `FUNC_SOLUONGKHADUNG`).
- **Tên View:** Tiền tố `VW_`, viết HOA toàn bộ, tiếng Việt KHÔNG dấu, viết liền (VD: `VW_CHITIETHOADONIN`, `VW_PHIEUHENLAYBANH`). File lưu tại `database_scripts/07_views/`.
- **Tham số đầu vào (Parameters):** Bắt buộc có tiền tố `P_` (VD: `P_MASP`, `P_MADON`).
- **Biến cục bộ (Local Variables):** Bắt buộc có tiền tố `V_` (VD: `V_MAX_CAKES`, `V_MADON`).
- **Biến con trỏ/vòng lặp (Cursor/Loop):** Bắt buộc có tiền tố `ROW_` (VD: `FOR ROW_CT IN...`).
- **Ràng buộc (Constraints):** Đặt theo format `CK_[TÊN BẢNG VIẾT TẮT]_[TÊN CỘT/LOGIC]` (VD: `CK_KH_DIEM`).

## 3.2. ĐỊNH DẠNG VÀ PHONG CÁCH CODE (FORMATTING & COMMENTING)
- **Casing:** Tất cả các từ khóa SQL/PLSQL (`SELECT`, `INTO`, `UPDATE`, `BEGIN`, `IF`), tên bảng, tên cột đều phải viết **IN HOA (UPPERCASE)**.
- **Header Comment:** Ngay phía trên dòng `CREATE OR REPLACE...`, bắt buộc có 1 dòng comment giải thích ngắn gọn bằng tiếng Việt có dấu (VD: `-- Procedure xử lý rủi ro hủy/bom hàng và hoàn kho`).
- **Step Comments:** Trong thân PL/SQL, logic phải được chia thành các bước rõ ràng, đánh số thứ tự (VD: `-- 1. Kiểm tra...`, `-- 2. Tính toán...`, `-- 3. Cập nhật...`).
- **Thư mục `./01_tables`:**
  + CẤM viết bất kỳ ghi chú (comment) nào trong tất cả các file của thư mục này.
  + Mọi thay đổi thuộc tính bằng lệnh `ALTER TABLE` BẮT BUỘC phải viết gom xuống phía dưới cùng của file.
  + Tuân thủ tuyệt đối bố cục các bảng đã tạo trước đó.
  + Tất cả Khóa chính (Primary Key) phải dùng cú pháp: `NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY`.

## 3.3. LOGIC NGHIỆP VỤ & BEST PRACTICES
- **CẤM Hardcode ID:** Tuyệt đối không gán cứng các ID trạng thái hoặc danh mục (VD: `MATRANGTHAI = 5`). Bắt buộc phải query động dựa trên tên (VD: `SELECT MATRANGTHAI INTO V_MA_HUY FROM TRANGTHAIDON WHERE UPPER(TENTRANGTHAI) = N'HỦY';`).
- **Sử dụng `RETURNING INTO`:** Thay vì dùng `INSERT/UPDATE` rồi `SELECT` lại, hãy dùng mệnh đề `RETURNING [CỘT] INTO [BIẾN V_]` để tối ưu hiệu năng.
- **An toàn dữ liệu Null:** Luôn bọc các phép toán tính toán hoặc hàm `SUM`, `MIN`, `MAX` bằng hàm `NVL(..., 0)` để tránh lỗi do giá trị NULL.
- **Xử lý Trigger:** Khi viết Trigger, luôn dùng các khối `IF INSERTING THEN... ELSIF UPDATING THEN... ELSIF DELETING THEN...` để phân tách hành động rõ ràng.
- **Phong cách code:** Sử dụng phong cách phù hợp với độ phức tạp thực tế — không viết code phức tạp cho logic đơn giản và ngược lại.

## 3.4. QUY TẮC NÂNG CAO (GIAO TIẾP JAVA & BẮT LỖI)
- **Master-Detail & CUD Phức tạp:** BẮT BUỘC đóng gói bằng Stored Procedure. CẤM để logic lặp Insert rớt xuống tầng Java.
- **Truyền danh sách (List):** Giao tiếp List từ Java xuống Oracle phải qua định dạng chuỗi JSON và parse bằng hàm `JSON_TABLE`.
- **Quản lý Giao dịch (Transaction):**
  + Chỉ gọi `COMMIT;` khi hoàn tất toàn bộ luồng xử lý (đặt ngay trước khối `EXCEPTION`).
  + Mọi Procedure thao tác DML phải có khối bẫy lỗi ở cuối:
    ```sql
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_[TEN_MA_LOI], 'Mô tả lỗi tiếng Việt: ' || SQLERRM);
    ```
- **Thông báo lỗi (Error Messages):** Đoạn text ném ra trong `RAISE_APPLICATION_ERROR` phải là tiếng Việt **CÓ DẤU** (VD: `'Số lượng bánh đã hết!'`). Java sẽ bắt và hiển thị trực tiếp message này lên Alert cho người dùng.

## 3.5. BẢNG MÃ LỖI CHUẨN (PKG_ERROR_CODES)
Toàn bộ mã lỗi được định nghĩa tập trung trong `PKG_ERROR_CODES` tại file `database_scripts/03_packages/package_error_codes.sql`. **CẤM** dùng số âm hardcode trực tiếp trong Procedure hay Trigger. Bắt buộc tham chiếu qua tên hằng số.

### Phân vùng mã lỗi

| Dải mã | Nhóm |
|---|---|
| `-20000` ~ `-20099` | Hệ thống / Dùng chung |
| `-20100` ~ `-20199` | Nhân sự & Khách hàng & Hạng thành viên |
| `-20200` ~ `-20299` | Sản phẩm & Danh mục sản phẩm |
| `-20300` ~ `-20399` | Kho & Công thức |
| `-20400` ~ `-20499` | Đơn hàng |
| `-20500` ~ `-20599` | Tài chính |

---

#### Nhóm 00 — Hệ thống / Dùng chung

| Tên hằng số | Mã | Mô tả |
|---|---|---|
| `ERR_HE_THONG_KHOIPHUC` | `-20001` | Lỗi hệ thống khi khôi phục dữ liệu |
| `ERR_THANH_TOAN_GIAO_DICH` | `-20002` | Lỗi hệ thống khi thanh toán giao dịch |
| `ERR_HUY_DON_GIAO_DICH` | `-20003` | Lỗi hệ thống giao dịch khi thực thi hủy đơn |
| `ERR_HUY_XUAT_KHO` | `-20004` | Lỗi hệ thống khi xuất kho sản xuất |
| `ERR_HUY_TAO_DON` | `-20005` | Lỗi hệ thống khi tạo đơn hàng |

---

#### Nhóm 01 — Nhân sự & Khách hàng

| Tên hằng số | Mã | Mô tả |
|---|---|---|
| `ERR_CA_KHONG_TON_TAI` | `-20101` | Ca làm việc không tồn tại hoặc đã đóng |
| `ERR_CA_MO_HE_THONG` | `-20102` | Lỗi hệ thống khi mở ca |
| `ERR_CA_DONG_HE_THONG` | `-20103` | Lỗi hệ thống khi đóng ca |
| `ERR_VAITRO_THEM_HE_THONG` | `-20104` | Lỗi hệ thống khi thêm vai trò |
| `ERR_VAITRO_KHONG_TON_TAI_CN` | `-20105` | Không tìm thấy vai trò khi cập nhật |
| `ERR_VAITRO_CAPNHAT_HE_THONG` | `-20106` | Lỗi hệ thống khi cập nhật vai trò |
| `ERR_VAITRO_KHONG_TON_TAI_XOA` | `-20107` | Không tìm thấy vai trò khi xóa |
| `ERR_VAITRO_XOA_HE_THONG` | `-20108` | Lỗi hệ thống khi xóa vai trò |
| `ERR_NV_THEM_HE_THONG` | `-20109` | Lỗi hệ thống khi thêm nhân viên |
| `ERR_NV_KHONG_TON_TAI_CN` | `-20110` | Không tìm thấy nhân viên khi cập nhật |
| `ERR_NV_CAPNHAT_HE_THONG` | `-20111` | Lỗi hệ thống khi cập nhật nhân viên |
| `ERR_NV_KHONG_TON_TAI_XOA` | `-20112` | Không tìm thấy nhân viên khi xóa |
| `ERR_NV_XOA_HE_THONG` | `-20113` | Lỗi hệ thống khi xóa nhân viên |
| `ERR_HANGTV_THEM_HE_THONG` | `-20114` | Lỗi hệ thống khi thêm hạng thành viên |
| `ERR_HANGTV_KHONG_TON_TAI_CN` | `-20115` | Không tìm thấy hạng thành viên khi cập nhật |
| `ERR_HANGTV_CAPNHAT_HE_THONG` | `-20116` | Lỗi hệ thống khi cập nhật hạng thành viên |
| `ERR_HANGTV_KHONG_TON_TAI_XOA` | `-20117` | Không tìm thấy hạng thành viên khi xóa |
| `ERR_HANGTV_XOA_HE_THONG` | `-20118` | Lỗi hệ thống khi xóa hạng thành viên |
| `ERR_KH_THEM_HE_THONG` | `-20119` | Lỗi hệ thống khi thêm khách hàng |
| `ERR_KH_KHONG_TON_TAI_CN` | `-20120` | Không tìm thấy khách hàng khi cập nhật |
| `ERR_KH_CAPNHAT_HE_THONG` | `-20121` | Lỗi hệ thống khi cập nhật khách hàng |
| `ERR_KH_KHONG_TON_TAI_XOA` | `-20122` | Không tìm thấy khách hàng khi xóa |
| `ERR_KH_XOA_HE_THONG` | `-20123` | Lỗi hệ thống khi xóa khách hàng |

---

#### Nhóm 02 — Sản phẩm & Danh mục

| Tên hằng số | Mã | Mô tả |
|---|---|---|
| `ERR_SP_HET_HANG` | `-20201` | Số lượng bánh trong kho đã hết |
| `ERR_SP_KHONG_TON_TAI` | `-20202` | Không tìm thấy sản phẩm trong hệ thống |
| `ERR_XUAT_HUY_BANH` | `-20203` | Số lượng hủy vượt quá tồn kho hiện tại |
| `ERR_HOAN_XUAT_BANH` | `-20204` | Lỗi hệ thống khi hoàn xuất bánh |
| `ERR_DM_THEM_HE_THONG` | `-20205` | Lỗi hệ thống khi thêm danh mục |
| `ERR_DM_KHONG_TON_TAI_CN` | `-20206` | Không tìm thấy danh mục khi cập nhật |
| `ERR_DM_CAPNHAT_HE_THONG` | `-20207` | Lỗi hệ thống khi cập nhật danh mục |
| `ERR_DM_KHONG_TON_TAI_XOA` | `-20208` | Không tìm thấy danh mục khi xóa |
| `ERR_DM_XOA_HE_THONG` | `-20209` | Lỗi hệ thống khi xóa danh mục |
| `ERR_SANPHAM_THEM_HE_THONG` | `-20210` | Lỗi hệ thống khi thêm sản phẩm |
| `ERR_SANPHAM_KHONG_TON_TAI_CN` | `-20211` | Không tìm thấy sản phẩm khi cập nhật |
| `ERR_SANPHAM_CAPNHAT_HE_THONG` | `-20212` | Lỗi hệ thống khi cập nhật sản phẩm |
| `ERR_SANPHAM_KHONG_TON_TAI_XOA` | `-20213` | Không tìm thấy sản phẩm khi xóa |
| `ERR_SANPHAM_XOA_HE_THONG` | `-20214` | Lỗi hệ thống khi xóa sản phẩm |
| `ERR_KICHCO_THEM_HE_THONG` | `-20215` | Lỗi hệ thống khi thêm kích cỡ bánh |
| `ERR_KICHCO_KHONG_TON_TAI_CN` | `-20216` | Không tìm thấy kích cỡ bánh khi cập nhật |
| `ERR_KICHCO_CAPNHAT_HE_THONG` | `-20217` | Lỗi hệ thống khi cập nhật kích cỡ bánh |
| `ERR_KICHCO_KHONG_TON_TAI_XOA` | `-20218` | Không tìm thấy kích cỡ bánh khi xóa |
| `ERR_KICHCO_XOA_HE_THONG` | `-20219` | Lỗi hệ thống khi xóa kích cỡ bánh |
| `ERR_COTBANH_THEM_HE_THONG` | `-20220` | Lỗi hệ thống khi thêm cốt bánh |
| `ERR_COTBANH_KHONG_TON_TAI_CN` | `-20221` | Không tìm thấy cốt bánh khi cập nhật |
| `ERR_COTBANH_CAPNHAT_HE_THONG` | `-20222` | Lỗi hệ thống khi cập nhật cốt bánh |
| `ERR_COTBANH_KHONG_TON_TAI_XOA` | `-20223` | Không tìm thấy cốt bánh khi xóa |
| `ERR_COTBANH_XOA_HE_THONG` | `-20224` | Lỗi hệ thống khi xóa cốt bánh |
| `ERR_NHANBANH_THEM_HE_THONG` | `-20225` | Lỗi hệ thống khi thêm nhân bánh |
| `ERR_NHANBANH_KHONG_TON_TAI_CN` | `-20226` | Không tìm thấy nhân bánh khi cập nhật |
| `ERR_NHANBANH_CAPNHAT_HE_THONG` | `-20227` | Lỗi hệ thống khi cập nhật nhân bánh |
| `ERR_NHANBANH_KHONG_TON_TAI_XOA` | `-20228` | Không tìm thấy nhân bánh khi xóa |
| `ERR_NHANBANH_XOA_HE_THONG` | `-20229` | Lỗi hệ thống khi xóa nhân bánh |
| `ERR_TRANGTRI_THEM_HE_THONG` | `-20230` | Lỗi hệ thống khi thêm kiểu trang trí |
| `ERR_TRANGTRI_KHONG_TON_TAI_CN` | `-20231` | Không tìm thấy kiểu trang trí khi cập nhật |
| `ERR_TRANGTRI_CAPNHAT_HE_THONG` | `-20232` | Lỗi hệ thống khi cập nhật kiểu trang trí |
| `ERR_TRANGTRI_KHONG_TON_TAI_XOA` | `-20233` | Không tìm thấy kiểu trang trí khi xóa |
| `ERR_TRANGTRI_XOA_HE_THONG` | `-20234` | Lỗi hệ thống khi xóa kiểu trang trí |

---

#### Nhóm 03 — Kho & Công thức

| Tên hằng số | Mã | Mô tả |
|---|---|---|
| `ERR_NL_KHONG_DAT_VSATTP` | `-20301` | Nguyên liệu không đạt chuẩn VSATTP |
| `ERR_NL_GIAN_LAN_HSD` | `-20302` | Phát hiện gian lận: HSD mới lớn hơn HSD cũ |
| `ERR_NL_HSD_KHONG_HOPLE` | `-20303` | HSD của lô ≤ ngày nhập kho |
| `ERR_NL_KHONG_CO_PHIEUNHAP` | `-20304` | Không tìm thấy phiếu nhập kho |
| `ERR_NL_KHONG_CO_LO_HANG` | `-20305` | Không tìm thấy lô hàng trong kho |
| `ERR_NL_KHONG_TON_TAI` | `-20306` | Không tìm thấy nguyên liệu |
| `ERR_NL_KHONG_THE_HUY_PN` | `-20307` | Nguyên liệu đã được sử dụng, không thể hủy phiếu nhập |
| `ERR_NHAP_KHO` | `-20308` | Lỗi hệ thống khi thực thi nhập kho |
| `ERR_NL_KHONG_DU` | `-20309` | Không đủ tồn kho nguyên liệu để xuất sản xuất |
| `ERR_NL_TON_AO` | `-20310` | Lỗi tồn kho ảo nguyên liệu |
| `ERR_NL_THEM_MOI` | `-20311` | Lỗi hệ thống khi thêm mới nguyên liệu |
| `ERR_NL_CAPNHAT_HE_THONG` | `-20312` | Lỗi hệ thống khi cập nhật nguyên liệu |
| `ERR_NL_XOA_HE_THONG` | `-20313` | Lỗi hệ thống khi xóa nguyên liệu |
| `ERR_PHIEU_NHAP_KHO` | `-20314` | Lỗi hệ thống khi nhập kho vật tư |
| `ERR_HOAN_PHIEU_NHAP_KHO` | `-20315` | Lỗi hệ thống khi hủy phiếu nhập kho |

---

#### Nhóm 04 — Đơn hàng

| Tên hằng số | Mã | Mô tả |
|---|---|---|
| `ERR_DON_VUOT_CONG_SUAT` | `-20401` | Vượt công suất sản xuất trong ngày |
| `ERR_KHONG_GIOI_HAN_SX` | `-20402` | Không tìm thấy giới hạn sản xuất trong ngày |
| `ERR_DON_CHUYEN_TRANGTHAI` | `-20403` | Lỗi khi chuyển trạng thái đơn hàng |

---

#### Nhóm 05 — Tài chính

| Tên hằng số | Mã | Mô tả |
|---|---|---|
| `ERR_TC_CAM_XOA_HOADON` | `-20501` | Vi phạm kế toán: cấm xóa vật lý hóa đơn |
| `ERR_TC_CAM_XOA_PHIEUTHUCHI` | `-20502` | Vi phạm kế toán: cấm xóa vật lý phiếu thu chi |
| `ERR_PTTT_THEM_HE_THONG` | `-20503` | Lỗi hệ thống khi thêm phương thức thanh toán |
| `ERR_PTTT_KHONG_TON_TAI_CN` | `-20504` | Không tìm thấy phương thức thanh toán khi cập nhật |
| `ERR_PTTT_CAPNHAT_HE_THONG` | `-20505` | Lỗi hệ thống khi cập nhật phương thức thanh toán |
| `ERR_PTTT_KHONG_TON_TAI_XOA` | `-20506` | Không tìm thấy phương thức thanh toán khi xóa |
| `ERR_PTTT_XOA_HE_THONG` | `-20507` | Lỗi hệ thống khi xóa phương thức thanh toán |
| `ERR_LOAITHUCHI_THEM_HE_THONG` | `-20508` | Lỗi hệ thống khi thêm loại thu chi |
| `ERR_LOAITHUCHI_KHONG_TON_TAI_CN` | `-20509` | Không tìm thấy loại thu chi khi cập nhật |
| `ERR_LOAITHUCHI_CAPNHAT_HE_THONG` | `-20510` | Lỗi hệ thống khi cập nhật loại thu chi |
| `ERR_LOAITHUCHI_KHONG_TON_TAI_XOA` | `-20511` | Không tìm thấy loại thu chi khi xóa |
| `ERR_LOAITHUCHI_XOA_HE_THONG` | `-20512` | Lỗi hệ thống khi xóa loại thu chi |
| `ERR_PHIEUTHUCHI_THEM_HE_THONG` | `-20513` | Lỗi hệ thống khi tạo phiếu thu chi |
| `ERR_HOADON_THEM_HE_THONG` | `-20514` | Lỗi hệ thống khi tạo hóa đơn |


### Ví dụ sử dụng đúng trong Procedure

```sql
-- ✅ ĐÚNG: tham chiếu qua tên hằng số
RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_TC_COC_KHONG_DU,
    'Tiền cọc không đủ 50% giá trị đơn hàng!');

-- ❌ SAI: hardcode số âm trực tiếp
RAISE_APPLICATION_ERROR(-20503, 'Tiền cọc không đủ!');
```
