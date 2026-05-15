-- Scheduled Job: Quét và hủy bánh thành phẩm hết hạn sử dụng mỗi ngày lúc 06:00
-- Yêu cầu: Tài khoản Oracle phải có quyền CREATE JOB
BEGIN
    -- Xóa job cũ nếu tồn tại (để script idempotent — chạy lại an toàn)
    BEGIN
        DBMS_SCHEDULER.DROP_JOB(
            job_name  => 'JOB_QUETBANH_HETHAN',
            force     => TRUE
        );
    EXCEPTION
        WHEN OTHERS THEN NULL;
    END;

    -- Tạo mới Job định kỳ
    DBMS_SCHEDULER.CREATE_JOB(
        job_name        => 'JOB_QUETBANH_HETHAN',
        job_type        => 'PLSQL_BLOCK',
        job_action      => 'DECLARE
                                V_SO_ME   NUMBER;
                                V_SO_BANH NUMBER;
                            BEGIN
                                PROC_XUATTHANHPHAM_HETHAN(1, V_SO_ME, V_SO_BANH);
                            END;',
        start_date      => SYSTIMESTAMP,
        repeat_interval => 'FREQ=DAILY;BYHOUR=6;BYMINUTE=0;BYSECOND=0',
        enabled         => TRUE,
        auto_drop       => FALSE,
        comments        => 'Quet va huy banh thanh pham het han moi ngay luc 06:00'
    );
END;
/
