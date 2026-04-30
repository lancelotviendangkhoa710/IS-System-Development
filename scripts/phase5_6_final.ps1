$srcBase = 'd:\Clone\src\main\java\com\bakery'

# =====================================================================
# FIX PHASE 4: Di chuyen Customer presenters tu 'customer/' -> 'khachhang/'
# =====================================================================
Write-Host "=== Fix Phase 4: presenters/customer -> khachhang ===" -ForegroundColor Magenta

$presBase = $srcBase + '\presenters'
$customerPresMap = @{
    'CustomerDeletedPresenter.java' = 'khachhang'
    'CustomerFormPresenter.java'    = 'khachhang'
    'CustomerInfoPresenter.java'    = 'khachhang'
}

New-Item -ItemType Directory -Path ($presBase + '\khachhang') -Force | Out-Null

foreach ($file in $customerPresMap.Keys) {
    $oldRel = 'src/main/java/com/bakery/presenters/customer/' + $file
    $newRel = 'src/main/java/com/bakery/presenters/khachhang/' + $file
    $oldAbs = 'd:\Clone\' + ($oldRel -replace '/', '\')
    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  OK: $file -> khachhang/" -ForegroundColor Green
        # Update package
        $newPath = 'd:\Clone\' + ($newRel -replace '/', '\')
        $c = [System.IO.File]::ReadAllText($newPath, [System.Text.Encoding]::UTF8)
        $c = $c -replace 'package com\.bakery\.presenters(\.customer)?;', 'package com.bakery.presenters.khachhang;'
        [System.IO.File]::WriteAllText($newPath, $c, [System.Text.Encoding]::UTF8)
    } else { Write-Host "  SKIP: $file" -ForegroundColor Yellow }
}

# Remove empty customer folder if exists
$customerPresDir = $presBase + '\customer'
if ((Test-Path $customerPresDir) -and ((Get-ChildItem $customerPresDir -Recurse).Count -eq 0)) {
    Remove-Item $customerPresDir -Force
}

# =====================================================================
# PHASE 5: views/interfaces
# =====================================================================
Write-Host ""
Write-Host "=== Phase 5: views/interfaces ===" -ForegroundColor Cyan

$ifBase = $srcBase + '\views\interfaces'

$ifMapping = @{
    'IDonHangView.java'             = 'banhang'
    'IDonHangDialogFactory.java'    = 'banhang'
    'INguyenLieuView.java'          = 'kho'
    'ISanPhamView.java'             = 'kho'
    'IDanhMucSPView.java'           = 'kho'
    'IDangNhapView.java'            = 'nhansu'
    'IBangDieuKhienView.java'       = 'baocao'
    'IDoiSoatView.java'             = 'baocao'
    'MembershipTierView.java'       = 'khachhang'
    'CustomerAddView.java'          = 'khachhang'
    'CustomerDeletedView.java'      = 'khachhang'
    'CustomerInfoView.java'         = 'khachhang'
    'CustomerUpdateView.java'       = 'khachhang'
    'IMoCaView.java'                = 'hethong'
    'IDoiSoatDongCaView.java'       = 'hethong'
    'ISoQuyView.java'               = 'hethong'
    'IManHinhChinhView.java'        = 'hethong'
}

foreach ($sub in @('banhang','kho','nhansu','baocao','khachhang','hethong')) {
    New-Item -ItemType Directory -Path ($ifBase + '\' + $sub) -Force | Out-Null
}

foreach ($file in $ifMapping.Keys) {
    $sub = $ifMapping[$file]
    $oldRel = 'src/main/java/com/bakery/views/interfaces/' + $file
    $newRel = 'src/main/java/com/bakery/views/interfaces/' + $sub + '/' + $file
    $oldAbs = 'd:\Clone\' + ($oldRel -replace '/', '\')
    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  OK: $file -> $sub/" -ForegroundColor Green
        $newAbs = 'd:\Clone\' + ($newRel -replace '/', '\')
        $c = [System.IO.File]::ReadAllText($newAbs, [System.Text.Encoding]::UTF8)
        $newPkg = 'package com.bakery.views.interfaces.' + $sub + ';'
        $c = $c -replace 'package com\.bakery\.views\.interfaces;', $newPkg
        [System.IO.File]::WriteAllText($newAbs, $c, [System.Text.Encoding]::UTF8)
    } else { Write-Host "  SKIP: $file" -ForegroundColor Yellow }
}

