$srcBase = 'd:\Clone\src\main\java\com\bakery'
$fxmlBase = 'd:\Clone\src\main\resources\fxml'

# =====================================================================
# BẢNG RENAME: OldName -> NewName (chi phan chinh thay doi)
# =====================================================================
$renameMap = @(
    # Presenters
    @{ Old='CustomerDeletedPresenter';  New='KhachHangDaXoaPresenter';      Dir='presenters\khachhang' }
    @{ Old='CustomerFormPresenter';     New='FormKhachHangPresenter';        Dir='presenters\khachhang' }
    @{ Old='CustomerInfoPresenter';     New='ThongTinKhachHangPresenter';    Dir='presenters\khachhang' }
    @{ Old='CustomerTierPresenter';     New='HangThanhVienPresenter';        Dir='presenters\khachhang' }
    # Services
    @{ Old='CustomerTierService';       New='HangThanhVienService';          Dir='services\khachhang' }
    # Controllers
    @{ Old='CustomerDeletedViewFXMLController'; New='KhachHangDaXoaViewFXMLController';     Dir='views\controllers\khachhang' }
    @{ Old='CustomerInfoViewFXMLController';    New='ThongTinKhachHangViewFXMLController';   Dir='views\controllers\khachhang' }
    @{ Old='MembershipTierController';          New='HangThanhVienController';               Dir='views\controllers\khachhang' }
    # Interfaces / View classes
    @{ Old='CustomerAddView';           New='ThemKhachHangView';            Dir='views\interfaces\khachhang' }
    @{ Old='CustomerDeletedView';       New='KhachHangDaXoaView';           Dir='views\interfaces\khachhang' }
    @{ Old='CustomerInfoView';          New='ThongTinKhachHangView';        Dir='views\interfaces\khachhang' }
    @{ Old='CustomerUpdateView';        New='CapNhatKhachHangView';         Dir='views\interfaces\khachhang' }
    @{ Old='MembershipTierView';        New='HangThanhVienView';            Dir='views\interfaces\khachhang' }
)

# =====================================================================
# STEP 1: git mv + doi ten class/package trong file
# =====================================================================
Write-Host "=== STEP 1: git mv + update class declaration ===" -ForegroundColor Cyan

foreach ($r in $renameMap) {
    $oldFile = $r.Old + '.java'
    $newFile = $r.New + '.java'
    $dir = $r.Dir
    $oldAbs = "$srcBase\$dir\$oldFile"
    $newAbs = "$srcBase\$dir\$newFile"
    $oldRel = ('src/main/java/com/bakery/' + ($dir -replace '\\', '/') + '/' + $oldFile)
    $newRel = ('src/main/java/com/bakery/' + ($dir -replace '\\', '/') + '/' + $newFile)

    if (Test-Path $oldAbs) {
        git -C 'd:\Clone' mv $oldRel $newRel 2>&1 | Out-Null
        Write-Host "  mv: $($r.Old) -> $($r.New)" -ForegroundColor Green

        if (Test-Path $newAbs) {
            $c = [System.IO.File]::ReadAllText($newAbs, [System.Text.Encoding]::UTF8)
            # Doi ten class
            $c = $c -replace "\bclass $($r.Old)\b", "class $($r.New)"
            $c = $c -replace "\binterface $($r.Old)\b", "interface $($r.New)"
            [System.IO.File]::WriteAllText($newAbs, $c, [System.Text.Encoding]::UTF8)
        }
    } else {
        Write-Host "  SKIP (not found): $oldFile" -ForegroundColor Yellow
    }
}

# =====================================================================
# STEP 2: Merge CustomerService (235 lines) -> KhachHangService
# Xoa KhachHangService cu (39 lines), CustomerService doi ten thanh KhachHangService
# =====================================================================
Write-Host ""
Write-Host "=== STEP 2: Merge CustomerService -> KhachHangService ===" -ForegroundColor Cyan

$oldKH   = "$srcBase\services\khachhang\KhachHangService.java"
$oldCust = "$srcBase\services\khachhang\CustomerService.java"
$newKH   = $oldKH

