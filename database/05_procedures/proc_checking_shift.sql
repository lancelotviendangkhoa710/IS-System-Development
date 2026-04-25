-- ============================================================
-- PROC_DONGCADOISOAT — Ghi kết quả đối soát cuối ca
--
-- Lưu ý thiết kế:
--   • Row DOISOAT đã được INSERT khi mở ca (với TIENKHAIBAODAUCA).
--     Procedure này chỉ UPDATE các cột còn lại — không INSERT lại.
--   • CALAMVIEC (THOIGIANDONGCA, TRANGTHAI) được Java cập nhật
--     riêng qua CaLamViecDAO.dongCa() — procedure KHÔNG làm lại.
--   • Chênh lệch được tính lại tại đây để đảm bảo tính toàn vẹn
--     (double-check), tham số P_CHENHLECH từ Java chỉ dùng tham khảo.
-- ============================================================
CREATE OR REPLACE PROCEDURE PROC_DONGCADOISOAT(
    P_MACA          IN CALAMVIEC.MACA%TYPE,
    P_TIENTHUCTEDEM IN DOISOAT.TIENTHUCTEDEM%TYPE,
    P_CHENHLECH     IN DOISOAT.CHENHLECH%TYPE,        -- tham khảo từ Java
    P_LYDOCHENHLECH IN DOISOAT.LYDOCHENHLECH%TYPE DEFAULT NULL
)
IS
    V_TONGTIENHETHONG   NUMBER;
    V_CHENHLECH         NUMBER;
    V_ROWS_UPDATED      NUMBER;
BEGIN
    -- 1. Tính lại tiền lý tưởng từ hệ thống (double-check)
    V_TONGTIENHETHONG := FUNC_TINHTIENMATLYTUONG(P_MACA);
    V_CHENHLECH       := P_TIENTHUCTEDEM - V_TONGTIENHETHONG;

    -- 2. Cập nhật DOISOAT (row đã tồn tại từ bước mở ca)
    UPDATE DOISOAT
    SET TONGTIENHETHONG = V_TONGTIENHETHONG,
        TIENTHUCTEDEM   = P_TIENTHUCTEDEM,
        CHENHLECH       = V_CHENHLECH,
        LYDOCHENHLECH   = P_LYDOCHENHLECH
    WHERE MACA = P_MACA;

    V_ROWS_UPDATED := SQL%ROWCOUNT;

    IF V_ROWS_UPDATED = 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_CA_KHONG_TON_TAI,
        'Không tìm thấy bản ghi đối soát cho ca: ' || P_MACA
        );
    END IF;

    -- Không COMMIT ở đây — Java tự quản lý transaction
    -- (caLamViecDAO.dongCa() sẽ UPDATE CALAMVIEC trong cùng connection)

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = PKG_ERROR_CODES.ERR_CA_KHONG_TON_TAI THEN RAISE; END IF;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_CA_DONG_HE_THONG,
        'Lỗi hệ thống khi ghi kết quả đối soát: ' || SQLERRM
        );
END;
/
