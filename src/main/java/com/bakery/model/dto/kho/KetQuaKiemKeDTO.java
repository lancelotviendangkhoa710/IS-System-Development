package com.bakery.model.dto.kho;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper kết quả lập báo cáo kiểm kê phiếu nhập kho.
 * Dùng để demo Phantom Read: so sánh soPhieuDaDem (COUNT phase 1)
 * với danhSachPhieu.size() (cursor phase 3 sau delay).
 * Nếu khác nhau → phantom read xảy ra.
 */
public class KetQuaKiemKeDTO {

    /** Số phiếu đếm được ở Phase 1 (trước delay). */
    private int soPhieuDaDem;

    /** Danh sách phiếu nhập đọc được ở Phase 3 (sau delay). */
    private List<PhieuNhapKhoDTO> danhSachPhieu;

    public KetQuaKiemKeDTO() {
        this.danhSachPhieu = new ArrayList<>();
    }

    public KetQuaKiemKeDTO(int soPhieuDaDem, List<PhieuNhapKhoDTO> danhSachPhieu) {
        this.soPhieuDaDem  = soPhieuDaDem;
        this.danhSachPhieu = danhSachPhieu != null ? danhSachPhieu : new ArrayList<>();
    }

    public int getSoPhieuDaDem() { return soPhieuDaDem; }
    public void setSoPhieuDaDem(int soPhieuDaDem) { this.soPhieuDaDem = soPhieuDaDem; }

    public List<PhieuNhapKhoDTO> getDanhSachPhieu() { return danhSachPhieu; }
    public void setDanhSachPhieu(List<PhieuNhapKhoDTO> danhSachPhieu) {
        this.danhSachPhieu = danhSachPhieu != null ? danhSachPhieu : new ArrayList<>();
    }

    /**
     * Kiểm tra có phantom read không.
     * @return true nếu số phiếu đếm trước ≠ số dòng cursor trả về
     */
    public boolean coPhantomRead() {
        return soPhieuDaDem != danhSachPhieu.size();
    }
}
