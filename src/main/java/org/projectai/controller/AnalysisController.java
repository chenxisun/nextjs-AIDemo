package org.projectai.controller;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.projectai.model.Invoice;
import org.projectai.util.PdfTextExtractor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class AnalysisController {


    @Value("${dashscope.api-key}")
    private String apiKey;

    @PostMapping("/invoice")
    public Map<String, Object> analyzeInvoice(
            @RequestParam("question") String question) throws NoApiKeyException, InputRequiredException, IOException {
        ApplicationParam request = ApplicationParam.builder()
                .apiKey(apiKey)
                .appId("5ec6f795a4884adfa3bbb7f8f860dc96")
                .prompt(question)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(request);

        Map<String, Object> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String cleanedJson = result.getOutput().getText().replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "");
        Invoice invoice = objectMapper.readValue(cleanedJson, Invoice.class);
        response.put("status", "success");
        response.put("data", invoice);
        return response;
    }


    @PostMapping("/contract")
    public Map<String, Object> analyzeContract(
            @RequestParam("file") MultipartFile file,
            @RequestParam("question") String question) throws NoApiKeyException, InputRequiredException, IOException {

        String text = PdfTextExtractor.extractTextFromPdf(file);
//        Message message = Message.builder().content(text).build();
//        List<Message> messages=new ArrayList<>();
//        messages.add(message);
        ApplicationParam request = ApplicationParam.builder()
                .apiKey(apiKey)
                .appId("5ec6f795a4884adfa3bbb7f8f860dc96")
//                .messages(messages)
                .prompt(text)
                .build();

        Application application = new Application();
        ApplicationResult result = application.call(request);

        Map<String, Object> response = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String cleanedJson = result.getOutput().getText().replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "");
         objectMapper.readTree(cleanedJson);
        response.put("status", "success");
        response.put("data",objectMapper.readTree(cleanedJson));
        return response;
    }
}
