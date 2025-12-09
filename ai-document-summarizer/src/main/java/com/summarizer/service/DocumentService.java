package com.summarizer.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.summarizer.dto.DocumentSummaryDto;
import com.summarizer.model.Document;
import com.summarizer.repository.DocumentRepository;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OpenAIClient openAIClient;
    private final Tika tika = new Tika();

    public DocumentService(DocumentRepository documentRepository, OpenAIClient openAIClient) {
        this.documentRepository = documentRepository;
        this.openAIClient = openAIClient;
    }

    public DocumentSummaryDto processFile(MultipartFile file, String mode) throws Exception {

        String extractedText = extractText(file);

        if (extractedText == null || extractedText.trim().isEmpty()) {
            String msg = "No readable text found.";
            Document saved = persist(file.getOriginalFilename(), extractedText, msg);
            return new DocumentSummaryDto(saved.getId(), saved.getFileName(), new ArrayList<>(), msg);
        }

        List<String> chunks = chunkText(extractedText, 2000);

        ExecutorService executor = Executors.newFixedThreadPool(1); // prevent rate limit
        List<Future<String>> futures = new ArrayList<>();

        for (String chunk : chunks) {
            futures.add(executor.submit(() -> retryChunkSummary(chunk, mode)));
        }

        List<String> sectionSummaries = new ArrayList<>();
        for (Future<String> f : futures) {
            try {
                sectionSummaries.add(f.get());
            } catch (Exception ex) {
                sectionSummaries.add("⚠ Section skipped due to API errors.");
            }
        }

        executor.shutdown();

        String overallSummary = openAIClient.finalOverview(sectionSummaries);

        Document saved = persist(file.getOriginalFilename(), extractedText, overallSummary);

        return new DocumentSummaryDto(saved.getId(), saved.getFileName(), sectionSummaries, overallSummary);
    }

    private String retryChunkSummary(String chunk, String mode) {
        int attempts = 0;
        while (attempts < 3) {
            String response = openAIClient.summarizeSection(chunk, mode);

            if (!response.contains("rate limit") && !response.toLowerCase().contains("429")) {
                return response;
            }

            attempts++;
            try {
                Thread.sleep(3000); // give API time to cool down
            } catch (InterruptedException ignored) {}
        }
        return "⚠ Skipped due to repeated rate limits.";
    }

    private Document persist(String name, String extracted, String summary) {
        return documentRepository.save(
                Document.builder()
                        .fileName(name)
                        .extractedText(extracted)
                        .summary(summary)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    private String extractText(MultipartFile file) throws Exception {
        String name = file.getOriginalFilename();
        if (name != null && name.toLowerCase().endsWith(".pdf")) {
            try (InputStream is = file.getInputStream(); PDDocument pdf = PDDocument.load(is)) {
                return new PDFTextStripper().getText(pdf);
            }
        }
        return tika.parseToString(file.getInputStream());
    }

    private List<String> chunkText(String text, int maxLen) {
        List<String> chunks = new ArrayList<>();
        String cleaned = text.replaceAll("\\s+", " ").trim();

        int start = 0;
        while (start < cleaned.length()) {
            chunks.add(cleaned.substring(start, Math.min(start + maxLen, cleaned.length())));
            start += maxLen;
        }
        return chunks;
    }
}
