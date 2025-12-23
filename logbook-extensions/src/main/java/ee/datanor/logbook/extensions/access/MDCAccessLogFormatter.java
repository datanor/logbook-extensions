package ee.datanor.logbook.extensions.access;

import ee.datanor.logbook.extensions.common.AbstractHttpLogFormatter;
import ee.datanor.logbook.extensions.common.HashUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;

import java.io.IOException;

@RequiredArgsConstructor
public class MDCAccessLogFormatter extends AbstractHttpLogFormatter {

    private final AccessLoggingConfigurationProperties properties;

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
        addCorrelationId(precorrelation, request);
        MDC.put("AL_REQUEST_HASH", HashUtil.generateHash(8));
        String requestLine = addRequestLine(request, "AL_REQUEST_LINE");
        MDC.put("AL_CLIENT_IP", request.getRemote());
        addHeaders(request.getHeaders(), "AL_REQUEST_HEADERS");

        if (shouldLogHttpMessageBody(request, properties.getIncludedRequestBodyMediaSubtypes())) {
            final String body = request.getBodyAsString();
            MDC.put("AL_REQUEST_BODY_LENGTH", body.length() + "");
            MDC.put("AL_REQUEST_BODY", limitStringLength(body, properties.getMaxLoggedRequestBodyLength()));
        } else {
            MDC.put("AL_REQUEST_BODY_LENGTH","-");
            MDC.put("AL_REQUEST_BODY", "-");
        }
        return "%s %s".formatted(properties.getRequestLinePrefix(), requestLine);
    }

    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {

        MDC.put("AL_PROCESSING_TIME", "" + correlation.getDuration().toMillis());
        MDC.put("AL_RESPONSE_STATUS", "" + response.getStatus());

        addHeaders(response.getHeaders(), "AL_RESPONSE_HEADERS");

        if (shouldLogHttpMessageBody(response, properties.getIncludedResponseBodyMediaSubtypes())) {
            final String body = response.getBodyAsString();
            MDC.put("AL_RESPONSE_BODY_LENGTH", body.length() + "");
            MDC.put("AL_RESPONSE_BODY", limitStringLength(body, properties.getMaxLoggedResponseBodyLength()));
        } else {
            MDC.put("AL_RESPONSE_BODY_LENGTH","-");
            MDC.put("AL_RESPONSE_BODY", "-");
        }
        return "%s %s".formatted(properties.getResponseLinePrefix(), MDC.get("AL_REQUEST_LINE"));
    }

    private void addCorrelationId(Precorrelation precorrelation, HttpRequest request) {
        String existingCorrelationId = request.getHeaders().getFirst(properties.getCorrelationIdHeaderName());
        String correlationId = precorrelation.getId();
        if (existingCorrelationId != null && existingCorrelationId.length() > 0) {
            correlationId = existingCorrelationId;
        }
        MDC.put("AL_CORRELATION_ID_HASH", correlationId);
    }
}
