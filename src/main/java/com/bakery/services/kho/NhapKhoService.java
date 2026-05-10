package com.bakery.services.kho;

import com.bakery.model.dto.kho.CTPhieuNhapDTO;
import com.bakery.services.BaseService;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service xử lý nhập kho từ file JSON / CSV.
 * Chứa toàn bộ logic parse + validate — không biết gì về UI hay DAO.
 */
public class NhapKhoService extends BaseService {

    // =========================================================
    // 1. PARSE FILE
    // =========================================================

    /**
     * Đọc file JSON và trả về danh sách lô hàng.
     * Format: [{ maNL, tenNL, xuatXu, maDVT, soLuong, donGia, ngaySanXuat, hanSuDung }, ...]
     */
    public List<CTPhieuNhapDTO> docFileJson(File file) throws Exception {
        String content = docNoiDungFile(file);
        List<CTPhieuNhapDTO> result = new ArrayList<>();

        List<Map<String, String>> objects = parseJsonArray(content);
        for (Map<String, String> obj : objects) {
            CTPhieuNhapDTO dto = new CTPhieuNhapDTO();
            dto.setMaNL(toInt(obj.get("maNL")));
            dto.setTenNL(nvl(obj.get("tenNL")));
            dto.setMaVachLo(nvl(obj.get("xuatXu")));   // xuatXu truyền tạm qua maVachLo
            dto.setSoLuong(toDouble(obj.get("soLuong")));
            dto.setDonGia(BigDecimal.valueOf(toDouble(obj.get("donGia"))));
            dto.setNgaySanXuat(parseNgay(obj.get("ngaySanXuat")));
            dto.setHanSuDung(parseNgay(obj.get("hanSuDung")));
            result.add(dto);
        }
        return result;
    }

