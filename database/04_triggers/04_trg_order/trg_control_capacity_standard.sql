-- Trigger cộng dồn số bánh khách đặt và kiểm soát năng lực (đơn hàng)
    CREATE OR REPLACE TRIGGER TRG_KIEMSOAT_CONGSUAT_DONHANG
    AFTER INSERT OR UPDATE OR DELETE ON CTDONHANG
    FOR EACH ROW
    DECLARE
        V_MADON NUMBER;
        V_MASP NUMBER;
        V_CHENHLECH NUMBER(10,2) := 0;
        V_NGAYGIAO DATE;
        V_THOIGIANCHUANBI NUMBER := 0;
        V_NGAYSANXUAT DATE;
        V_SOBANH_HIENTAI NUMBER;
        V_GIOIHAN NUMBER;
    BEGIN
        IF INSERTING THEN
            V_CHENHLECH := :NEW.SOLUONG;
            V_MADON := :NEW.MADON;
            V_MASP := :NEW.MASP;
        ELSIF UPDATING THEN
            V_CHENHLECH := :NEW.SOLUONG - :OLD.SOLUONG;
            V_MADON := :NEW.MADON;
            V_MASP := :NEW.MASP;
        ELSIF DELETING THEN
            V_CHENHLECH := -(:OLD.SOLUONG);
            V_MADON := :OLD.MADON;
            V_MASP := :OLD.MASP;
        END IF;

        -- Lấy thời gian chuẩn bị từ SANPHAM (Dùng try-catch phòng trường hợp sai kiểu dữ liệu)
        BEGIN
            SELECT NVL(THOIGIANCHUANBI, 0) INTO V_THOIGIANCHUANBI FROM SANPHAM WHERE MASP = V_MASP;
        EXCEPTION
            WHEN OTHERS THEN V_THOIGIANCHUANBI := 0;
        END;

        SELECT TRUNC(NGAYGIONHANBANH) INTO V_NGAYGIAO
        FROM DONDATHANG
        WHERE MADON = V_MADON;

        -- Tính ngày sản xuất thực tế bằng cách lùi thời gian giao hàng lại
        V_NGAYSANXUAT := V_NGAYGIAO - V_THOIGIANCHUANBI;

        SELECT NVL(SOBANHDANHAN, 0), NVL(GIOIHANSOBANH, 0)
        INTO V_SOBANH_HIENTAI, V_GIOIHAN
        FROM NANGLUCSANXUAT
        WHERE TRUNC(NGAYSANXUAT) = V_NGAYSANXUAT
        FOR UPDATE;

        IF (V_SOBANH_HIENTAI + V_CHENHLECH) > V_GIOIHAN THEN
            RAISE_APPLICATION_ERROR(-20005,
                'TU CHOI NHAN DON: Ngay San Xuat Thuc Te (' || TO_CHAR(V_NGAYSANXUAT, 'DD/MM/YYYY') || ') ' ||
                ' da dat cong suat toi da! (Da nhan: ' || V_SOBANH_HIENTAI || '/' || V_GIOIHAN || ' banh, Khach dat them: ' || V_CHENHLECH || ' banh). ' ||
                'Vui long khuyen khich khach doi sang ngay khac!');
        END IF;

        UPDATE NANGLUCSANXUAT
        SET SOBANHDANHAN = V_SOBANH_HIENTAI + V_CHENHLECH
        WHERE TRUNC(NGAYSANXUAT) = V_NGAYSANXUAT;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('Luu y: Chua co du lieu gioi han cong suat cho ngay san xuat ' || TO_CHAR(V_NGAYSANXUAT, 'DD/MM/YYYY'));
    END;
    /
