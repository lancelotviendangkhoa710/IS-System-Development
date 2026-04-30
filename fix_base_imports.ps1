$srcBase = 'd:\Clone\src\main\java\com\bakery'

# =================================================================
# FIX 1: Them import BasePresenter cho tat ca Presenter subclass
# =================================================================
Write-Host "=== FIX 1: BasePresenter import ===" -ForegroundColor Cyan

$presFiles = Get-ChildItem "$srcBase\presenters" -Filter '*.java' -Recurse |
    Select-String 'extends BasePresenter' | Select-Object -ExpandProperty Path

foreach ($path in $presFiles) {
    $c = [System.IO.File]::ReadAllText($path, [System.Text.Encoding]::UTF8)
    if (-not $c.Contains('import com.bakery.presenters.BasePresenter;')) {
        $c = $c -replace '(package com\.bakery\.presenters\.[a-z]+;)', "`$1`nimport com.bakery.presenters.BasePresenter;"
        [System.IO.File]::WriteAllText($path, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  Fixed: $([System.IO.Path]::GetFileName($path))" -ForegroundColor Green
    }
}

# =================================================================
# FIX 2: Them import BaseService cho tat ca Service subclass
# =================================================================
Write-Host ""
Write-Host "=== FIX 2: BaseService import ===" -ForegroundColor Cyan

$svcFiles = Get-ChildItem "$srcBase\services" -Filter '*.java' -Recurse |
    Select-String 'extends BaseService' | Select-Object -ExpandProperty Path

foreach ($path in $svcFiles) {
    $c = [System.IO.File]::ReadAllText($path, [System.Text.Encoding]::UTF8)
    if (-not $c.Contains('import com.bakery.services.BaseService;')) {
        $c = $c -replace '(package com\.bakery\.services\.[a-z]+;)', "`$1`nimport com.bakery.services.BaseService;"
        [System.IO.File]::WriteAllText($path, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  Fixed: $([System.IO.Path]::GetFileName($path))" -ForegroundColor Green
    }
}

# =================================================================
# FIX 3: DonHangService - cross-subpackage imports + FQN fix
# =================================================================
Write-Host ""
Write-Host "=== FIX 3: DonHangService cross-subpackage imports ===" -ForegroundColor Cyan

$donHangSvcPath = "$srcBase\services\banhang\DonHangService.java"
if (Test-Path $donHangSvcPath) {
    $c = [System.IO.File]::ReadAllText($donHangSvcPath, [System.Text.Encoding]::UTF8)

    # Them import thieu cho SanPhamService va KhachHangService
    if (-not $c.Contains('import com.bakery.services.kho.SanPhamService;')) {
        $c = $c -replace '(package com\.bakery\.services\.banhang;)', "`$1`nimport com.bakery.services.kho.SanPhamService;"
        Write-Host "  Added: import SanPhamService" -ForegroundColor Green
    }
    if (-not $c.Contains('import com.bakery.services.khachhang.KhachHangService;')) {
        $c = $c -replace '(package com\.bakery\.services\.banhang;)', "`$1`nimport com.bakery.services.khachhang.KhachHangService;"
        Write-Host "  Added: import KhachHangService" -ForegroundColor Green
    }

    # Fix FQN cu: com.bakery.model.dto.YeuCauChiTietDonHangDTO -> dung class name + import
    if ($c.Contains('com.bakery.model.dto.YeuCauChiTietDonHangDTO')) {
        $c = $c.Replace('com.bakery.model.dto.YeuCauChiTietDonHangDTO', 'YeuCauChiTietDonHangDTO')
        if (-not $c.Contains('import com.bakery.model.dto.banhang.YeuCauChiTietDonHangDTO;')) {
            $c = $c -replace '(package com\.bakery\.services\.banhang;)', "`$1`nimport com.bakery.model.dto.banhang.YeuCauChiTietDonHangDTO;"
        }
        Write-Host "  Fixed: YeuCauChiTietDonHangDTO FQN -> short name + import" -ForegroundColor Green
    }

    [System.IO.File]::WriteAllText($donHangSvcPath, $c, [System.Text.Encoding]::UTF8)
    Write-Host "  DonHangService.java saved." -ForegroundColor Green
}

# =================================================================
# FIX 4: Quet toan bo - fix cac FQN cu con sot lai trong toan project
# =================================================================
Write-Host ""
Write-Host "=== FIX 4: Quet FQN cu con sot lai trong project ===" -ForegroundColor Cyan

# Map: FQN cu -> (short name, import moi)
$fqnMap = @{
    'com.bakery.model.dto.YeuCauChiTietDonHangDTO'       = @{ Short = 'YeuCauChiTietDonHangDTO';       Import = 'import com.bakery.model.dto.banhang.YeuCauChiTietDonHangDTO;' }
    'com.bakery.model.dto.YeuCauChiTietDonTuyChinhDTO'   = @{ Short = 'YeuCauChiTietDonTuyChinhDTO';   Import = 'import com.bakery.model.dto.banhang.YeuCauChiTietDonTuyChinhDTO;' }
    'com.bakery.model.dto.YeuCauTaoDonHangDTO'           = @{ Short = 'YeuCauTaoDonHangDTO';           Import = 'import com.bakery.model.dto.banhang.YeuCauTaoDonHangDTO;' }
    'com.bakery.model.dto.DonDatHangDTO'                 = @{ Short = 'DonDatHangDTO';                 Import = 'import com.bakery.model.dto.banhang.DonDatHangDTO;' }
    'com.bakery.model.dto.KhachHangDTO'                  = @{ Short = 'KhachHangDTO';                  Import = 'import com.bakery.model.dto.khachhang.KhachHangDTO;' }
    'com.bakery.model.dto.NhanVienDTO'                   = @{ Short = 'NhanVienDTO';                   Import = 'import com.bakery.model.dto.nhansu.NhanVienDTO;' }
    'com.bakery.model.dto.SanPhamDTO'                    = @{ Short = 'SanPhamDTO';                    Import = 'import com.bakery.model.dto.kho.SanPhamDTO;' }
}

$allJava = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse
foreach ($jf in $allJava) {
    $c = [System.IO.File]::ReadAllText($jf.FullName, [System.Text.Encoding]::UTF8)
    $orig = $c
    foreach ($fqn in $fqnMap.Keys) {
        if ($c.Contains($fqn) -and -not ($c.Contains($fqn + ';'))) {
            # Chi replace neu la FQN trong code (khong phai import)
            $shortName = $fqnMap[$fqn].Short
            $importLine = $fqnMap[$fqn].Import
            $c = $c.Replace($fqn, $shortName)
            if (-not $c.Contains($importLine)) {
                # Them import sau package declaration
                $c = $c -replace '(package com\.bakery\.[a-z.]+;)', "`$1`n$importLine"
            }
        }
    }
    if ($c -ne $orig) {
        [System.IO.File]::WriteAllText($jf.FullName, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  FQN fixed: $($jf.Name)" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "All fixes applied." -ForegroundColor Cyan

# Commit
git -C 'd:\Clone' add .
git -C 'd:\Clone' commit -m "fix: them import BasePresenter/BaseService cho subpackage + fix cross-subpackage service imports trong DonHangService"
Write-Host "Committed." -ForegroundColor Green
