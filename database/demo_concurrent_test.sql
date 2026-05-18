
SELECT MASP, TENSP, SOLUONGTON FROM SANPHAM WHERE MASP = 5;

-- Bước 2: Bắt đầu transaction, lock sản phẩm
SET SERVEROUTPUT ON;
DECLARE
    V_TONKHO NUMBER;
BEGIN
    -- Lock dòng sản phẩm (FOR UPDATE) — T2 sẽ phải chờ ở đây
    SELECT SOLUONGTON INTO V_TONKHO
    FROM SANPHAM WHERE MASP = 5
    FOR UPDATE;

    DBMS_OUTPUT.PUT_LINE('T1: Tồn kho hiện tại = ' || V_TONKHO);
    DBMS_OUTPUT.PUT_LINE('T1: Đang xử lý đơn hàng... (chờ 10 giây)');

    -- Giả lập thời gian xử lý đơn hàng (10 giây)
    -- Trong 10 giây này, chuyển sang SESSION 2 chạy cùng script
    DBMS_LOCK.SLEEP(10);

    -- Trừ kho
    UPDATE SANPHAM SET SOLUONGTON = V_TONKHO - 3 WHERE MASP = 5;
    DBMS_OUTPUT.PUT_LINE('T1: Đã trừ 3, tồn mới = ' || (V_TONKHO - 3));

    COMMIT;
    DBMS_OUTPUT.PUT_LINE('T1: COMMIT thành công!');
END;
/

-- ****** SESSION 2 (Thu ngân B — chạy trong 10 giây chờ của T1) ******
SET SERVEROUTPUT ON;
DECLARE
    V_TONKHO NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('T2: Đang chờ lock...');

    -- FOR UPDATE → T2 bị BLOCK cho đến khi T1 COMMIT
    SELECT SOLUONGTON INTO V_TONKHO
    FROM SANPHAM WHERE MASP = 5
    FOR UPDATE;

    -- Khi T1 COMMIT, T2 mới chạy tiếp — đọc giá trị MỚI NHẤT
    DBMS_OUTPUT.PUT_LINE('T2: Lock được cấp! Tồn kho hiện tại = ' || V_TONKHO);

    IF V_TONKHO < 5 THEN
        DBMS_OUTPUT.PUT_LINE('T2: KHÔNG ĐỦ HÀNG! Chỉ còn ' || V_TONKHO);
        ROLLBACK;
    ELSE
        UPDATE SANPHAM SET SOLUONGTON = V_TONKHO - 5 WHERE MASP = 5;
        DBMS_OUTPUT.PUT_LINE('T2: Đã trừ 5, tồn mới = ' || (V_TONKHO - 5));
        COMMIT;
    END IF;
END;
/

-- Bước 3: Kiểm tra kết quả
SELECT MASP, TENSP, SOLUONGTON FROM SANPHAM WHERE MASP = 5;


-- ============================================================
-- DEMO 2: DEADLOCK — Xuất kho nguyên liệu theo thứ tự ngược
-- Minh họa: 2 tổ trưởng xuất kho cùng lúc với thứ tự lock ngược
-- ============================================================

-- ****** SESSION 1 (Lock MANL=3 trước, rồi MANL=7) ******
SET SERVEROUTPUT ON;
DECLARE
    V_TON1 NUMBER;
    V_TON2 NUMBER;
BEGIN
    -- Lock nguyên liệu 3 (bột mì) trước
    SELECT SOLUONGTONTONG INTO V_TON1
    FROM NGUYENLIEU WHERE MANL = 3
    FOR UPDATE;
    DBMS_OUTPUT.PUT_LINE('T1: Locked MANL=3, tồn=' || V_TON1);

    -- Chờ 10 giây để T2 kịp lock MANL=7
    DBMS_LOCK.SLEEP(10);

    -- Giờ thử lock MANL=7 → NẾU T2 đã lock MANL=7 → DEADLOCK!
    -- Dùng NOWAIT để phát hiện ngay thay vì treo
    BEGIN
        SELECT SOLUONGTONTONG INTO V_TON2
        FROM NGUYENLIEU WHERE MANL = 7
        FOR UPDATE NOWAIT;  -- ORA-00054 nếu T2 đang giữ lock
        DBMS_OUTPUT.PUT_LINE('T1: Locked MANL=7 thành công, tồn=' || V_TON2);
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -54 THEN
                DBMS_OUTPUT.PUT_LINE('T1: ORA-00054 — Nguyên liệu MANL=7 đang bị lock bởi session khác!');
            ELSIF SQLCODE = -60 THEN
                DBMS_OUTPUT.PUT_LINE('T1: ORA-00060 — DEADLOCK detected! Oracle tự chọn victim.');
            END IF;
            ROLLBACK;
    END;

    COMMIT;
END;
/

-- ****** SESSION 2 (Lock MANL=7 trước, rồi MANL=3 — THỨ TỰ NGƯỢC) ******
SET SERVEROUTPUT ON;
DECLARE
    V_TON1 NUMBER;
    V_TON2 NUMBER;
