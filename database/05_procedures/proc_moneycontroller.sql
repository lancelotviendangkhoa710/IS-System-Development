CREATE OR REPLACE PROCEDURE PROC_DongCaDoiSoat (
    p_MaCa            IN NUMBER,
    p_TienThucTeDem   IN NUMBER,
    p_LyDoChenhLech   IN VARCHAR2 DEFAULT NULL,
    P_test  in number

)
IS
    v_TongTienHeThong NUMBER;
    v_ChenhLech       NUMBER;
BEGIN
    -- 1. Lấy con số hệ thống mới nhất (Double-check)
    v_TongTienHeThong := FUNC_TinhTienMatLyTuong(p_MaCa);
    v_ChenhLech       := p_TienThucTeDem - v_TongTienHeThong;

    -- 2. Lưu kết quả đối soát
    INSERT INTO DOISOAT (MACA, TIEN_HETHONG, TIEN_THUCTE, CHENH_LECH, LYDO, NGAY_DOISOAT)
    VALUES (p_MaCa, v_TongTienHeThong, p_TienThucTeDem, v_ChenhLech, p_LyDoChenhLech, SYSDATE);

    -- 3. Cập nhật trạng thái ca
    UPDATE CALAMVIEC
    SET ThoiGianDongCa = SYSDATE,
        TrangThai = 'Đã đóng'
    WHERE MACA = p_MaCa AND TrangThai = 'Đang mở';

    -- 4. Nếu không tìm thấy ca để update, báo lỗi về Java
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