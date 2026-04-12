-- Procedure Tạo phiếu nhập kho
CREATE OR REPLACE PROCEDURE PROC_NHAPKHO(
    P_MANV IN NUMBER,
    P_MANCC IN NUMBER,
    P_JSON_DATALIST IN CLOB
)
IS
    V_MAPN NUMBER;
    V_CURRENT_MANL NUMBER;
BEGIN
    -- 1 Khởi tạo chứng từ gốc
    INSERT INTO PHIEUNHAPKHO (MANV, MANCC)
    VALUES (P_MANV, P_MANCC)
    RETURNING MAPN INTO V_MAPN;

    -- 2 Quét JSON và Đẩy chi tiết
    FOR ROW_DATA IN (
        SELECT J.MANL, J.TENNL, J.XUATXU, J.MADVT, J.SOLUONG, J.DONGIA, J.NGAYSANXUAT, J.HANSUDUNG
        FROM JSON_TABLE(P_JSON_DATALIST, '$[*]'
            COLUMNS (
                MANL NUMBER PATH '$.MaNL',
                TENNL NVARCHAR2(200) PATH '$.TenNL',
                XUATXU NVARCHAR2(100) PATH '$.XuatXu',
                MADVT NUMBER PATH '$.MaDVT',
                SOLUONG NUMBER PATH '$.SoLuong',
                DONGIA NUMBER PATH '$.DonGia',
                NGAYSANXUAT VARCHAR2(20) PATH '$.NgaySanXuat',
                HANSUDUNG VARCHAR2(20) PATH '$.HanSuDung'
            )
        ) J
    )
    LOOP
        -- Kiểm tra nếu MaNL bị rỗng
        IF ROW_DATA.MANL IS NULL THEN
            INSERT INTO NGUYENLIEU (TENNL, XUATXU, MADVT)
            VALUES (ROW_DATA.TENNL, ROW_DATA.XUATXU, ROW_DATA.MADVT)
            RETURNING MANL INTO V_CURRENT_MANL;
        ELSE
            -- Dùng nguyên liệu đã có sẵn
            V_CURRENT_MANL := ROW_DATA.MANL;
        END IF;

        -- Đưa hàng vào Lô mới
        INSERT INTO CTPHIEUNHAP (MAPN, MANL, SOLUONG, DONGIA, SOLUONGCONLAI, NGAYSANXUAT, HANSUDUNG)
        VALUES (V_MAPN, V_CURRENT_MANL, ROW_DATA.SOLUONG, ROW_DATA.DONGIA, ROW_DATA.SOLUONG,
                TO_DATE(ROW_DATA.NGAYSANXUAT, 'YYYY-MM-DD'), TO_DATE(ROW_DATA.HANSUDUNG, 'YYYY-MM-DD'));
    END LOOP;

    -- 3 Bàn giao cho Trigger & Chốt Giao Dịch
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NHAP_KHO, 'Lỗi hệ thống khi nhập kho vật tư (Transaction Rollbacked): ' || SQLERRM);
END;
/

-- Procedure Hủy nhập kho
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

-- Procedure Xuất Kho Sản Xuất
CREATE OR REPLACE PROCEDURE PROC_XUATKHOSANXUAT(
    P_MASP IN NUMBER,
    P_SOLUONGSANXUAT IN NUMBER,
    P_MANV IN NUMBER
)
    IS
    V_MAPX         NUMBER;
    V_TONGTON      NUMBER;
    V_LUONGCANDUNG NUMBER;

    -- Xếp hàng nguyên liệu cần theo Công thức nướng bánh
    CURSOR C_CONGTHUC IS
        SELECT C.MANL, N.TENNL, (C.SOLUONGTIEUHAO * P_SOLUONGSANXUAT) AS TONG_CAN_DUNG
        FROM CONGTHUC C
                 JOIN NGUYENLIEU N ON C.MANL = N.MANL
        WHERE C.MASP = P_MASP;

    -- Xếp hàng ưu tiên các lô hàng Còn Phiếu & Nhập Sớm Nhất (FIFO)
    CURSOR C_LOHANG(P_MANL_TARGET NUMBER) IS
        SELECT MALO, SOLUONGCONLAI
        FROM CTPHIEUNHAP
        WHERE MANL = P_MANL_TARGET
          AND SOLUONGCONLAI > 0
        ORDER BY HANSUDUNG ASC, MALO ASC
            FOR UPDATE OF SOLUONGCONLAI;
