package by.art.taskprocessingsystem.exception;

public class WebhookDeliveryException extends RuntimeException {

    private static final String STATUS_MESSAGE_TEMPLATE = "Webhook delivery to %s failed with status %d";
    private static final String MESSAGE_TEMPLATE = "Webhook delivery to %s failed";

    public WebhookDeliveryException(String url, int statusCode, Throwable cause) {
        super(String.format(STATUS_MESSAGE_TEMPLATE, url, statusCode), cause);
    }

    public WebhookDeliveryException(String url, Throwable cause) {
        super(String.format(MESSAGE_TEMPLATE, url), cause);
    }
}
