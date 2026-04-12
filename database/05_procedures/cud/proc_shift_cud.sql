-- ==============================================================
-- PROCEDURE CUD (Create, Update) - MODULE CA LÀM VIỆC & ĐỐI SOÁT
-- Gồm: CA LÀM VIỆC (CALAMVIEC), ĐỐI SOÁT (DOISOAT)
-- Tác giả: Antigravity
-- ==============================================================

-- --------------------------------------------------------------
-- 1. CHỈ CREATE: MỞ CA LÀM VIỆC
-- --------------------------------------------------------------
CREATE OR REPLACE PROCEDURE PROC_MOCA(
    P_MANV IN NUMBER,
    P_MAMAYPOS IN VARCHAR2,
    P_MACA_OUT OUT NUMBER
)
IS
BEGIN
    INSERT INTO CALAMVIEC (MANV, MAMAYPOS, THOIGIANMOCA, TRANGTHAI)
    VALUES (P_MANV, P_MAMAYPOS, SYSDATE, 'Đang mở')
    RETURNING MACA INTO P_MACA_OUT;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_CA_MO_HE_THONG, 'Lỗi hệ thống khi Khởi chạy Mở Ca làm việc: ' || SQLERRM);
END;
/

-- --------------------------------------------------------------
-- 2. CHỈ UPDATE & CREATE: ĐÓNG CA VÀ LẬP BIÊN BẢN ĐỐI SOÁT
-- --------------------------------------------------------------
CREATE OR REPLACE PROCEDURE PROC_DONGCADOISOAT(
    P_MACA            IN NUMBER,
    P_TIENTHUCTEDEM   IN NUMBER,
    P_LYDOCHENHLECH   IN VARCHAR2 DEFAULT NULL
)
IS
    V_TONGTIENHETHONG NUMBER;
    V_CHENHLECH       NUMBER;
BEGIN
    -- 2.1 LẤY CON SỐ HỆ THỐNG MỚI NHẤT (DOUBLE-CHECK)
    V_TONGTIENHETHONG := FUNC_TINHTIENMATLYTUONG(P_MACA);
    V_CHENHLECH       := P_TIENTHUCTEDEM - V_TONGTIENHETHONG;

    -- 2.2 LƯU KẾT QUẢ ĐỐI SOÁT (Chỉ được lập 1 lần lúc đóng ca)
    INSERT INTO DOISOAT (MACA, TONGTIENHETHONG, TIENTHUCTEDEM, CHENHLECH, LYDOCHENHLECH)
    VALUES (P_MACA, V_TONGTIENHETHONG, P_TIENTHUCTEDEM, V_CHENHLECH, P_LYDOCHENHLECH);

    -- 2.3 CẬP NHẬT TRẠNG THÁI CA
    UPDATE CALAMVIEC
    SET THOIGIANDONGCA = SYSDATE,
        TRANGTHAI = 'Đã đóng'
    WHERE MACA = P_MACA AND TRANGTHAI = 'Đang mở';

    -- NẾU KHÔNG TÌM THẤY CA ĐỂ UPDATE, BÁO LỖI VỀ JAVA
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_CA_KHONG_TON_TAI, 'Lỗi: Ca làm việc không tồn tại hoặc đã được đóng trước đó.');
    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = PKG_ERROR_CODES.ERR_CA_KHONG_TON_TAI THEN RAISE; END IF;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_CA_DONG_HE_THONG, 'Lỗi hệ thống khi Đóng ca và Đối soát: ' || SQLERRM);
END;
/
