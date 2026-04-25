-- ============================================================
-- CUD Procedures cho bảng NHACUNGCAP (Nhà Cung Cấp)
-- ============================================================

-- Procedure Thêm Nhà Cung Cấp
CREATE OR REPLACE PROCEDURE PROC_THEM_NHACUNGCAP(
    P_TENNCC IN NHACUNGCAP.TENNCC%TYPE,
    P_SDT IN NHACUNGCAP.SDT%TYPE,
    P_DIACHI IN NHACUNGCAP.DIACHI%TYPE,
    P_MANCC_OUT OUT NHACUNGCAP.MANCC%TYPE
) IS
    V_COUNT NUMBER;
BEGIN
    -- 1. Kiểm tra SĐT có bị trùng không (nếu có SĐT)
    IF P_SDT IS NOT NULL AND TRIM(P_SDT) IS NOT NULL THEN
        SELECT COUNT(*) INTO V_COUNT FROM NHACUNGCAP WHERE SDT = P_SDT AND THOIDIEMXOA IS NULL;
        IF V_COUNT > 0 THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NCC_TRUNG_SDT, 'Số điện thoại nhà cung cấp đã tồn tại.');
        END IF;
    END IF;

    -- 2. Thêm mới
    INSERT INTO NHACUNGCAP (TENNCC, SDT, DIACHI)
    VALUES (TRIM(P_TENNCC), TRIM(P_SDT), TRIM(P_DIACHI))
    RETURNING MANCC INTO P_MANCC_OUT;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = PKG_ERROR_CODES.ERR_NCC_TRUNG_SDT THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NCC_THEM_HE_THONG, 'Lỗi hệ thống khi thêm nhà cung cấp: ' || SQLERRM);
        END IF;
END;
/

-- Procedure Cập Nhật Nhà Cung Cấp
CREATE OR REPLACE PROCEDURE PROC_SUA_NHACUNGCAP(
    P_MANCC IN NHACUNGCAP.MANCC%TYPE,
    P_TENNCC IN NHACUNGCAP.TENNCC%TYPE,
    P_SDT IN NHACUNGCAP.SDT%TYPE,
    P_DIACHI IN NHACUNGCAP.DIACHI%TYPE
) IS
    V_COUNT NUMBER;
BEGIN
    -- 1. Kiểm tra tồn tại
    SELECT COUNT(*) INTO V_COUNT FROM NHACUNGCAP WHERE MANCC = P_MANCC AND THOIDIEMXOA IS NULL;
    IF V_COUNT = 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NCC_KHONG_TON_TAI_CN, 'Nhà cung cấp không tồn tại hoặc đã bị ngừng giao dịch.');
    END IF;

    -- 2. Kiểm tra trùng SĐT với nhà cung cấp khác
    IF P_SDT IS NOT NULL AND TRIM(P_SDT) IS NOT NULL THEN
        SELECT COUNT(*) INTO V_COUNT FROM NHACUNGCAP WHERE SDT = P_SDT AND MANCC != P_MANCC AND THOIDIEMXOA IS NULL;
        IF V_COUNT > 0 THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NCC_TRUNG_SDT, 'Số điện thoại đã được sử dụng bởi nhà cung cấp khác.');
        END IF;
    END IF;

    -- 3. Cập nhật
    UPDATE NHACUNGCAP
    SET TENNCC = TRIM(P_TENNCC),
        SDT = TRIM(P_SDT),
        DIACHI = TRIM(P_DIACHI)
    WHERE MANCC = P_MANCC;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE IN (PKG_ERROR_CODES.ERR_NCC_KHONG_TON_TAI_CN, PKG_ERROR_CODES.ERR_NCC_TRUNG_SDT) THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NCC_CAPNHAT_HE_THONG, 'Lỗi hệ thống khi cập nhật nhà cung cấp: ' || SQLERRM);
        END IF;
END;
/

-- Procedure Xóa Nhà Cung Cấp (Soft Delete hoặc Hard Delete tùy theo lịch sử giao dịch)
CREATE OR REPLACE PROCEDURE PROC_XOA_NHACUNGCAP(
    P_MANCC IN NHACUNGCAP.MANCC%TYPE,
    P_MANV_CAPNHAT IN NHANVIEN.MANV%TYPE
) IS
    V_COUNT NUMBER;
BEGIN
    -- 1. Kiểm tra tồn tại
    SELECT COUNT(*) INTO V_COUNT FROM NHACUNGCAP WHERE MANCC = P_MANCC AND THOIDIEMXOA IS NULL;
    IF V_COUNT = 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NCC_KHONG_TON_TAI_XOA, 'Nhà cung cấp không tồn tại hoặc đã bị ngừng giao dịch.');
    END IF;

    -- 2. Kiểm tra lịch sử giao dịch (Phiếu nhập kho)
    SELECT COUNT(*) INTO V_COUNT FROM PHIEUNHAPKHO WHERE MANCC = P_MANCC;
    IF V_COUNT > 0 THEN
        -- 3a. Đã có giao dịch -> Chuyển trạng thái ngừng giao dịch (Xóa mềm)
        UPDATE NHACUNGCAP
        SET THOIDIEMXOA = CURRENT_TIMESTAMP,
            MANX = P_MANV_CAPNHAT
        WHERE MANCC = P_MANCC;
    ELSE
        -- 3b. Chưa có giao dịch -> Xóa cứng vĩnh viễn
        DELETE FROM NHACUNGCAP WHERE MANCC = P_MANCC;
    END IF;
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = PKG_ERROR_CODES.ERR_NCC_KHONG_TON_TAI_XOA THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NCC_XOA_HE_THONG, 'Lỗi hệ thống khi xóa nhà cung cấp: ' || SQLERRM);
        END IF;
END;
/
