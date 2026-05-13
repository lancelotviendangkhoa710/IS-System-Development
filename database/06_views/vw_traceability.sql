-- View Truy xuất nguồn gốc nguyên liệu — từ mẻ sản xuất tới lô nhập và nhà cung cấp
CREATE OR REPLACE VIEW VW_TRACUUNGUONGOC AS
SELECT
    ME.MAME,
    ME.NGAYSANXUAT                          AS NGAY_SAN_XUAT,
    ME.SOLUONGSANXUAT,
    SP.MASP,
    SP.TENSP,
    NL.MANL,
    NL.TENNL                                AS TEN_NGUYEN_LIEU,
    CX.SOLUONG                              AS SOLUONG_DA_DUNG,
    CTN.MALO,
    CTN.MAVACH_LO,
    CTN.NGAYSANXUAT                         AS NSX_NGUYEN_LIEU,
    CTN.HANSUDUNG,
    CTN.DONGIA                              AS GIA_NHAP,
    PN.MAPN,
    PN.NGAYNHAP,
    NCC.MANCC,
    NCC.TENNCC,
    NCC.SDT                                 AS SDT_NCC,
    NCC.DIACHI                              AS DIACHI_NCC
FROM MESANXUAT          ME
JOIN SANPHAM            SP  ON SP.MASP   = ME.MASP
JOIN PHIEUXUATKHO       PX  ON PX.MAPX   = ME.MAPX
JOIN CTPHIEUXUAT_NL     CX  ON CX.MAPX   = PX.MAPX
JOIN CTPHIEUNHAP        CTN ON CTN.MALO   = CX.MALO
JOIN NGUYENLIEU         NL  ON NL.MANL    = CTN.MANL
JOIN PHIEUNHAPKHO       PN  ON PN.MAPN    = CTN.MAPN
JOIN NHACUNGCAP         NCC ON NCC.MANCC  = PN.MANCC;
