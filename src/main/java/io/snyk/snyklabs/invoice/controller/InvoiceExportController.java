package io.snyk.snyklabs.invoice.controller;

import com.itextpdf.text.Cell;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Table;
import com.itextpdf.text.pdf.PdfWriter;
import io.snyk.snyklabs.invoice.dto.InvoiceRequestDto;
import io.snyk.snyklabs.invoice.dto.TransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/admin/invoice")
public class InvoiceExportController {

    private static final Logger log = LoggerFactory.getLogger(InvoiceExportController.class);

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportInvoice(@RequestBody InvoiceRequestDto invoiceRequest, HttpServletResponse response) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            
            document.open();
            
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
            
            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            
            document.add(new Paragraph(" "));
            
            Paragraph invoiceNumber = new Paragraph("Invoice #: " + invoiceRequest.getInvoiceNumber(), headerFont);
            document.add(invoiceNumber);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Paragraph generatedDate = new Paragraph("Generated: " + dateFormat.format(new Date()), normalFont);
            document.add(generatedDate);
            
            document.add(new Paragraph(" "));
            
            Paragraph customerInfo = new Paragraph("Customer Information", headerFont);
            document.add(customerInfo);
            
            Paragraph customerName = new Paragraph("Name: " + invoiceRequest.getCustomerName(), normalFont);
            document.add(customerName);
            
            Paragraph customerEmail = new Paragraph("Email: " + invoiceRequest.getCustomerEmail(), normalFont);
            document.add(customerEmail);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            Table table = new Table(5);
            table.setWidth(100);
            table.setPadding(5);
            
            Cell headerCell1 = new Cell(new Phrase("Transaction ID", headerFont));
            headerCell1.setHeader(true);
            table.addCell(headerCell1);
            
            Cell headerCell2 = new Cell(new Phrase("Date", headerFont));
            headerCell2.setHeader(true);
            table.addCell(headerCell2);
            
            Cell headerCell3 = new Cell(new Phrase("Description", headerFont));
            headerCell3.setHeader(true);
            table.addCell(headerCell3);
            
            Cell headerCell4 = new Cell(new Phrase("Amount", headerFont));
            headerCell4.setHeader(true);
            table.addCell(headerCell4);
            
            Cell headerCell5 = new Cell(new Phrase("Customer", headerFont));
            headerCell5.setHeader(true);
            table.addCell(headerCell5);
            
            table.endHeaders();
            
            double totalAmount = 0.0;
            
            if (invoiceRequest.getTransactions() != null) {
                for (TransactionDto transaction : invoiceRequest.getTransactions()) {
                    table.addCell(new Cell(new Phrase(transaction.getTransactionId(), normalFont)));
                    table.addCell(new Cell(new Phrase(transaction.getDate(), normalFont)));
                    table.addCell(new Cell(new Phrase(transaction.getDescription(), normalFont)));
                    table.addCell(new Cell(new Phrase(transaction.getAmount(), normalFont)));
                    table.addCell(new Cell(new Phrase(transaction.getCustomerName(), normalFont)));
                    
                    try {
                        String amountStr = transaction.getAmount().replace("$", "").replace(",", "");
                        totalAmount += Double.parseDouble(amountStr);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid amount format: " + transaction.getAmount());
                    }
                }
            }
            
            document.add(table);
            
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            
            Paragraph total = new Paragraph("Total Amount: $" + String.format("%.2f", totalAmount), headerFont);
            total.setAlignment(Paragraph.ALIGN_RIGHT);
            document.add(total);
            
            document.close();
            writer.close();
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=invoice_" + invoiceRequest.getInvoiceNumber() + ".pdf");
            headers.add("Content-Type", "application/pdf");
            
            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
            
        } catch (DocumentException | IOException e) {
            log.error("Error generating PDF invoice", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
