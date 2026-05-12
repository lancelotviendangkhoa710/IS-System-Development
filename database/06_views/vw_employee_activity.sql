-- View phục vụ màn hình Lịch sử hệ thống (Employee Activity Audit Log)
-- Fix: Loại bỏ JOIN VAITRO_CHUCNANG + CHUCNANG gây Cartesian explosion (1 NV N vaitro × M chucnang = N×M dòng trùng)
CREATE OR REPLACE VIEW VW_HoatDongNhanVien AS
SELECT
    H.MAHOATDONG,
    H.MANV,
    NV.HOTEN  AS TENNHANVIEN,

    (SELECT VT2.TENVAITRO
     FROM NHANVIEN_VAITRO NVVT2
              JOIN VAITRO VT2 ON VT2.MAVAITRO = NVVT2.MAVAITRO
     WHERE NVVT2.MANV = H.MANV
       AND NVVT2.MAVAITRO = (SELECT MIN(NVVT3.MAVAITRO)
                              FROM NHANVIEN_VAITRO NVVT3
                              WHERE NVVT3.MANV = H.MANV)) AS VAITRO,
    H.NHOM    AS MODULE,
    H.NHOM,
    H.HANHDONG,
    H.ENTITY_ID,
    H.THOIGIAN
FROM HOATDONGNHANVIEN H
         JOIN NHANVIEN NV ON NV.MANV = H.MANV
;
