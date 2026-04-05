CREATE OR REPLACE PROCEDURE PROC_XUATKHOSANXUAT (
    P_MASP           IN NUMBER,
    P_SOLUONGSANXUAT IN NUMBER,
    P_MANV           IN NUMBER
)
IS
    V_MAPX           NUMBER;
    V_SOLUONGCANXUAT NUMBER;
    V_SOLUONGXUATLO  NUMBER;
    V_TONGTON        NUMBER;

-- Duyệt từng nguyên liệu trong công thức của MaSP
    CURSOR C_CONGTHUC IS
        SELECT CT.MANL,
               CT.SOLUONGTIEUHAO * P_SOLUONGSANXUAT AS SOLUONG_CAN_XUAT
        FROM CONGTHUC CT
        WHERE CT.MASP = P_MASP;

-- Duyệt từng lô của một nguyên liệu theo FEFO
-- FOR UPDATE NOWAIT nghĩa là khóa dòng ngay, không chờ nếu bị session khác giữ
    CURSOR C_LOHANG (P_MANL IN NUMBER) IS
        SELECT MALO, SOLUONGCONLAI
        FROM CTPHIEUNHAP
        WHERE MANL = P_MANL AND SOLUONGCONLAI > 0
        ORDER BY HANSUDUNG ASC
        FOR UPDATE OF SOLUONGCONLAI NOWAIT;

    V_ROWCT     C_CONGTHUC%ROWTYPE;
    V_ROWLOHANG C_LOHANG%ROWTYPE;
BEGIN
-- BƯỚC 1: Tạo phiếu xuất kho
-- INSERT trước để lấy MAPX, vì CTPHIEUXUAT_NL cần FK đến PHIEUXUATKHO
-- LYDOXUAT ghi rõ xuất cho mẻ nướng nào
    INSERT INTO PHIEUXUATKHO (LYDOXUAT, MANV)
    VALUES (
        'Xuất nguyên liệu sản xuất của sản phẩm có mã sản phẩm = ' || P_MASP ||
        ' | Số lượng = ' || P_SOLUONGSANXUAT || ' | Mã nhân viên =' || P_MANV,
        P_MANV
    )
    RETURNING MAPX INTO V_MAPX;
-- không cần truyển NGAYXUAT là vì nó được tự điền nhờ cái DEFAULT CURRENT_TIMESTAMP

-- BƯỚC 2: Kiểm tra tồn kho toàn bộ nguyên liệu trước khi xuất bất kỳ lô nào
-- Phải kiểm tra hết tất cả nguyên liệu trước,
-- tránh tình huống đã xuất được 3/5 nguyên liệu rồi mới phát hiện
-- nguyên liệu thứ 4 thiếu hàng thì phải ROLLBACK sẽ gây lãng phí
    FOR V_ROWCT IN C_CONGTHUC LOOP
        SELECT NVL(SUM(SOLUONGCONLAI), 0) INTO V_TONGTON
        FROM CTPHIEUNHAP
        WHERE MANL = V_ROWCT.MANL AND SOLUONGCONLAI > 0;

        IF V_TONGTON < V_ROWCT.SOLUONG_CAN_XUAT THEN
            RAISE_APPLICATION_ERROR(-20010,
                'Kho không đủ nguyên liệu có mã nguyên liệu = ' || V_ROWCT.MANL ||
                '. Cần: ' || V_ROWCT.SOLUONG_CAN_XUAT ||
                ' | Còn: ' || V_TONGTON);
        END IF;
    END LOOP;

-- BƯỚC 3: Duyệt lại từng nguyên liệu để thực hiện xuất kho FEFO
-- Mở cursor FEFO có FOR UPDATE NOWAIT để khóa dòng
    FOR V_ROWCT IN C_CONGTHUC LOOP
        V_SOLUONGCANXUAT := V_ROWCT.SOLUONG_CAN_XUAT;

        OPEN C_LOHANG(V_ROWCT.MANL);
        LOOP
            FETCH C_LOHANG INTO V_ROWLOHANG;
            EXIT WHEN C_LOHANG%NOTFOUND OR V_SOLUONGCANXUAT <= 0;

            -- Tính số lượng thực tế lấy từ lô này
            IF V_ROWLOHANG.SOLUONGCONLAI >= V_SOLUONGCANXUAT THEN
                V_SOLUONGXUATLO  := V_SOLUONGCANXUAT;
                V_SOLUONGCANXUAT := 0;
            ELSE
                V_SOLUONGXUATLO  := V_ROWLOHANG.SOLUONGCONLAI;
                V_SOLUONGCANXUAT := V_SOLUONGCANXUAT - V_ROWLOHANG.SOLUONGCONLAI;
            END IF;

            -- Trừ tồn lô trong CTPHIEUNHAP
            UPDATE CTPHIEUNHAP
            SET SOLUONGCONLAI = SOLUONGCONLAI - V_SOLUONGXUATLO
            WHERE CURRENT OF C_LOHANG;

            -- Ghi chứng từ từng lô, Trigger AFTER INSERT sẽ trừ SOLUONGTONTONG trong NGUYENLIEU
            -- Bảng CTPHIEUXUAT_NL có PK (MAPX, MALO), không có MANL riêng
            -- nên MALO đã đủ để biết nguyên liệu nào nhờ join qua CTPHIEUNHAP
            INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
            VALUES (V_MAPX, V_ROWLOHANG.MALO, V_SOLUONGXUATLO);

        END LOOP;
        CLOSE C_LOHANG;

    END LOOP;

-- BƯỚC 4: COMMIT toàn bộ transaction một lần duy nhất
-- Bao gồm: INSERT PHIEUXUATKHO + tất cả UPDATE CTPHIEUNHAP + tất cả INSERT CTPHIEUXUAT_NL của mọi nguyên liệu
    COMMIT;

-- Đảm bảo cursor luôn được đóng dù có lỗi xảy ra giữa chừng
EXCEPTION
    WHEN OTHERS THEN
        IF C_LOHANG%ISOPEN THEN
            CLOSE C_LOHANG;
        END IF;
        ROLLBACK;
        RAISE;
END;