BEGIN
    -- 1. KIỂM TRA TOÀN DIỆN KHẢ NĂNG
    FOR REC IN C_CONGTHUC
        LOOP
            -- Pessimistic Lock trên Tổng Kho
            SELECT SOLUONGTONTONG
            INTO V_TONGTON
            FROM NGUYENLIEU
            WHERE MANL = REC.MANL
                FOR UPDATE;

            -- Nếu 1 nguyên liệu bất kì không đủ, lập tức đập vỡ Giao dịch và văng Exception cứu hệ thống
            IF V_TONGTON < REC.TONG_CAN_DUNG THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_KHONG_DU,
                                        'Kho không đủ định mức (NL: ' || REC.TENNL || ') để làm ' || P_SOLUONGSANXUAT ||
                                        ' cái bánh. Cần: ' || REC.TONG_CAN_DUNG || ' nhưng chỉ còn: ' || V_TONGTON);
            END IF;
        END LOOP;

    -- 2. TẠO CHỨNG TỪ XUẤT TỔNG
    INSERT INTO PHIEUXUATKHO (MANV, LYDOXUAT)
    VALUES (P_MANV, 'Xuat kho san xuat ' || P_SOLUONGSANXUAT || ' banh (Ma SP: ' || P_MASP || ')')
    RETURNING MAPX INTO V_MAPX;

    -- 3. XUẤT THEO RÚT GỌN LÔ (FIFO) VÀ GHI CHI TIẾT
    FOR REC IN C_CONGTHUC
        LOOP
            V_LUONGCANDUNG := REC.TONG_CAN_DUNG;

            FOR LO_REC IN C_LOHANG(REC.MANL)
                LOOP
                    -- Trồi ra khi luợng cần rút đã bằng 0
                    IF V_LUONGCANDUNG <= 0 THEN
                        EXIT;
                    END IF;

                    IF LO_REC.SOLUONGCONLAI >= V_LUONGCANDUNG THEN
                        -- Lô này cân đủ phần còn lại
                        INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
                        VALUES (V_MAPX, LO_REC.MALO, V_LUONGCANDUNG);

                        V_LUONGCANDUNG := 0;
                    ELSE
                        -- Lô này không đủ, vét sạch Lô chứa và dồn sang Lô khác
                        INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
                        VALUES (V_MAPX, LO_REC.MALO, LO_REC.SOLUONGCONLAI);

                        V_LUONGCANDUNG := V_LUONGCANDUNG - LO_REC.SOLUONGCONLAI;
                    END IF;
                END LOOP;

            -- Safe check báo rủi ro (Rất hiếm thi gặp trừ khi Tổng Tồn đồng bộ sai với Tồn Chi Tiết Lô)
            IF V_LUONGCANDUNG > 0 THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_KHONG_DU,
                                        'Đồng bộ dữ liệu tồn ảo ở mức lô hàng: ' || REC.TENNL);
            END IF;
        END LOOP;

    -- 4. BÀN GIAO CHO TRIGGER & CHỐT SỔ
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = PKG_ERROR_CODES.ERR_NL_KHONG_DU THEN
            RAISE;
        END IF;

        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_XUAT_KHO, 'Lỗi hệ thống khi xuất kho sản xuất (Transaction Rollbacked): ' || SQLERRM);
END;
/

-- Procedure Xuất hủy bánh
CREATE OR REPLACE PROCEDURE PROC_XUATHUYBANH(
    P_MASP IN NUMBER,
    P_SOLUONGHUY IN NUMBER,
    P_LYDOXUAT IN NVARCHAR2,
    P_MANV IN NUMBER
)
IS
    V_SOLUONGTON NUMBER;
    V_MAPX NUMBER;
BEGIN
    -- 1. Xác thực (Checking Stock)
    BEGIN
        SELECT SOLUONGTON INTO V_SOLUONGTON
        FROM SANPHAM
        WHERE MASP = P_MASP;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_SP_KHONG_TON_TAI, 'Mã sản phẩm không tồn tại trong kho!');
    END;

    -- Chống Nhân viên gõ nhầm số lượng lớn hơn số bánh đang có thật trên kệ
    IF V_SOLUONGTON < P_SOLUONGHUY THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_XUAT_HUY_BANH, 'Số lượng hủy vượt quá số lượng tồn kho hiện tại! (Tồn: ' || V_SOLUONGTON || ')');
    END IF;

    -- 2. Lập chứng từ (INSERT INTO PHIEUXUATKHO)
    INSERT INTO PHIEUXUATKHO (LYDOXUAT, MANV)
    VALUES (P_LYDOXUAT, P_MANV)
    RETURNING MAPX INTO V_MAPX;

    -- 3. Ghi chi tiết (INSERT INTO CTPHIEUXUAT_TP)
    INSERT INTO CTPHIEUXUAT_TP (MAPX, MASP, SOLUONG)
    VALUES (V_MAPX, P_MASP, P_SOLUONGHUY);

    -- 4. Bàn giao cho Trigger và Chốt sổ
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = PKG_ERROR_CODES.ERR_XUAT_HUY_BANH OR SQLCODE = PKG_ERROR_CODES.ERR_SP_KHONG_TON_TAI THEN
            RAISE;
        ELSE
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HE_THONG_KHOIPHUC, 'Lỗi hệ thống khi xuất hủy bánh (Transaction Rollbacked): ' || SQLERRM);
        END IF;
END;
/