    /**
     * Đọc file CSV và trả về danh sách lô hàng.
     * Dòng 1 là header: maNL,tenNL,xuatXu,maDVT,soLuong,donGia,ngaySanXuat,hanSuDung
     * Hỗ trợ UTF-8 BOM (Excel export).
     */
    public List<CTPhieuNhapDTO> docFileCsv(File file) throws Exception {
        List<CTPhieuNhapDTO> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String dongDau = br.readLine();
            if (dongDau == null) throw new Exception("File CSV rỗng.");
            dongDau = dongDau.replace("\uFEFF", ""); // bỏ BOM

            String[] headers = dongDau.split(",");
            int idxMaNL = timCot(headers, "maNL");
            int idxTenNL = timCot(headers, "tenNL");
            int idxXX    = timCot(headers, "xuatXu");
            int idxSL    = timCot(headers, "soLuong");
            int idxDG    = timCot(headers, "donGia");
            int idxNSX   = timCot(headers, "ngaySanXuat");
            int idxHSD   = timCot(headers, "hanSuDung");

            String dong;
            while ((dong = br.readLine()) != null) {
                if (dong.trim().isEmpty()) continue;
                String[] cols = dong.split(",", -1);

                CTPhieuNhapDTO dto = new CTPhieuNhapDTO();
                dto.setMaNL(layIntCot(cols, idxMaNL));
                dto.setTenNL(layChuoiCot(cols, idxTenNL));
                dto.setMaVachLo(layChuoiCot(cols, idxXX));
                dto.setSoLuong(layDoubleCot(cols, idxSL));
                dto.setDonGia(BigDecimal.valueOf(layDoubleCot(cols, idxDG)));
                dto.setNgaySanXuat(parseNgay(layChuoiCot(cols, idxNSX)));
                dto.setHanSuDung(parseNgay(layChuoiCot(cols, idxHSD)));
                result.add(dto);
            }
        }
        return result;
    }

    // =========================================================
    // 2. VALIDATE
    // =========================================================

    /**
     * Validate toàn bộ danh sách lô hàng.
     * @return danh sách thông báo lỗi (rỗng = hợp lệ)
     */
    public List<String> validate(List<CTPhieuNhapDTO> danhSach) {
        List<String> loi = new ArrayList<>();
        if (danhSach.isEmpty()) {
            loi.add("File không có dữ liệu hợp lệ.");
            return loi;
        }
        LocalDate homNay = LocalDate.now();
        for (int i = 0; i < danhSach.size(); i++) {
            int dong = i + 1;
            CTPhieuNhapDTO dto = danhSach.get(i);

            // V1: NL mới phải có tên
            if (dto.getMaNL() == 0 && dto.getTenNL().trim().isEmpty()) {
                loi.add("Dòng " + dong + ": Tên nguyên liệu không được trống khi thêm mới.");
            }
            // V2: Số lượng > 0
            if (dto.getSoLuong() <= 0) {
                loi.add("Dòng " + dong + ": Số lượng phải lớn hơn 0 (hiện tại: " + dto.getSoLuong() + ").");
            }
            // V3: Đơn giá > 0
            if (dto.getDonGia() == null || dto.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
                loi.add("Dòng " + dong + ": Đơn giá phải lớn hơn 0.");
            }
            // V4: HSD không quá khứ
            if (dto.getHanSuDung() != null && dto.getHanSuDung().isBefore(homNay)) {
                loi.add("Dòng " + dong + ": Hạn sử dụng " + dto.getHanSuDung() + " đã quá hạn.");
            }
        }
        return loi;
    }

    // =========================================================
    // 3. BUILD JSON PAYLOAD
    // =========================================================

    /**
     * Chuyển List<CTPhieuNhapDTO> thành JSON string để gọi PROC_TAOPHIEUNHAPKHO.
     * xuatXu được lấy từ maVachLo.
     */
    public String buildJsonPayload(List<CTPhieuNhapDTO> danhSach) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < danhSach.size(); i++) {
            CTPhieuNhapDTO ct = danhSach.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                .append("\"maNL\":").append(ct.getMaNL()).append(",")
                .append("\"tenNL\":\"").append(thoatJson(ct.getTenNL())).append("\",")
                .append("\"xuatXu\":\"").append(thoatJson(ct.getMaVachLo())).append("\",")
                .append("\"maDVT\":1,")
                .append("\"soLuong\":").append(ct.getSoLuong()).append(",")
                .append("\"donGia\":").append(ct.getDonGia() != null ? ct.getDonGia().toPlainString() : "0").append(",")
                .append("\"ngaySanXuat\":\"").append(ct.getNgaySanXuat() != null ? ct.getNgaySanXuat() : "").append("\",")
                .append("\"hanSuDung\":\"").append(ct.getHanSuDung() != null ? ct.getHanSuDung() : "").append("\"")
                .append("}");
        }
        json.append("]");
        return json.toString();
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private String docNoiDungFile(File file) throws Exception {
        try (InputStream is = new FileInputStream(file)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8).replace("\uFEFF", "");
        }
    }

    /**
     * Parse mảng JSON đơn giản thành List<Map<key, value>>.
     * Chỉ hỗ trợ flat object (không lồng nhau) — đủ cho format nhập kho.
     */
    private List<Map<String, String>> parseJsonArray(String json) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        Pattern blockPattern = Pattern.compile("\\{([^}]+)\\}");
        Pattern pairPattern  = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(?:\"([^\"]*)\"|([\\d.\\-]+))");

        Matcher blockMatcher = blockPattern.matcher(json);
        while (blockMatcher.find()) {
            Map<String, String> map = new HashMap<>();
            Matcher pairMatcher = pairPattern.matcher(blockMatcher.group(1));
            while (pairMatcher.find()) {
                // group 2 = string value, group 3 = numeric value
                String val = pairMatcher.group(2) != null ? pairMatcher.group(2) : pairMatcher.group(3);
                map.put(pairMatcher.group(1), val);
            }
            if (!map.isEmpty()) result.add(map);
        }
        if (result.isEmpty() && json.trim().startsWith("[")) {
            throw new Exception("Không thể parse file JSON. Kiểm tra định dạng file.");
        }
        return result;
    }

    private int timCot(String[] headers, String ten) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(ten)) return i;
        }
        return -1;
    }

    private LocalDate parseNgay(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return LocalDate.parse(s.trim()); } catch (Exception e) { return null; }
    }

    private String thoatJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String nvl(String s) { return s != null ? s : ""; }
    private int toInt(String s) {
        try { return s != null ? Integer.parseInt(s.trim()) : 0; } catch (Exception e) { return 0; }
    }
    private double toDouble(String s) {
        try { return s != null ? Double.parseDouble(s.trim()) : 0; } catch (Exception e) { return 0; }
    }

    // --- CSV helpers ---
    private int layIntCot(String[] cols, int idx) {
        if (idx < 0 || idx >= cols.length) return 0;
        try { return Integer.parseInt(cols[idx].trim()); } catch (Exception e) { return 0; }
    }
    private double layDoubleCot(String[] cols, int idx) {
        if (idx < 0 || idx >= cols.length) return 0;
        try { return Double.parseDouble(cols[idx].trim()); } catch (Exception e) { return 0; }
    }
    private String layChuoiCot(String[] cols, int idx) {
        if (idx < 0 || idx >= cols.length) return "";
        return cols[idx].trim();
    }
}
