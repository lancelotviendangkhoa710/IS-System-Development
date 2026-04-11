-- Procedure Cập nhật trạng thái đa năng kèm Lưu vết tự động (Transaction khép kín)
CREATE OR REPLACE PROCEDURE PROC_CHUYENTRANGTHAIDON(
    P_MADON IN NUMBER,
    P_MATRANGTHAI_MOI IN NUMBER,
    P_MANV_CAPNHAT IN NUMBER,
    P_HINHTHUCNHAN IN NUMBER DEFAULT NULL
)
IS
    V_MATRANGTHAI_CU NUMBER;
    V_MATRANGTHAI_CHOT NUMBER;
BEGIN
    -- 1. Lấy trạng thái hiện tại (Để ghi lại vào History Log)
    SELECT MATRANGTHAI INTO V_MATRANGTHAI_CU
    FROM DONDATHANG
    WHERE MADON = P_MADON;

    V_MATRANGTHAI_CHOT := P_MATRANGTHAI_MOI;

    -- 2. Rẽ nhánh thông minh: Nếu giao diện bếp báo Hoàn Thành, hệ thống tự ép Trạng Thái Dựa theo Hình Thức Nhận
    IF P_HINHTHUCNHAN IS NOT NULL THEN
        IF P_HINHTHUCNHAN = 2 THEN
            -- Giao tận nơi (2) -> Đẩy sang "Chờ giao"
            SELECT MATRANGTHAI INTO V_MATRANGTHAI_CHOT
            FROM TRANGTHAIDON
            WHERE UPPER(TENTRANGTHAI) = 'CHỜ GIAO';
        ELSIF P_HINHTHUCNHAN = 1 THEN
            -- Lấy tại tiệm (1) -> Đẩy sang "Chờ khách lấy"
            SELECT MATRANGTHAI INTO V_MATRANGTHAI_CHOT
            FROM TRANGTHAIDON
            WHERE UPPER(TENTRANGTHAI) = 'CHỜ KHÁCH LẤY';
        END IF;
    END IF;

    -- 3. Thực thi đổi trạng thái (Giao dịch thật)
    UPDATE DONDATHANG
    SET MATRANGTHAI = V_MATRANGTHAI_CHOT
    WHERE MADON = P_MADON;

    -- 4. Kẹp luôn dòng dữ liệu Lịch sử không thể chối cãi vào bằng chung 1 Transaction
    INSERT INTO LICHSUDONHANG(MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT)
    VALUES (P_MADON, V_MATRANGTHAI_CU, V_MATRANGTHAI_CHOT, CURRENT_TIMESTAMP, P_MANV_CAPNHAT);

    -- 5. Chốt sổ đồng bộ cả Cập Nhật Mới + Lịch sử Cũ
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_DON_CHUYEN_TRANGTHAI, 'Lỗi xảy ra khi chuyển trạng thái đơn (Transaction Rollbacked): ' || SQLERRM);
END;
/
