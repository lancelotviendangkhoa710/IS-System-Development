package com.bakery.reports;

import java.nio.file.Path;

public record RevenueReportResult(Path outputPdf, int invoiceCount, double totalRevenue) {
}
