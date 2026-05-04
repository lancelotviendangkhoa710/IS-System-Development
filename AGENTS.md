# AGENTS.md

## Mục tiêu
File này định nghĩa quy trình bắt buộc cho mọi cuộc hội thoại trong project `D:\Clone`.

## Startup bắt buộc (mỗi cuộc hội thoại mới)
1. Trước khi phân tích, trả lời, hoặc sửa code: BẮT BUỘC đọc toàn bộ file trong thư mục `/.agents/1_core/`.
2. Danh sách Core cần đọc:
   - `/.agents/1_core/general.md`
   - `/.agents/1_core/skill.md`
   - `/.agents/1_core/workflow.md`
   - `/.agents/1_core/naming-convention.md`
   - `/.agents/1_core/tree_folder.txt`
   - `/.agents/1_core/commit.md`
   - `/.agents/1_core/review_agent.md`
3. **ĐỊNH TUYẾN NGỮ CẢNH (Context Routing):** XÁC ĐỊNH MỤC TIÊU TASK và nạp thêm rules tương ứng:
   - **Sửa Giao diện (View, FXML, CSS, Controller):** Đọc thêm `/.agents/2_tech/ui_spec.md`, `/.agents/2_tech/javarules.md`.
   - **Sửa Logic Java (Presenter, Service, DTO):** Đọc thêm `/.agents/2_tech/javarules.md`, `/.agents/3_business/business_domain.md`.
   - **Sửa Truy xuất dữ liệu (DAO, Oracle DB):** Đọc thêm `/.agents/2_tech/javarules.md`, `/.agents/2_tech/dbrules.md`, `/.agents/2_tech/database_architecture.md`.
   - **Làm Tính năng mới:** Đọc thêm Use Case tương ứng trong `/.agents/3_business/use_cases/`.
4. **TUYỆT ĐỐI TUÂN THỦ (100% COMPLIANCE):** Không có bất kỳ ngoại lệ nào. AI BẮT BUỘC phải tuân thủ 100% quy tắc đã nạp trước khi viết code, không được bỏ qua dù là sửa lỗi nhỏ (hotfix). Sự chính xác của kiến trúc quan trọng hơn tốc độ.
5. Sau khi đọc xong, trả lời mở đầu bằng một tóm tắt ngắn các ràng buộc chính sẽ áp dụng từ các file đã đọc.
6. Nếu chưa đọc xong, chỉ được thông báo đang đọc context; không được đưa giải pháp kỹ thuật cuối cùng.

## Quy tắc thực thi cốt lõi
- Luôn ưu tiên tuân thủ rule trong `/.agents/` khi chúng liên quan trực tiếp đến task.
- Nếu có xung đột giữa nhiều rule trong `/.agents/`, báo rõ là lỗi gì, lên kế hoạch và chờ sự chấp nhận của tôi rồi mới thực hiện
- Khi chỉnh sửa code, giải thích ngắn gọn rule nào trong `/.agents/` đang chi phối quyết định.

## THÁI ĐỘ LÀM VIỆC (VIBE CODING PROTOCOL)
- **Kháng cự Bad Code:** Nếu user đưa yêu cầu vi phạm MVP (vd: truy vấn DB từ Controller) hoặc phá vỡ UI (không dùng Amber Palette), AI phải TỪ CHỐI làm sai, tự động điều chỉnh sang hướng đúng và báo cáo.
- **Surgical & Trực tiếp:** Chỉ trả về nội dung cần thiết. Không xin lỗi, không giải thích dài dòng. Show me the code.
- **Boy Scout Rule (Clean as you go):** Sửa đến đâu, thấy code xung quanh dơ thì dọn luôn đến đó. Đừng để lại nợ kỹ thuật.

## POST-CODE REVIEW (BẮT BUỘC SAU MỖI TASK)
Sau khi hoàn thành bất kỳ task nào, AI PHẢI tự động chạy quy trình trong `/.agents/1_core/review_agent.md`.
Quy trình gồm 5 phase:
1. **Impact Scan** — `gitnexus_impact()` mọi symbol đã sửa.
2. **Architecture Audit** — Kiểm tra vi phạm MVP theo checklist V/P/S/D/DTO.
3. **SQL Audit** — Kiểm tra naming, EXCEPTION, COMMIT theo checklist DB.
4. **Naming Convention Scan** — Quét toàn bộ file đã chạm.
5. **REVIEW AGENT REPORT** — Xuất báo cáo trước khi báo "DONE".

Quy tắc Fix:
- **Tự fix ngay (Zero-Permission):** naming, null guard, empty catch, missing comment.
- **Hỏi User trước:** thay đổi DB schema, số tham số Procedure, refactor Interface.
- **Hard Block + Rollback:** SQL Injection, SQL trong View, lưu đơn chưa thanh toán.

## Gợi ý cho user
Ở đầu mỗi chat mới, user có thể nhắn:
`Đọc AGENTS.md và toàn bộ .agents trước khi làm.`

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **Clone** (2444 symbols, 7070 relationships, 207 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/Clone/context` | Codebase overview, check index freshness |
| `gitnexus://repo/Clone/clusters` | All functional areas |
| `gitnexus://repo/Clone/processes` | All execution flows |
| `gitnexus://repo/Clone/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->
