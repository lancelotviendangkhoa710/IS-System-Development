$srcBase = 'd:\Clone\src\main\java\com\bakery'
$presBase = $srcBase + '\presenters'

$mapping = @{
    # banhang
    'DonHangPresenter.java'          = 'banhang'
    'SoQuyPresenter.java'            = 'banhang'
    # kho
    'NguyenLieuPresenter.java'       = 'kho'
    'SanPhamPresenter.java'          = 'kho'
    'DanhMucSPPresenter.java'        = 'kho'
    # nhansu
    'DangNhapPresenter.java'         = 'nhansu'
    'DangKyPresenter.java'           = 'nhansu'
    # baocao
    'BangDieuKhienPresenter.java'    = 'baocao'
    # khachhang
    'CustomerTierPresenter.java'     = 'khachhang'
    'CustomerInfoPresenter.java'     = 'khachhang'
    'CustomerDeletedPresenter.java'  = 'khachhang'
    'CustomerFormPresenter.java'     = 'khachhang'
    # hethong
    'MoCaPresenter.java'             = 'hethong'
    'DoiSoatDongCaPresenter.java'    = 'hethong'
    'ManHinhChinhPresenter.java'     = 'hethong'
}

# --- Phase 4a: Tao thu muc con va git mv ---
Write-Host "=== Phase 4a: Tao subfolder + git mv ===" -ForegroundColor Cyan

foreach ($sub in @('banhang','kho','nhansu','baocao','khachhang','hethong')) {
    $path = $presBase + '\' + $sub
    New-Item -ItemType Directory -Path $path -Force | Out-Null
}

foreach ($file in $mapping.Keys) {
    $sub = $mapping[$file]
    $oldRel = 'src/main/java/com/bakery/presenters/' + $file
    $newRel = 'src/main/java/com/bakery/presenters/' + $sub + '/' + $file
    $oldAbs = 'd:\Clone\' + ($oldRel -replace '/', '\')

    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  OK: $file -> $sub/" -ForegroundColor Green
    } else {
        Write-Host "  SKIP (not found): $file" -ForegroundColor Yellow
    }
}

# --- Phase 4b: Cap nhat package declaration ---
Write-Host ""
Write-Host "=== Phase 4b: Cap nhat package declaration ===" -ForegroundColor Cyan

foreach ($file in $mapping.Keys) {
    $sub = $mapping[$file]
    $filePath = $presBase + '\' + $sub + '\' + $file

    if (Test-Path $filePath) {
        $content = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)
        $newPackage = 'package com.bakery.presenters.' + $sub + ';'
        $updated = $content -replace 'package com\.bakery\.presenters;', $newPackage
        if ($updated -ne $content) {
            [System.IO.File]::WriteAllText($filePath, $updated, [System.Text.Encoding]::UTF8)
            Write-Host "  package updated: $file" -ForegroundColor Green
        } else {
            Write-Host "  no match: $file" -ForegroundColor Yellow
        }
    }
}

# --- Phase 4c: Cap nhat import toan bo project ---
Write-Host ""
Write-Host "=== Phase 4c: Cap nhat import toan bo project ===" -ForegroundColor Cyan

$allJava = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse

foreach ($javaFile in $allJava) {
    $content = [System.IO.File]::ReadAllText($javaFile.FullName, [System.Text.Encoding]::UTF8)
    $original = $content

    foreach ($file in $mapping.Keys) {
        $className = [System.IO.Path]::GetFileNameWithoutExtension($file)
        $sub = $mapping[$file]
        $oldImport = 'import com.bakery.presenters.' + $className + ';'
        $newImport = 'import com.bakery.presenters.' + $sub + '.' + $className + ';'
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
Write-Host "Phase 4 DONE - Presenters layer restructured." -ForegroundColor Cyan
