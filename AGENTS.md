# AGENTS.md

## Mục tiêu
Quy trình bắt buộc cho mọi cuộc hội thoại trong project `D:\Clone` — Bakery Management System.

---

## STARTUP (mỗi cuộc hội thoại mới)

### Bước 1 — Nạp Core (BẮT BUỘC, luôn luôn)
Đọc đúng thứ tự các file sau trước khi phân tích hoặc viết code:
1. `/.agents/1_core/core_compact.md` — Rules cốt lõi: Stack, MVP, Naming, Java, SQL, UI, Vibe, GitNexus
2. `/.agents/1_core/tree_folder.txt` — Cấu trúc thư mục dự án
3. `/.agents/1_core/commit.md` — Quy trình commit
4. `/.agents/1_core/workflow.md` — Use Case template + Feature dev process

### Bước 2 — Context Routing (nạp thêm theo loại task)
| Task | File cần đọc thêm |
|---|---|
| Sửa FXML / CSS / Controller | `/.agents/2_tech/ui_rules.md`, `/.agents/2_tech/javarules.md` |
| Tạo màn hình **mới** | `/.agents/2_tech/ui_rules.md`, `/.agents/2_tech/ui_templates.md`, `/.agents/2_tech/javarules.md` |
| Sửa Presenter / Service / DTO | `/.agents/2_tech/javarules.md`, `/.agents/3_business/business_domain.md` |
| Sửa DAO / Oracle DB / SQL | `/.agents/2_tech/javarules.md`, `/.agents/2_tech/dbrules.md`, `/.agents/2_tech/database_architecture.md` |
| Làm tính năng mới | `/.agents/3_business/use_cases_index.md` + UC tương ứng trong `/.agents/3_business/use_cases/` |

> `review_agent.md` **KHÔNG** nạp lúc startup — chỉ chạy **sau khi viết xong code**.

### Bước 3 — Xác nhận
Sau khi đọc xong, mở đầu bằng tóm tắt 2–3 dòng các ràng buộc chính sẽ áp dụng.
Nếu chưa đọc xong: chỉ thông báo đang đọc context, KHÔNG đưa giải pháp kỹ thuật.

---

## QUY TẮC THỰC THI
- Ưu tiên rules trong `/.agents/` khi liên quan trực tiếp đến task.
- Xung đột giữa nhiều rule → báo rõ xung đột, lên kế hoạch, chờ User xác nhận.
- Khi sửa code → giải thích ngắn rule nào đang chi phối quyết định.
- **100% COMPLIANCE:** Không ngoại lệ kể cả hotfix nhỏ. Kiến trúc đúng > tốc độ.

---

## POST-CODE REVIEW (BẮT BUỘC sau mỗi task)
Chạy `/.agents/1_core/review_agent.md` ngay sau khi xong code, TRƯỚC KHI báo "DONE":
1. Impact Scan — `gitnexus_impact()` mọi symbol đã sửa
2. Architecture Audit — Checklist MVP (V/P/S/D/DTO)
3. SQL Audit — Naming, EXCEPTION, COMMIT *(chỉ khi có thay đổi .sql)*
4. UI Audit — Checklist FXML *(chỉ khi có thay đổi .fxml/.css)*
5. Naming Scan — Java + SQL
6. Xuất **REVIEW AGENT REPORT**

---

## GITNEXUS

> Index: **IS-System-Development** — 4921 symbols · 14707 relationships · 300 flows
> Stale index? → `npx gitnexus analyze`

**Luôn làm:**
- Trước khi sửa bất kỳ symbol: `gitnexus_impact({target, direction: "upstream"})` → báo blast radius.
- HIGH/CRITICAL → DỪNG, cảnh báo User trước khi tiếp tục.
- Khám phá code lạ: `gitnexus_query({query: "concept"})` thay vì grep.
- Cần full context 1 symbol: `gitnexus_context({name: "symbolName"})`.
- Trước commit: `gitnexus_detect_changes()`.

**Không bao giờ:**
- Sửa symbol mà không chạy `gitnexus_impact` trước.
- Bỏ qua cảnh báo HIGH/CRITICAL.
- Rename bằng find-and-replace — dùng `gitnexus_rename`.
- Commit khi chưa chạy `gitnexus_detect_changes()`.

**Resources:**
| Resource | Dùng cho |
|---|---|
| `gitnexus://repo/IS-System-Development/context` | Tổng quan codebase |
| `gitnexus://repo/IS-System-Development/clusters` | Các functional area |
| `gitnexus://repo/IS-System-Development/processes` | Toàn bộ execution flows |
| `gitnexus://repo/IS-System-Development/process/{name}` | Trace flow cụ thể |
