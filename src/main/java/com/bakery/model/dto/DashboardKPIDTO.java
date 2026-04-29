package com.bakery.model.dto;

import java.math.BigDecimal;

/**
 * Dữ liệu tổng hợp cho 4 ô KPI trên Dashboard.
 * Record — chỉ đọc, không cần setter.
 */
public record DashboardKPIDTO(
        BigDecimal doanhThuHomNay,
        int        soHoaDonHomNay,
        int        donDangXuLy,
        int        canhBaoTonKho
) {}
