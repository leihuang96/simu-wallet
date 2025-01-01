package io.github.leihuang96.transaction_service.application.export;

import io.github.leihuang96.transaction_service.infrastructure.repository.entity.TransactionEntity;
import org.springframework.stereotype.Component;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

@Component
public class ExcelExporter {
    public void exportToExcel(List<TransactionEntity> transactions, String filePath) throws Exception {
        Workbook workbook = new XSSFWorkbook(); // 创建 Excel 工作簿
        Sheet sheet = workbook.createSheet("Transactions"); // 创建表格

        // Header row
        Row headerRow = sheet.createRow(0); // 创建表头行
        String[] headers = {"ID", "User ID", "Type", "Source Amount", "Source Currency", "Target Amount",
                "Target Currency", "Fee", "Status", "Date"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]); // 设置表头单元格的值
        }

        // Data rows
        int rowIndex = 1;
        for (TransactionEntity transaction : transactions) {
            Row row = sheet.createRow(rowIndex++); // 创建数据行
            row.createCell(0).setCellValue(transaction.getId());
            row.createCell(1).setCellValue(transaction.getUserId());
            row.createCell(2).setCellValue(transaction.getType());
            row.createCell(3).setCellValue(transaction.getSourceAmount().doubleValue());
            row.createCell(4).setCellValue(transaction.getSourceCurrency());
            row.createCell(5).setCellValue(transaction.getTargetAmount() != null ? transaction.getTargetAmount().doubleValue() : 0);
            row.createCell(6).setCellValue(transaction.getTargetCurrency());
            row.createCell(7).setCellValue(transaction.getFee().doubleValue());
            row.createCell(8).setCellValue(transaction.getStatus());
            row.createCell(9).setCellValue(transaction.getInitiatedAt().toString());
        }

        // 保存到文件
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut); // 写入文件
        }
        workbook.close(); // 关闭工作簿
    }
}