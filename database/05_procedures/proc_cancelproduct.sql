-- Procedure Xuất hủy bánh (do lỗi kỹ thuật, hết hạn sử dụng, bom hàng)
CREATE OR REPLACE PROCEDURE PROC_XUATHUYBANH(
    P_MASP IN NUMBER,
    P_SOLUONGHUY IN NUMBER,
    P_LYDOXUAT IN NVARCHAR2,
    P_MANV IN NUMBER
)
IS
    V_SOLUONGTON NUMBER;
    V_MAPX NUMBER;
BEGIN
    -- 1. Xác thực (Checking Stock)
    BEGIN
        SELECT SOLUONGTON INTO V_SOLUONGTON
        FROM SANPHAM
        WHERE MASP = P_MASP;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_SP_KHONG_TON_TAI, 'Ma san pham khong ton tai trong kho!');
    END;

    -- Chống Nhân viên gõ nhầm số lượng lớn hơn số bánh đang có thật trên kệ
    IF V_SOLUONGTON < P_SOLUONGHUY THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_XUAT_HUY_BANH, 'So luong huy vuot qua so luong ton kho hien tai! (Ton: ' || V_SOLUONGTON || ')');
    END IF;

    -- 2. Lập chứng từ (INSERT INTO PHIEUXUATKHO)
    INSERT INTO PHIEUXUATKHO (LYDOXUAT, MANV)
    VALUES (P_LYDOXUAT, P_MANV)
    RETURNING MAPX INTO V_MAPX;

    -- 3. Ghi chi tiết (INSERT INTO CTPHIEUXUAT_TP)
    INSERT INTO CTPHIEUXUAT_TP (MAPX, MASP, SOLUONG)
    VALUES (V_MAPX, P_MASP, P_SOLUONGHUY);

    -- 4. Bàn giao cho Trigger và Chốt sổ
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = PKG_ERROR_CODES.ERR_XUAT_HUY_BANH OR SQLCODE = PKG_ERROR_CODES.ERR_SP_KHONG_TON_TAI THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HE_THONG_KHOIPHUC, 'Loi he thong khi xuat huy banh: ' || SQLERRM);
        END IF;
END;
/
