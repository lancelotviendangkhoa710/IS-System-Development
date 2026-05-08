-- Procedure chốt giao dịch an toàn và Tự động thăng hạng khách hàng
CREATE OR REPLACE PROCEDURE PROC_THANHTOANVATHANGHANG(
    P_MAHD IN HOADON.MAHD%type,
    P_MAKH IN KHACHHANG.MAKH%type DEFAULT NULL,
    P_SOTIENTHANHTOAN IN HOADON.TONGTIENTHANHTOAN%type
)
IS
    V_MADON DONDATHANG.MADON%type;
    V_MATT_HOANTHANH TRANGTHAIDON.MATRANGTHAI%type;
    V_DIEM_CONGDON KHACHHANG.DIEMTICHLUY%type := 0;
    V_DIEM_HIENTAI KHACHHANG.DIEMTICHLUY%type := 0;
    V_MAHANG_MOI HANGTHANHVIEN.MAHANG%type := NULL;
BEGIN
    -- 1. Chốt đơn hàng (Cập nhật trạng thái)
    SELECT MADON INTO V_MADON
    FROM HOADON
    WHERE MAHD = P_MAHD;

    -- Lấy ID trạng thái ứng với "Hoàn thành"
    SELECT MATRANGTHAI INTO V_MATT_HOANTHANH
    FROM TRANGTHAIDON
    WHERE UPPER(TENTRANGTHAI) = 'HOÀN THÀNH';

    -- Tiến hành UPDATE
    UPDATE DONDATHANG
    SET MATRANGTHAI = V_MATT_HOANTHANH
    WHERE MADON = V_MADON;

    -- 2. Tích điểm và Thăng Hạng (chỉ thực hiện nếu có MaKH, loại trừ Khách Vãng Lai)
    IF P_MAKH IS NOT NULL THEN
        -- Quy đổi tiền thành điểm (cứ 10.000đ = 1 điểm)
        V_DIEM_CONGDON := FLOOR(P_SOTIENTHANHTOAN / 10000);

        -- Cộng dồn vào điểm hiện tại của khách
        UPDATE KHACHHANG
        SET DIEMTICHLUY = DIEMTICHLUY + V_DIEM_CONGDON
        WHERE MAKH = P_MAKH;

        -- Lấy ra mức điểm mới nhất để rà soát thăng hạng
        SELECT DIEMTICHLUY INTO V_DIEM_HIENTAI
        FROM KHACHHANG
        WHERE MAKH = P_MAKH;

        -- 3. Quét hạng thành viên (tìm hạng cao nhất mà khách đủ điều kiện)
        BEGIN
            SELECT MAHANG INTO V_MAHANG_MOI
            FROM HANGTHANHVIEN
            WHERE DIEMTOITHIEU <= V_DIEM_HIENTAI
              AND THOIDIEMXOA IS NULL
            ORDER BY DIEMTOITHIEU DESC
            FETCH FIRST 1 ROW ONLY;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                V_MAHANG_MOI := NULL;
        END;

        -- Cập nhật hạng chốt
        UPDATE KHACHHANG
        SET MAHANG = V_MAHANG_MOI
        WHERE MAKH = P_MAKH;
    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_THANH_TOAN_GIAO_DICH, 'Loi he thong khi thanh toan giao dich: ' || SQLERRM);
END;
/