package org.projectai.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class FileTextExtractor {
    
    private final PdfTextExtractor pdfExtractor;
    private final OcrTextExtractor ocrExtractor;
    
    public FileTextExtractor() throws IOException {
        this.pdfExtractor = new PdfTextExtractor();
        this.ocrExtractor = new OcrTextExtractor();
    }

    /**
     * 根据文件类型提取文本内容
     */
    public String extractText(MultipartFile file) throws IOException {
        String mimeType = file.getContentType();
        
        try {
            if (mimeType != null) {
                if (mimeType.equals("application/pdf")) {
                    return pdfExtractor.extractTextFromPdf(file);
                } else if (mimeType.startsWith("image/")) {
                    return ocrExtractor.extractTextFromImage(file);
                }
            }
            // 其他类型文件返回空字符串或抛出不支持异常
            throw new UnsupportedOperationException("不支持的文件类型: " + mimeType);
        } catch (Exception e) {
            throw new IOException("文件处理失败: " + e.getMessage(), e);
        }
    }
}