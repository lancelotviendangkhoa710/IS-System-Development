-- ================================================================
-- DEMO 4.4: Deadlock (Tắc nghẽn)
-- ================================================================
-- Kịch bản: Hai nhân viên kho cập nhật MUCTONANTOAN của bột mì
-- (MANL=1001) và đường trắng (MANL=1002) đồng thời.
--
-- Cách tạo Deadlock:
--   Java Thread A gọi: PROC_CAPNHAT_MUCTON_DEMO(1001, 50, 1002, 40)
--   Java Thread B gọi: PROC_CAPNHAT_MUCTON_DEMO(1002, 55, 1001, 45)
--                                               ↑ thứ tự ngược lại
-- → A khóa 1001, chờ 1002; B khóa 1002, chờ 1001 → Deadlock
-- → Oracle phát hiện chu trình, ném ORA-00060 cho một phiên
--
-- [TRẠNG THÁI HIỆN TẠI] BUG — cập nhật theo thứ tự tham số truyền vào
--
-- [CÁCH FIX] Bỏ comment khối IF / SWAP bên dưới (7 dòng)
-- → Procedure tự sắp xếp MANL tăng dần trước khi UPDATE
-- → Cả hai thread đều lock 1001 trước → Thread B chờ Thread A
-- → Không tạo được chu trình → Không Deadlock
-- ================================================================

CREATE OR REPLACE PROCEDURE PROC_CAPNHAT_MUCTON_DEMO (
    P_MANL_1    IN NGUYENLIEU.MANL%TYPE,
    P_MUCTON_1  IN NGUYENLIEU.MUCTONANTOAN%TYPE,
    P_MANL_2    IN NGUYENLIEU.MANL%TYPE,
    P_MUCTON_2  IN NGUYENLIEU.MUCTONANTOAN%TYPE
)
IS
    V_MANL_A    NGUYENLIEU.MANL%TYPE         := P_MANL_1;
    V_MUCTON_A  NGUYENLIEU.MUCTONANTOAN%TYPE := P_MUCTON_1;
    V_MANL_B    NGUYENLIEU.MANL%TYPE         := P_MANL_2;
    V_MUCTON_B  NGUYENLIEU.MUCTONANTOAN%TYPE := P_MUCTON_2;
    V_SWAP_MANL   NGUYENLIEU.MANL%TYPE;
    V_SWAP_MUCTON NGUYENLIEU.MUCTONANTOAN%TYPE;
    V_X         NUMBER := 0;
BEGIN
    -- ============================================================
    -- [FIX] Bỏ comment 7 dòng dưới để kích hoạt Resource Ordering:
    -- IF V_MANL_A > V_MANL_B THEN
    --     V_SWAP_MANL   := V_MANL_A;   V_MANL_A   := V_MANL_B;   V_MANL_B   := V_SWAP_MANL;
    --     V_SWAP_MUCTON := V_MUCTON_A; V_MUCTON_A := V_MUCTON_B; V_MUCTON_B := V_SWAP_MUCTON;
    -- END IF;
    -- ↑ Khi bật: cả hai phiên đều lock MANL nhỏ hơn trước
    -- → Không tạo được chu trình trong Wait-for Graph → Không Deadlock
    -- ============================================================

    -- Cập nhật tài nguyên thứ nhất (theo thứ tự tham số — có thể gây deadlock)
    UPDATE NGUYENLIEU
    SET MUCTONANTOAN = V_MUCTON_A
    WHERE MANL = V_MANL_A;

    -- [DEMO DELAY] Giả lập thời gian xử lý để Thread kia kịp lock tài nguyên thứ 2
    FOR I IN 1..50000000 LOOP V_X := V_X + I; END LOOP;

    -- Cập nhật tài nguyên thứ hai (nếu Thread kia đang giữ lock → bị chặn → Deadlock)
    UPDATE NGUYENLIEU
    SET MUCTONANTOAN = V_MUCTON_B
    WHERE MANL = V_MANL_B;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        -- ORA-00060: Oracle phát hiện deadlock và ném lỗi ở đây
        -- Java nhận SQLCODE = -60, hiển thị thông báo lỗi cho người dùng
        RAISE;
END;
/
