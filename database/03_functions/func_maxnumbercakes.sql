CREATE OR REPLACE FUNCTION TinhSoBanhToiDa(
    p_TenSP VARCHAR2
)
RETURN NUMBER
IS
    v_SoBanh NUMBER;
BEGIN

    SELECT MIN(FLOOR(nl.SoLuongTonTong / ct.SoLuongTieuHao))
    INTO v_SoBanh
    FROM SANPHAM sp
    JOIN CONGTHUC ct ON sp.MaSP = ct.MaSP
    JOIN NGUYENLIEU nl ON ct.MaNL = nl.MaNL
    WHERE sp.TenSP = p_TenSP;

    RETURN v_SoBanh;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
END;