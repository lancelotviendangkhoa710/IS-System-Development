$srcBase = 'd:\Clone\src\main\java\com\bakery'

# =====================================================================
# CLEANUP: Dọn sạch tất cả vấn đề còn lại
# =====================================================================

Write-Host "=== CLEANUP: Fix khachhang presenter packages ===" -ForegroundColor Yellow

# 1. Fix package trong 3 file presenters da o khachhang/ nhung chua duoc update package
$khachPresFiles = @(
    'CustomerDeletedPresenter.java',
    'CustomerFormPresenter.java',
    'CustomerInfoPresenter.java'
)
foreach ($f in $khachPresFiles) {
    $path = $srcBase + '\presenters\khachhang\' + $f
    if (Test-Path $path) {
        $c = [System.IO.File]::ReadAllText($path, [System.Text.Encoding]::UTF8)
        $updated = $c -replace 'package com\.bakery\.presenters(\.customer)?;', 'package com.bakery.presenters.khachhang;'
        [System.IO.File]::WriteAllText($path, $updated, [System.Text.Encoding]::UTF8)
        Write-Host "  package fixed: $f" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "=== CLEANUP: Di chuyen KhachHangPresenter -> khachhang ===" -ForegroundColor Yellow

$src1 = $srcBase + '\presenters\customer\KhachHangPresenter.java'
$dst1 = $srcBase + '\presenters\khachhang\KhachHangPresenter.java'
if (Test-Path $src1) {
    Copy-Item $src1 $dst1
    $c = [System.IO.File]::ReadAllText($dst1, [System.Text.Encoding]::UTF8)
    $c = $c -replace 'package com\.bakery\.presenters(\.customer)?;', 'package com.bakery.presenters.khachhang;'
    [System.IO.File]::WriteAllText($dst1, $c, [System.Text.Encoding]::UTF8)
    Remove-Item $src1
    Write-Host "  Moved: KhachHangPresenter.java -> khachhang/" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== CLEANUP: Xoa file duplicate trong presenters/customer ===" -ForegroundColor Yellow

$dupsToRemove = @('CustomerDeletedPresenter.java','CustomerFormPresenter.java','CustomerInfoPresenter.java')
foreach ($f in $dupsToRemove) {
    $p = $srcBase + '\presenters\customer\' + $f
    if (Test-Path $p) {
        Remove-Item $p -Force
        Write-Host "  Removed duplicate: $f" -ForegroundColor Green
    }
}

# Xoa folder customer neu rong
$custPresDir = $srcBase + '\presenters\customer'
if ((Test-Path $custPresDir) -and (@(Get-ChildItem $custPresDir -Recurse).Count -eq 0)) {
    Remove-Item $custPresDir -Force -Recurse
    Write-Host "  Removed empty folder: presenters/customer/" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== CLEANUP: Di chuyen KhachHangViewFXMLController -> controllers/khachhang ===" -ForegroundColor Yellow

$src2 = $srcBase + '\views\controllers\customer\KhachHangViewFXMLController.java'
$dst2 = $srcBase + '\views\controllers\khachhang\KhachHangViewFXMLController.java'
if (Test-Path $src2) {
    Copy-Item $src2 $dst2
    $c = [System.IO.File]::ReadAllText($dst2, [System.Text.Encoding]::UTF8)
    $c = $c -replace 'package com\.bakery\.views\.controllers(\.customer)?;', 'package com.bakery.views.controllers.khachhang;'
    [System.IO.File]::WriteAllText($dst2, $c, [System.Text.Encoding]::UTF8)
    Remove-Item $src2
    Write-Host "  Moved: KhachHangViewFXMLController.java -> khachhang/" -ForegroundColor Green
}

# Xoa folder controllers/customer neu rong
$custCtrlDir = $srcBase + '\views\controllers\customer'
if ((Test-Path $custCtrlDir) -and (@(Get-ChildItem $custCtrlDir -Recurse).Count -eq 0)) {
    Remove-Item $custCtrlDir -Force -Recurse
    Write-Host "  Removed empty folder: controllers/customer/" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== CLEANUP: Di chuyen KhachHangView.java -> interfaces/khachhang ===" -ForegroundColor Yellow

$src3 = $srcBase + '\views\interfaces\KhachHangView.java'
$dst3 = $srcBase + '\views\interfaces\khachhang\KhachHangView.java'
if (Test-Path $src3) {
    Copy-Item $src3 $dst3
    $c = [System.IO.File]::ReadAllText($dst3, [System.Text.Encoding]::UTF8)
    $c = $c -replace 'package com\.bakery\.views\.interfaces;', 'package com.bakery.views.interfaces.khachhang;'
    [System.IO.File]::WriteAllText($dst3, $c, [System.Text.Encoding]::UTF8)
    Remove-Item $src3
    Write-Host "  Moved: KhachHangView.java -> khachhang/" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== CLEANUP: git add toan bo thay doi ===" -ForegroundColor Yellow

# Stage tat ca: ca file moi (untracked) va deletion (D)
git -C 'd:\Clone' add . 2>&1 | Out-Null
git -C 'd:\Clone' add -u . 2>&1 | Out-Null
Write-Host "  git add . done" -ForegroundColor Green

Write-Host ""
Write-Host "=== CLEANUP: Cap nhat import cho KhachHangPresenter + KhachHangViewFXMLController ===" -ForegroundColor Yellow

$allJava = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse
foreach ($jf in $allJava) {
    $c = [System.IO.File]::ReadAllText($jf.FullName, [System.Text.Encoding]::UTF8)
    $orig = $c
    $c = $c.Replace('import com.bakery.presenters.KhachHangPresenter;', 'import com.bakery.presenters.khachhang.KhachHangPresenter;')
    $c = $c.Replace('import com.bakery.presenters.customer.KhachHangPresenter;', 'import com.bakery.presenters.khachhang.KhachHangPresenter;')
    $c = $c.Replace('import com.bakery.views.controllers.KhachHangViewFXMLController;', 'import com.bakery.views.controllers.khachhang.KhachHangViewFXMLController;')
    $c = $c.Replace('import com.bakery.views.controllers.customer.KhachHangViewFXMLController;', 'import com.bakery.views.controllers.khachhang.KhachHangViewFXMLController;')
    $c = $c.Replace('import com.bakery.views.interfaces.KhachHangView;', 'import com.bakery.views.interfaces.khachhang.KhachHangView;')
    # Fix presenters/customer imports to khachhang
    $c = $c.Replace('import com.bakery.presenters.customer.CustomerDeletedPresenter;', 'import com.bakery.presenters.khachhang.CustomerDeletedPresenter;')
    $c = $c.Replace('import com.bakery.presenters.customer.CustomerFormPresenter;', 'import com.bakery.presenters.khachhang.CustomerFormPresenter;')
    $c = $c.Replace('import com.bakery.presenters.customer.CustomerInfoPresenter;', 'import com.bakery.presenters.khachhang.CustomerInfoPresenter;')
    if ($c -ne $orig) {
        [System.IO.File]::WriteAllText($jf.FullName, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  imports fixed: $($jf.Name)" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "=== FINAL STRUCTURE ===" -ForegroundColor Cyan

@('presenters','services') | ForEach-Object {
    Write-Host "  $_/: " -NoNewline
    $dirs = Get-ChildItem "$srcBase\$_" -Directory | Select-Object -ExpandProperty Name
    Write-Host ($dirs -join ', ')
}
@('model/dao','model/dto','views/controllers','views/interfaces') | ForEach-Object {
    Write-Host "  $_/: " -NoNewline
    $dirs = Get-ChildItem "$srcBase\$_" -Directory | Select-Object -ExpandProperty Name
    Write-Host ($dirs -join ', ')
}

Write-Host ""
Write-Host "CLEANUP DONE." -ForegroundColor Green
