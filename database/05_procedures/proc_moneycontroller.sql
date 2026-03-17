CREATE OR REPLACE PROCEDURE PROC_DongCaDoiSoat (
    p_MaCa              IN NUMBER,
    p_TienKhaiBaoDauCa  IN NUMBER, -- Thêm tham số này để khớp với hàm FUNC
    p_TienThucTeDem     IN NUMBER,
    p_LyDoChenhLech     IN VARCHAR2 DEFAULT NULL
)
IS
    v_TongTienHeThong NUMBER;
    v_ChenhLech       NUMBER;
BEGIN
    -- 1. Lấy con số hệ thống từ hàm FUNC (Đã truyền đủ 2 đối số)
    v_TongTienHeThong := FUNC_TinhTienMatLyTuong(p_MaCa, p_TienKhaiBaoDauCa);

    -- 2. Tính toán chênh lệch
    v_ChenhLech := p_TienThucTeDem - v_TongTienHeThong;

    -- 3. Lưu kết quả đối soát

    INSERT INTO DOISOAT (MACA, TONGTIENHETHONG, TIENTHUCTEDEM, CHENHLECH, LYDOCHENHLECH)
    VALUES (p_MaCa, v_TongTienHeThong, p_TienThucTeDem, v_ChenhLech, p_LyDoChenhLech);

    -- 4. Cập nhật trạng thái ca và lưu số tiền thực tế vào bảng CALAMVIEC (nếu cần)
    UPDATE CALAMVIEC
    SET ThoiGianDongCa = SYSDATE,
        TrangThai = 'Đã đóng'
    WHERE MACA = p_MaCa
      AND TrangThai = 'Đang mở';

    --Kiểm tra nếu không có dòng nào được cập nhật (Ca không tồn tại hoặc đã đóng)
    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Lỗi: Ca làm việc không tồn tại hoặc đã được đóng trước đó.');
    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;

        RAISE;
END;
/