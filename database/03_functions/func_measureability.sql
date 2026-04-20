-- Hàm xác định số lượng bánh tùy chỉnh còn trống có thể nhận được trong một ngày
CREATE OR REPLACE FUNCTION FUNC_DIEMKHADUNG(
    P_NGAYCANKIEMTRA IN NANGLUCSANXUAT.NGAYSANXUAT%type
) RETURN NUMBER
IS
    V_GIOIHAN NUMBER := 0;
    V_TONG_DADAT NUMBER := 0;
    V_KHA_DUNG NUMBER := 0;
BEGIN
    -- 1. Lấy Giới hạn năng lực sản xuất của xưởng trong ngày đó
    BEGIN
        SELECT GIOIHANSOBANH
        INTO V_GIOIHAN
        FROM NANGLUCSANXUAT
        WHERE TRUNC(NGAYSANXUAT) = TRUNC(P_NGAYCANKIEMTRA);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            V_GIOIHAN := 0; -- Nếu xưởng chưa cấu hình tải trọng cho ngày này, mặc định là 0 (Không nhận)
    END;

    -- 2. Quét tải trọng hiện tại của bánh tùy chỉnh (Loại trừ các đơn Hủy)
    -- Dùng NVL để tránh lỗi NULL khi không có đơn nào
    SELECT NVL(SUM(CTC.SOLUONG), 0)
    INTO V_TONG_DADAT
    FROM DONDATHANG DDH
    JOIN CTDONTUYCHINH CTC ON DDH.MADON = CTC.MADON
    JOIN TRANGTHAIDON TT ON DDH.MATRANGTHAI = TT.MATRANGTHAI
    WHERE TRUNC(DDH.NGAYGIONHANBANH) = TRUNC(P_NGAYCANKIEMTRA)
      AND UPPER(TT.TENTRANGTHAI) NOT LIKE '%HỦY%';

    -- 3. Thực thi phép trừ
    V_KHA_DUNG := V_GIOIHAN - V_TONG_DADAT;
    
    -- Nếu phép tính bị âm thì quy về 0
    IF V_KHA_DUNG < 0 THEN
        RETURN 0;
    ELSE
        RETURN V_KHA_DUNG;
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/
