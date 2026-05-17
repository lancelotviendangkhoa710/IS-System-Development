-- Hàm xác định lô nguyên liệu cần xuất theo FEFO (First Expired, First Out)
-- Input:  P_MANL — mã nguyên liệu
-- Output: Cursor các lô còn hàng, sắp xếp FEFO (hạn sử dụng gần nhất xuất trước)
--
-- Dùng để tra cứu lô ưu tiên trong UI kế hoạch sản xuất / thủ kho kiểm tra.
-- (Thực tế xuất kho vẫn dùng cursor FIFO/FEFO trong PROC_XUATKHOSANXUAT)
CREATE OR REPLACE FUNCTION FUNC_XACDINHPHIEUNHAPFEFO(
    P_MANL IN NGUYENLIEU.MANL%type
) RETURN SYS_REFCURSOR
IS
    V_REFCURSOR SYS_REFCURSOR;
BEGIN
    OPEN V_REFCURSOR FOR
        SELECT CTN.MALO,
               CTN.SOLUONGCONLAI,
               CTN.HANSUDUNG,
               CTN.DONGIA
        FROM CTPHIEUNHAP CTN
        WHERE CTN.MANL = P_MANL
          AND CTN.SOLUONGCONLAI > 0
        ORDER BY CTN.HANSUDUNG ASC NULLS LAST, CTN.MALO ASC;

    RETURN V_REFCURSOR;
EXCEPTION
    WHEN OTHERS THEN
        OPEN V_REFCURSOR FOR
            SELECT -1 AS MALO, 0 AS SOLUONGCONLAI, NULL AS HANSUDUNG, 0 AS DONGIA
            FROM DUAL WHERE 1=0;
        RETURN V_REFCURSOR;
END;
/
