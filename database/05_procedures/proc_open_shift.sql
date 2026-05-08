-- ============================================================
-- PROC_MOCA — Mở ca làm việc và tạo bản ghi đối soát đầu ca
--
-- Tham số:
--   P_MANV              : Mã nhân viên thực hiện mở ca
--   P_MAMAYPOS          : Mã máy POS được chọn
--   P_TIENKHAIBAODAUCA  : Tiền mặt nhân viên khai báo đầu ca (0 nếu để trống)
--   P_MACA_OUT          : OUT — Mã ca vừa tạo
--
-- Lưu ý: KHÔNG COMMIT bên trong — Java quản lý transaction.
-- ============================================================
CREATE OR REPLACE PROCEDURE PROC_MOCA(
    P_MANV             IN  NHANVIEN.MANV%TYPE,
    P_MAMAYPOS         IN  CALAMVIEC.MAMAYPOS%TYPE,
    P_TIENKHAIBAODAUCA IN  NUMBER DEFAULT 0,
    P_MACA_OUT         OUT CALAMVIEC.MACA%TYPE
)
IS
BEGIN
    -- 1. Tạo ca làm việc mới
    INSERT INTO CALAMVIEC (MANV, MAMAYPOS, THOIGIANMOCA, TRANGTHAI)
    VALUES (P_MANV, P_MAMAYPOS, SYSDATE, N'Đang mở')
    RETURNING MACA INTO P_MACA_OUT;

    -- 2. Tạo bản ghi đối soát đầu ca ngay lập tức (cùng transaction)
    INSERT INTO DOISOAT (MACA, TIENKHAIBAODAUCA)
    VALUES (P_MACA_OUT, NVL(P_TIENKHAIBAODAUCA, 0));

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(
            PKG_ERROR_CODES.ERR_CA_MO_HE_THONG,
            'Loi he thong khi Mo ca lam viec: ' || SQLERRM
        );
END;
/
