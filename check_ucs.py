import os
import re

agent_use_cases_path = r"d:\Clone\.agents\3_business\agent_use_cases.md"
use_cases_dir = r"d:\Clone\.agents\3_business\use_cases"
controllers_dir = r"d:\Clone\src\main\java\com\bakery\views\controllers"

with open(agent_use_cases_path, "r", encoding="utf-8") as f:
    agent_content = f.read()

ucs = re.findall(r"\| (UC\d{2}) \| (.*?) \|", agent_content)

existing_mds = [f for f in os.listdir(use_cases_dir) if f.endswith(".md")]

controllers = []
for root, _, files in os.walk(controllers_dir):
    for file in files:
        if file.endswith(".java"):
            controllers.append(file)
controllers_str = " ".join(controllers).lower()

status_report = {"hoan_thanh": [], "dang_do": [], "chua_lam": []}

keyword_map = {
    "UC01": ["dangnhap"], "UC02": ["dangxuat", "mainmenu"], "UC03": ["doimatkhau", "taikhoan", "matkhau", "nhansu"],
    "UC04": ["themnhanvien", "nhansu"], "UC05": ["quanlynhanvien", "nhansu"], "UC06": ["nhanvien", "nhansu"],
    "UC07": ["nhanvien", "nhansu"], "UC08": ["phanquyen", "matran"], "UC09": ["lichsuhethong"],
    "UC10": ["khachhang"], "UC11": ["khachhang"], "UC12": ["khachhang"], "UC13": ["khachhang"],
    "UC14": ["lichsumuahang"], "UC15": ["hangthanhvien"],
    "UC16": ["hoadon", "thanhtoan"], "UC17": ["huyhoadon", "lydoxacnhan"], 
    "UC18": ["taodonhang"], "UC19": ["theodoidonhang", "donhang"], "UC20": ["donhang"], "UC21": ["huydon", "hoancoc"],
    "UC22": ["moca"], "UC23": ["doisoat", "dongca"],
    "UC24": ["danhmuc"], "UC25": ["danhmuc"], "UC26": ["danhmuc"],
    "UC27": ["sanpham"], "UC28": ["sanpham"], "UC29": ["sanpham"], "UC30": ["sanpham"],
    "UC31": ["congthuc"], "UC32": ["congthuc"], "UC33": ["congthuc"], "UC34": ["congthuc"],
    "UC35": ["tinhtoan", "dinhluong"], "UC36": ["cauhinhgioihan"],
    "UC37": ["nguyenlieu"], "UC38": ["nguyenlieu"], "UC39": ["nguyenlieu"],
    "UC40": ["nhapkho"], "UC41": ["xuatkho"], "UC42": ["xuathuy"], "UC43": ["canhbao"], 
    "UC44": ["thekho", "kho", "kiemke"], "UC45": ["truyvet", "nguongoc"],
    "UC46": ["nhacungcap"], "UC47": ["nhacungcap"], "UC48": ["nhacungcap"], "UC49": ["nhacungcap"],
    "UC50": ["baocao"], "UC51": ["baocao", "dashboard"], "UC52": ["baocao", "kiemkekho"],
    "UC53": ["soquy", "thuchi"], "UC54": ["soquy", "thuchi"], "UC55": ["soquy", "thuchi"], "UC56": ["soquy", "thuchi"], "UC57": ["soquy", "thuchi"]
}

for uc_id, uc_name in ucs:
    uc_name = uc_name.strip()
    has_md = any(uc_id in md for md in existing_mds)
    keywords = keyword_map.get(uc_id, [])
    has_code = any(kw in controllers_str for kw in keywords)
            
    if has_md and has_code:
        status_report["hoan_thanh"].append(f"{uc_id}: {uc_name}")
    elif has_md or has_code:
        has_str = "Spec" if has_md else "Code"
        status_report["dang_do"].append(f"{uc_id}: {uc_name} (Đã có: {has_str})")
    else:
        status_report["chua_lam"].append(f"{uc_id}: {uc_name}")

print(f"HOÀN THÀNH: {len(status_report['hoan_thanh'])}")
for item in status_report["hoan_thanh"]: print("  - " + item)
print(f"\nDANG DỞ: {len(status_report['dang_do'])}")
for item in status_report["dang_do"]: print("  - " + item)
print(f"\nCHƯA LÀM: {len(status_report['chua_lam'])}")
for item in status_report["chua_lam"]: print("  - " + item)
