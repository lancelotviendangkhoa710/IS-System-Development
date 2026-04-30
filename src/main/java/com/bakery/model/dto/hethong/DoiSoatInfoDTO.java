package com.bakery.model.dto.hethong;

import java.math.BigDecimal;

/**
 * Thông tin hiển thị cho dialog Đối soát Đóng ca.
 * Chứa dữ liệu lấy từ CALAMVIEC + DOISOAT + doanh thu tính bởi hệ thống.
 */
public record DoiSoatInfoDTO(
        int maCa,
        String maMayPOS,
        BigDecimal tienKhaiBaoDauCa,
        BigDecimal doanhThuPhatSinh
) {}
