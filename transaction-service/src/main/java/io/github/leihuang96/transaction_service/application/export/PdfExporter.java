package io.github.leihuang96.transaction_service.application.export;

import io.github.leihuang96.transaction_service.infrastructure.repository.entity.TransactionEntity;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.util.List;

@Component
public class PdfExporter {
    public void exportToPdf(List<TransactionEntity> transactions, String filePath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        document.add(new Paragraph("Transaction Records"));
        for (TransactionEntity transaction : transactions) {
            document.add(new Paragraph(transaction.toString()));
        }
        document.close();
    }
}