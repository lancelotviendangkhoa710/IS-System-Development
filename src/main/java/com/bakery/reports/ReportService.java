package com.bakery.reports;

import com.bakery.dao.HoaDonDAO;
import com.bakery.dto.HoaDonDTO;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ReportService {
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String FONT_ALIAS = "report-vietnamese";

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();

    public RevenueReportResult exportRevenueReportPdf(LocalDate fromDate, LocalDate toDate, Path outputPdf) {
        Objects.requireNonNull(fromDate, "fromDate");
        Objects.requireNonNull(toDate, "toDate");
        Objects.requireNonNull(outputPdf, "outputPdf");

        if (fromDate.isAfter(toDate)) {
            throw new ReportException("Ngay bat dau phai nho hon hoac bang ngay ket thuc.", null);
        }

        LocalDateTime fromInclusive = fromDate.atStartOfDay();
        LocalDateTime toExclusive = toDate.plusDays(1).atStartOfDay();
        List<HoaDonDTO> invoices;
        try {
            invoices = hoaDonDAO.layHoaDonTheoKhoangThoiGian(fromInclusive, toExclusive);
        } catch (RuntimeException e) {
            throw new ReportException("Khong the lay du lieu hoa don: " + safeMessage(e), e);
        }

        try {
            Path parent = outputPdf.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            double totalRevenue = invoices.stream().mapToDouble(HoaDonDTO::getTongTienThanhToan).sum();
            writePdf(invoices, fromDate, toDate, outputPdf);
            return new RevenueReportResult(outputPdf, invoices.size(), totalRevenue);
        } catch (IOException | DocumentException e) {
            throw new ReportException("Khong the tao file PDF: " + outputPdf + ". Ly do: " + safeMessage(e), e);
        }
    }

    private void writePdf(List<HoaDonDTO> invoices, LocalDate fromDate, LocalDate toDate, Path outputPdf)
            throws IOException, DocumentException {
        registerFonts();

        try (FileOutputStream out = new FileOutputStream(outputPdf.toFile())) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FONT_ALIAS, "Identity-H", true, 16, Font.BOLD, Color.BLACK);
            Font subtitleFont = FontFactory.getFont(FONT_ALIAS, "Identity-H", true, 11, Font.NORMAL, Color.BLACK);
            Font normalFont = FontFactory.getFont(FONT_ALIAS, "Identity-H", true, 10, Font.NORMAL, Color.BLACK);
            Font headerFont = FontFactory.getFont(FONT_ALIAS, "Identity-H", true, 10, Font.BOLD, Color.BLACK);

            document.add(new Paragraph("BAO CAO DOANH THU", titleFont));
            document.add(new Paragraph("Khoang ngay: " + DATE_FMT.format(fromDate) + " - " + DATE_FMT.format(toDate), subtitleFont));
            document.add(new Paragraph("Ngay xuat: " + DATE_TIME_FMT.format(LocalDateTime.now()), subtitleFont));
            document.add(new Paragraph(" ", normalFont));

            double totalRevenue = invoices.stream().mapToDouble(HoaDonDTO::getTongTienThanhToan).sum();

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{1f, 2f});
            summaryTable.addCell(summaryCell("So hoa don", headerFont));
            summaryTable.addCell(summaryCell(String.valueOf(invoices.size()), normalFont));
            summaryTable.addCell(summaryCell("Tong doanh thu", headerFont));
            summaryTable.addCell(summaryCell(formatMoney(totalRevenue), normalFont));
            document.add(summaryTable);
            document.add(new Paragraph(" ", normalFont));

            PdfPTable detailTable = new PdfPTable(new float[]{0.7f, 1.2f, 2.2f, 1.2f, 1.2f, 1.5f});
            detailTable.setWidthPercentage(100);
            detailTable.setHeaderRows(1);

            detailTable.addCell(tableHeader("STT", headerFont));
            detailTable.addCell(tableHeader("Ma HD", headerFont));
            detailTable.addCell(tableHeader("Ngay xuat", headerFont));
            detailTable.addCell(tableHeader("Loai HD", headerFont));
            detailTable.addCell(tableHeader("VAT", headerFont));
            detailTable.addCell(tableHeader("Tong tien", headerFont));

            if (invoices.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("Khong co du lieu trong khoang ngay da chon.", normalFont));
                empty.setColspan(6);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                empty.setPadding(8f);
                detailTable.addCell(empty);
            } else {
                int index = 1;
                for (HoaDonDTO invoice : invoices) {
                    detailTable.addCell(tableCell(String.valueOf(index++), normalFont, Element.ALIGN_CENTER));
                    detailTable.addCell(tableCell(String.valueOf(invoice.getMaHD()), normalFont, Element.ALIGN_CENTER));
                    detailTable.addCell(tableCell(formatDateTime(invoice.getNgayXuatHd()), normalFont, Element.ALIGN_LEFT));
                    detailTable.addCell(tableCell(nullToBlank(invoice.getLoaiHD()), normalFont, Element.ALIGN_LEFT));
                    detailTable.addCell(tableCell(formatMoney(invoice.getThueVAT()), normalFont, Element.ALIGN_RIGHT));
                    detailTable.addCell(tableCell(formatMoney(invoice.getTongTienThanhToan()), normalFont, Element.ALIGN_RIGHT));
                }

                PdfPCell totalLabel = new PdfPCell(new Phrase("Tong cong", headerFont));
                totalLabel.setColspan(5);
                totalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalLabel.setPadding(8f);
                detailTable.addCell(totalLabel);
                detailTable.addCell(tableCell(formatMoney(totalRevenue), headerFont, Element.ALIGN_RIGHT));
            }

            document.add(detailTable);
            document.close();
        }
    }

    private static void registerFonts() {
        for (String fontPath : new String[]{
                "C:/Windows/Fonts/arial.ttf",
                "C:/Windows/Fonts/tahoma.ttf",
                "C:/Windows/Fonts/calibri.ttf"
        }) {
            if (new File(fontPath).exists()) {
                FontFactory.register(fontPath, FONT_ALIAS);
                return;
            }
        }
    }

    private static PdfPCell tableHeader(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(7f);
        cell.setBackgroundColor(new Color(235, 235, 235));
        return cell;
    }

    private static PdfPCell tableCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(7f);
        return cell;
    }

    private static PdfPCell summaryCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(7f);
        return cell;
    }

    private static String formatMoney(double value) {
        return String.format(Locale.forLanguageTag("vi-VN"), "%,.0f VND", value);
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? "" : DATE_TIME_FMT.format(value);
    }

    private static String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    private static String safeMessage(Throwable t) {
        String msg = t.getMessage();
        return (msg == null || msg.isBlank()) ? t.getClass().getSimpleName() : msg;
    }
}
