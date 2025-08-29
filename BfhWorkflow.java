package com.example.bfh.workflow;

import com.example.bfh.config.AppProperties;
import com.example.bfh.dto.GenerateWebhookRequest;
import com.example.bfh.model.Solution;
import com.example.bfh.repo.SolutionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Service
public class BfhWorkflow {

    private static final Logger log = LoggerFactory.getLogger(BfhWorkflow.class);

    private final WebClient webClient;
    private final AppProperties props;
    private final SolutionRepository repository;

    public BfhWorkflow(WebClient webClient, AppProperties props, SolutionRepository repository) {
        this.webClient = webClient;
        this.props = props;
        this.repository = repository;
    }

    public void execute() {
        try {
            log.info("[1/4] Generating webhook…");
            var req = new GenerateWebhookRequest(props.getName(), props.getRegNo(), props.getEmail());

            JsonNode resp = webClient.post()
                    .uri(props.getGenerateEndpoint())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            if (resp == null) throw new IllegalStateException("Empty response from generateWebhook");

            String webhook = textOrNull(resp, "webhook");
            String accessToken = textOrNull(resp, "accessToken");

            log.info("Webhook URL: {}", webhook != null ? webhook : "<none — will use fallback>");
            if (accessToken == null || accessToken.isBlank()) {
                throw new IllegalStateException("accessToken missing in generateWebhook response");
            }

            log.info("[2/4] Selecting SQL based on regNo last two digits…");
            String finalQuery = loadFinalQueryForRegNo(props.getRegNo());

            log.info("[3/4] Storing solution locally (H2) …");
            repository.save(new Solution(props.getRegNo(), finalQuery));

            if (props.isDryRun()) {
                log.info("DRY RUN — skipping final submission. Final query:\n{}", finalQuery);
                return;
            }

            String submitUrl = (webhook != null && !webhook.isBlank()) ? webhook : props.getSubmitEndpoint();

            log.info("[4/4] Submitting to {} …", submitUrl);
            webClient.post()
                    .uri(submitUrl)
                    .header(HttpHeaders.AUTHORIZATION, accessToken) // raw JWT per spec
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("finalQuery", finalQuery))
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("✅ Submission completed.");
        } catch (WebClientResponseException e) {
            log.error("HTTP {} error: {}\nBody: {}", e.getRawStatusCode(), e.getMessage(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Workflow failed: {}", e.toString(), e);
        }
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode f = node.get(field);
        return f != null && !f.isNull() ? f.asText() : null;
    }

    private String loadFinalQueryForRegNo(String regNo) throws IOException {
        int lastTwo = extractLastTwoDigits(regNo);
        boolean isOdd = (lastTwo % 2) != 0;
        String resourcePath = isOdd ? "queries/question1.sql" : "queries/question2.sql";
        var res = new ClassPathResource(resourcePath);
        if (!res.exists()) {
            throw new IllegalStateException("Missing resource: " + resourcePath);
        }
        byte[] bytes = res.getContentAsByteArray();
        String sql = new String(bytes, StandardCharsets.UTF_8).trim();
        if (sql.isBlank()) {
            throw new IllegalStateException("Final SQL in " + resourcePath + " is blank. Fill it before running.");
        }
        log.info("Selected {} ({}), lastTwo={}, isOdd={}", resourcePath, res.getFilename(), lastTwo, isOdd);
        return sql;
    }

    private static int extractLastTwoDigits(String regNo) {
        Objects.requireNonNull(regNo, "regNo");
        String digits = regNo.replaceAll("\\D", "");
        if (digits.isEmpty()) throw new IllegalArgumentException("regNo contains no digits: " + regNo);
        String last2 = digits.length() >= 2 ? digits.substring(digits.length() - 2) : digits;
        return Integer.parseInt(last2);
    }
}