# Update imports for interfaces
Write-Host "  -> Updating interface imports across project..." -ForegroundColor Gray
$allJava5 = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse
foreach ($jf in $allJava5) {
    $c = [System.IO.File]::ReadAllText($jf.FullName, [System.Text.Encoding]::UTF8)
    $orig = $c
    foreach ($file in $ifMapping.Keys) {
        $cn = [System.IO.Path]::GetFileNameWithoutExtension($file)
        $sub = $ifMapping[$file]
        $c = $c.Replace("import com.bakery.views.interfaces.$cn;", "import com.bakery.views.interfaces.$sub.$cn;")
    }
    if ($c -ne $orig) {
        [System.IO.File]::WriteAllText($jf.FullName, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  imports updated: $($jf.Name)" -ForegroundColor Green
    }
}

# =====================================================================
# PHASE 6: views/controllers
# =====================================================================
Write-Host ""
Write-Host "=== Phase 6: views/controllers ===" -ForegroundColor Cyan

$ctrlBase = $srcBase + '\views\controllers'

# Controllers at root level
$ctrlMapping = @{
    'DonHangViewFXMLController.java'          = 'banhang'
    'TaoDonHangViewFXMLController.java'       = 'banhang'
    'HoaDonViewFXMLController.java'           = 'banhang'
    'TheoDoiDonHangViewFXMLController.java'   = 'banhang'
    'ThuNganViewFXMLController.java'          = 'banhang'
    'ThanhToanDialogViewFXMLController.java'  = 'banhang'
    'LyDoXacNhanDialogViewFXMLController.java'= 'banhang'
    'KhachHangDialogViewFXMLController.java'  = 'banhang'
    'KhoViewFXMLController.java'              = 'kho'
    'NguyenLieuViewFXMLController.java'       = 'kho'
    'SanPhamViewFXMLController.java'          = 'kho'
    'DanhMucSPViewFXMLController.java'        = 'kho'
    'QuanLyNhaCungCapViewFXMLController.java' = 'kho'
    'DangNhapViewFXMLController.java'         = 'nhansu'
    'QuanLyNhanVienViewFXMLController.java'   = 'nhansu'
    'BaoCaoViewFXMLController.java'           = 'baocao'
    'BangDieuKhienViewFXMLController.java'    = 'baocao'
    'MembershipTierController.java'           = 'khachhang'
    'MoCaViewFXMLController.java'             = 'hethong'
    'DoiSoatDongCaViewFXMLController.java'    = 'hethong'
    'SoQuyViewFXMLController.java'            = 'hethong'
    'MainMenuViewFXMLController.java'         = 'hethong'
    'MainViewFXMLController.java'             = 'hethong'
}

# Controllers already in 'customer/' subfolder - move to 'khachhang/'
$customerCtrlFiles = @(
    'AbstractCustomerController.java'
    'CustomerAddViewFXMLController.java'
    'CustomerDeletedViewFXMLController.java'
    'CustomerInfoViewFXMLController.java'
    'CustomerUpdateViewFXMLController.java'
)

foreach ($sub in @('banhang','kho','nhansu','baocao','khachhang','hethong')) {
    New-Item -ItemType Directory -Path ($ctrlBase + '\' + $sub) -Force | Out-Null
}

# Move root-level controllers
foreach ($file in $ctrlMapping.Keys) {
    $sub = $ctrlMapping[$file]
    $oldRel = 'src/main/java/com/bakery/views/controllers/' + $file
    $newRel = 'src/main/java/com/bakery/views/controllers/' + $sub + '/' + $file
    $oldAbs = 'd:\Clone\' + ($oldRel -replace '/', '\')
    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  OK: $file -> $sub/" -ForegroundColor Green
        $newAbs = 'd:\Clone\' + ($newRel -replace '/', '\')
        $c = [System.IO.File]::ReadAllText($newAbs, [System.Text.Encoding]::UTF8)
        $newPkg = 'package com.bakery.views.controllers.' + $sub + ';'
        $c = $c -replace 'package com\.bakery\.views\.controllers;', $newPkg
        [System.IO.File]::WriteAllText($newAbs, $c, [System.Text.Encoding]::UTF8)
    } else { Write-Host "  SKIP: $file" -ForegroundColor Yellow }
}

# Move customer/* -> khachhang/
Write-Host "  Moving customer/* -> khachhang/" -ForegroundColor Gray
foreach ($file in $customerCtrlFiles) {
    $oldRel = 'src/main/java/com/bakery/views/controllers/customer/' + $file
    $newRel = 'src/main/java/com/bakery/views/controllers/khachhang/' + $file
    $oldAbs = 'd:\Clone\' + ($oldRel -replace '/', '\')
    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  OK: $file -> khachhang/" -ForegroundColor Green
        $newAbs = 'd:\Clone\' + ($newRel -replace '/', '\')
        $c = [System.IO.File]::ReadAllText($newAbs, [System.Text.Encoding]::UTF8)
        $c = $c -replace 'package com\.bakery\.views\.controllers(\.customer)?;', 'package com.bakery.views.controllers.khachhang;'
        [System.IO.File]::WriteAllText($newAbs, $c, [System.Text.Encoding]::UTF8)
    } else { Write-Host "  SKIP: $file" -ForegroundColor Yellow }
}

