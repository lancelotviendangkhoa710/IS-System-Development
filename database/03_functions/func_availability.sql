-- Hàm dự báo số lượng bánh tối đa có thể sản xuất dựa trên nguyên liệu tồn kho
CREATE OR REPLACE FUNCTION FUNC_SOLUONGKHADUNG(
    P_MASP IN SANPHAM.MASP%type
) RETURN NUMBER
IS
    V_MAX_CAKES NUMBER := 0;
    V_HAS_RECIPE NUMBER := 0;
BEGIN
    -- 1. Kiểm tra xem bánh có đăng ký công thức hay không
    SELECT COUNT(*) INTO V_HAS_RECIPE
    FROM CONGTHUC
    WHERE MASP = P_MASP;

    -- Nếu không có công thức, mặc định không thể sản xuất thêm
    IF V_HAS_RECIPE = 0 THEN
        RETURN 0;
    END IF;

    -- 2. Quét công thức, chia cho tồn kho và tìm điểm thắt cổ chai bằng hàm MIN()
    SELECT NVL(MIN(
                CASE 
                    WHEN NVL(N.SOLUONGTONTONG, 0) < 0 THEN 0
                    ELSE FLOOR(NVL(N.SOLUONGTONTONG, 0) / C.SOLUONGTIEUHAO)
                END
           ), 0)
    INTO V_MAX_CAKES
    FROM CONGTHUC C
    JOIN NGUYENLIEU N ON C.MANL = N.MANL
    WHERE C.MASP = P_MASP AND C.SOLUONGTIEUHAO > 0;

    RETURN V_MAX_CAKES;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/
