package by.art.taskprocessingsystem.unit.service.impl;

import by.art.taskprocessingsystem.exception.WebhookDeliveryException;
import by.art.taskprocessingsystem.service.impl.WebhookDeliveryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WebhookDeliveryHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldDeliverWebhookSuccessfully() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        WebhookDeliveryHandler handler = new WebhookDeliveryHandler(builder.build(), objectMapper);

        String payload = "{\"url\":\"https://example.com/hook\", \"method\": \"POST\", \"body\":{\"event\":\"task.done\"}}";


        server.expect(requestTo("https://example.com/hook"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess());


        assertThatCode(() -> handler.handle(payload)).doesNotThrowAnyException();
        server.verify();
    }

    @Test
    void shouldThrowWebhookDeliveryExceptionOnServerError() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        WebhookDeliveryHandler handler = new WebhookDeliveryHandler(builder.build(), objectMapper);

        server.expect(requestTo("https://example.com/hook")).andRespond(withServerError());

        String payload = "{\"url\":\"https://example.com/hook\", \"method\": \"GET\", \"body\":{}}";

        assertThatThrownBy(() -> handler.handle(payload)).isInstanceOf(WebhookDeliveryException.class);
    }

    @Test
    void shouldThrowExceptionWhenUrlIsMissing() {
        WebhookDeliveryHandler handler = new WebhookDeliveryHandler(RestClient.builder().build(), objectMapper);

        assertThatThrownBy(() -> handler.handle("{\"body\":{}}"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}