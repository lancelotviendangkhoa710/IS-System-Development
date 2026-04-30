$srcBase = 'd:\Clone\src\main\java\com\bakery'
$svcBase = $srcBase + '\services'

$mapping = @{
    # banhang
    'DonHangService.java'         = 'banhang'
    'ThanhToanService.java'       = 'banhang'
    'QuanLyDonHangService.java'   = 'banhang'
    'TheoDoiDonService.java'      = 'banhang'
    'TuyChinhBanhService.java'    = 'banhang'
    # kho
    'NguyenLieuService.java'      = 'kho'
    'SanPhamService.java'         = 'kho'
    'DanhMucSPService.java'       = 'kho'
    'NhaCungCapService.java'      = 'kho'
    # nhansu
    'NhanVienService.java'        = 'nhansu'
    'PhanQuyenService.java'       = 'nhansu'
    'XacThucService.java'         = 'nhansu'
    # baocao
    'ThongKeService.java'         = 'baocao'
    'BangDieuKhienService.java'   = 'baocao'
    # khachhang
    'CustomerService.java'        = 'khachhang'
    'CustomerTierService.java'    = 'khachhang'
    'KhachHangService.java'       = 'khachhang'
    # hethong
    'CaLamViecService.java'       = 'hethong'
    'DoiSoatService.java'         = 'hethong'
    'SoQuyService.java'           = 'hethong'
}

# --- Phase 3a: Tao thu muc con va git mv ---
Write-Host "=== Phase 3a: Tao subfolder + git mv ===" -ForegroundColor Cyan

foreach ($sub in @('banhang','kho','nhansu','baocao','khachhang','hethong')) {
    $path = $svcBase + '\' + $sub
    New-Item -ItemType Directory -Path $path -Force | Out-Null
}

foreach ($file in $mapping.Keys) {
    $sub = $mapping[$file]
    $oldRel = 'src/main/java/com/bakery/services/' + $file
    $newRel = 'src/main/java/com/bakery/services/' + $sub + '/' + $file
    $oldAbs = 'd:\Clone\' + ($oldRel -replace '/', '\')

    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  OK: $file -> $sub/" -ForegroundColor Green
    } else {
        Write-Host "  SKIP (not found): $file" -ForegroundColor Yellow
    }
}

# --- Phase 3b: Cap nhat package declaration ---
Write-Host ""
Write-Host "=== Phase 3b: Cap nhat package declaration ===" -ForegroundColor Cyan

foreach ($file in $mapping.Keys) {
    $sub = $mapping[$file]
    $filePath = $svcBase + '\' + $sub + '\' + $file

    if (Test-Path $filePath) {
        $content = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)
        $newPackage = 'package com.bakery.services.' + $sub + ';'
        $updated = $content -replace 'package com\.bakery\.services;', $newPackage
        if ($updated -ne $content) {
            [System.IO.File]::WriteAllText($filePath, $updated, [System.Text.Encoding]::UTF8)
            Write-Host "  package updated: $file" -ForegroundColor Green
        } else {
            Write-Host "  no match: $file" -ForegroundColor Yellow
        }
    }
}

# --- Phase 3c: Cap nhat import toan bo project ---
Write-Host ""
Write-Host "=== Phase 3c: Cap nhat import toan bo project ===" -ForegroundColor Cyan

$allJava = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse

foreach ($javaFile in $allJava) {
    $content = [System.IO.File]::ReadAllText($javaFile.FullName, [System.Text.Encoding]::UTF8)
    $original = $content

    foreach ($file in $mapping.Keys) {
        $className = [System.IO.Path]::GetFileNameWithoutExtension($file)
        $sub = $mapping[$file]
        $oldImport = 'import com.bakery.services.' + $className + ';'
        $newImport = 'import com.bakery.services.' + $sub + '.' + $className + ';'
        if ($content.Contains($oldImport)) {
            $content = $content.Replace($oldImport, $newImport)
        }
    }

    if ($content -ne $original) {
        [System.IO.File]::WriteAllText($javaFile.FullName, $content, [System.Text.Encoding]::UTF8)
        Write-Host "  imports updated: $($javaFile.Name)" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "Phase 3 DONE - Services layer restructured." -ForegroundColor Cyan
