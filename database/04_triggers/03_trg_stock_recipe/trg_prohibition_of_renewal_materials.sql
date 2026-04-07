-- Trigger chặn hạn sử dụng chỉ còn 10 ngày kể từ lúc nhập & chống gian lận hạn sử dụng
CREATE OR REPLACE TRIGGER TRG_CHONGGIANLAN_HSD

BEFORE INSERT OR UPDATE OF HANSUDUNG ON CTPHIEUNHAP
FOR EACH ROW
DECLARE
    V_NGAYNHAP DATE;
BEGIN
    -- Chống gian lận hạn sử dụng
    IF UPDATING THEN
        IF :NEW.HANSUDUNG > :OLD.HANSUDUNG THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_GIAN_LAN_HSD,
                'CANH BAO GIAN LAN: Han su dung moi (' || TO_CHAR(:NEW.HANSUDUNG, 'DD/MM/YYYY') ||
                ') khong duoc phep lon hon han su dung cu (' || TO_CHAR(:OLD.HANSUDUNG, 'DD/MM/YYYY') || ').');
        END IF;
    END IF;

    -- Chặn hạn sử dụng chỉ còn 10 ngày kể từ lúc nhập
    SELECT NGAYNHAP INTO V_NGAYNHAP
    FROM PHIEUNHAPKHO
    WHERE MAPN = :NEW.MAPN;

    IF TRUNC(:NEW.HANSUDUNG) <= TRUNC(V_NGAYNHAP) THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_HSD_KHONG_HOPLE,
            'LOI NGHIEP VU: Han su dung cua lo nay (' || TO_CHAR(:NEW.HANSUDUNG, 'DD/MM/YYYY') ||
            ') va ngay nhap kho (' || TO_CHAR(V_NGAYNHAP, 'DD/MM/YYYY') || ') da qua han!.');
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_KHONG_CO_PHIEUNHAP, 'Loi: Khong tim thay phieu nhap kho.');
END;
/