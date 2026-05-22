

CREATE OR REPLACE PROCEDURE PROC_HUYHOADONBANLE(
    P_MADON         IN DONDATHANG.MADON%TYPE,
    P_LYDOHUY       IN NVARCHAR2,
    P_MANV_CAPNHAT  IN NHANVIEN.MANV%TYPE
)
IS
    V_MATRANGTHAI_CU NUMBER;
    V_MATT_HUY       NUMBER;
    V_MAHOADON       NUMBER;
    V_HINHTHUCNHAN   NUMBER;
BEGIN
    -- 1. Lấy trạng thái hiện tại và hình thức nhận (Lock dòng đơn — ngăn 2 NV cùng hủy 1 đơn)
    SELECT MATRANGTHAI, HINHTHUCNHAN
    INTO V_MATRANGTHAI_CU, V_HINHTHUCNHAN
    FROM DONDATHANG
    WHERE MADON = P_MADON
    FOR UPDATE;

    -- 2. Chỉ cho phép hủy đơn bán trực tiếp (HINHTHUCNHAN = 1)
    IF V_HINHTHUCNHAN != 1 THEN
        RAISE_APPLICATION_ERROR(
            PKG_ERROR_CODES.ERR_TRANG_THAI_KHONG_HOP_LE,
            'Chi duoc huy hoa don ban le (truc tiep tai quay). Don dat truoc su dung chuc nang Huy Don.'
        );
    END IF;

    -- 3. Kiểm tra trạng thái hiện tại có phải Hoàn thành không
    SELECT UPPER(TENTRANGTHAI) INTO V_MATT_HUY
    FROM TRANGTHAIDON WHERE MATRANGTHAI = V_MATRANGTHAI_CU;

    IF V_MATT_HUY != 'HOAN THANH' AND V_MATT_HUY != N'HOÀN THÀNH' THEN
        RAISE_APPLICATION_ERROR(
            PKG_ERROR_CODES.ERR_TRANG_THAI_KHONG_HOP_LE,
            'Hoa don nay khong o trang thai Hoan Thanh, khong the huy.'
        );
    END IF;

    -- 4. Lấy ID trạng thái Hủy
    SELECT MATRANGTHAI INTO V_MATT_HUY
    FROM TRANGTHAIDON
    WHERE UPPER(TENTRANGTHAI) IN ('HUY', N'HỦY')
    FETCH FIRST 1 ROW ONLY;

    -- 5. Cập nhật trạng thái đơn → Hủy
    UPDATE DONDATHANG
    SET MATRANGTHAI = V_MATT_HUY
    WHERE MADON = P_MADON;

    -- 6. Hoàn kho theo từng sản phẩm trong chi tiết đơn

    FOR ROW_CT IN (SELECT MASP, SOLUONG FROM CTDONHANG WHERE MADON = P_MADON) LOOP
        UPDATE SANPHAM
        SET SOLUONGTON = NVL(SOLUONGTON, 0) + ROW_CT.SOLUONG
        WHERE MASP = ROW_CT.MASP;
    END LOOP;

    -- 7. Ghi nhật ký lịch sử
    INSERT INTO LICHSUDONHANG(MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT, GHICHU)
    VALUES (P_MADON, V_MATRANGTHAI_CU, V_MATT_HUY, CURRENT_TIMESTAMP, P_MANV_CAPNHAT,
            N'[HỦY HĐ BÁN LẺ] ' || SUBSTR(P_LYDOHUY, 1, 90));

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
select * from SANPHAM ; commit