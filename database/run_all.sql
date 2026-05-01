-- =============================================================================
-- MASTER SETUP SCRIPT FOR BAKERY MANAGEMENT SYSTEM (ORACLE DB)
-- =============================================================================
-- Mô tả: Tự động thực thi toàn bộ script database theo đúng trình tự phụ thuộc.
-- Cách dùng:
--   1. Mở SQL*Plus hoặc SQL Developer.
--   2. Kết nối vào schema của bạn.
--   3. Chạy lệnh: @run_all.sql
-- =============================================================================

SET ECHO ON;
SET FEEDBACK ON;
SET DEFINE OFF;
SET SERVEROUTPUT ON;

PROMPT >>> BAT DAU KHOI TAO DATABASE <<<

-- 1. TAO BANG (TABLES)
PROMPT [1/7] Dang tao cac bang...
@@01_tables/01_employee_customer.sql;
@@01_tables/02_product.sql;
@@01_tables/03_stock_recipe.sql;
@@01_tables/04_order.sql;
@@01_tables/05_finance.sql;

-- 2. RANG BUOC & MA LOI (CONSTRAINTS & PACKAGES)
PROMPT [2/7] Dang tao rang buoc va package ma loi...
@@02_constraints/package_error_codes.sql;
@@02_constraints/01_ck_emp_cus.sql;
@@02_constraints/02_ck_product.sql;
@@02_constraints/03_ck_stock_recipe.sql;
@@02_constraints/04_ck_order.sql;
@@02_constraints/05_ck_finance.sql;
@@02_constraints/ck_status.sql;

-- 3. HAM TINH TOAN (FUNCTIONS)
PROMPT [3/7] Dang tao cac ham (Functions)...
@@03_functions/func_availability.sql;
@@03_functions/func_bestseller.sql;
@@03_functions/func_calccost.sql;
@@03_functions/func_calculate_custorm_cake.sql;
@@03_functions/func_customprice.sql;
@@03_functions/func_fefo.sql;
@@03_functions/func_idealcash.sql;
@@03_functions/func_maxnumbercake.sql;
@@03_functions/func_measureability.sql;
@@03_functions/func_profitgross.sql;
@@03_functions/func_profit_paymethod.sql;
@@03_functions/func_recipe.sql;
@@03_functions/func_stock_alert.sql;

-- 4. TRINH KICH HOAT (TRIGGERS)
PROMPT [4/7] Dang tao cac triggers...
@@04_triggers/03_trg_stock_recipe/trg_avgcost_tolstock.sql;
@@04_triggers/03_trg_stock_recipe/trg_check_hygienic_food.sql;
@@04_triggers/03_trg_stock_recipe/trg_materials_export.sql;
@@04_triggers/03_trg_stock_recipe/trg_prohibit_expired_scam.sql;
@@04_triggers/03_trg_stock_recipe/trg_remove_expired_cakes.sql;
@@04_triggers/03_trg_stock_recipe/trg_total_imports.sql;
@@04_triggers/04_trg_order/trg_assign_price_custom.sql;
@@04_triggers/04_trg_order/trg_assign_price_standard.sql;
@@04_triggers/04_trg_order/trg_control_capacity_custom.sql;
@@04_triggers/04_trg_order/trg_control_capacity_standard.sql;
@@04_triggers/04_trg_order/trg_except_stock.sql;
@@04_triggers/04_trg_order/trg_update_inventory_stock.sql;
@@04_triggers/04_trg_order/trg_update_order_total.sql;
@@04_triggers/05_trg_finance/trg_prohibit_delete_invoice.sql;
@@04_triggers/05_trg_finance/trg_prohibit_delete_voucher.sql;

-- 5. THU TUC (PROCEDURES)
PROMPT [5/7] Dang tao cac thu tuc (Procedures)...
@@05_procedures/cud/proc_customer_cud.sql;
@@05_procedures/cud/proc_employee_cud.sql;
@@05_procedures/cud/proc_finance_cud.sql;
@@05_procedures/cud/proc_material_cud.sql;
@@05_procedures/cud/proc_order_cud.sql;
@@05_procedures/cud/proc_product_cud.sql;
@@05_procedures/cud/proc_stock_cud.sql;
@@05_procedures/cud/proc_supplier_cud.sql;
@@05_procedures/proc_backup.sql;
@@05_procedures/proc_cancel_refund.sql;
@@05_procedures/proc_change_status.sql;
@@05_procedures/proc_checking_shift.sql;
@@05_procedures/proc_open_shift.sql;
@@05_procedures/proc_payment_uprannk.sql;

-- 6. VIEW BAO CAO (VIEWS)
PROMPT [6/7] Dang tao cac views...
@@06_views/vw_order_detail.sql;
@@06_views/vw_order_list.sql;
@@06_views/vw_order_tracking.sql;
@@06_views/vw_pickup_slip.sql;
@@06_views/vw_stock_material.sql;

-- 7. CAP NHAT PHIEN BAN & SEED DATA
PROMPT [7/7] Dang cap nhat cau hinh va nap du lieu mau...
@@config/alter_chucnang_module.sql;
@@config/alter_hoadon_trangthai.sql;
@@config/alter_phieuthuchi_trangthai.sql;
@@config/script_insert_data.sql;

PROMPT >>> HOAN THANH KHOI TAO DATABASE <<<
COMMIT;
