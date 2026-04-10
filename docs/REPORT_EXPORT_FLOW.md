# Huong Dan Chay Chuc Nang Xuat Bao Cao Doanh Thu (PDF)

## 1. Muc tieu
Tai lieu nay mo ta:
- Cach chay ung dung va dung chuc nang `Export PDF`.
- Flow xu ly va cac ham duoc goi theo thu tu.
- Diem mo rong de them tinh nang moi an toan.

## 2. Dieu kien moi truong
- JDK 25 (theo `pom.xml`: `<maven.compiler.release>25</maven.compiler.release>`).
- Maven da cai dat va co trong `PATH`.
- Oracle Database dang chay va dung thong tin ket noi trong `src/main/resources/application.properties`.

## 3. Cach chay ung dung

### Cach 1: Maven (khuyen nghi)
Tai thu muc goc du an:

```bash
mvn clean javafx:run
```

Plugin JavaFX duoc cau hinh tai:
- `pom.xml` -> `org.openjfx:javafx-maven-plugin`
- `mainClass`: `com.bakery/com.bakery.main.App`

### Cach 2: IntelliJ
1. Mo tab Maven.
2. Chay goal `javafx:run`.
3. Khong can tu set VM options JavaFX khi chay theo Maven goal.

## 4. Cach su dung chuc nang Export PDF
1. Chon `From date` va `To date`.
2. Bam `Choose PDF...` de chon duong dan file xuat.
3. Bam `Export PDF`.
4. Kiem tra thong bao thanh cong va file PDF duoc tao.

## 5. Flow xu ly (ham nao goi ham nao)

### 5.1 Trigger tu giao dien
- File: `src/main/resources/views/Dashboard.fxml`
- Nut `Export PDF` goi:
  - `onAction="#onExportRevenuePdf"`

### 5.2 Controller
- File: `src/main/java/com/bakery/controllers/MainController.java`
- Ham chinh:
  - `onExportRevenuePdf()`

Trong ham nay:
1. Doc du lieu tu UI (`fromDatePicker`, `toDatePicker`, `outputPathField`).
2. Validate du lieu ngay va duong dan file.
3. Tao `Task<RevenueReportResult>` chay nen.
4. Trong `Task.call()` goi:
   - `reportService.exportRevenueReportPdf(fromDate, toDate, pdfPath)`
5. `setOnSucceeded`:
   - Cap nhat tong so hoa don, tong doanh thu, thong bao thanh cong.
6. `setOnFailed`:
   - Hien thong bao loi.

### 5.3 Service tao bao cao
- File: `src/main/java/com/bakery/reports/ReportService.java`
- Ham chinh:
  - `exportRevenueReportPdf(LocalDate fromDate, LocalDate toDate, Path outputPdf)`

Trong ham nay:
1. Validate `fromDate`, `toDate`, `outputPdf`.
2. Chuyen doi khoang ngay:
   - `fromInclusive = fromDate.atStartOfDay()`
   - `toExclusive = toDate.plusDays(1).atStartOfDay()`
3. Goi DAO:
   - `hoaDonDAO.layHoaDonTheoKhoangThoiGian(fromInclusive, toExclusive)`
4. Tao folder neu chua ton tai.
5. Tinh tong doanh thu.
6. Goi `writePdf(...)` de sinh file PDF.
7. Tra ve `RevenueReportResult(outputPdf, invoiceCount, totalRevenue)`.

Ham phu quan trong:
- `writePdf(...)`: tao noi dung PDF bang OpenPDF.
- `registerFonts()`: dang ky font he thong Windows cho tieng Viet.

### 5.4 DAO truy van du lieu
- File: `src/main/java/com/bakery/dao/HoaDonDAO.java`
- Ham:
  - `layHoaDonTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgayKhongTinh)`

Thuc hien:
1. Tao ket noi DB qua `openConnection()`.
2. Chay SQL:
   - `SELECT * FROM HOADON WHERE NGAYXUATHD >= ? AND NGAYXUATHD < ? ORDER BY NGAYXUATHD, MAHD`
3. Map `ResultSet` sang `HoaDonDTO`.

### 5.5 DB connection
- File: `src/main/java/com/bakery/utils/DBConnect.java`
- Ham:
  - `DBConnect.getConnection()`

Doc thong tin ket noi tu:
- `src/main/resources/application.properties`

## 6. File module va mo rong
- File module hien tai:
  - `src/main/java/module-info.java`

Dang khai bao:
- JavaFX modules.
- `java.sql`.
- `java.desktop` (can cho `java.awt.Color` trong PDF).
- `com.github.librepdf.openpdf`.
- `com.oracle.database.jdbc`.
- `opens com.bakery.controllers to javafx.fxml`.
- `exports com.bakery.main`.

Khi mo rong tinh nang:
1. Neu them thu vien moi va co su dung truc tiep, bo sung `requires ...` trong `module-info.java`.
2. Neu FXML can truy cap package controller moi, bo sung `opens <package> to javafx.fxml`.
3. Neu package can cho module khac dung, bo sung `exports <package>`.

## 7. Checklist khi gap loi
- `mvn` khong chay: kiem tra Maven da vao `PATH`.
- Loi ket noi DB: kiem tra Oracle service va `application.properties`.
- Loi FXML controller: kiem tra `fx:controller` va `opens ... to javafx.fxml` trong `module-info.java`.
- Loi Java version: dam bao JDK dang dung phu hop voi `pom.xml` (release 25).
