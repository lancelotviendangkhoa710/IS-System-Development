$srcBase = 'd:\Clone\src\main\java\com\bakery'
$daoBase = $srcBase + '\model\dao'

$mapping = @{
    # banhang
    'DonHangDAO.java'       = 'banhang'
    'HoaDonDAO.java'        = 'banhang'
    'PhuongThucTTDAO.java'  = 'banhang'
    # kho
    'SanPhamDAO.java'       = 'kho'
    'DanhMucSPDAO.java'     = 'kho'
    'NguyenLieuDAO.java'    = 'kho'
    'NhaCungCapDAO.java'    = 'kho'
    'CotBanhDAO.java'       = 'kho'
    'KichCoBanhDAO.java'    = 'kho'
    'KieuTrangTriDAO.java'  = 'kho'
    'NhanBanhDAO.java'      = 'kho'
    'DonViTinhDAO.java'     = 'kho'
    # nhansu
    'NhanVienDAO.java'      = 'nhansu'
    'VaiTroDAO.java'        = 'nhansu'
    'PhanQuyenDAO.java'     = 'nhansu'
    # baocao
    'ThongKeDAO.java'       = 'baocao'
    'BangDieuKhienDAO.java' = 'baocao'
    # khachhang
    'KhachHangDAO.java'     = 'khachhang'
    'HangThanhVienDAO.java' = 'khachhang'
    # hethong
    'CaLamViecDAO.java'     = 'hethong'
    'DoiSoatDAO.java'       = 'hethong'
    'LoaiThuChiDAO.java'    = 'hethong'
    'PhieuThuChiDAO.java'   = 'hethong'
}

# --- Phase 2a: Tao thu muc con va git mv ---
Write-Host "=== Phase 2a: Tao subfolder + git mv ===" -ForegroundColor Cyan

foreach ($sub in @('banhang','kho','nhansu','baocao','khachhang','hethong')) {
    $path = $daoBase + '\' + $sub
    New-Item -ItemType Directory -Path $path -Force | Out-Null
}

foreach ($file in $mapping.Keys) {
    $sub = $mapping[$file]
    $oldRel = 'src/main/java/com/bakery/model/dao/' + $file
    $newRel = 'src/main/java/com/bakery/model/dao/' + $sub + '/' + $file
    $oldAbs = 'd:\Clone\' + ($oldRel -replace '/', '\')

    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  OK: $file -> $sub/" -ForegroundColor Green
    } else {
        Write-Host "  SKIP (not found): $file" -ForegroundColor Yellow
    }
}

# --- Phase 2b: Cap nhat package declaration ---
Write-Host ""
Write-Host "=== Phase 2b: Cap nhat package declaration ===" -ForegroundColor Cyan

foreach ($file in $mapping.Keys) {
    $sub = $mapping[$file]
    $filePath = $daoBase + '\' + $sub + '\' + $file

    if (Test-Path $filePath) {
        $content = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)
        $newPackage = 'package com.bakery.model.dao.' + $sub + ';'
        $updated = $content -replace 'package com\.bakery\.model\.dao;', $newPackage
        if ($updated -ne $content) {
            [System.IO.File]::WriteAllText($filePath, $updated, [System.Text.Encoding]::UTF8)
            Write-Host "  package updated: $file" -ForegroundColor Green
        } else {
            Write-Host "  no match: $file" -ForegroundColor Yellow
        }
    }
}

# --- Phase 2c: Cap nhat import toan bo project ---
Write-Host ""
Write-Host "=== Phase 2c: Cap nhat import toan bo project ===" -ForegroundColor Cyan

$allJava = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse

foreach ($javaFile in $allJava) {
    $content = [System.IO.File]::ReadAllText($javaFile.FullName, [System.Text.Encoding]::UTF8)
    $original = $content

    foreach ($file in $mapping.Keys) {
        $className = [System.IO.Path]::GetFileNameWithoutExtension($file)
        $sub = $mapping[$file]
        $oldImport = 'import com.bakery.model.dao.' + $className + ';'
        $newImport = 'import com.bakery.model.dao.' + $sub + '.' + $className + ';'
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
Write-Host "Phase 2 DONE - DAO layer restructured." -ForegroundColor Cyan
