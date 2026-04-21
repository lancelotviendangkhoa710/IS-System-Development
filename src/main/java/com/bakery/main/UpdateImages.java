package com.bakery.main;

import com.bakery.utils.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateImages {
    public static void main(String[] args) {
        try (Connection conn = DBConnect.getConnection()) {
            String sql = "UPDATE SANPHAM SET HINHANH = ? WHERE MASP = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // SP 1
                ps.setString(1, "/images/products/cake_vani_16.png");
                ps.setInt(2, 1);
                ps.addBatch();
                
                // SP 2
                ps.setString(1, "/images/products/cake_socola_18.png");
                ps.setInt(2, 2);
                ps.addBatch();
                
                // SP 3
                ps.setString(1, "/images/products/cake_redvelvet_16.png");
                ps.setInt(2, 3);
                ps.addBatch();


                
                int[] results = ps.executeBatch();
                System.out.println("Cập nhật thành công " + results.length + " sản phẩm!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
