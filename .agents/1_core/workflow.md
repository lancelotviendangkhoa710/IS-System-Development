# QUY TRÌNH LÀM VIỆC (WORKFLOW)

## 1. Quy trình Phát triển Chức năng (Feature Development)
Mọi chức năng khi được triển khai hoặc sửa đổi lớn phải tuân thủ quy trình sau:

1.  **Phân tích & Thiết kế (Analysis & Design):**
    - Xác định Actor, Tiền điều kiện, Hậu điều kiện và các luồng sự kiện (Chính/Phụ/Lỗi).
    - Tập trung vào mô tả nghiệp vụ tổng quan, **KHÔNG** ghi chi tiết kỹ thuật (tên hàm, procedure, bảng DB).
    - Vẽ Activity Diagram (Mermaid) với 3 swimlanes: Người dùng, Hệ thống, CSDL.
    - **BẮT BUỘC:** Sơ đồ phải mô tả rõ các bước kiểm tra (sử dụng hình thoi Decision) và các luồng rẽ nhánh khi dữ liệu sai/thiếu.
2.  **Triển khai Code (Implementation):**
    - Tuân thủ MVP và các rule trong `.agents/2_tech/`.
3.  **Đồng bộ Tài liệu (Documentation Sync):**
    - **BẮT BUỘC:** Khi hoàn thành một chức năng, phải tạo hoặc cập nhật file Markdown tương ứng trong thư mục `/.agents/3_business/use_cases/`.
    - Tên file theo định dạng: `UCxx_TenChucNang.md`.
4.  **Hậu kiểm Tự động (Post-Code Review):**
    - BẮT BUỘC chạy toàn bộ quy trình trong `/.agents/1_core/review_agent.md` ngay sau khi code xong.
    - Tự fix lỗi cấp độ thấp (naming, null guard, empty catch).
    - Báo cáo kết quả theo format `REVIEW AGENT REPORT` trước khi thông báo "DONE".
5.  **Kiểm tra & Commit:**
    - Chạy `gitnexus_detect_changes()` và commit theo chuẩn `commit.md`.

## 2. Cấu trúc Tài liệu Use Case (Chuẩn)
Mỗi file Use Case Markdown phải trình bày dưới dạng bảng (như ví dụ nghiệp vụ):

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Tên chức năng |
| **Mô tả Use-case** | Mô tả tóm tắt mục đích |
| **Actors** | Đối tượng thực hiện |
| **Tiền điều kiện** | Điều kiện cần để thực hiện |
| **Hậu điều kiện** | Kết quả sau khi thực hiện thành công |
| **Luồng sự kiện chính** | Các bước thực hiện thành công (đánh số 1, 2, 3...) |
| **Luồng sự kiện phụ** | Các rẽ nhánh nghiệp vụ (1a, 2a...) |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | Các trường hợp thất bại hoặc thoát |

**Kèm theo:** Sơ đồ Activity Diagram sử dụng Mermaid với 3 swimlanes.

---

## 3. Quy trình Hậu kiểm (Post-Code Review Workflow)

Sau khi hoàn thành code (trước commit), AI phải tự thực hiện theo thứ tự:

```
[CODE DONE]
    ↓
[PHASE 1] Impact Scan — gitnexus_impact() cho mỗi symbol đã sửa
    ↓ (nếu HIGH/CRITICAL → DỪNG, báo User)
[PHASE 2] Architecture Audit — Kiểm tra Vi phạm MVP (V/P/S/D/DTO)
    ↓ (tự fix lỗi cấp thấp)
[PHASE 3] SQL Audit — Kiểm tra naming, EXCEPTION block, COMMIT
    ↓ (tự fix lỗi cấp thấp)
[PHASE 4] Naming Convention Scan — Java + SQL
    ↓ (tự fix)
[PHASE 5] Xuất REVIEW AGENT REPORT
    ↓
[COMMIT] — Chỉ commit khi status = ✅ CLEAN hoặc ⚠️ WARNINGS đã được User xác nhận
```

> Chi tiết từng phase: xem `/.agents/1_core/review_agent.md`.
