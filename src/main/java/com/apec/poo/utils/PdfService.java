package com.apec.poo.utils;

import com.apec.poo.entities.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@ApplicationScoped
public class PdfService {

    public String generatePdf(String filePath, List<Transaction> transaction) {
        try {
            Document document = new Document(PageSize.B3.rotate()); // PDF en horizontal
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // === 1. HEADER (Encabezado) ===
            Font headerFont = new Font(Font.HELVETICA, 24, Font.BOLD);
            Paragraph header = new Paragraph("Transaction Report", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            // Espaciado después del encabezado
            document.add(new Paragraph("\n"));

            // === 2. TABLA CON 9 COLUMNAS ===
            PdfPTable table = new PdfPTable(9); // 9 columnas
            table.setWidthPercentage(100); // La tabla ocupa el 100% del ancho del PDF
            table.setSpacingBefore(10f);   // Espaciado antes de la tabla
            table.setSpacingAfter(10f);    // Espaciado después de la tabla
            table.setWidths(new float[]{3, 3, 3, 3, 3, 3, 3, 3, 3}); // Columnas iguales

            // Encabezados de la tabla
            String[] headers = {"Client name", "Product name", "Product code", "Quantity available", "Registration date",
                    "Price", "Total quantity bought", "Total price", "Transaction date"};
            for (String col : headers) {
                table.addCell(getHeaderCell(col));
            }

           List<String[]> content = transaction.stream().map(t -> new String[]{
                   t.getClient().getName() + " " + t.getClient().getLastName(),
                    t.getProduct().getName(),
                    t.getProduct().getCode(),
                    String.valueOf(t.getProduct().getQuantity()),
                    t.getProduct().getRegistryDate().toString(),
                    t.getProduct().getPrice().toString(),
                    t.getQuantityTransaction().toString(),
                    t.getTotalPrice().toString(),
                    t.getTransactionDate().toString()
            }).toList();

            for (String[] row : content) {
                for (String cell : row) {
                    table.addCell(getContentCell(cell));
                }
            }

            // Agregar la tabla al documento
            document.add(table);
            document.close();

            return "The PDF file has been generated in: " + filePath;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return "Error trying to generate the PDF file.";
        }
    }




    private PdfPCell getHeaderCell(String text) {
        Font boldFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, boldFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }


    private PdfPCell getContentCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.HELVETICA, 15)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }
}

