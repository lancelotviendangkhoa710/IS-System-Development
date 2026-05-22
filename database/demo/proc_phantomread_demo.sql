-- ================================================================
-- DEMO 4.3: Phantom Read (Đọc bóng ma)
-- ================================================================
-- Kịch bản: Quản lý kho kiểm kê tổng tồn kho nguyên liệu bơ sữa
-- (MANL=5), truy vấn SUM(SOLUONGCONLAI) 2 lần trong cùng giao dịch.
-- Giữa 2 lần, nhân viên kho INSERT thêm lô mới 20kg và COMMIT.
--
-- [TRẠNG THÁI HIỆN TẠI] BUG — READ COMMITTED (mặc định Oracle)
-- → Lần truy vấn 2 tạo snapshot mới → thấy lô mới (phantom row)
-- → P_TONG_LAN1=100 ≠ P_TONG_LAN2=120 → báo cáo kiểm kê mâu thuẫn
--
-- [CÁCH FIX] Bỏ comment dòng EXECUTE IMMEDIATE ở đầu BEGIN bên dưới
-- → Oracle dùng snapshot chụp lúc giao dịch bắt đầu
-- → Lô mới (phantom) không xuất hiện trong tập kết quả của phiên này
-- → P_TONG_LAN1 = P_TONG_LAN2 = 100 → báo cáo nhất quán
-- ================================================================

CREATE OR REPLACE PROCEDURE PROC_KIEMKE_TONKHO_DEMO (
    P_MANL      IN  NGUYENLIEU.MANL%TYPE,
    P_TONG_LAN1 OUT NUMBER,
    P_TONG_LAN2 OUT NUMBER
)
IS
    V_X NUMBER := 0;
BEGIN
    -- ============================================================
    -- [FIX] Bỏ comment dòng dưới để kích hoạt SERIALIZABLE:
    -- EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';
    -- ↑ Khi bật: phantom row (lô mới INSERT sau snapshot) bị loại bỏ
    -- ============================================================

    -- Truy vấn lần 1: Quản lý ghi vào đầu báo cáo "Tồn đầu kỳ"
    SELECT NVL(SUM(SOLUONGCONLAI), 0) INTO P_TONG_LAN1
    FROM CTPHIEUNHAP
    WHERE MANL = P_MANL;

    -- [DEMO DELAY] Quản lý đang soạn phần giữa báo cáo (≈2–4 giây)
    -- Trong khoảng này, nhân viên kho gọi PROC_TAOPHIEUNHAPKHO từ phiên khác
    FOR I IN 1..80000000 LOOP V_X := V_X + I; END LOOP;

    -- Truy vấn lần 2: Quản lý kiểm tra lại để điền "Tồn cuối kỳ"
    -- [BUG]  READ COMMITTED → snapshot mới → thấy lô mới đã commit (phantom row)
    -- [FIX]  SERIALIZABLE  → snapshot cũ  → không thấy lô mới
    SELECT NVL(SUM(SOLUONGCONLAI), 0) INTO P_TONG_LAN2
    FROM CTPHIEUNHAP
    WHERE MANL = P_MANL;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
