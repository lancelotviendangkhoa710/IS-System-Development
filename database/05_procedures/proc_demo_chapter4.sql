-- Demo Phantom Read và Non-repeatable Read — Chương 4: Truy xuất đồng thời
-- Dùng trong SQL Developer với 2 session để chứng minh học thuật READ COMMITTED vs SERIALIZABLE.
-- Không có Java DAO gọi những procedure này — chỉ dùng qua SQL Developer.

-- ============================================================
-- PROC_DEMO_DOCKHONGLAPLAI — §4.2 Non-repeatable Read
-- Đọc GIABAN của sản phẩm 2 lần trong 1 transaction, có delay giữa 2 lần đọc.
-- BUG  (mặc định): SERIALIZABLE comment → READ COMMITTED
--                  → lần 2 thấy giá mới nếu session khác đã UPDATE COMMIT
-- FIX             : bỏ comment EXECUTE IMMEDIATE → snapshot cố định từ đầu transaction
--                  → lần 2 vẫn trả về giá cũ dù session khác đã commit
-- ============================================================
CREATE OR REPLACE PROCEDURE PROC_DEMO_DOCKHONGLAPLAI (
    P_MASP     IN  SANPHAM.MASP%TYPE,
    P_GIA_LAN1 OUT SANPHAM.GIABAN%TYPE,
    P_GIA_LAN2 OUT SANPHAM.GIABAN%TYPE
) IS
    V_X NUMBER := 0;
BEGIN
    -- [DEMO TOGGLE] §4.2 Non-repeatable Read:
    -- BUG (mặc định): dòng dưới đang comment → READ COMMITTED
    --                 → lần 2 thấy giá mới nếu session khác đã UPDATE & COMMIT trong lúc delay
    -- FIX            : bỏ comment → SERIALIZABLE → snapshot cố định từ thời điểm transaction bắt đầu
    --                 → 2 lần đọc trả về cùng giá trị dù có commit từ session khác ở giữa
    --
    -- EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';

    -- 1. Đọc giá lần 1 — ghi nhận P_GIA_LAN1
    SELECT GIABAN INTO P_GIA_LAN1
    FROM SANPHAM
    WHERE MASP = P_MASP;

    -- 2. Delay ~10 giây để session 2 kịp UPDATE GIABAN và COMMIT
    --    (Điều chỉnh số vòng lặp nếu máy nhanh/chậm hơn)
    FOR I IN 1..120000000 LOOP
        V_X := V_X + I;
    END LOOP;

    -- 3. Đọc giá lần 2 — cùng câu query, cùng transaction
    SELECT GIABAN INTO P_GIA_LAN2
    FROM SANPHAM
    WHERE MASP = P_MASP;

    -- Không có DML → không cần COMMIT/ROLLBACK

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20901, N'Lỗi demo Non-repeatable Read: ' || SQLERRM);
END;
/


-- ============================================================
-- PROC_DEMO_DOCBONGMA — §4.3 Phantom Read
-- Đếm số lô nguyên liệu còn tồn 2 lần trong 1 transaction, có delay giữa 2 lần đếm.
-- BUG  (mặc định): SERIALIZABLE comment → READ COMMITTED
--                  → lần 2 thấy lô mới nếu session khác đã INSERT CTPHIEUNHAP & COMMIT
-- FIX             : bỏ comment EXECUTE IMMEDIATE → snapshot cố định
--                  → lần 2 vẫn trả về số lô cũ (phantom rows bị ẩn hoàn toàn)
-- ============================================================
CREATE OR REPLACE PROCEDURE PROC_DEMO_DOCBONGMA (
    P_MANL     IN  NGUYENLIEU.MANL%TYPE,
    P_DEM_LAN1 OUT NUMBER,
    P_DEM_LAN2 OUT NUMBER
) IS
    V_X NUMBER := 0;
BEGIN
    -- [DEMO TOGGLE] §4.3 Phantom Read:
    -- BUG (mặc định): dòng dưới đang comment → READ COMMITTED
    --                 → lần 2 thấy lô mới nếu session khác nhập kho & COMMIT trong lúc delay
    -- FIX            : bỏ comment → SERIALIZABLE → snapshot cố định
    --                 → phantom rows không xuất hiện trong cả 2 lần đếm
    --
    -- EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';

    -- 1. Đếm số lô còn tồn lần 1
    SELECT COUNT(*) INTO P_DEM_LAN1
    FROM CTPHIEUNHAP
    WHERE MANL = P_MANL
      AND SOLUONGCONLAI > 0;

    -- 2. Delay ~10 giây để session 2 kịp nhập lô mới và COMMIT
    FOR I IN 1..120000000 LOOP
        V_X := V_X + I;
    END LOOP;

    -- 3. Đếm lại — cùng điều kiện, cùng transaction
    SELECT COUNT(*) INTO P_DEM_LAN2
    FROM CTPHIEUNHAP
    WHERE MANL = P_MANL
      AND SOLUONGCONLAI > 0;

    -- Không có DML → không cần COMMIT/ROLLBACK

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20902, N'Lỗi demo Phantom Read: ' || SQLERRM);
END;
/


-- ============================================================
-- HƯỚNG DẪN SỬ DỤNG VỚI SQL DEVELOPER
-- ============================================================
--
-- §4.2 Non-repeatable Read — BUG:
-- Session 1:
--   VARIABLE gia1 NUMBER;
--   VARIABLE gia2 NUMBER;
--   EXEC PROC_DEMO_DOCKHONGLAPLAI(1001, :gia1, :gia2);
--   -- (đang delay 10s → chạy session 2)
--   PRINT gia1;  -- 150000
--   PRINT gia2;  -- 180000 → NON-REPEATABLE READ!
--
-- Session 2 (trong lúc session 1 delay):
--   UPDATE SANPHAM SET GIABAN = 180000 WHERE MASP = 1001; COMMIT;
--
-- §4.2 FIX: bỏ comment EXECUTE IMMEDIATE trong proc → compile lại → lặp lại:
--   PRINT gia1;  -- 150000
--   PRINT gia2;  -- 150000 → Nhất quán ✅
--
-- ============================================================
--
-- §4.3 Phantom Read — BUG:
-- Session 1:
--   VARIABLE dem1 NUMBER;
--   VARIABLE dem2 NUMBER;
--   EXEC PROC_DEMO_DOCBONGMA(<MANL_BuaSua>, :dem1, :dem2);
--   -- (đang delay 10s → chạy session 2)
--   PRINT dem1;  -- 3
--   PRINT dem2;  -- 4 → PHANTOM READ!
--
-- Session 2 (trong lúc session 1 delay):
--   -- Nhập kho lô Bơ sữa mới qua màn hình Nhập Kho trong app
--   -- hoặc gọi trực tiếp: EXEC PROC_TAOPHIEUNHAPKHO(...)
--
-- §4.3 FIX: bỏ comment EXECUTE IMMEDIATE → compile lại → lặp lại:
--   PRINT dem1;  -- 3
--   PRINT dem2;  -- 3 → Nhất quán ✅
-- ============================================================
