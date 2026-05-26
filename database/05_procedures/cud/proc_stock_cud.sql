    -- Procedure Tạo phiếu nhập kho
    CREATE OR REPLACE PROCEDURE PROC_TAOPHIEUNHAPKHO (
        P_MANV          IN PHIEUNHAPKHO.MANV%TYPE,
        P_MANCC         IN PHIEUNHAPKHO.MANCC%TYPE,
        P_JSON_DATALIST IN CLOB,
        P_MACA          IN PHIEUTHUCHI.MACA%TYPE,
        P_MAPN_OUT      OUT PHIEUNHAPKHO.MAPN%TYPE
    )
        IS
        V_MAPN          PHIEUNHAPKHO.MAPN%TYPE;
        V_CURRENT_MANL  NGUYENLIEU.MANL%TYPE;
        V_MALOAITHUCHI  LOAITHUCHI.MALOAITHUCHI%TYPE;
        V_TONGTIENNHAP  PHIEUTHUCHI.SOTIEN%TYPE;
        V_HESOQUYDOI    NGUYENLIEU.HESOQUYDOI%TYPE;
        V_SOLUONG_COSO  NUMBER(10,2);
    BEGIN
        -- 1. Khởi tạo chứng từ gốc
        INSERT INTO PHIEUNHAPKHO (MANV, MANCC, NGAYNHAP)
        VALUES (P_MANV, P_MANCC, SYSDATE)
        RETURNING MAPN INTO V_MAPN;

        P_MAPN_OUT := V_MAPN;

        -- 2. Quét JSON và Đẩy chi tiết
        FOR ROW_DATA IN (
            SELECT J.MANL, J.TENNL, J.XUATXU, J.MADVT, J.SOLUONG, J.DONGIA, J.NGAYSANXUAT, J.HANSUDUNG
            FROM JSON_TABLE(P_JSON_DATALIST, '$[*]'
                            COLUMNS (
                                MANL NUMBER PATH '$.maNL',
                                TENNL NVARCHAR2(200) PATH '$.tenNL',
                                XUATXU NVARCHAR2(100) PATH '$.xuatXu',
                                MADVT NUMBER PATH '$.maDVT',
                                SOLUONG NUMBER PATH '$.soLuong',
                                DONGIA NUMBER PATH '$.donGia',
                                NGAYSANXUAT VARCHAR2(20) PATH '$.ngaySanXuat',
                                HANSUDUNG VARCHAR2(20) PATH '$.hanSuDung'
                                )
                 ) J
            )
            LOOP
                IF ROW_DATA.MANL IS NULL OR ROW_DATA.MANL = 0 THEN
                    BEGIN
                        SELECT MANL INTO V_CURRENT_MANL
                        FROM NGUYENLIEU
                        WHERE UPPER(TRIM(TENNL)) = UPPER(TRIM(ROW_DATA.TENNL))
                          AND THOIDIEMXOA IS NULL
                            FETCH FIRST 1 ROW ONLY;
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            INSERT INTO NGUYENLIEU (TENNL, XUATXU, MADVT)
                            VALUES (ROW_DATA.TENNL, ROW_DATA.XUATXU, ROW_DATA.MADVT)
                            RETURNING MANL INTO V_CURRENT_MANL;
                    END;
                ELSE
                    V_CURRENT_MANL := ROW_DATA.MANL;
                END IF;


                BEGIN
                    SELECT NVL(HESOQUYDOI, 1) INTO V_HESOQUYDOI
                    FROM NGUYENLIEU
                    WHERE MANL = V_CURRENT_MANL;
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN V_HESOQUYDOI := 1;
                END;

                -- Số lượng cơ bản = số lượng nhập × hệ số quy đổi
                V_SOLUONG_COSO := ROW_DATA.SOLUONG * NVL(V_HESOQUYDOI, 1);

                INSERT INTO CTPHIEUNHAP (MAPN, MANL, SOLUONG, DONGIA, SOLUONGCONLAI, NGAYSANXUAT, HANSUDUNG)
                VALUES (
                           V_MAPN,
                           V_CURRENT_MANL,
                           V_SOLUONG_COSO,      -- FIX: lưu đơn vị CƠ BẢN (sau quy đổi)
                           ROW_DATA.DONGIA,
                           V_SOLUONG_COSO,      -- FIX: SOLUONGCONLAI cũng theo đơn vị cơ bản
                           CASE WHEN ROW_DATA.NGAYSANXUAT IS NOT NULL THEN TO_DATE(ROW_DATA.NGAYSANXUAT, 'YYYY-MM-DD') ELSE NULL END,
                           CASE WHEN ROW_DATA.HANSUDUNG IS NOT NULL THEN TO_DATE(ROW_DATA.HANSUDUNG, 'YYYY-MM-DD') ELSE NULL END
                       );
            END LOOP;

        -- 4. Tính tổng tiền nhập (dựa trên soLuong gốc × donGia để phản ánh đúng hóa đơn
        SELECT NVL(SUM(J.SOLUONG * J.DONGIA), 0)
        INTO V_TONGTIENNHAP
        FROM JSON_TABLE(P_JSON_DATALIST, '$[*]'
                 COLUMNS (
                     SOLUONG NUMBER PATH '$.soLuong',
                     DONGIA  NUMBER PATH '$.donGia'
                 )) J;

        -- 5. Lấy mã loại thu chi 'Nhap hang' — guard riêng để NO_DATA_FOUND có thông báo rõ nghĩa
        BEGIN
            SELECT MALOAITHUCHI INTO V_MALOAITHUCHI
            FROM LOAITHUCHI
            WHERE UPPER(TRIM(TENLOAITHUCHI)) = UPPER(TRIM(N'Nhap hang'))
              AND PHANLOAI = 'Chi'
              AND THOIDIEMXOA IS NULL
            FETCH FIRST 1 ROW ONLY;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_PHIEU_NHAP_KHO,
                    N'Lỗi cấu hình: Không tìm thấy loại thu chi "Nhap hang" trong bảng LOAITHUCHI. Hãy chạy lại script_insert_data.sql.');
        END;

        -- 6. Tạo phiếu chi nhập hàng
        INSERT INTO PHIEUTHUCHI (MALOAITHUCHI, SOTIEN, MANV, MACA, MAPN, GHICHU)
        VALUES (V_MALOAITHUCHI, V_TONGTIENNHAP, P_MANV, NULLIF(P_MACA, 0), V_MAPN,
                N'Tự động — Nhập kho phiếu #' || V_MAPN);

        -- 7. Ghi log hoạt động nhân viên
        INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
        VALUES (P_MANV, 'KHO', 'Nhap kho phieu #' || V_MAPN, V_MAPN);

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_PHIEU_NHAP_KHO, N'Lỗi hệ thống khi nhập kho vật tư: ' || SQLERRM);
    END;
    /




    CREATE OR REPLACE PROCEDURE PROC_XUATKHOSANXUAT (
        P_MASP IN SANPHAM.MASP%type,
        P_SOLUONGSANXUAT IN CONGTHUC.SOLUONGTIEUHAO%type,
        P_MANV IN NHANVIEN.MANV%type
    )
        IS

        TYPE T_REC_CONGTHUC IS RECORD (
                                          MANL      CONGTHUC.MANL%TYPE,
                                          TENNL     NGUYENLIEU.TENNL%TYPE,
                                          TONG_CAN_DUNG NUMBER
                                      );
        TYPE T_TAB_CONGTHUC IS TABLE OF T_REC_CONGTHUC INDEX BY PLS_INTEGER;
        V_TAB      T_TAB_CONGTHUC;
        V_IDX      PLS_INTEGER := 0;

        V_MAPX         CTPHIEUXUAT_NL.MAPX%type;
        V_TONGTON      NGUYENLIEU.SOLUONGTONTONG%type;
        V_LUONGCANDUNG CONGTHUC.SOLUONGTIEUHAO%type;
        V_TEN_NL_DANG_LOCK NGUYENLIEU.TENNL%TYPE;


        CURSOR C_CONGTHUC IS
            SELECT C.MANL, N.TENNL, (C.SOLUONGTIEUHAO * P_SOLUONGSANXUAT) AS TONG_CAN_DUNG
            FROM CONGTHUC C
                     JOIN NGUYENLIEU N ON C.MANL = N.MANL
            WHERE C.MASP = P_MASP
            --- ORDER BY C.SOLUONGTIEUHAO DESC;
            ORDER BY C.MANL ASC;

        CURSOR C_LOHANG(P_MANL_TARGET NUMBER) IS
            SELECT MALO, SOLUONGCONLAI
            FROM CTPHIEUNHAP
            WHERE MANL = P_MANL_TARGET
              AND SOLUONGCONLAI > 0
            ORDER BY HANSUDUNG ASC, MALO ASC
                FOR UPDATE OF SOLUONGCONLAI;
    BEGIN
        FOR REC IN C_CONGTHUC
            LOOP

                V_TEN_NL_DANG_LOCK := REC.TENNL;
                SELECT SOLUONGTONTONG
                INTO V_TONGTON
                FROM NGUYENLIEU
                WHERE MANL = REC.MANL
                    --FOR UPDATE WAIT 5;
                      FOR UPDATE;

                IF V_TONGTON < REC.TONG_CAN_DUNG THEN
                    RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_KHONG_DU,
                                            'Kho khong du dinh muc (NL: ' || REC.TENNL || ') de lam ' || P_SOLUONGSANXUAT ||
                                            ' cai banh. Can: ' || REC.TONG_CAN_DUNG || ' nhung chi con: ' || V_TONGTON);
                END IF;


                V_IDX := V_IDX + 1;
                V_TAB(V_IDX).MANL := REC.MANL;
                V_TAB(V_IDX).TENNL := REC.TENNL;
                V_TAB(V_IDX).TONG_CAN_DUNG := REC.TONG_CAN_DUNG;
                DBMS_SESSION.SLEEP(5);
            END LOOP;

        -- 2. TẠO CHỨNG TỪ XUẤT TỔNG
        INSERT INTO PHIEUXUATKHO (MANV, LYDOXUAT)
        VALUES (P_MANV, 'Lam banh')
        RETURNING MAPX INTO V_MAPX;

        -- 2b. GHI MẺ SẢN XUẤT (bridge SANPHAM PHIEUXUATKHO — phục vụ truy xuất nguồn gốc)
        INSERT INTO MESANXUAT (MASP, SOLUONGSANXUAT, MANV, MAPX)
        VALUES (P_MASP, P_SOLUONGSANXUAT, P_MANV, V_MAPX);

        FOR I IN 1..V_TAB.COUNT
            LOOP
                V_LUONGCANDUNG := V_TAB(I).TONG_CAN_DUNG;

                FOR LO_REC IN C_LOHANG(V_TAB(I).MANL)
                    LOOP
                        IF V_LUONGCANDUNG <= 0 THEN EXIT; END IF;

                        IF LO_REC.SOLUONGCONLAI >= V_LUONGCANDUNG THEN
                            INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
                            VALUES (V_MAPX, LO_REC.MALO, V_LUONGCANDUNG);
                            V_LUONGCANDUNG := 0;
                        ELSE
                            INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
                            VALUES (V_MAPX, LO_REC.MALO, LO_REC.SOLUONGCONLAI);
                            V_LUONGCANDUNG := V_LUONGCANDUNG - LO_REC.SOLUONGCONLAI;
                        END IF;
                    END LOOP;

                IF V_LUONGCANDUNG > 0 THEN
                    RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_TON_AO,
                                            'Dong bo du lieu ton ao o muc lo hang: ' || V_TAB(I).TENNL);
                END IF;
            END LOOP;

        -- 4. CẬP NHẬT TỒN KHO THÀNH PHẨM (tồn cũ + số bánh vừa làm ra)
        UPDATE SANPHAM
        SET SOLUONGTON = NVL(SOLUONGTON, 0) + P_SOLUONGSANXUAT
        WHERE MASP = P_MASP;

        -- 5. GHI LOG HOẠT ĐỘNG NHÂN VIÊN (IMP-03: thêm audit trail cho nhất quán)
        INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
        VALUES (P_MANV, 'KHO', 'Xuat kho SX SP #' || P_MASP || ' SL:' || P_SOLUONGSANXUAT, V_MAPX);
        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;

            IF SQLCODE = -60 THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_XUAT_KHO,
                                        'DEADLOCK_DETECTED|' || NVL(V_TEN_NL_DANG_LOCK, 'khong xac dinh'));
            END IF;
            IF SQLCODE = -30006 THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_XUAT_KHO,
                                        'LOCK_TIMEOUT|' || V_TEN_NL_DANG_LOCK);
            END IF;
            IF SQLCODE = PKG_ERROR_CODES.ERR_NL_TON_AO
                OR SQLCODE = PKG_ERROR_CODES.ERR_NL_KHONG_DU THEN
                RAISE;
            END IF;
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_XUAT_KHO,
                                    'Loi he thong khi xuat kho san xuat: ' || SQLERRM);
    END;
    /

    -- Procedure Xuất kho sản xuất cho nhiều loại bánh cùng lúc (Xuất mẻ bánh)
    CREATE OR REPLACE PROCEDURE PROC_XUATKHOMULTISANXUAT (
        P_JSON_DATALIST IN CLOB,
        P_MANV          IN NHANVIEN.MANV%TYPE
    )
    IS
        TYPE T_REC_CONGTHUC IS RECORD (
            MANL          CONGTHUC.MANL%TYPE,
            TENNL         NGUYENLIEU.TENNL%TYPE,
            TONG_CAN_DUNG NUMBER
        );
        TYPE T_TAB_CONGTHUC IS TABLE OF T_REC_CONGTHUC INDEX BY PLS_INTEGER;
        V_TAB          T_TAB_CONGTHUC;
        V_IDX          PLS_INTEGER := 0;

        V_MAPX         PHIEUXUATKHO.MAPX%TYPE;
        V_TONGTON      NGUYENLIEU.SOLUONGTONTONG%TYPE;
        V_LUONGCANDUNG NGUYENLIEU.SOLUONGTONTONG%TYPE;
        V_TEN_NL_DANG_LOCK NGUYENLIEU.TENNL%TYPE;

        -- Cursor lấy danh sách nguyên liệu cần dùng (đã gộp và sắp xếp MANL tăng dần để tránh deadlock)
        CURSOR C_AGGREGATED_CONGTHUC IS
            SELECT C.MANL, N.TENNL, SUM(C.SOLUONGTIEUHAO * J.SOLUONG) AS TONG_CAN_DUNG
            FROM JSON_TABLE(P_JSON_DATALIST, '$[*]'
                            COLUMNS (
                                MASP NUMBER PATH '$.maSP',
                                SOLUONG NUMBER PATH '$.soLuong'
                            )
                 ) J
            JOIN CONGTHUC C ON C.MASP = J.MASP
            JOIN NGUYENLIEU N ON C.MANL = N.MANL
            GROUP BY C.MANL, N.TENNL
            ORDER BY C.MANL ASC;

        -- Cursor duyệt các lô nguyên liệu theo FEFO (Hạn sử dụng gần nhất trước)
        CURSOR C_LOHANG(P_MANL_TARGET NUMBER) IS
            SELECT MALO, SOLUONGCONLAI
            FROM CTPHIEUNHAP
            WHERE MANL = P_MANL_TARGET
              AND SOLUONGCONLAI > 0
            ORDER BY HANSUDUNG ASC, MALO ASC
            FOR UPDATE OF SOLUONGCONLAI;
    BEGIN
        -- 1. DUYỆT CÔNG THỨC GỘP, KHÓA NGUYÊN LIỆU (PESSIMISTIC LOCK) VÀ KIỂM TRA TỒN KHO
        FOR REC IN C_AGGREGATED_CONGTHUC LOOP
            V_TEN_NL_DANG_LOCK := REC.TENNL;
            SELECT SOLUONGTONTONG
            INTO V_TONGTON
            FROM NGUYENLIEU
            WHERE MANL = REC.MANL
            FOR UPDATE;

            IF V_TONGTON < REC.TONG_CAN_DUNG THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_KHONG_DU,
                                        'Kho khong du dinh muc (NL: ' || REC.TENNL || ') de lam banh. Can: ' || REC.TONG_CAN_DUNG || ' nhung chi con: ' || V_TONGTON);
            END IF;

            V_IDX := V_IDX + 1;
            V_TAB(V_IDX).MANL := REC.MANL;
            V_TAB(V_IDX).TENNL := REC.TENNL;
            V_TAB(V_IDX).TONG_CAN_DUNG := REC.TONG_CAN_DUNG;
        END LOOP;

        -- 2. TẠO CHỨNG TỪ XUẤT KHO DUY NHẤT
        INSERT INTO PHIEUXUATKHO (MANV, LYDOXUAT)
        VALUES (P_MANV, 'Lam banh')
        RETURNING MAPX INTO V_MAPX;

        -- 3. GHI MẺ SẢN XUẤT VÀ CẬP NHẬT TỒN KHO THÀNH PHẨM CHO TỪNG LOẠI BÁNH
        FOR ROW_SP IN (
            SELECT J.MASP, J.SOLUONG
            FROM JSON_TABLE(P_JSON_DATALIST, '$[*]'
                            COLUMNS (
                                MASP NUMBER PATH '$.maSP',
                                SOLUONG NUMBER PATH '$.soLuong'
                            )
                 ) J
        ) LOOP
            -- Ghi mẻ sản xuất
            INSERT INTO MESANXUAT (MASP, SOLUONGSANXUAT, MANV, MAPX)
            VALUES (ROW_SP.MASP, ROW_SP.SOLUONG, P_MANV, V_MAPX);

            -- Cập nhật tồn kho thành phẩm
            UPDATE SANPHAM
            SET SOLUONGTON = NVL(SOLUONGTON, 0) + ROW_SP.SOLUONG
            WHERE MASP = ROW_SP.MASP;

            -- Ghi log hoạt động nhân viên
            INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
            VALUES (P_MANV, 'KHO', 'Xuat kho SX SP #' || ROW_SP.MASP || ' SL:' || ROW_SP.SOLUONG, V_MAPX);
        END LOOP;

        -- 4. TRỪ TỒN KHO NGUYÊN LIỆU THEO FIFO/FEFO
        FOR I IN 1..V_TAB.COUNT LOOP
            V_LUONGCANDUNG := V_TAB(I).TONG_CAN_DUNG;

            FOR LO_REC IN C_LOHANG(V_TAB(I).MANL) LOOP
                EXIT WHEN V_LUONGCANDUNG <= 0;

                IF LO_REC.SOLUONGCONLAI >= V_LUONGCANDUNG THEN
                    INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
                    VALUES (V_MAPX, LO_REC.MALO, V_LUONGCANDUNG);
                    V_LUONGCANDUNG := 0;
                ELSE
                    INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
                    VALUES (V_MAPX, LO_REC.MALO, LO_REC.SOLUONGCONLAI);
                    V_LUONGCANDUNG := V_LUONGCANDUNG - LO_REC.SOLUONGCONLAI;
                END IF;
            END LOOP;

            IF V_LUONGCANDUNG > 0 THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_TON_AO,
                                        'Dong bo du lieu ton ao o muc lo hang: ' || V_TAB(I).TENNL);
            END IF;
        END LOOP;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            IF SQLCODE = -60 THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_XUAT_KHO,
                                        'DEADLOCK_DETECTED|' || NVL(V_TEN_NL_DANG_LOCK, 'khong xac dinh'));
            END IF;
            IF SQLCODE = -30006 THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_XUAT_KHO,
                                        'LOCK_TIMEOUT|' || V_TEN_NL_DANG_LOCK);
            END IF;
            IF SQLCODE = PKG_ERROR_CODES.ERR_NL_TON_AO
                OR SQLCODE = PKG_ERROR_CODES.ERR_NL_KHONG_DU THEN
                RAISE;
            END IF;
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_XUAT_KHO,
                                    'Loi he thong khi xuat kho san xuat: ' || SQLERRM);
    END;
    /
    -- Procedure Xuất hủy bánh bảo quản hỏng
    CREATE OR REPLACE PROCEDURE PROC_XUATHUYBANH (
        P_MASP       IN SANPHAM.MASP%TYPE,
        P_SOLUONGHUY IN SANPHAM.SOLUONGTON%TYPE,
        P_MANV       IN NHANVIEN.MANV%TYPE
    )
    IS
        V_SOLUONGTON SANPHAM.SOLUONGTON%TYPE;
        V_MAPX       CTPHIEUXUAT_TP.MAPX%TYPE;
    BEGIN
        -- 1. Xác thực tồn kho (Checking Stock)
        BEGIN
            SELECT SOLUONGTON INTO V_SOLUONGTON
            FROM SANPHAM
            WHERE MASP = P_MASP;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_SP_KHONG_TON_TAI, 'Ma san pham khong ton tai trong kho!');
        END;

        -- Chống nhập số lượng lớn hơn tồn thực tế
        IF V_SOLUONGTON < P_SOLUONGHUY THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_XUAT_HUY_BANH,
                'So luong huy vuot qua so luong ton kho hien tai! (Ton: ' || V_SOLUONGTON || ')');
        END IF;

        -- 2. Lập chứng từ với lý do cố định đúng constraint
        INSERT INTO PHIEUXUATKHO (MANV, LYDOXUAT)
        VALUES (P_MANV, 'San pham hong')
        RETURNING MAPX INTO V_MAPX;

        -- 3. Ghi chi tiết → TRG_TRUKHO_PHIEUXUATTP tự trừ SANPHAM.SOLUONGTON
        INSERT INTO CTPHIEUXUAT_TP (MAPX, MASP, SOLUONG)
        VALUES (V_MAPX, P_MASP, P_SOLUONGHUY);

        -- 4. Bàn giao cho Trigger và Chốt sổ
        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            IF SQLCODE = PKG_ERROR_CODES.ERR_XUAT_HUY_BANH
            OR SQLCODE = PKG_ERROR_CODES.ERR_SP_KHONG_TON_TAI THEN
                RAISE;
            ELSE
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HOAN_XUAT_BANH, 'Loi he thong khi xuat huy banh: ' || SQLERRM);
            END IF;
    END;
    /

    -- Procedure Xuat huy nguyen lieu hong (FIFO theo lo, khong can cong thuc)
    CREATE OR REPLACE PROCEDURE PROC_XUATNGUYENLIEUHONG (
        P_MANL       IN NGUYENLIEU.MANL%TYPE,
        P_SOLUONGHUY IN NGUYENLIEU.SOLUONGTONTONG%TYPE,
        P_MANV       IN NHANVIEN.MANV%TYPE
    )
    IS
        V_MAPX          CTPHIEUXUAT_NL.MAPX%TYPE;
        V_TENNL         NGUYENLIEU.TENNL%TYPE;
        V_TONGTON       NGUYENLIEU.SOLUONGTONTONG%TYPE;
        V_LUONGCANDUNG  NGUYENLIEU.SOLUONGTONTONG%TYPE;

        CURSOR C_LOHANG IS
            SELECT MALO, SOLUONGCONLAI
            FROM CTPHIEUNHAP
            WHERE MANL = P_MANL
              AND SOLUONGCONLAI > 0
            ORDER BY HANSUDUNG ASC, MALO ASC
            FOR UPDATE OF SOLUONGCONLAI;
    BEGIN
        -- 1. Lay ten va kiem tra ton kho (Pessimistic Lock)
        BEGIN
            SELECT TENNL, SOLUONGTONTONG
            INTO V_TENNL, V_TONGTON
            FROM NGUYENLIEU
            WHERE MANL = P_MANL
            FOR UPDATE;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_KHONG_CO_NGUYEN_LIEU,
                    'Nguyen lieu khong ton tai trong kho!');
        END;

        IF V_TONGTON < P_SOLUONGHUY THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_XUAT_HUY_NL,
                'So luong huy (' || P_SOLUONGHUY || ') vuot qua ton kho hien tai (' || V_TONGTON || ')!');
        END IF;

        -- 2. Tao phieu xuat voi ly do co dinh dung constraint
        INSERT INTO PHIEUXUATKHO (MANV, LYDOXUAT)
        VALUES (P_MANV, 'Nguyen lieu hong')
        RETURNING MAPX INTO V_MAPX;

        -- 3. Rut lo theo FIFO
        V_LUONGCANDUNG := P_SOLUONGHUY;
        FOR LO_REC IN C_LOHANG LOOP
            EXIT WHEN V_LUONGCANDUNG <= 0;

            IF LO_REC.SOLUONGCONLAI >= V_LUONGCANDUNG THEN
                INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
                VALUES (V_MAPX, LO_REC.MALO, V_LUONGCANDUNG);
                V_LUONGCANDUNG := 0;
            ELSE
                INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
                VALUES (V_MAPX, LO_REC.MALO, LO_REC.SOLUONGCONLAI);
                V_LUONGCANDUNG := V_LUONGCANDUNG - LO_REC.SOLUONGCONLAI;
            END IF;
        END LOOP;

        -- Safe check: dam bao da rut du (tranh ton ao)
        IF V_LUONGCANDUNG > 0 THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_TON_AO,
                'Dong bo du lieu ton ao: ton chi tiet lo khong khop voi tong ton (' || V_TENNL || ').');
        END IF;

        -- 4. Ghi log hoat dong va chot so
        INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
        VALUES (P_MANV, 'KHO', 'Xuat huy NL hong #' || P_MANL || ' SL:' || P_SOLUONGHUY, V_MAPX);

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            IF SQLCODE = PKG_ERROR_CODES.ERR_XUAT_HUY_NL
            OR SQLCODE = PKG_ERROR_CODES.ERR_KHONG_CO_NGUYEN_LIEU
            OR SQLCODE = PKG_ERROR_CODES.ERR_NL_TON_AO THEN
                RAISE;
            END IF;
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_XUAT_HUY_NL,
                'Loi he thong khi xuat huy nguyen lieu hong: ' || SQLERRM);
    END;
    /

    -- Procedure Xuat huy banh bi sai sot trong qua trinh lam banh
    CREATE OR REPLACE PROCEDURE PROC_XUATSAISOTBANH (
        P_MASP       IN SANPHAM.MASP%TYPE,
        P_SOLUONGHUY IN SANPHAM.SOLUONGTON%TYPE,
        P_MANV       IN NHANVIEN.MANV%TYPE
    )
    IS
        V_SOLUONGTON SANPHAM.SOLUONGTON%TYPE;
        V_MAPX       CTPHIEUXUAT_TP.MAPX%TYPE;
    BEGIN
        -- 1. Xac thuc ton kho thanh pham
        BEGIN
            SELECT SOLUONGTON INTO V_SOLUONGTON
            FROM SANPHAM WHERE MASP = P_MASP;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_SP_KHONG_TON_TAI,
                    'Ma san pham khong ton tai trong kho!');
        END;

        IF V_SOLUONGTON < P_SOLUONGHUY THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_XUAT_HUY_BANH,
                'So luong huy vuot qua ton kho hien tai! (Ton: ' || V_SOLUONGTON || ')');
        END IF;

        -- 2. Lap chung tu voi ly do co dinh dung constraint
        INSERT INTO PHIEUXUATKHO (MANV, LYDOXUAT)
        VALUES (P_MANV, 'Sai sot trong qua trinh lam banh')
        RETURNING MAPX INTO V_MAPX;

        -- 3. Ghi chi tiet -> TRG_TRUKHO_PHIEUXUATTP tu tru SANPHAM.SOLUONGTON
        INSERT INTO CTPHIEUXUAT_TP (MAPX, MASP, SOLUONG)
        VALUES (V_MAPX, P_MASP, P_SOLUONGHUY);

        -- 4. Chot so
        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            IF SQLCODE = PKG_ERROR_CODES.ERR_XUAT_HUY_BANH
            OR SQLCODE = PKG_ERROR_CODES.ERR_SP_KHONG_TON_TAI THEN
                RAISE;
            ELSE
                RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HOAN_XUAT_BANH,
                    'Loi he thong khi xuat huy banh sai sot: ' || SQLERRM);
            END IF;
    END;
    /





