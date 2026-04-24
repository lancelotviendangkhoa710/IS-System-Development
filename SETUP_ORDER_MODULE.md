# Huong dan cai dat va chay Order Module (JavaFX)

## 1. Dieu kien moi truong
- JDK 25 (hoac toi thieu 21, khuyen nghi 25 de khop `pom.xml`).
- Maven 3.8+.
- Oracle Database 12c+.
- IntelliJ IDEA (khuyen nghi) hoac IDE bat ky ho tro Maven.

## 2. Cac thu vien dang dung
- `javafx-controls`, `javafx-fxml`, `javafx-graphics`.
- `ojdbc8` de ket noi Oracle.
- `pdfbox` cho xu ly PDF.

Luu y: Cau hinh Swing/FlatLaf da duoc loai bo.

## 3. Chuan bi database
- Chay `database/config/script_insert_data.sql` de nap du lieu mau.
- Build day du function/procedure/trigger trong thu muc `database/`.
- Dam bao bang `DONDATHANG` co cot `SDTGIAO VARCHAR2(20)`.

## 4. Build project
```bash
mvn clean compile
```

Neu may chua co lenh `mvn`, cai Maven hoac mo bang IntelliJ de IDE tu dong sync dependency.

## 5. Chay ung dung
- Main class hien tai: `com.bakery.main.App`.
- IntelliJ: click chuot phai vao [App.java](/D:/Clone/src/main/java/com/bakery/main/App.java:1) -> Run `App.main()`.
- Maven (neu can):
```bash
mvn javafx:run
```

## 6. Kien truc luong chay
1. `App` load `OrderView.fxml`.
2. `OrderViewFXMLController` khoi tao `OrderPresenter` + `OrderService`.
3. `OrderPresenter.taiDuLieuBanDau()` load du lieu san pham, tuy chinh, trang thai.
4. Cac dialog tao don/thanh toan duoc mo qua `IOrderDialogFactory` (`CreateOrderViewFXMLController`).
