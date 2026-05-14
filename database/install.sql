-- ============================================================
-- H3K BAKERY MANAGEMENT SYSTEM — Master Database Install Script
-- ============================================================
-- Chạy file này 1 lần duy nhất để tạo toàn bộ cấu trúc DB.
-- Yêu cầu: Oracle Database 19c+, kết nối bằng SQL*Plus hoặc SQLcl
--
-- Cách dùng (SQL*Plus):
--   sqlplus USERNAME/PASSWORD@HOSTNAME:1521/SERVICE_NAME @install.sql
--
-- Cách dùng (SQLcl):
--   sql USERNAME/PASSWORD@HOSTNAME:1521/SERVICE_NAME @install.sql
-- ============================================================

PROMPT ============================================================
PROMPT  H3K Bakery — Bắt đầu cài đặt cơ sở dữ liệu
PROMPT ============================================================

-- ── BƯỚC 1: Tạo bảng ─────────────────────────────────────────
PROMPT [1/6] Tạo bảng...
@@01_tables/01_employee_customer.sql
@@01_tables/02_product.sql
@@01_tables/03_stock_recipe.sql
@@01_tables/04_order.sql
@@01_tables/05_finance.sql

-- ── BƯỚC 2: Ràng buộc & Package lỗi ─────────────────────────
PROMPT [2/6] Tạo ràng buộc và package lỗi...
@@02_constraints/01_ck_emp_cus.sql
@@02_constraints/02_ck_product.sql
@@02_constraints/03_ck_stock_recipe.sql
@@02_constraints/04_ck_order.sql
@@02_constraints/05_ck_finance.sql
@@02_constraints/ck_status.sql
@@02_constraints/package_error_codes.sql

-- ── BƯỚC 3: Hàm (Functions) ──────────────────────────────────
PROMPT [3/6] Tạo functions...
@@03_functions/func_availability.sql
@@03_functions/func_bestseller.sql
@@03_functions/func_calccost.sql
@@03_functions/func_customprice.sql
@@03_functions/func_fefo.sql
@@03_functions/func_idealcash.sql
@@03_functions/func_maxnumbercake.sql
@@03_functions/func_measureability.sql
@@03_functions/func_profit_paymethod.sql
@@03_functions/func_profitgross.sql
@@03_functions/func_recipe.sql
@@03_functions/func_stock_alert.sql

-- ── BƯỚC 4: Triggers ─────────────────────────────────────────
PROMPT [4/6] Tạo triggers...
@@04_triggers/03_trg_stock_recipe/trg_avgcost_tolstock.sql
@@04_triggers/03_trg_stock_recipe/trg_check_hygienic_food.sql
@@04_triggers/03_trg_stock_recipe/trg_materials_export.sql
@@04_triggers/03_trg_stock_recipe/trg_prohibit_expired_scam.sql
@@04_triggers/03_trg_stock_recipe/trg_recalc_product_cost.sql
@@04_triggers/03_trg_stock_recipe/trg_remove_expired_cakes.sql
@@04_triggers/03_trg_stock_recipe/trg_total_imports.sql
@@04_triggers/04_trg_order/trg_assign_price_custom.sql
@@04_triggers/04_trg_order/trg_assign_price_standard.sql
@@04_triggers/04_trg_order/trg_control_capacity_custom.sql
@@04_triggers/04_trg_order/trg_except_stock.sql
@@04_triggers/04_trg_order/trg_update_order_total.sql
@@04_triggers/05_trg_finance/trg_prohibit_delete_invoice.sql
@@04_triggers/05_trg_finance/trg_prohibit_delete_voucher.sql

-- ── BƯỚC 5: Stored Procedures ────────────────────────────────
PROMPT [5/6] Tạo stored procedures...
@@05_procedures/proc_backup.sql
@@05_procedures/proc_cancel_invoice.sql
@@05_procedures/proc_cancel_refund.sql
@@05_procedures/proc_change_status.sql
@@05_procedures/proc_checking_shift.sql
@@05_procedures/proc_open_shift.sql
@@05_procedures/proc_payment_uprannk.sql
@@05_procedures/proc_purge_expired.sql
@@05_procedures/cud/proc_customer_cud.sql
@@05_procedures/cud/proc_employee_cud.sql
@@05_procedures/cud/proc_finance_cud.sql
@@05_procedures/cud/proc_material_cud.sql
@@05_procedures/cud/proc_order_cud.sql
@@05_procedures/cud/proc_product_cud.sql
@@05_procedures/cud/proc_stock_cud.sql
@@05_procedures/cud/proc_supplier_cud.sql

-- ── BƯỚC 6: Views ────────────────────────────────────────────
PROMPT [6/6] Tạo views...
@@06_views/vw_employee_activity.sql
@@06_views/vw_order_detail.sql
@@06_views/vw_order_list.sql
@@06_views/vw_order_tracking.sql
@@06_views/vw_pickup_slip.sql
@@06_views/vw_stock_material.sql
@@06_views/vw_traceability.sql

-- ── DỮ LIỆU MẪU ──────────────────────────────────────────────
PROMPT Nạp dữ liệu mẫu (demo)...
@@config/script_insert_data.sql

PROMPT ============================================================
PROMPT  Cài đặt hoàn tất! Vui lòng cấu hình application.properties
PROMPT ============================================================
EXIT;
