-- Seed tai khoan nhan vien de test tinh nang dang nhap/tao tai khoan
-- Chay voi schema BAKERY_MANAGER

DECLARE
    v_role_id NUMBER;
    v_cnt     NUMBER;
BEGIN
    BEGIN
        SELECT MAVAITRO INTO v_role_id
        FROM VAITRO
        WHERE TENVAITRO = 'NV'
        FETCH FIRST 1 ROW ONLY;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            INSERT INTO VAITRO (TENVAITRO, MOTA)
            VALUES ('NV', 'Default employee role for auth test')
            RETURNING MAVAITRO INTO v_role_id;
    END;

    SELECT COUNT(*) INTO v_cnt FROM NHANVIEN WHERE TENDANGNHAP = 'baker01';
    IF v_cnt = 0 THEN
        INSERT INTO NHANVIEN (MAVAITRO, HOTEN, SDT, TENDANGNHAP, MATKHAU, TRANGTHAILAMVIEC)
        VALUES (v_role_id, 'Nguyen Thi Banh', '0912345601', 'baker01', '123', 1);
    END IF;

    SELECT COUNT(*) INTO v_cnt FROM NHANVIEN WHERE TENDANGNHAP = 'cashier01';
    IF v_cnt = 0 THEN
        INSERT INTO NHANVIEN (MAVAITRO, HOTEN, SDT, TENDANGNHAP, MATKHAU, TRANGTHAILAMVIEC)
        VALUES (v_role_id, 'Tran Thu Ngan', '0912345602', 'cashier01', '123', 1);
    END IF;

    COMMIT;
END;
/

SELECT MANV, HOTEN, SDT, TENDANGNHAP, TRANGTHAILAMVIEC
FROM NHANVIEN
WHERE TENDANGNHAP IN ('baker01', 'cashier01')
ORDER BY TENDANGNHAP;
