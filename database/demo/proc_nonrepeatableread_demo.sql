-- ================================================================
-- DEMO 4.2: Non-repeatable Read (Đọc không lặp lại)
-- ================================================================
-- Kịch bản: Thu ngân đọc GIABAN của bánh (MASP=1001) 2 lần trong
-- cùng 1 giao dịch. Giữa 2 lần đọc, quản lý cập nhật giá từ 150k → 180k.
--
-- [TRẠNG THÁI HIỆN TẠI] BUG — READ COMMITTED (mặc định Oracle)
-- → Lần đọc 2 tạo snapshot mới → thấy giá đã commit = 180.000đ
-- → P_GIA_LAN1 ≠ P_GIA_LAN2 → khách bị tính tiền sai
--
-- [CÁCH FIX] Bỏ comment dòng EXECUTE IMMEDIATE ở đầu BEGIN bên dưới
-- → Oracle dùng snapshot chụp lúc giao dịch bắt đầu cho mọi SELECT
-- → P_GIA_LAN1 = P_GIA_LAN2 = 150.000đ, khách không bị thiệt
-- ================================================================

CREATE OR REPLACE PROCEDURE PROC_XACNHAN_GIA_DEMO (
    P_MASP      IN  SANPHAM.MASP%TYPE,
    P_GIA_LAN1  OUT SANPHAM.GIABAN%TYPE,
    P_GIA_LAN2  OUT SANPHAM.GIABAN%TYPE
)
IS
    V_X NUMBER := 0;
BEGIN
    -- ============================================================
    -- [FIX] Bỏ comment dòng dưới để kích hoạt SERIALIZABLE:
    -- EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';
    -- ↑ Khi bật: mọi SELECT trong giao dịch này dùng cùng 1 snapshot
    -- ============================================================

    -- Lần đọc 1: Hệ thống hiển thị giá cho khách xem
    SELECT GIABAN INTO P_GIA_LAN1
    FROM SANPHAM
    WHERE MASP = P_MASP;

    -- [DEMO DELAY] Giả lập thời gian khách điền thông tin đặt hàng (≈2–4 giây)
    -- Trong khoảng này, quản lý gọi UPDATE GIABAN và COMMIT từ phiên khác
    FOR I IN 1..80000000 LOOP V_X := V_X + I; END LOOP;

    -- Lần đọc 2: Hệ thống đọc giá lại để tính tiền thanh toán
    -- [BUG]  READ COMMITTED → snapshot mới → thấy giá vừa commit
    -- [FIX]  SERIALIZABLE  → snapshot cũ  → vẫn thấy 150.000đ
    SELECT GIABAN INTO P_GIA_LAN2
    FROM SANPHAM
    WHERE MASP = P_MASP;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