BEGIN
    -- Lock nguyên liệu 7 (bơ) trước — THỨ TỰ NGƯỢC với T1
    SELECT SOLUONGTONTONG INTO V_TON1
    FROM NGUYENLIEU WHERE MANL = 7
    FOR UPDATE;
    DBMS_OUTPUT.PUT_LINE('T2: Locked MANL=7, tồn=' || V_TON1);

    -- Chờ 5 giây
    DBMS_LOCK.SLEEP(5);

    -- Thử lock MANL=3 → T1 đang giữ → DEADLOCK!
    BEGIN
        SELECT SOLUONGTONTONG INTO V_TON2
        FROM NGUYENLIEU WHERE MANL = 3
        FOR UPDATE NOWAIT;
        DBMS_OUTPUT.PUT_LINE('T2: Locked MANL=3 thành công, tồn=' || V_TON2);
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -54 THEN
                DBMS_OUTPUT.PUT_LINE('T2: ORA-00054 — Nguyên liệu MANL=3 đang bị lock bởi session khác!');
            ELSIF SQLCODE = -60 THEN
                DBMS_OUTPUT.PUT_LINE('T2: ORA-00060 — DEADLOCK detected!');
            END IF;
            ROLLBACK;
    END;

    COMMIT;
END;
/


-- ============================================================
-- DEMO 3: GIẢI PHÁP DEADLOCK — Lock Ordering (ORDER BY MANL ASC)
-- Cả 2 session đều lock theo thứ tự MANL tăng dần → Không deadlock
-- ============================================================

-- ****** SESSION 1 (Lock MANL=3 rồi MANL=7 — đúng thứ tự) ******
SET SERVEROUTPUT ON;
DECLARE
    V_TON NUMBER;
BEGIN
    FOR REC IN (SELECT MANL FROM CONGTHUC WHERE MASP = 5 ORDER BY MANL ASC) LOOP
        SELECT SOLUONGTONTONG INTO V_TON
        FROM NGUYENLIEU WHERE MANL = REC.MANL
        FOR UPDATE;
        DBMS_OUTPUT.PUT_LINE('T1: Locked MANL=' || REC.MANL || ', tồn=' || V_TON);
    END LOOP;

    DBMS_LOCK.SLEEP(10);  -- Chờ để T2 thử lock
    DBMS_OUTPUT.PUT_LINE('T1: Tất cả NL đã lock thành công! Không deadlock.');
    COMMIT;
END;
/

-- ****** SESSION 2 (cũng lock MANL tăng dần — CÙNG THỨ TỰ) ******
SET SERVEROUTPUT ON;
DECLARE
    V_TON NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('T2: Đang chờ T1 giải phóng...');
    FOR REC IN (SELECT MANL FROM CONGTHUC WHERE MASP = 8 ORDER BY MANL ASC) LOOP
        SELECT SOLUONGTONTONG INTO V_TON
        FROM NGUYENLIEU WHERE MANL = REC.MANL
        FOR UPDATE;
        DBMS_OUTPUT.PUT_LINE('T2: Locked MANL=' || REC.MANL || ', tồn=' || V_TON);
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('T2: Tất cả NL đã lock thành công! Không deadlock.');
    COMMIT;
END;
/


-- ============================================================
-- DEMO 4: SERIALIZABLE — Ngăn Non-Repeatable Read cho báo cáo
-- ============================================================

-- ****** SESSION 1 (Quản lý lập báo cáo) ******
SET SERVEROUTPUT ON;
-- Bật SERIALIZABLE để snapshot cố định
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

DECLARE
    V_DIEM1 NUMBER;
    V_DIEM2 NUMBER;
BEGIN
    -- Lần đọc 1
    SELECT DIEMTICHLUY INTO V_DIEM1 FROM KHACHHANG WHERE MAKH = 101;
    DBMS_OUTPUT.PUT_LINE('Báo cáo đọc lần 1: điểm = ' || V_DIEM1);

    -- Chờ 10 giây → trong lúc này T2 UPDATE + COMMIT
    DBMS_LOCK.SLEEP(10);

    -- Lần đọc 2 — vẫn thấy giá trị cũ (snapshot nhất quán)
    SELECT DIEMTICHLUY INTO V_DIEM2 FROM KHACHHANG WHERE MAKH = 101;
    DBMS_OUTPUT.PUT_LINE('Báo cáo đọc lần 2: điểm = ' || V_DIEM2);

    IF V_DIEM1 = V_DIEM2 THEN
        DBMS_OUTPUT.PUT_LINE('✓ SERIALIZABLE: 2 lần đọc NHẤT QUÁN!');
    ELSE
        DBMS_OUTPUT.PUT_LINE('✗ LỖI: 2 lần đọc KHÔNG nhất quán!');
    END IF;
END;
/
COMMIT;

-- ****** SESSION 2 (Thu ngân cộng điểm) ******
SET SERVEROUTPUT ON;
BEGIN
    UPDATE KHACHHANG SET DIEMTICHLUY = DIEMTICHLUY + 50 WHERE MAKH = 101;
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('T2: Đã cộng 50 điểm cho KH 101 và COMMIT');
END;
/
