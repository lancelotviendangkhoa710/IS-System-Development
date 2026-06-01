    -- Procedure tạo đơn hàng (Two-Phase SERIALIZABLE)
    -- Phase 1: Snapshot giá + tồn kho → cache → delay → COMMIT
    -- Phase 2: Ghi đơn + tự trừ kho bằng snapshot (Lost Update demo)

    CREATE OR REPLACE PROCEDURE PROC_TAODONHANG(
        P_NGAYGIONHANBANH IN DONDATHANG.NGAYGIONHANBANH%TYPE,
        P_MAKH          IN DONDATHANG.MAKH%TYPE DEFAULT NULL,
        P_MANV_LAP      IN DONDATHANG.MANV_LAP%TYPE,
        P_MATRANGTHAI   IN DONDATHANG.MATRANGTHAI%TYPE,
        P_TIENDACOC     IN DONDATHANG.TIENDACOC%TYPE DEFAULT 0,
        P_HINHTHUCNHAN  IN DONDATHANG.HINHTHUCNHAN%TYPE DEFAULT 1,
        P_DIACHIGIAO    IN DONDATHANG.DIACHIGIAO%TYPE DEFAULT NULL,
        P_JSONCHITIET   IN CLOB,
        P_MADON_OUT     OUT DONDATHANG.MADON%TYPE
    )
        IS
        TYPE T_REC_CHITIET IS RECORD (
                                         MASP        NUMBER,
                                         SOLUONG     NUMBER,
                                         DONGIA      NUMBER,
                                         IS_CUSTOM   VARCHAR2(10),
                                         GHICHU      NVARCHAR2(200),
                                         PHUKIEN     NVARCHAR2(500),
                                         MAKC        NUMBER,
                                         MACOT       NUMBER,
                                         MANHAN      NUMBER,
                                         MATRANGTRI  NUMBER
                                     );
        TYPE T_TAB_CHITIET IS TABLE OF T_REC_CHITIET INDEX BY PLS_INTEGER;
        V_TAB       T_TAB_CHITIET;
        V_IDX       PLS_INTEGER := 0;


        TYPE T_TAB_TONKHO IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
        V_TONKHO_CACHE  T_TAB_TONKHO;

        V_TONGTIEN    NUMBER := 0;
        V_TONKHO      NUMBER := 0;
        V_TENSP       NVARCHAR2(200);
    BEGIN


       EXECUTE IMMEDIATE 'ALTER SESSION SET ISOLATION_LEVEL = SERIALIZABLE';
      --EXECUTE IMMEDIATE 'ALTER SESSION SET ISOLATION_LEVEL = READ COMMITTED';

        -- 0. Validate JSON input
        IF P_JSONCHITIET IS NULL OR DBMS_LOB.GETLENGTH(P_JSONCHITIET) = 0 THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON,
                                    'Du lieu chi tiet don hang rong. Vui long them it nhat 1 san pham vao don.');
        END IF;

        -- 1. Parse JSON vào collection
        FOR J IN (
            SELECT J.MASP, J.SOLUONG, J.DONGIA, J.IS_CUSTOM,
                   J.GHICHU, J.PHUKIEN, J.MAKC, J.MACOT, J.MANHAN, J.MATRANGTRI
            FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
                            COLUMNS (
                                MASP    NUMBER PATH '$.maSP',
                                SOLUONG NUMBER PATH '$.soLuong',
                                DONGIA  NUMBER PATH '$.donGia',
                                IS_CUSTOM VARCHAR2(10) PATH '$.isCustom',
                                GHICHU  NVARCHAR2(200) PATH '$.ghiChu',
                                PHUKIEN NVARCHAR2(500) PATH '$.phuKien',
                                MAKC    NUMBER PATH '$.maKC',
                                MACOT   NUMBER PATH '$.maCot',
                                MANHAN  NUMBER PATH '$.maNhan',
                                MATRANGTRI NUMBER PATH '$.maTrangTri'
                                )
                 ) J
            ) LOOP
                V_IDX := V_IDX + 1;
                V_TAB(V_IDX).MASP := J.MASP;
                V_TAB(V_IDX).SOLUONG := J.SOLUONG;
                V_TAB(V_IDX).DONGIA := J.DONGIA;
                V_TAB(V_IDX).IS_CUSTOM := J.IS_CUSTOM;
                V_TAB(V_IDX).GHICHU := J.GHICHU;
                V_TAB(V_IDX).PHUKIEN := J.PHUKIEN;
                V_TAB(V_IDX).MAKC := J.MAKC;
                V_TAB(V_IDX).MACOT := J.MACOT;
                V_TAB(V_IDX).MANHAN := J.MANHAN;
                V_TAB(V_IDX).MATRANGTRI := J.MATRANGTRI;
                V_TONGTIEN := V_TONGTIEN + NVL(J.SOLUONG * J.DONGIA, 0);
            END LOOP;

        IF V_TAB.COUNT = 0 THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON,
                                    'Du lieu JSON khong chua chi tiet san pham hop le.');
        END IF;

        -- ============================================================
        -- PHASE 1: Cả 2 thu ngân đều đọc cùng giá trị SOLUONGTON ở đây
        -- ============================================================


        FOR I IN 1..V_TAB.COUNT LOOP
                IF LOWER(NVL(V_TAB(I).IS_CUSTOM, 'false')) = 'false' THEN
                    SELECT SOLUONGTON, TENSP, GIABAN
                    INTO V_TONKHO, V_TENSP, V_TAB(I).DONGIA
                    FROM SANPHAM
                    WHERE MASP = V_TAB(I).MASP;
                    V_TONKHO_CACHE(I) := V_TONKHO;

                    IF V_TONKHO < V_TAB(I).SOLUONG THEN
                        IF V_TONKHO = 0 THEN
                            RAISE_APPLICATION_ERROR(
                                    PKG_ERROR_CODES.ERR_SP_HET_HANG,
                                    'San pham "' || V_TENSP || '" da het hang. Co nguoi vua mua truoc ban, vui long chon san pham khac.'
                            );
                        ELSE
                            RAISE_APPLICATION_ERROR(
                                    PKG_ERROR_CODES.ERR_SP_HET_HANG,
                                    'San pham "' || V_TENSP || '" chi con ' || V_TONKHO || ' cai, khong du ' || V_TAB(I).SOLUONG || ' cai yeu cau.'
                            );
                        END IF;
                    END IF;
                END IF;
            END LOOP;

        -- [DELAY] Tạo thời gian để:
        -- • NRR demo: Quản lý kịp UPDATE giá + COMMIT ở session khác
        -- • Lost Update demo: Thu ngân 2 kịp bắt đầu procedure
        DBMS_SESSION.SLEEP(3);

        -- [DEMO Non-repeatable Read] Đọc lại GIABAN sau delay → GHI ĐÈ V_TAB(I).DONGIA
        -- • READ COMMITTED: đọc giá MỚI → INSERT giá mới → BUG NRR hiện trong đơn
        -- • SERIALIZABLE: đọc giá CŨ (snapshot) → INSERT giá cũ
        FOR I IN 1..V_TAB.COUNT LOOP
            IF LOWER(NVL(V_TAB(I).IS_CUSTOM, 'false')) = 'false' THEN
                SELECT GIABAN INTO V_TAB(I).DONGIA
                FROM SANPHAM
                WHERE MASP = V_TAB(I).MASP;
            END IF;
        END LOOP;
            COMMIT;



        -- 3. Insert Đơn Hàng
        INSERT INTO DONDATHANG (NGAYGIONHANBANH, MAKH, MANV_LAP, MATRANGTHAI, TONGTIENHDBAN, TIENDACOC, HINHTHUCNHAN, DIACHIGIAO)
        VALUES (P_NGAYGIONHANBANH, P_MAKH, P_MANV_LAP, P_MATRANGTHAI, 0, 0, P_HINHTHUCNHAN, P_DIACHIGIAO)
        RETURNING MADON INTO P_MADON_OUT;

        -- 4. Insert chi tiết 

        PKG_ERROR_CODES.G_SKIP_STOCK_TRIGGER := TRUE;
        FOR I IN 1..V_TAB.COUNT LOOP
            IF LOWER(NVL(V_TAB(I).IS_CUSTOM, 'false')) = 'false' THEN
                INSERT INTO CTDONHANG (MADON, MASP, SOLUONG, DONGIA)
                VALUES (P_MADON_OUT, V_TAB(I).MASP, V_TAB(I).SOLUONG, V_TAB(I).DONGIA);
            ELSIF LOWER(V_TAB(I).IS_CUSTOM) = 'true' THEN
                -- DONGIAVON được TRG_CTDONTUYCHINH_GIAVON tự động gán, không cần truyền
                INSERT INTO CTDONTUYCHINH (
                    MADON, MASP, SOLUONG, DONGIA,
                    LOICHUCTRENBANH, GHICHUTHOBANH,
                    MAKC, MACOT, MANHAN, MATRANGTRI,
                    THOIGIANCHUANBI
                ) VALUES (
                    P_MADON_OUT, V_TAB(I).MASP, V_TAB(I).SOLUONG, V_TAB(I).DONGIA,
                    V_TAB(I).GHICHU, V_TAB(I).PHUKIEN,
                    NULLIF(V_TAB(I).MAKC, 0), NULLIF(V_TAB(I).MACOT, 0),
                    NULLIF(V_TAB(I).MANHAN, 0), NULLIF(V_TAB(I).MATRANGTRI, 0),
                    1 
                );
            END IF;
        END LOOP;
        PKG_ERROR_CODES.G_SKIP_STOCK_TRIGGER := FALSE;
        DBMS_SESSION.SLEEP(2);

        FOR I IN 1..V_TAB.COUNT LOOP
            IF LOWER(NVL(V_TAB(I).IS_CUSTOM, 'false')) = 'false' THEN
                UPDATE SANPHAM
                SET SOLUONGTON = V_TONKHO_CACHE(I) - V_TAB(I).SOLUONG
                WHERE MASP = V_TAB(I).MASP;
            END IF;
        END LOOP;

        -- 4.5. Gán tiền cọc
        UPDATE DONDATHANG
        SET TIENDACOC = NVL(P_TIENDACOC, 0)
        WHERE MADON = P_MADON_OUT;

        -- 5. Lịch sử & log
        INSERT INTO LICHSUDONHANG (MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT)
        VALUES (P_MADON_OUT, NULL, P_MATRANGTHAI, CURRENT_TIMESTAMP, P_MANV_LAP);

        INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
        VALUES (P_MANV_LAP, 'DON_HANG', 'Tao don hang moi #' || P_MADON_OUT, P_MADON_OUT);

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            -- Reset flag + isolation level
            PKG_ERROR_CODES.G_SKIP_STOCK_TRIGGER := FALSE;
            EXECUTE IMMEDIATE 'ALTER SESSION SET ISOLATION_LEVEL = READ COMMITTED';
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON, 'Loi he thong khi Tao Don Hang: ' || SQLERRM);
    END;
    /
    commit ;

    CREATE OR REPLACE PROCEDURE PROC_HUYDON_HOANCOC (
        P_MADON     IN DONDATHANG.MADON%TYPE,
        P_LYDOHUY   IN NVARCHAR2,
        P_MANV      IN NHANVIEN.MANV%TYPE,
        P_SOTIENHOANTIEN IN NUMBER,
        P_MACA      IN CALAMVIEC.MACA%TYPE
    )
    IS
        V_MATRANGTHAI_HUY   NUMBER;
        V_MALOAITHUCHI_CHI  NUMBER;
        V_MAPHIEUTC_OUT     PHIEUTHUCHI.MAPHIEUTC%TYPE;
    BEGIN
        -- 1. Lấy mã trạng thái "Đã hủy"
        SELECT MATRANGTHAI INTO V_MATRANGTHAI_HUY
        FROM TRANGTHAIDON
        WHERE UPPER(TENTRANGTHAI) LIKE N'%H%Y%'
          AND ROWNUM = 1;

        -- 2. Cập nhật trạng thái đơn sang "Đã hủy"
        UPDATE DONDATHANG
        SET MATRANGTHAI = V_MATRANGTHAI_HUY
        WHERE MADON = P_MADON;

        IF SQL%ROWCOUNT = 0 THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_DON, N'Không tìm thấy đơn hàng để hủy: ' || P_MADON);
        END IF;

        -- 3. Ghi lịch sử hủy
        INSERT INTO LICHSUDONHANG (MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT, GHICHU)
        VALUES (P_MADON, NULL, V_MATRANGTHAI_HUY, CURRENT_TIMESTAMP, P_MANV, P_LYDOHUY);

        -- 4. Ghi log hoạt động nhân viên
        INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
        VALUES (P_MANV, 'DON_HANG', N'Huy don hang #' || P_MADON || N' — ' || P_LYDOHUY, P_MADON);

        -- 5. Nếu có hoàn tiền → tạo phiếu chi trong CÙNG transaction
        IF NVL(P_SOTIENHOANTIEN, 0) > 0 THEN
            -- Lấy mã loại chi "Hoàn tiền" (động, không hardcode ID)
            BEGIN
                SELECT MALOAITHUCHI INTO V_MALOAITHUCHI_CHI
                FROM LOAITHUCHI
                WHERE UPPER(TENLOAITHUCHI) LIKE N'%HOAN%TIEN%'
                  AND PHANLOAI = 'Chi'
                  AND THOIDIEMXOA IS NULL
                  AND ROWNUM = 1;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    -- Tự tạo loại chi "Hoàn tiền" nếu chưa có
                    INSERT INTO LOAITHUCHI (TENLOAITHUCHI, PHANLOAI, THOIDIEMXOA, MANX)
                    VALUES (N'Hoàn tiền', 'Chi', NULL, NULL)
                    RETURNING MALOAITHUCHI INTO V_MALOAITHUCHI_CHI;
            END;

            -- Tạo phiếu chi hoàn tiền — liên kết với đơn hàng
            INSERT INTO PHIEUTHUCHI (MALOAITHUCHI, SOTIEN, MANV, MAHD, MAPN, MACA, GHICHU)
            VALUES (
                V_MALOAITHUCHI_CHI,
                P_SOTIENHOANTIEN,
                P_MANV,
                NULL,                  -- MAHD: không có hóa đơn
                NULL,                  -- MAPN: không phải phiếu nhập kho
                NULLIF(P_MACA, 0),    -- Bug 4: 0 → NULL tránh FK violation
                N'Hoàn tiền cọc đơn hàng #' || P_MADON
            );
        END IF;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_DON, N'Lỗi hệ thống khi hủy đơn hàng: ' || SQLERRM);
    END;
    /