if ((Test-Path $oldKH) -and (Test-Path $oldCust)) {
    # Xoa file KhachHangService cu (it chuc nang hon)
    git -C 'd:\Clone' rm 'src/main/java/com/bakery/services/khachhang/KhachHangService.java' 2>&1 | Out-Null
    Remove-Item $oldKH -Force
    Write-Host "  Deleted old KhachHangService.java (39 lines)" -ForegroundColor Red

    # Doi CustomerService.java thanh KhachHangService.java
    git -C 'd:\Clone' mv 'src/main/java/com/bakery/services/khachhang/CustomerService.java' `
                         'src/main/java/com/bakery/services/khachhang/KhachHangService.java' 2>&1 | Out-Null

    $c = [System.IO.File]::ReadAllText($oldCust, [System.Text.Encoding]::UTF8)
    if (Test-Path $oldCust) { # gio la KhachHangService.java
        $c = $c -replace '\bclass CustomerService\b', 'class KhachHangService'
        [System.IO.File]::WriteAllText($oldCust, $c, [System.Text.Encoding]::UTF8)
    }
    # Git mv doi file vat ly nen can doc tu vi tri moi
    $c2 = [System.IO.File]::ReadAllText($newKH, [System.Text.Encoding]::UTF8)
    $c2 = $c2 -replace '\bclass CustomerService\b', 'class KhachHangService'
    [System.IO.File]::WriteAllText($newKH, $c2, [System.Text.Encoding]::UTF8)
    Write-Host "  CustomerService.java -> KhachHangService.java (235 lines kept)" -ForegroundColor Green
}

# =====================================================================
# STEP 3: Cap nhat import + class reference trong TOAN BO project
# =====================================================================
Write-Host ""
Write-Host "=== STEP 3: Update imports + class refs across project ===" -ForegroundColor Cyan

# Build full replace table (old -> new)
$replaceTable = @{}
foreach ($r in $renameMap) {
    $replaceTable[$r.Old] = $r.New
}
$replaceTable['CustomerService'] = 'KhachHangService'

$allJava = Get-ChildItem -Path $srcBase -Filter '*.java' -Recurse
foreach ($jf in $allJava) {
    $c = [System.IO.File]::ReadAllText($jf.FullName, [System.Text.Encoding]::UTF8)
    $orig = $c
    foreach ($old in $replaceTable.Keys) {
        $new = $replaceTable[$old]
        # Replace import statements
        $c = $c.Replace("import com.bakery.services.khachhang.$old;", "import com.bakery.services.khachhang.$new;")
        $c = $c.Replace("import com.bakery.presenters.khachhang.$old;", "import com.bakery.presenters.khachhang.$new;")
        $c = $c.Replace("import com.bakery.views.controllers.khachhang.$old;", "import com.bakery.views.controllers.khachhang.$new;")
        $c = $c.Replace("import com.bakery.views.interfaces.khachhang.$old;", "import com.bakery.views.interfaces.khachhang.$new;")
        # Replace class references (type usage, instantiation)
        $c = $c -replace "\b$old\b", $new
    }
    if ($c -ne $orig) {
        [System.IO.File]::WriteAllText($jf.FullName, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  refs updated: $($jf.Name)" -ForegroundColor Green
    }
}

# =====================================================================
# STEP 4: Cap nhat FXML files (fx:controller attribute)
# =====================================================================
Write-Host ""
Write-Host "=== STEP 4: Update FXML fx:controller ===" -ForegroundColor Cyan

$allFxml = Get-ChildItem -Path $fxmlBase -Filter '*.fxml' -Recurse -ErrorAction SilentlyContinue
foreach ($fxml in $allFxml) {
    $c = [System.IO.File]::ReadAllText($fxml.FullName, [System.Text.Encoding]::UTF8)
    $orig = $c
    foreach ($old in $replaceTable.Keys) {
        $new = $replaceTable[$old]
        $c = $c.Replace(".$old`"", ".$new`"")
    }
    if ($c -ne $orig) {
        [System.IO.File]::WriteAllText($fxml.FullName, $c, [System.Text.Encoding]::UTF8)
        Write-Host "  fxml updated: $($fxml.Name)" -ForegroundColor Green
    }
}

# =====================================================================
# STEP 5: Commit
# =====================================================================
git -C 'd:\Clone' add .
git -C 'd:\Clone' commit -m "refactor: doi ten class Customer*/Membership* sang Tieng Viet theo naming-convention - KhachHangDaXoa, HangThanhVien, ThongTinKhachHang, v.v. - merge CustomerService (235 lines) vao KhachHangService"

Write-Host ""
Write-Host "DONE - Naming convention applied." -ForegroundColor Green
