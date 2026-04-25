-- =============================================================
-- Thêm cột TRANGTHAI vào bảng PHIEUTHUCHI
-- Chạy script này 1 lần trên DB để bật tính năng huỷ phiếu.
-- An toàn để chạy lại: bỏ qua nếu cột đã tồn tại (ORA-01430).
-- =============================================================
BEGIN
    EXECUTE IMMEDIATE 'ALTER TABLE PHIEUTHUCHI ADD TRANGTHAI NVARCHAR2(50) DEFAULT ''active'' NOT NULL';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE = -1430 THEN NULL; -- ORA-01430: column already exists, skip
    ELSE RAISE;
    END IF;
END;
/

-- Đảm bảo các bản ghi cũ có giá trị hợp lệ
UPDATE PHIEUTHUCHI SET TRANGTHAI = 'active' WHERE TRANGTHAI IS NULL;
COMMIT;