# Remove empty customer folder
$customerCtrlDir = $ctrlBase + '\customer'
if ((Test-Path $customerCtrlDir) -and ((Get-ChildItem $customerCtrlDir -Recurse).Count -eq 0)) {
    Remove-Item $customerCtrlDir -Force
    Write-Host "  Removed empty customer/ folder" -ForegroundColor Gray
}

# Update imports for controllers
Write-Host "  -> Updating controller imports across project..." -ForegroundColor Gray
$allJava6 = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse
$allCtrlClasses = @{}
foreach ($f in $ctrlMapping.Keys) {
    $allCtrlClasses[[System.IO.Path]::GetFileNameWithoutExtension($f)] = $ctrlMapping[$f]
}
foreach ($f in $customerCtrlFiles) {
    $allCtrlClasses[[System.IO.Path]::GetFileNameWithoutExtension($f)] = 'khachhang'
}

foreach ($jf in $allJava6) {
    $c = [System.IO.File]::ReadAllText($jf.FullName, [System.Text.Encoding]::UTF8)
    $orig = $c
    foreach ($cn in $allCtrlClasses.Keys) {
        $sub = $allCtrlClasses[$cn]
        $c = $c.Replace("import com.bakery.views.controllers.$cn;", "import com.bakery.views.controllers.$sub.$cn;")
        $c = $c.Replace("import com.bakery.views.controllers.customer.$cn;", "import com.bakery.views.controllers.$sub.$cn;")
    }
    if ($c -ne $orig) {
        [System.IO.File]::WriteAllText($jf.FullName, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  imports updated: $($jf.Name)" -ForegroundColor Green
    }
}

# =====================================================================
# PHASE 6b: Cap nhat import cho Customer Presenters da fix
# =====================================================================
Write-Host ""
Write-Host "=== Fix Phase 4b: Update imports for Customer Presenters ===" -ForegroundColor Magenta
$allJava4b = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse
foreach ($jf in $allJava4b) {
    $c = [System.IO.File]::ReadAllText($jf.FullName, [System.Text.Encoding]::UTF8)
    $orig = $c
    $c = $c.Replace("import com.bakery.presenters.customer.CustomerDeletedPresenter;", "import com.bakery.presenters.khachhang.CustomerDeletedPresenter;")
    $c = $c.Replace("import com.bakery.presenters.customer.CustomerFormPresenter;", "import com.bakery.presenters.khachhang.CustomerFormPresenter;")
    $c = $c.Replace("import com.bakery.presenters.customer.CustomerInfoPresenter;", "import com.bakery.presenters.khachhang.CustomerInfoPresenter;")
    if ($c -ne $orig) {
        [System.IO.File]::WriteAllText($jf.FullName, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  customer presenter import fixed: $($jf.Name)" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "ALL PHASES DONE." -ForegroundColor Green
Write-Host "Summary:" -ForegroundColor Cyan
Write-Host "  Phase 1: DTO          - 40 files" -ForegroundColor White
Write-Host "  Phase 2: DAO          - 23 files" -ForegroundColor White
Write-Host "  Phase 3: Services     - 20 files" -ForegroundColor White
Write-Host "  Phase 4: Presenters   - 15 files" -ForegroundColor White
Write-Host "  Phase 5: Interfaces   - 17 files" -ForegroundColor White
Write-Host "  Phase 6: Controllers  - 28 files" -ForegroundColor White
Write-Host "  Total: ~143 files restructured" -ForegroundColor Yellow
