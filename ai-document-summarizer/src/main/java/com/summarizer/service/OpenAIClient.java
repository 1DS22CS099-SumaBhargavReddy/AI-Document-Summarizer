package com.summarizer.service;

import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    private final Dotenv dotenv = Dotenv.configure()
            .directory("src/main/resources")  // 👈 force load
            .ignoreIfMissing()
            .load();

    private final String apiKey = dotenv.get("OPENAI_API_KEY");
    private final String openAiUrl = dotenv.get("OPENAI_API_URL");  // ex: https://api.openai.com/v1/responses
    private final String model = dotenv.get("OPENAI_MODEL");        // ex: gpt-4o-mini

    public OpenAIClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(20))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();

        System.out.println("\n==============================");
        System.out.println("🔍 OpenAI Key Loaded? " + (apiKey != null));
        System.out.println("🔍 Model: " + model);
        System.out.println("🔍 Endpoint: " + openAiUrl);
        System.out.println("==============================\n");
    }

    /** CORE: Call /v1/responses */
    public String generateSummary(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                    "model", model,
                    "input", prompt,
                    "temperature", 0.2
            );

            ResponseEntity<String> response =
                    restTemplate.postForEntity(openAiUrl, new HttpEntity<>(body, headers), String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode output = root.path("output");

                if (output.isTextual()) return output.asText().trim();
                if (output.has("text")) return output.path("text").asText().trim();
            }

        } catch (Exception e) {
            System.out.println("⚠ OpenAI API error: " + e.getMessage());
        }
        return fallback(prompt);
    }

    /** Section-wise summarization */
    public String summarizeSection(String text, String mode) {
        String stylePrompt = switch (mode.toLowerCase()) {
            case "brief" -> "Summarize in exactly 3 bullet points.";
            case "detailed" -> "Summarize deeply in structured paragraphs without fluff.";
            default -> "Summarize in 5-7 bullet points.";
        };

        return generateSummary(stylePrompt + "\n\nCONTENT:\n" + text);
    }

    /** Final overview merge */
    public String finalOverview(List<String> sections) {
        String prompt = """
                Provide a final executive summary (6–8 lines).
                Avoid repetition. Only key distilled insights.

                Sections:
                """ + sections;

        return generateSummary(prompt);
    }

    /** Local backup if OpenAI fails */
    private String fallback(String text) {
        String cleaned = text.replaceAll("\\s+", " ").trim();
        return cleaned.length() > 400 ? cleaned.substring(0, 400) + "..." : cleaned;
    }
}
