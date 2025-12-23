package ee.datanor.logbook.extensions.httpclient;

import lombok.RequiredArgsConstructor;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpMessage;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.RequestURI;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SimpleHttpClientLogFormatter implements HttpLogFormatter {

    private final HttpClientLoggingConfigurationProperties properties;

    @Override
    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
        String requestLine = getRequestLine(request);
        String headers = getHeaders(request.getHeaders());

        String body = "-";
        String bodyLength = "-";
        if (shouldLogHttpMessageBody(request, properties.getIncludedRequestBodyMediaSubtypes())) {
            final String originalBody = request.getBodyAsString();
            body = limitStringLength(originalBody, properties.getMaxLoggedRequestBodyLength());
            bodyLength = originalBody.length() + "";
        }
        return "%s %s %s %s %s %s".formatted(properties.getRequestLinePrefix(), precorrelation.getId(), requestLine, headers, body, bodyLength);
    }


    @Override
    public String format(Correlation correlation, HttpResponse response) throws IOException {
        String headers = getHeaders(response.getHeaders());
        String body = "-";
        String bodyLength = "-";
        if (shouldLogHttpMessageBody(response, properties.getIncludedResponseBodyMediaSubtypes())) {
            final String originalBbody = response.getBodyAsString();
            body = limitStringLength(originalBbody, properties.getMaxLoggedResponseBodyLength());
            bodyLength = originalBbody.length() + "";
        }
        return "%s %s %s %d %s %s %s".formatted(
                properties.getResponseLinePrefix(),
                correlation.getId(),
                headers,
                response.getStatus(),
                correlation.getDuration().toMillis() + "ms",
                body,
                bodyLength);
    }

    private static String getHeaders(HttpHeaders request) {
        return request.entrySet().stream()
                .map(entry -> "%s: %s".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));
    }

    private static String getRequestLine(HttpRequest request) {
        return "%s %s %s".formatted(request.getMethod(), RequestURI.reconstruct(request), request.getProtocolVersion());
    }

    private boolean shouldLogHttpMessageBody(HttpMessage httpMessage, Set<String> mediaSubTypes) {
        String contentType = httpMessage.getContentType();
        Set<String> loggedMediaSubTypes = mediaSubTypes;
        if (loggedMediaSubTypes == null) {
            loggedMediaSubTypes = Set.of();
        }
        return contentType == null || contentType.isEmpty() || loggedMediaSubTypes.stream().anyMatch(st -> contentType.contains("/" + st));
    }

    private String limitStringLength(String input, int maxLength) {
        if (input.length() > maxLength) {
            return input.substring(0, maxLength);
        } else {
            return input;
        }
    }
}
