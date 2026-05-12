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

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **IS-System-Development** (5868 symbols, 17445 relationships, 300 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

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
| `gitnexus://repo/IS-System-Development/context` | Codebase overview, check index freshness |
| `gitnexus://repo/IS-System-Development/clusters` | All functional areas |
| `gitnexus://repo/IS-System-Development/processes` | All execution flows |
| `gitnexus://repo/IS-System-Development/process/{name}` | Step-by-step execution trace |

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
