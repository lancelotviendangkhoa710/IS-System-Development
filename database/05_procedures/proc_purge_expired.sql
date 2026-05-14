-- Procedure xóa vĩnh viễn các bản ghi soft-delete quá N ngày + ghi lịch sử hệ thống
-- Nhận tên bảng + tên cột PK động để dùng chung cho mọi bảng có THOIDIEMXOA
CREATE OR REPLACE PROCEDURE PROC_XOAVINHVIEN_QUAHAN (
    P_TENBANG   IN VARCHAR2,
    P_TENCOTPK  IN VARCHAR2,
    P_SO_NGAY   IN NUMBER DEFAULT 120,
    P_SO_DONG   OUT NUMBER,
    P_MANV      IN NUMBER DEFAULT NULL
)
AS
    V_SQL VARCHAR2(2000);
BEGIN
    -- Đếm trước để trả về số bản ghi bị xóa
    V_SQL := 'SELECT COUNT(*) FROM ' || P_TENBANG
          || ' WHERE THOIDIEMXOA IS NOT NULL'
          || '   AND THOIDIEMXOA <= SYSDATE - :1';
    EXECUTE IMMEDIATE V_SQL INTO P_SO_DONG USING P_SO_NGAY;

    -- Xóa vĩnh viễn
    IF P_SO_DONG > 0 THEN
        V_SQL := 'DELETE FROM ' || P_TENBANG
              || ' WHERE THOIDIEMXOA IS NOT NULL'
              || '   AND THOIDIEMXOA <= SYSDATE - :1';
        EXECUTE IMMEDIATE V_SQL USING P_SO_NGAY;

        -- Ghi lịch sử hệ thống
        IF P_MANV IS NOT NULL THEN
            INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG)
            VALUES (P_MANV, 'HE_THONG',
                    'Xoa vinh vien ' || P_SO_DONG || ' ban ghi qua ' || P_SO_NGAY || ' ngay tu bang ' || P_TENBANG);
        END IF;
    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HE_THONG_XOAVINHVIEN,
            'Loi he thong khi xoa vinh vien bang ' || P_TENBANG || ': ' || SQLERRM);
END;
/
