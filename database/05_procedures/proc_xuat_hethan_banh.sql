-- Procedure Xuất hủy tự động toàn bộ bánh thành phẩm đã quá hạn sử dụng
-- Được gọi bởi JOB_QUETBANH_HETHAN mỗi ngày lúc 06:00
CREATE OR REPLACE PROCEDURE PROC_XUATTHANHPHAM_HETHAN (
    P_MANV    IN  NUMBER DEFAULT 1,
    P_SO_ME   OUT NUMBER,
    P_SO_BANH OUT NUMBER
)
AS
    V_MAPX       PHIEUXUATKHO.MAPX%TYPE;
    V_GIAVON     SANPHAM.GIAVON%TYPE;
BEGIN
    -- 1. Khởi tạo bộ đếm
    P_SO_ME   := 0;
    P_SO_BANH := 0;

    -- 2. Tạo phiếu xuất kho tổng — lý do cố định nhận diện hủy hết hạn
    INSERT INTO PHIEUXUATKHO (NGAYXUAT, LYDOXUAT, MANV)
    VALUES (CURRENT_TIMESTAMP, N'Bao cao het han su dung (tu dong)', P_MANV)
    RETURNING MAPX INTO V_MAPX;

    -- 3. Duyệt từng mẻ sản xuất còn tồn đã quá hạn
    FOR ROW_ME IN (
        SELECT ME.MAME, ME.MASP, ME.SOLUONGCONLAI
        FROM MESANXUAT ME
        WHERE TRUNC(ME.HANSUDUNG) < TRUNC(SYSDATE)
          AND NVL(ME.SOLUONGCONLAI, 0) > 0
        ORDER BY ME.HANSUDUNG ASC, ME.MAME ASC
    ) LOOP
        -- 3a. Lấy giá vốn hiện tại của sản phẩm
        SELECT NVL(GIAVON, 0)
        INTO V_GIAVON
        FROM SANPHAM
        WHERE MASP = ROW_ME.MASP;

        -- 3b. Insert chi tiết phiếu xuất → TRG_TRUKHO_PHIEUXUATTP tự trừ SANPHAM.SOLUONGTON
        INSERT INTO CTPHIEUXUAT_TP (MAPX, MASP, SOLUONG, DONGIAVON)
        VALUES (V_MAPX, ROW_ME.MASP, ROW_ME.SOLUONGCONLAI, V_GIAVON);

        -- 3c. Đánh dấu mẻ này đã được xử lý hoàn toàn
        UPDATE MESANXUAT
        SET SOLUONGCONLAI = 0
        WHERE MAME = ROW_ME.MAME;

        -- 3d. Tích lũy bộ đếm
        P_SO_ME   := P_SO_ME + 1;
        P_SO_BANH := P_SO_BANH + ROW_ME.SOLUONGCONLAI;
    END LOOP;

    -- 4. Ghi log hệ thống (chỉ khi có hủy thực sự)
    IF P_SO_BANH > 0 THEN
        INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
        VALUES (
            P_MANV,
            'HE_THONG',
            N'Tu dong huy ' || P_SO_BANH || N' banh het han tu ' || P_SO_ME || N' me (phieu #' || V_MAPX || N')',
            V_MAPX
        );
    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(
            PKG_ERROR_CODES.ERR_XUAT_THANHPHAM_HETHAN,
            N'Lỗi hệ thống khi xuất hủy bánh hết hạn: ' || SQLERRM
        );
END;
/
