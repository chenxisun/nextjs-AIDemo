package org.projectai.util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.springframework.web.multipart.MultipartFile;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

public class OcrTextExtractor {

    private final Tesseract tesseract;

    public OcrTextExtractor() throws IOException {
        this.tesseract = new Tesseract();
        initializeTesseract();
    }

    private void initializeTesseract() throws IOException {
        // 1. 设置语言包路径（自动下载或使用内置）
        Path tessdataPath = Paths.get(System.getProperty("java.io.tmpdir"), "tessdata");
        if (!Files.exists(tessdataPath)) {
            Files.createDirectories(tessdataPath);
            downloadLanguageData(tessdataPath);
        }

        // 2. 配置Tesseract参数
        tesseract.setDatapath(tessdataPath.toString());
        tesseract.setLanguage("eng+chi_sim"); // 中英文混合
        tesseract.setPageSegMode(6);  // 稀疏文本识别模式
        tesseract.setOcrEngineMode(1); // LSTM引擎
    }

    /**
     * 从图片文件中提取文本
     *
     * @param imageFile 上传的图片文件
     * @return 识别出的文本内容
     */
    public String extractTextFromImage(MultipartFile imageFile) throws IOException, TesseractException {
        // 1. 验证文件类型
        if (!imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("仅支持图片文件");
        }

        // 2. 转换为BufferedImage
        BufferedImage image = ImageIO.read(imageFile.getInputStream());
        if (image == null) {
            throw new IOException("无法读取图片数据");
        }

        // 3. 执行OCR识别
        return tesseract.doOCR(preprocessImage(image));
    }

    /**
     * 图片预处理（增强识别率）
     */
    private BufferedImage preprocessImage(BufferedImage original) {
        // 转换为灰度图
        BufferedImage gray = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );
        gray.getGraphics().drawImage(original, 0, 0, null);
        return gray;
    }

    /**
     * 下载语言数据（首次运行时自动调用）
     */
    private void downloadLanguageData(Path tessdataPath) throws IOException {
        String[] languages = {"eng", "chi_sim"};
        String baseUrl = "https://github.com/tesseract-ocr/tessdata/raw/main/";

        for (String lang : languages) {
            Path langFile = tessdataPath.resolve(lang + ".traineddata");
            if (!Files.exists(langFile)) {
                try (InputStream in = new URL(baseUrl + lang + ".traineddata").openStream()) {
                    Files.copy(in, langFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}

