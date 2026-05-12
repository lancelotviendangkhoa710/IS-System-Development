# UI TEMPLATES — FXML CHUẨN
> Chỉ đọc khi **tạo màn hình mới**. Khi sửa màn hình đã có: chỉ cần `ui_rules.md`.

## Template 1: Màn hình Quản lý (Master-Detail Layout)
Dùng cho mọi màn hình CRUD: NguyenLieu, SanPham, DanhMuc, v.v.

```xml
<VBox xmlns="http://javafx.com/javafx/25"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="com.bakery.views.controllers.[Tên]ViewFXMLController"
      spacing="20" styleClass="bg-app">

    <padding><Insets top="30" right="40" bottom="30" left="40"/></padding>

    <!-- ① HEADER: Quay lại + Tiêu đề + Nút hành động chính -->
    <HBox alignment="CENTER_LEFT" spacing="15">
        <Button text="← Quay lại" styleClass="btn-secondary" onAction="#onQuayLai"/>
        <VBox spacing="5">
            <Label text="[Tiêu đề màn hình]" styleClass="lbl-title-screen"/>
            <Label text="[Mô tả ngắn]" styleClass="lbl-body"/>
        </VBox>
        <Region HBox.hgrow="ALWAYS"/>
        <Button fx:id="btnThemMoi" text="➕ Thêm mới" styleClass="btn-primary" onAction="#onThemMoi"/>
    </HBox>

    <!-- ② CONTENT: Left TableView + Right Form Card -->
    <HBox spacing="30" VBox.vgrow="ALWAYS">

        <!-- Left: TableView + Search -->
        <VBox spacing="10" HBox.hgrow="ALWAYS">
            <HBox spacing="10" alignment="CENTER_LEFT">
                <Label text="Danh sách [...]" styleClass="lbl-title-card"/>
                <Region HBox.hgrow="ALWAYS"/>
                <TextField fx:id="txtTimKiem" promptText="🔍 Tìm kiếm..."
                           prefWidth="220" onKeyReleased="#onTimKiem" styleClass="text-field"/>
            </HBox>
            <TableView fx:id="tbl[TenMan]" styleClass="table-view" VBox.vgrow="ALWAYS">
                <columnResizePolicy>
                    <TableView fx:constant="CONSTRAINED_RESIZE_POLICY"/>
                </columnResizePolicy>
            </TableView>
        </VBox>

        <!-- Right: Form Card -->
        <VBox spacing="20" styleClass="card" prefWidth="400" alignment="TOP_CENTER">
            <Label text="Chi tiết [...]" styleClass="lbl-title-card"/>

            <!-- Form fields (mỗi field = VBox spacing="8") -->
            <VBox spacing="8">
                <Label text="[Tên trường]: *" styleClass="lbl-body-bold"/>
                <TextField fx:id="txt[TenTruong]" promptText="..." styleClass="text-field"/>
            </VBox>

            <Region VBox.vgrow="ALWAYS"/>

            <!-- Action buttons -->
            <VBox spacing="12" maxWidth="Infinity">
                <Button fx:id="btnLuuThayDoi" text="💾 Lưu thay đổi"
                        styleClass="btn-primary" maxWidth="Infinity" onAction="#onLuuThayDoi"/>
                <Button fx:id="btnXoa" text="🗑 Xóa [...]"
                        styleClass="btn-danger" maxWidth="Infinity" onAction="#onXoa"/>
            </VBox>
        </VBox>

    </HBox>

    <!-- ③ FOOTER STATUS BAR -->
    <HBox>
        <Label fx:id="lblThongBao" styleClass="lbl-small-bold"/>
    </HBox>

</VBox>
```

## Template 2: Dialog
```xml
<VBox xmlns="http://javafx.com/javafx/25" xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="..." spacing="0">

    <!-- Header -->
    <HBox styleClass="dialog-header" alignment="CENTER_LEFT" spacing="12">
        <Label text="[Tiêu đề dialog]" styleClass="dialog-header-title"/>
    </HBox>

    <!-- Body -->
    <VBox styleClass="dialog-body" spacing="16" VBox.vgrow="ALWAYS">
        <!-- Nội dung form -->
    </VBox>

    <!-- Footer -->
    <HBox styleClass="dialog-footer" alignment="CENTER_RIGHT" spacing="10">
        <Button text="Hủy" styleClass="btn-secondary" onAction="#onHuy"/>
        <Button text="Xác nhận" styleClass="btn-primary" onAction="#onXacNhan"/>
    </HBox>
</VBox>
```
