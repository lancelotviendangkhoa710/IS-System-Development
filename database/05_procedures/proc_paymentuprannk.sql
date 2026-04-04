-- Procedure chốt giao dịch an toàn và Tự động thăng hạng khách hàng
CREATE OR REPLACE PROCEDURE PROC_THANHTOANVATHANGHANG(
    P_MAHD IN NUMBER,
    P_MAKH IN NUMBER DEFAULT NULL,
    P_SOTIENTHANHTOAN IN NUMBER
)
IS
    V_MADON NUMBER;
    V_MATT_HOANTHANH NUMBER;
    V_DIEM_CONGDON NUMBER := 0;
    V_DIEM_HIENTAI NUMBER := 0;
    V_MAHANG_MOI NUMBER := NULL;
BEGIN
    -- 1. Chốt đơn hàng (Cập nhật trạng thái)
    -- Truy vấn ngược từ Hóa Đơn lấy ra Mã Đơn hàng gốc
    SELECT MADON INTO V_MADON
    FROM HOADON
    WHERE MAHD = P_MAHD;

    -- Lấy ID trạng thái ứng với "Hoàn thành" một cách bảo mật (không gọi Code cứng)
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