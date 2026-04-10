-- Procedure Xuất Kho Sản Xuất (Có sử dụng Pessimistic Locking & FIFO)
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

        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_XUAT_KHO, 'Lỗi hệ thống khi xuất kho sản xuất: ' || SQLERRM);
END;
/
