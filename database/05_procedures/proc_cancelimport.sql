-- Procedure bảo vệ tính toàn vẹn khi thủ kho bấm nút "Trả hàng/Hủy phiếu nhập"
CREATE OR REPLACE PROCEDURE PROC_HUYPHIEUNHAPKHO(
    P_MAPN IN NUMBER
)
IS
    V_DA_SUDUNG NUMBER := 0;
BEGIN
    -- 1. Kiểm tra rủi ro (Rất quan trọng): Lô hàng này đã bị lấy nguyên liệu đem chế biến chưa?
    SELECT COUNT(*) INTO V_DA_SUDUNG
    FROM CTPHIEUNHAP
    WHERE MAPN = P_MAPN
      AND SOLUONGCONLAI < SOLUONG;

    -- 2. Bắt lỗi và Chặn đứng giao dịch nếu phát hiện dấu vết xé niêm phong
    IF V_DA_SUDUNG > 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_KHONG_THE_HUY_PN, 'Không thể hủy phiếu nhập này vì nguyên liệu đã được mang đi làm bánh!');
    END IF;

    -- 3. Thực thi Hủy (Xóa Chi tiết trước (Con), Xóa Phiếu gốc sau (Cha))
    -- (Việc trừ ngược lại kho vật lý sẽ do Trigger AFTER DELETE của bảng CTPHIEUNHAP tự động lo liệu ngầm phía sau)
    DELETE FROM CTPHIEUNHAP WHERE MAPN = P_MAPN;
    DELETE FROM PHIEUNHAPKHO WHERE MAPN = P_MAPN;

    -- 4. Chốt sổ hoàn tất giao dịch khép kín
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
