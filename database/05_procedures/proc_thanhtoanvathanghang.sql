CREATE OR REPLACE PROCEDURE PROC_ThanhToanVaThangHang (
    p_MaDon      IN  NUMBER,
    p_MaCA       IN  NUMBER,
    p_MaPTTT     IN  NUMBER,
    p_ThueVAT    IN  NUMBER DEFAULT 0,
    p_LoaiHD     IN  NVARCHAR2 DEFAULT 'BAN_LE',
    p_MaHD       OUT NUMBER,
    p_TongTien   OUT NUMBER,
    p_ThongBao   OUT VARCHAR2
) AS
    C_HE_SO_QUY_DOI CONSTANT NUMBER := 20000;
    v_MaKH          NUMBER;
    v_DiemTichLuyCu NUMBER;
    v_DiemMoi       NUMBER;
    v_MaHangCu      NUMBER;
    v_MaHangMoi     NUMBER;
    v_TenHangMoi    VARCHAR2(50);
BEGIN

    SELECT MAKH, TONGTIENHDBAN INTO v_MaKH, p_TongTien FROM DONDATHANG WHERE MADON = p_MaDon;

    SELECT DIEMTICHLUY, MAHANG INTO v_DiemTichLuyCu, v_MaHangCu FROM KHACHHANG WHERE MAKH = v_MaKH;
    v_DiemMoi := v_DiemTichLuyCu + FLOOR(p_TongTien / C_HE_SO_QUY_DOI);

    BEGIN
        SELECT MAHANG, TENHANG INTO v_MaHangMoi, v_TenHangMoi FROM HANGTHANHVIEN
        WHERE v_DiemMoi >= DIEMTOITHIEU ORDER BY DIEMTOITHIEU DESC FETCH FIRST 1 ROW ONLY;
    EXCEPTION WHEN NO_DATA_FOUND THEN v_MaHangMoi := v_MaHangCu;
    END;

    UPDATE KHACHHANG SET DIEMTICHLUY = v_DiemMoi, MAHANG = v_MaHangMoi WHERE MAKH = v_MaKH;

    -- [Bước 6: Tạo thông báo]
    IF v_MaHangMoi > v_MaHangCu THEN
        p_ThongBao := 'Thăng hạng: ' || v_TenHangMoi;
    ELSE
        p_ThongBao := 'Thanh toán thành công!';
    END IF;

    -- Bước 7: GỌI PROCEDURE CUD
    PROC_HOADON_INSERT(
        p_MaDon,
        p_MaCA,
        p_ThueVAT,
        p_TongTien,
        p_MaPTTT,
        p_LoaiHD,
         p_MaHD
    );

    -- [Bước 8: Cập nhật trạng thái đơn hàng]
    UPDATE LICHSUDONHANG SET MATRANGTHAI_MOI = 5 WHERE MADON = p_MaDon;

    -- CHỐT DỮ LIỆU CUỐI CÙNG
    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_QUY_TRINH_THANH_TOAN, 'Lỗi quy trình thanh toán: ' || SQLERRM);
END;
/