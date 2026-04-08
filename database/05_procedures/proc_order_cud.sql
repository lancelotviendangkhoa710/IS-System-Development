-- Procedure Khởi tạo Đơn Hàng (Master-Detail với JSON_TABLE)
CREATE OR REPLACE PROCEDURE PROC_TAODONHANG(
    P_NGAYGIONHANBANH IN TIMESTAMP,
    P_MAKH IN NUMBER DEFAULT NULL,
    P_MANV_LAP IN NUMBER,
    P_MATRANGTHAI IN NUMBER,
    P_TIENDACOC IN NUMBER DEFAULT 0,
    P_HINHTHUCNHAN IN NUMBER DEFAULT 1,
    P_DIACHIGIAO IN NVARCHAR2 DEFAULT NULL,
    P_JSONCHITIET IN CLOB,
    P_MADON_OUT OUT NUMBER
)
IS
BEGIN
    -- 1. Insert Đơn Hàng Góc (Master Record)
    -- TONGTIENHDBAN sẽ mặc định là 0. Việc cộng dồn tổng tiền sẽ được bàn giao
    -- cho thiết kế Trigger TRG_CTDONHANG_UPDATE tự động thực thi khi có dòng chi tiết đẩy vào.
    INSERT INTO DONDATHANG (NGAYGIONHANBANH, MAKH, MANV_LAP, MATRANGTHAI, TIENDACOC, HINHTHUCNHAN, DIACHIGIAO)
    VALUES (P_NGAYGIONHANBANH, P_MAKH, P_MANV_LAP, P_MATRANGTHAI, NVL(P_TIENDACOC, 0), P_HINHTHUCNHAN, P_DIACHIGIAO)
    RETURNING MADON INTO P_MADON_OUT;

    -- 2. Đẩy đồng loạt chi tiết đơn hàng (Sử dụng JSON_TABLE siêu tốc độ)
    -- Ghi toàn bộ dữ liệu vô bảng chuẩn CTDONHANG
    INSERT INTO CTDONHANG (MADON, MASP, SOLUONG, DONGIA)
    SELECT P_MADON_OUT, J.MASP, J.SOLUONG, J.DONGIA
    FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
        COLUMNS (
            MASP NUMBER PATH '$.maSP',
            SOLUONG NUMBER PATH '$.soLuong',
            DONGIA NUMBER PATH '$.donGia'
        )
    ) J;

    -- 3. Đẩy chi tiết thuộc tính Tùy Chỉnh
    -- Quét lại JSON 1 lần nữa nhưng có bộ lọc IS_CUSTOM = true để đưa vào Base Mở rộng (CTDONTUYCHINH)
    -- Mapping: node 'ghiChu' vào LOICHUCTRENBANH, node 'phuKien' vào GHICHUTHOBANH
    INSERT INTO CTDONTUYCHINH (MADON, MASP, SOLUONG, DONGIA, LOICHUCTRENBANH, GHICHUTHOBANH)
    SELECT P_MADON_OUT, J.MASP, J.SOLUONG, J.DONGIA, J.GHICHU, J.PHUKIEN
    FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
        COLUMNS (
            MASP NUMBER PATH '$.maSP',
            SOLUONG NUMBER PATH '$.soLuong',
            DONGIA NUMBER PATH '$.donGia',
            IS_CUSTOM VARCHAR2(10) PATH '$.isCustom',
            GHICHU NVARCHAR2(200) PATH '$.ghiChu',
            PHUKIEN NVARCHAR2(500) PATH '$.phuKien'
        )
    ) J
    WHERE LOWER(J.IS_CUSTOM) = 'true';

    -- 4. Bàn giao sức mạnh tổng hợp cho Trigger & Chốt Giao Dịch Cứng
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        -- Phản ứng tức thời ngắt toàn quyền, thu hồi Master-Detail đảm bảo Partial Transaction không xảy ra
        ROLLBACK;
        -- Re-throw Exception có kèm Header Thông Báo để Java Exception Handler tiện lợi tóm dính
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON, 'Loi he thong khi Tao Don Hang (Master-Detail): ' || SQLERRM);
END;
/
