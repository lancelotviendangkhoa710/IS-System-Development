package com.bakery.model.dto;

import java.io.Serializable;

/**
 * Lớp cơ sở cho các đối tượng Data Transfer Object (DTO).
 */
public abstract class BaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Có thể bổ sung các thuộc tính chung nếu hệ thống mở rộng thêm 
    // ví dụ: ngayTao, ngayCapNhat, nguoiTao.
}
