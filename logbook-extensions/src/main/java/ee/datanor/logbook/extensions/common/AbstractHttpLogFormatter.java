package ee.datanor.logbook.extensions.common;

import org.slf4j.MDC;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpMessage;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.RequestURI;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractHttpLogFormatter implements HttpLogFormatter {

    protected static void addHeaders(HttpHeaders request, String mdcKey) {
        String requestHeaders = request.entrySet().stream()
                .map(entry -> "%s: %s".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));

        MDC.put(mdcKey, requestHeaders);
    }

    protected static String addRequestLine(HttpRequest request, String mdcKey) {
        String requestLine = "%s %s %s".formatted(request.getMethod(), RequestURI.reconstruct(request), request.getProtocolVersion());
        MDC.put(mdcKey, requestLine);
        return requestLine;
    }

    protected boolean shouldLogHttpMessageBody(HttpMessage httpMessage, Set<String> mediaSubTypes) {
        String contentType = httpMessage.getContentType();
        Set<String> loggedMediaSubTypes = mediaSubTypes;
        if (loggedMediaSubTypes == null) {
            loggedMediaSubTypes = Set.of();
        }
        return contentType == null || contentType.isEmpty() ||
                loggedMediaSubTypes.stream().anyMatch(st -> contentType.contains("/" + st)) ||
                loggedMediaSubTypes.stream().anyMatch(st -> contentType.contains("+" + st));
    }

    protected String limitStringLength(String input, int maxLength) {
        if (input.length() > maxLength) {
            return input.substring(0, maxLength);
        } else {
            return input;
        }
    }
}
