-- ================================================================
-- FIX: Đơn vị tính NGUYENLIEU + Hệ số quy đổi
-- Chạy trên Oracle 12c+
-- Tác giả: auto-generated
-- ================================================================

-- ─── BƯỚC 1: Thêm DVT còn thiếu ─────────────────────────────────
INSERT INTO DONVITINH (TENDVT)
SELECT N'mL' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = UPPER(TRIM(N'mL')));

INSERT INTO DONVITINH (TENDVT)
SELECT N'Lít' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = UPPER(TRIM(N'Lít')));

INSERT INTO DONVITINH (TENDVT)
SELECT N'Chai' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = UPPER(TRIM(N'Chai')));

COMMIT;

-- ─── BƯỚC 2: Kiểm tra DVT đã tồn tại chưa ──────────────────────
-- (Chạy query này để xác nhận trước khi UPDATE)
-- SELECT MADVT, TENDVT FROM DONVITINH ORDER BY MADVT;

-- ─── BƯỚC 3: UPDATE NGUYENLIEU — gán DVT và HESOQUYDOI đúng ─────
-- Nguyên tắc:
--   Hàng khô dạng bột/hạt → DVT = Kg, HESOQUYDOI = 1000 (1 Kg = 1000g)
--   Hàng rắn đơn giản     → DVT = Gram, HESOQUYDOI = 1
--   Dạng lỏng             → DVT = mL, HESOQUYDOI = 1
--   Đơn chiếc             → DVT = Cái, HESOQUYDOI = 1

-- Bột mì số 8: nhập theo Kg
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = 'KG' AND ROWNUM = 1),
    HESOQUYDOI = 1000
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Bột mì số 8'));

-- Đường cát trắng: nhập theo Kg
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = 'KG' AND ROWNUM = 1),
    HESOQUYDOI = 1000
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Đường cát trắng'));

-- Hạt hạnh nhân: nhập theo Kg
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = 'KG' AND ROWNUM = 1),
    HESOQUYDOI = 1000
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Hạt hạnh nhân'));

-- Cacao nguyên chất: nhập theo Kg
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = 'KG' AND ROWNUM = 1),
    HESOQUYDOI = 1000
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Cacao nguyên chất'));

-- Bơ lạt Anchor: nhập theo Gram (từng block 250g cắt lẻ theo gram)
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = 'GRAM' AND ROWNUM = 1),
    HESOQUYDOI = 1
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Bơ lạt Anchor'));

-- Bột nở (Baking Powder): nhập theo Gram
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = 'GRAM' AND ROWNUM = 1),
    HESOQUYDOI = 1
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Bột nở (Baking Powder)'));

-- Phô mai cream cheese: nhập theo Gram
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE UPPER(TRIM(TENDVT)) = 'GRAM' AND ROWNUM = 1),
    HESOQUYDOI = 1
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Phô mai cream cheese'));

-- Sữa tươi không đường: nhập theo mL (hộp 1000mL = đo từng mL)
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE TENDVT = 'mL' AND ROWNUM = 1),
    HESOQUYDOI = 1
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Sữa tươi không đường'));

-- Vani chiết xuất: nhập theo mL
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE TENDVT = 'mL' AND ROWNUM = 1),
    HESOQUYDOI = 1
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Vani chiết xuất'));

-- Trứng gà tươi: ĐÃ ĐÚNG (Cái) — chỉ đảm bảo HESOQUYDOI = 1
UPDATE NGUYENLIEU
SET MADVT = (SELECT MADVT FROM DONVITINH WHERE TENDVT = 'Cái' AND ROWNUM = 1),
    HESOQUYDOI = 1
WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(N'Trứng gà tươi'));

COMMIT;



-- ─── BƯỚC 4: Kiểm tra kết quả ────────────────────────────────────
SELECT NL.TENNL,
       DVT.TENDVT,
       NL.HESOQUYDOI,
       NL.GIAVONTRUNGBINH,
       NL.SOLUONGTONTONG,
       NL.MUCTONANTOAN
FROM NGUYENLIEU NL
JOIN DONVITINH DVT ON NL.MADVT = DVT.MADVT
ORDER BY NL.TENNL;
select * from DONVITINH