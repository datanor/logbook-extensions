package ee.datanor.logbook.extensions.httpclient;

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
public class MDCHttpClientLogFormatter extends AbstractHttpLogFormatter {

    private final HttpClientLoggingConfigurationProperties properties;

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
        MDC.put("HC_REQUEST_HASH", HashUtil.generateHash(8));
        String requestLine = addRequestLine(request, "HC_REQUEST_LINE");

        addHeaders(request.getHeaders(), "HC_REQUEST_HEADERS");
        if (shouldLogHttpMessageBody(request, properties.getIncludedRequestBodyMediaSubtypes())) {
            final String body = request.getBodyAsString();
            MDC.put("HC_REQUEST_BODY_LENGTH", body.length() + "");
            MDC.put("HC_REQUEST_BODY", limitStringLength(body, properties.getMaxLoggedRequestBodyLength()));
        } else {
            MDC.put("HC_REQUEST_BODY_LENGTH","-");
            MDC.put("HC_REQUEST_BODY", "-");
        }
        return "%s %s".formatted(properties.getRequestLinePrefix(), requestLine);
    }


    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
        MDC.put("HC_REQUEST_TIME", "" + correlation.getDuration().toMillis());
        MDC.put("HC_RESPONSE_STATUS", "" + response.getStatus());

        addHeaders(response.getHeaders(), "HC_RESPONSE_HEADERS");

        if (shouldLogHttpMessageBody(response, properties.getIncludedResponseBodyMediaSubtypes())) {
            final String body = response.getBodyAsString();
            MDC.put("HC_RESPONSE_BODY_LENGTH", body.length() + "");
            MDC.put("HC_RESPONSE_BODY", limitStringLength(body, properties.getMaxLoggedResponseBodyLength()));
        } else {
            MDC.put("HC_RESPONSE_BODY_LENGTH","-");
            MDC.put("HC_RESPONSE_BODY", "-");
        }

        return "%s %s".formatted(properties.getResponseLinePrefix(), MDC.get("HC_REQUEST_LINE"));
    }

}
