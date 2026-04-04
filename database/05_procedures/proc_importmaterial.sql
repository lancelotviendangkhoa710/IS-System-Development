-- Procedure Nhập Kho
CREATE OR REPLACE PROCEDURE PROC_NHAPKHO(
    P_MANV IN NUMBER,
    P_MANCC IN NUMBER,
    P_JSON_DATALIST IN CLOB
)
IS
    V_MAPN NUMBER;
BEGIN
    -- 1. Khởi tạo chứng từ gốc
    INSERT INTO PHIEUNHAPKHO (MANV, MANCC)
    VALUES (P_MANV, P_MANCC)
    RETURNING MAPN INTO V_MAPN;

    -- 2. Đọc danh sách và Ghi chi tiết
    INSERT INTO CTPHIEUNHAP (MAPN, MANL, SOLUONG, DONGIA, SOLUONGCONLAI, NGAYSANXUAT, HANSUDUNG)
    SELECT V_MAPN, 
           J.MANL, 
           J.SOLUONG, 
           J.DONGIA, 
           J.SOLUONG,
           TO_DATE(J.NGAYSANXUAT, 'YYYY-MM-DD'), 
           TO_DATE(J.HANSUDUNG, 'YYYY-MM-DD')
    FROM JSON_TABLE(P_JSON_DATALIST, '$[*]'
        COLUMNS (
            MANL NUMBER PATH '$.MaNL',
            SOLUONG NUMBER PATH '$.SoLuong',
            DONGIA NUMBER PATH '$.DonGia',
            NGAYSANXUAT VARCHAR2(20) PATH '$.NgaySanXuat',
            HANSUDUNG VARCHAR2(20) PATH '$.HanSuDung'
        )
    ) J;

    -- 3. Bàn giao sức nặng tính toán cho Trigger & Chốt Giao Dịch
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NHAP_KHO, 'Loi he thong khi nhap kho vat tu: ' || SQLERRM);
END;
/
