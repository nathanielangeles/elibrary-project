package com.elibrary.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for adding watermarks to PDF files
 * Uses Apache PDFBox library
 */
public class PDFWatermarkUtil {
    
    /**
     * Add watermark to the first page of a PDF file
     * @param inputFile Original PDF file
     * @param outputFile Output PDF file with watermark
     * @param watermarkText Text to use as watermark (typically LRN)
     * @throws IOException If file operation fails
     */
    public static void addWatermark(File inputFile, File outputFile, String watermarkText) throws IOException {
        PDDocument document = null;
        
        try {
            // Load the PDF document
            document = PDDocument.load(inputFile);
            
            if (document.getNumberOfPages() > 0) {
                // Get the first page
                PDPage firstPage = document.getPage(0);
                
                // Create content stream for the first page
                PDPageContentStream contentStream = new PDPageContentStream(
                    document, firstPage, PDPageContentStream.AppendMode.APPEND, true, true
                );
                
                // Set font and size for watermark
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                
                // Set watermark position (bottom left of the page)
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 30);
                
                // Add watermark text
                String fullWatermark = "Downloaded by LRN: " + watermarkText;
                contentStream.showText(fullWatermark);
                contentStream.endText();
                
                // Close the content stream
                contentStream.close();
            }
            
            // Save the modified document
            document.save(outputFile);
            
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }
    
    /**
     * Add watermark and return the watermarked file
     * @param originalFilePath Path to original PDF
     * @param lrn Student LRN number
     * @param downloadDirectory Directory to save watermarked PDF
     * @return Watermarked PDF file
     * @throws IOException If file operation fails
     */
    public static File createWatermarkedCopy(String originalFilePath, String lrn, String downloadDirectory) 
            throws IOException {
        File originalFile = new File(originalFilePath);
        
        // Create output filename with LRN
        String outputFileName = "watermarked_" + lrn + "_" + originalFile.getName();
        File outputFile = new File(downloadDirectory, outputFileName);
        
        // Add watermark
        addWatermark(originalFile, outputFile, lrn);
        
        return outputFile;
    }
}
