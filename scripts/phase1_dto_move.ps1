$srcBase = 'd:\Clone\src\main\java\com\bakery'
$dtoBase = $srcBase + '\model\dto'

$mapping = @{
    'DonDatHangDTO.java'                 = 'banhang'
    'CTDonHangDTO.java'                  = 'banhang'
    'CTDonTuyChinhDTO.java'              = 'banhang'
    'HoaDonDTO.java'                     = 'banhang'
    'PhuongThucTTDTO.java'               = 'banhang'
    'TrangThaiDonDTO.java'               = 'banhang'
    'YeuCauTaoDonHangDTO.java'           = 'banhang'
    'YeuCauChiTietDonHangDTO.java'       = 'banhang'
    'YeuCauChiTietDonTuyChinhDTO.java'   = 'banhang'
    'LichSuDonHangDTO.java'              = 'banhang'
    'SanPhamDTO.java'                    = 'kho'
    'DanhMucSPDTO.java'                  = 'kho'
    'NguyenLieuDTO.java'                 = 'kho'
    'NhaCungCapDTO.java'                 = 'kho'
    'CongThucDTO.java'                   = 'kho'
    'DonViTinhDTO.java'                  = 'kho'
    'NangLucSanXuatDTO.java'             = 'kho'
    'CTPhieuNhapDTO.java'                = 'kho'
    'CTPhieuXuatNLDTO.java'              = 'kho'
    'CTPhieuXuatTPDTO.java'              = 'kho'
    'PhieuNhapKhoDTO.java'               = 'kho'
    'PhieuXuatKhoDTO.java'               = 'kho'
    'CotBanhDTO.java'                    = 'kho'
    'KichCoBanhDTO.java'                 = 'kho'
    'KieuTrangTriDTO.java'               = 'kho'
    'NhanBanhDTO.java'                   = 'kho'
    'NhanVienDTO.java'                   = 'nhansu'
    'VaiTroDTO.java'                     = 'nhansu'
    'ChucNangDTO.java'                   = 'nhansu'
    'VaiTroChucNangDTO.java'             = 'nhansu'
    'TopSanPhamDTO.java'                 = 'baocao'
    'BangDieuKhienKPIDTO.java'           = 'baocao'
    'KhachHangDTO.java'                  = 'khachhang'
    'HangThanhVienDTO.java'              = 'khachhang'
    'CaLamViecDTO.java'                  = 'hethong'
    'DoiSoatDTO.java'                    = 'hethong'
    'DoiSoatInfoDTO.java'                = 'hethong'
    'LoaiThuChiDTO.java'                 = 'hethong'
    'PhieuThuChiDTO.java'                = 'hethong'
    'ModuleDef.java'                     = 'hethong'
}

# --- Phase 1a: Tao thu muc con va git mv ---
Write-Host "=== Phase 1a: Tao subfolder + git mv ===" -ForegroundColor Cyan

foreach ($sub in @('banhang','kho','nhansu','baocao','khachhang','hethong')) {
    $path = $dtoBase + '\' + $sub
    New-Item -ItemType Directory -Path $path -Force | Out-Null
}

foreach ($file in $mapping.Keys) {
    $sub = $mapping[$file]
    $oldRel = 'src/main/java/com/bakery/model/dto/' + $file
    $newRel = 'src/main/java/com/bakery/model/dto/' + $sub + '/' + $file
    $oldAbs = 'd:\Clone\' + ($oldRel -replace '/', '\')

    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  OK: $file -> $sub/" -ForegroundColor Green
    } else {
        Write-Host "  SKIP (not found): $file" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "=== Phase 1b: Cap nhat package declaration ===" -ForegroundColor Cyan

foreach ($file in $mapping.Keys) {
    $sub = $mapping[$file]
    $filePath = $dtoBase + '\' + $sub + '\' + $file

    if (Test-Path $filePath) {
        $content = [System.IO.File]::ReadAllText($filePath, [System.Text.Encoding]::UTF8)
        $newPackage = 'package com.bakery.model.dto.' + $sub + ';'
        $updated = $content -replace 'package com\.bakery\.model\.dto;', $newPackage
        if ($updated -ne $content) {
            [System.IO.File]::WriteAllText($filePath, $updated, [System.Text.Encoding]::UTF8)
            Write-Host "  package updated: $file" -ForegroundColor Green
        } else {
            Write-Host "  package already ok or not matched: $file" -ForegroundColor Yellow
        }
    }
}

Write-Host ""
Write-Host "=== Phase 1c: Cap nhat import toan bo project ===" -ForegroundColor Cyan

$allJava = Get-ChildItem -Path ($srcBase) -Filter '*.java' -Recurse

foreach ($javaFile in $allJava) {
    $content = [System.IO.File]::ReadAllText($javaFile.FullName, [System.Text.Encoding]::UTF8)
    $original = $content
    $changed = $false

    foreach ($file in $mapping.Keys) {
        $className = [System.IO.Path]::GetFileNameWithoutExtension($file)
        $sub = $mapping[$file]
        $oldImport = 'import com.bakery.model.dto.' + $className + ';'
        $newImport = 'import com.bakery.model.dto.' + $sub + '.' + $className + ';'
        if ($content.Contains($oldImport)) {
            $content = $content.Replace($oldImport, $newImport)
            $changed = $true
        }
    }

    if ($changed) {
        [System.IO.File]::WriteAllText($javaFile.FullName, $content, [System.Text.Encoding]::UTF8)
        Write-Host "  imports updated: $($javaFile.Name)" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "Phase 1 DONE - DTO layer restructured." -ForegroundColor Cyan
