package by.art.taskprocessingsystem.service.impl;

import by.art.taskprocessingsystem.entity.TaskType;
import by.art.taskprocessingsystem.exception.WebhookDeliveryException;
import by.art.taskprocessingsystem.service.TaskHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliveryHandler implements TaskHandler {

    private final RestClient webhookRestClient;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(String payload) {
        WebhookPayload webhookPayload = parsePayload(payload);
        log.info("Delivering webhook to {}", webhookPayload.url());

        try {
            webhookRestClient.method(webhookPayload.method())
                    .uri(webhookPayload.url())
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> webhookPayload.headers().forEach(httpHeaders::add))
                    .body(webhookPayload.body())
                    .retrieve()
                    .toBodilessEntity();
            log.info("Webhook delivered to {}", webhookPayload.url());
        } catch (RestClientResponseException e) {
            throw new WebhookDeliveryException(webhookPayload.url(), e.getStatusCode().value(), e);
        } catch (RestClientException e) {
            throw new WebhookDeliveryException(webhookPayload.url(), e);
        }
    }

    @Override
    public TaskType getType() {
        return TaskType.WEBHOOK_DELIVERY;
    }

    private WebhookPayload parsePayload(String payload) {
        JsonNode root;
        try {
            root = objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Webhook payload is not valid JSON", e);
        }

        String url = root.path("url").asText(null);
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Webhook payload must contain a non-blank 'url' field");
        }

        String method = root.path("method").asText(null);
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("Webhook payload must contain a non-blank 'method' field");
        }

        HttpMethod httpMethod = HttpMethod.valueOf(method);

        JsonNode body = root.has("body") ? root.get("body") : objectMapper.createObjectNode();

        Map<String, String> headers = new HashMap<>();
        JsonNode headersNode = root.get("headers");
        if (headersNode != null && headersNode.isObject()) {
            headersNode.properties().forEach(entry -> headers.put(entry.getKey(), entry.getValue().asText()));
        }

        return new WebhookPayload(url, httpMethod, body, headers);
    }

    private record WebhookPayload(String url, HttpMethod method, JsonNode body, Map<String, String> headers) {
    }
}