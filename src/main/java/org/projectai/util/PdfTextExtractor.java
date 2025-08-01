package org.projectai.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class PdfTextExtractor {

    /**
     * 从MultipartFile中提取PDF文本内容
     * @param file PDF文件
     * @return 提取的文本内容
     * @throws IOException 如果文件读取失败或不是有效的PDF
     */
    public static String extractTextFromPdf(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {
            
            if (document.isEncrypted()) {
                throw new IOException("加密的PDF文档不支持提取文本");
            }
            
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 从字节数组中提取PDF文本内容
     * @param pdfBytes PDF文件字节数组
     * @return 提取的文本内容
     * @throws IOException 如果不是有效的PDF
     */
    public static String extractTextFromPdf(byte[] pdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            if (document.isEncrypted()) {
                throw new IOException("加密的PDF文档不支持提取文本");
            }
            
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}