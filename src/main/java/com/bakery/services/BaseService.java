package com.bakery.services;

/**
 * Lớp cơ sở cho các lớp Service trong hệ thống.
 * Cung cấp các phương thức dùng chung cho nghiệp vụ (Business Logic).
 */
public abstract class BaseService {

    /**
     * Kiểm tra một chuỗi không được null hoặc rỗng.
     * @param value Giá trị cần kiểm tra
     * @param fieldName Tên trường (để hiển thị thông báo lỗi)
     * @throws Exception nếu chuỗi rỗng
     */
    protected void validateString(String value, String fieldName) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            throw new Exception(fieldName + " không được để trống.");
        }
    }

    /**
     * Kiểm tra đối tượng không được null.
     * @param obj Đối tượng cần kiểm tra
     * @param fieldName Tên trường
     * @throws Exception nếu đối tượng null
     */
    protected void validateNotNull(Object obj, String fieldName) throws Exception {
        if (obj == null) {
            throw new Exception(fieldName + " không được để trống.");
        }
    }

    /**
     * Kiểm tra một số phải là số dương.
     * @param value Giá trị cần kiểm tra
     * @param fieldName Tên trường
     * @throws Exception nếu giá trị <= 0
     */
    protected void validatePositive(double value, String fieldName) throws Exception {
        if (value <= 0) {
            throw new Exception(fieldName + " phải là số dương lớn hơn 0.");
        }
    }
}
