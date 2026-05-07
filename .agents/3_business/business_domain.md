# 5. NGỮ CẢNH NGHIỆP VỤ CỐT LÕI (BUSINESS DOMAIN CONTEXT)
Dự án là Hệ thống quản lý tiệm bánh chuyên nghiệp. Mọi logic code phải bám sát các quy trình sau:

## 5.1. [cite_start]Quy trình Thanh toán và Chốt đơn [cite: 2094]
- [cite_start]**Tính toán thời gian thực:** Giá bánh tùy chỉnh phải được cộng dồn liên tục từ `GiaCoBan` và các `PhuPhi` (Size, Cốt, Nhân) ngay trên giao diện[cite: 2101, 2102].
- **Chốt đơn:** Chỉ truyền mã ID các tùy chọn xuống DB. [cite_start]Hàm `FUNC_TinhGiaBanhTuyChinh` dưới DB sẽ tự tính lại giá để đóng băng vào hóa đơn, cấm lưu giá trực tiếp từ UI[cite: 2122, 2123, 3622].
- [cite_start]**Hoàn thành:** In hóa đơn nhiệt 80mm qua JasperReports và gọi Procedure chốt giao dịch, cộng điểm tích lũy, tự động nâng hạng VIP nếu đủ điểm[cite: 2112, 2113].

## 5.2. [cite_start]Quy trình Đặt cọc bánh Tùy chỉnh [cite: 2140]
- **Quy tắc 50%:** Khách đặt bánh custom phải cọc tối thiểu 50% giá trị đơn hàng. [cite_start]Giao diện phải chặn (Fail-Fast) nếu thu ngân gõ số tiền nhỏ hơn mức này[cite: 2145, 2146].
- [cite_start]**Cộng dồn két:** Tiền cọc phải được update cộng dồn ngầm dưới DB bằng lệnh `TienDaCoc = NVL(TienDaCoc, 0) + p_SoTienCoc` để chống xung đột khi đa luồng[cite: 2159, 2160, 2161].

## 5.3. [cite_start]Quy trình Đóng ca & Đối soát mù [cite: 2180]
- [cite_start]Khi thu ngân bấm Đóng ca, hệ thống gọi hàm `FUNC_TinhTienMatLyTuong` để tính tổng tiền mặt cần có (Tiền đầu ca + Thu - Chi) và GIẤU con số này đi[cite: 2182, 2185, 2187].
- [cite_start]Ép thu ngân tự đếm tiền và nhập số đếm thực tế[cite: 2191, 2192].
- [cite_start]Nếu tiền lệch (âm/dương), hệ thống chặn đóng ca, ép nhập lý do giải trình thì mới cho phép chốt sổ[cite: 2197, 2198, 2199].

## 5.4. [cite_start]Quy trình Kho và Sản xuất FEFO [cite: 2224]
- [cite_start]**Kiểm soát năng lực:** Khi nhận đơn, lùi ngày bằng công thức `Ngày Nhận Bánh - Thời Gian Chuẩn Bị` để đối chiếu với tải trọng xưởng[cite: 2126, 2127]. [cite_start]Quá tải lập tức chặn[cite: 2127].
- [cite_start]**Xuất kho FEFO:** Khi thợ bếp bấm nướng bánh, hệ thống tự gọi hàm `FUNC_XacDinhPhieuNhapFEFO` để quét các lô nguyên liệu có Hạn sử dụng gần nhất (ASC) để trừ kho trước[cite: 2231, 2232].