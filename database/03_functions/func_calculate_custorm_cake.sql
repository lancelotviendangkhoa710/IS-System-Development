CREATE OR REPLACE FUNCTION FUNC_TINH_GIA_TUY_CHINH (
    p_MaSP NUMBER,
    p_MaKC NUMBER,
    p_MaCot NUMBER,
    p_MaNhan NUMBER,
    p_MaTT NUMBER
) RETURN NUMBER IS
    v_TongGia NUMBER(15,2) := 0;
    v_GiaGoc NUMBER(15,2) := 0;
    v_GiaKC NUMBER(15,2) := 0;
    v_GiaCot NUMBER(15,2) := 0;
    v_GiaNhan NUMBER(15,2) := 0;
    v_GiaTT NUMBER(15,2) := 0;
BEGIN
    -- 1. Lấy giá cơ bản của sản phẩm (Bánh mẫu)
    SELECT GIACOBAN INTO v_GiaGoc FROM SANPHAM WHERE MASP = p_MaSP;

    -- 2. Lấy giá cộng thêm từ kích cỡ (ví dụ: size 20cm + 50k, 24cm + 100k)
    IF p_MaKC IS NOT NULL THEN
        SELECT PHUPHI INTO v_GiaKC FROM KICHCOBANH WHERE MAKC = p_MaKC;
    END IF;

    -- 3. Lấy giá cộng thêm từ cốt bánh
    IF p_MaCot IS NOT NULL THEN
        SELECT PHUPHI INTO v_GiaCot FROM COTBANH WHERE MACOT = p_MaCot;
    END IF;

    -- 4. Lấy giá cộng thêm từ nhân bánh
    IF p_MaNhan IS NOT NULL THEN
        SELECT PHUPHI INTO v_GiaNhan FROM NHANBANH WHERE MANHAN = p_MaNhan;
    END IF;

    -- 5. Lấy giá trang trí
    IF p_MaTT IS NOT NULL THEN
        SELECT PHUPHI INTO v_GiaTT FROM KIEUTRANGTRI WHERE MATRANGTRI = p_MaTT;
    END IF;

    -- Tính tổng
    v_TongGia := v_GiaGoc + v_GiaKC + v_GiaCot + v_GiaNhan + v_GiaTT;

    RETURN v_TongGia;
EXCEPTION
    WHEN OTHERS THEN RETURN 0;
END;