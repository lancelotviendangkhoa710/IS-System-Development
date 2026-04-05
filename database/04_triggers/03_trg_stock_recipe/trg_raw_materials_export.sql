CREATE OR REPLACE TRIGGER TRG_XUATSLNGUYENLIEU
AFTER INSERT OR UPDATE OR DELETE ON CTPHIEUXUAT_NL
FOR EACH ROW
DECLARE
    V_CHENHLECH NUMBER := 0;
    V_ROWS_UPDATED NUMBER;  -- Đếm số dòng bị ảnh hưởng
BEGIN
    IF DELETING THEN
        RAISE_APPLICATION_ERROR(-20002,
            'Lỗi: Không được phép xóa chứng từ xuất kho.');
    END IF;

    IF INSERTING THEN
        V_CHENHLECH := :NEW.SOLUONG;
    ELSIF UPDATING THEN
        V_CHENHLECH := :NEW.SOLUONG - :OLD.SOLUONG;
    END IF;

    UPDATE NGUYENLIEU
    SET SOLUONGTONTONG = SOLUONGTONTONG - V_CHENHLECH
    WHERE MANL = (
        SELECT MANL FROM CTPHIEUNHAP WHERE MALO = :NEW.MALO
    );

    -- Kiểm tra UPDATE có thực sự tìm được nguyên liệu không
    -- SQL%ROWCOUNT = 0 nghĩa là subquery không tìm thấy MALO hợp lệ
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20003,
            'Lỗi: Không tìm thấy lô hàng MALO=' || :NEW.MALO ||
            ' trong kho. Tồn kho không được cập nhật.');
    END IF;

END TRG_XUATSLNGUYENLIEU;
/

/*
NHỮNG THAY ĐỔI SO VỚI TRIGGER BAN ĐẦU
1/ Xóa UPDATE CTPHIEUNHAP ra khỏi Trigger
    - Bản cũ: Trigger tự UPDATE SOLUONGCONLAI trong CTPHIEUNHAP
    - Bản mới: Bỏ hoàn toàn, vì PROC_XuatKhoSanXuat đã tự UPDATE
      CTPHIEUNHAP bằng lệnh "WHERE CURRENT OF c_LoHang" rồi.
    - Lý do: Nếu giữ lại sẽ bị trừ 2 lần → tồn kho âm sai.
    - Phân công rõ ràng sau khi sửa:
        + Procedure chịu trách nhiệm trừ CTPHIEUNHAP.SOLUONGCONLAI
        + Trigger chỉ chịu trách nhiệm trừ NGUYENLIEU.SOLUONGTONTONG

2/ Thay nhánh DELETING (hoàn kho) → CHẶN xóa chứng từ
    - Bản cũ: Khi xóa dòng trong CTPHIEUXUAT_NL, Trigger tự động
      cộng ngược lại tồn kho (hoàn kho).
    - Bản mới: Chặn xóa hoàn toàn bằng RAISE_APPLICATION_ERROR.
    - Lý do: Chứng từ xuất kho là bằng chứng lịch sử kế toán.
      Một khi đã COMMIT, nguyên liệu thực tế đã rời khỏi kho.
      Nếu cho xóa → DB cộng ngược tồn kho nhưng ngoài thực tế
      nguyên liệu đã không còn → số liệu lệch thực tế.
      Trường hợp xuất nhầm phải xử lý bằng phiếu nhập bù,
      không được xóa chứng từ cũ. (Cùng triết lý với Trigger
      chống xóa HOADON của nhóm)

3/ Thay cách xử lý Exception
    - Bản cũ: Dùng SELECT MANL INTO V_MANL (câu riêng) → nếu
      không tìm thấy thì bắt NO_DATA_FOUND.
    - Bản mới: Gộp SELECT vào thẳng subquery trong câu UPDATE,
      sau đó kiểm tra SQL%ROWCOUNT = 0 để phát hiện trường hợp
      MALO không hợp lệ.
    - Lý do: Khi dùng subquery trong UPDATE, Oracle không ném
      NO_DATA_FOUND mà chỉ âm thầm không cập nhật dòng nào
      (silent failure) → SQL%ROWCOUNT là cách duy nhất phát hiện
      và cho phép thông báo lỗi rõ ràng hơn cho người dùng.
*/