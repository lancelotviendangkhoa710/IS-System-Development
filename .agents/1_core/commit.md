---
description: Quy trình Auto Commit & Đẩy code (Vibe Coding)
---

# QUY TRÌNH TỰ ĐỘNG HÓA COMMIT (VIBE CODING)

Dành cho AI Agent khi thực hiện lưu vết (commit):
KHÔNG CẦN XIN PHÉP. Tự động thực thi tuần tự các bước dưới đây khi được yêu cầu commit:

1. **Kiểm tra rủi ro (GitNexus)**:
   Chạy tool `gitnexus_detect_changes()` để rà soát phạm vi ảnh hưởng. Cảnh báo user nếu rủi ro HIGH/CRITICAL.

2. **Stage toàn bộ thay đổi**:
   `git add .`

3. **Khảo sát (Diff)**:
   `git diff --cached`

3. **Sinh Commit Message tự động**:
   Phân tích code và tự động tạo message theo chuẩn **Conventional Commits**:
   - `feat:` (Tính năng mới)
   - `fix:` (Sửa lỗi)
   - `refactor:` (Tái cấu trúc, Clean code, gỡ CSS cứng)
   - `style:` (Format code, UI)
   - `chore:` (Cấu hình)
   *Lưu ý: Nội dung commit phải viết bằng tiếng Việt ngắn gọn, nêu đúng trọng tâm file bị sửa.*

4. **Thực thi Commit**:
   `git commit -m "<ai_generated_message>"`

5. **Đẩy code (Push) - NẾU CÓ YÊU CẦU**:
   `git push`