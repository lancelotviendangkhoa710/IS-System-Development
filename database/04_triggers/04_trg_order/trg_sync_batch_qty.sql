-- Trigger đồng bộ SOLUONGCONLAI trong MESANXUAT theo FEFO khi khách đặt hàng
-- Khi 1 đơn hàng thêm dòng chi tiết (CTDONHANG), trigger tự rút dần từng mẻ
-- theo thứ tự gần hết hạn nhất trước (FEFO) — Java không cần thay đổi gì

CREATE OR REPLACE TRIGGER TRG_DONGBO_SOLUONG_MESANXUAT
AFTER INSERT ON CTDONHANG
FOR EACH ROW
DECLARE
    -- Số lượng bánh còn cần rút từ các mẻ (giảm dần qua từng vòng lặp)
    V_SOLUONG_CANRUT   NUMBER(10,2) := 0;
    -- Số lượng thực tế rút được từ mẻ hiện tại trong vòng lặp
    V_SOLUONG_RUTDUOC NUMBER(10,2) := 0;
BEGIN
    V_SOLUONG_CANRUT := NVL(:NEW.SOLUONG, 0);
    IF V_SOLUONG_CANRUT <= 0 THEN RETURN; END IF;

    -- Duyệt các mẻ của đúng sản phẩm, ưu tiên lô gần hết hạn nhất (FEFO)
    -- Bỏ qua mẻ đã hết hạn (không nên bán bánh hết hạn)
    FOR ROW_ME IN (
        SELECT MAME, SOLUONGCONLAI
        FROM MESANXUAT
        WHERE MASP = :NEW.MASP
          AND NVL(SOLUONGCONLAI, 0) > 0
          AND (HANSUDUNG IS NULL OR TRUNC(HANSUDUNG) >= TRUNC(SYSDATE))
        ORDER BY HANSUDUNG ASC NULLS LAST, MAME ASC
        FOR UPDATE OF SOLUONGCONLAI
    ) LOOP
        EXIT WHEN V_SOLUONG_CANRUT <= 0;

        -- Rút tối đa có thể từ mẻ này, không bao giờ rút quá số còn lại của mẻ
        V_SOLUONG_RUTDUOC := LEAST(ROW_ME.SOLUONGCONLAI, V_SOLUONG_CANRUT);

        UPDATE MESANXUAT
        SET SOLUONGCONLAI = SOLUONGCONLAI - V_SOLUONG_RUTDUOC
        WHERE MAME = ROW_ME.MAME;

        -- Trừ phần đã rút ra khỏi lượng còn cần rút
        V_SOLUONG_CANRUT := V_SOLUONG_CANRUT - V_SOLUONG_RUTDUOC;
    END LOOP;

    -- Nếu V_SOLUONG_CANRU > 0 sau loop: tổng các mẻ không đủ bù số bán.
    -- TRG_TRUKHO_DONHANG (chạy song song) đã xử lý bắt lỗi ERR_SP_HET_HANG → không raise thêm.

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
            PKG_ERROR_CODES.ERR_BANH_HETHAN,
            N'Lỗi khi đồng bộ số lượng còn lại mẻ sản xuất: ' || SQLERRM
        );
END;
/
