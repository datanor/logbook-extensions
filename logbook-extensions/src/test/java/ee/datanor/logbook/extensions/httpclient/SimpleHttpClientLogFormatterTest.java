package ee.datanor.logbook.extensions.httpclient;

import ee.datanor.logbook.extensions.fixtures.MockCorrelation;
import ee.datanor.logbook.extensions.fixtures.MockHttpRequest;
import ee.datanor.logbook.extensions.fixtures.MockHttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleHttpClientLogFormatterTest {

    private static final int MAX_LOGGED_REQUEST_BODY_LENGTH = 10;
    private static final int MAX_LOGGED_RESPONSE_BODY_LENGTH = 10;

    private HttpClientLoggingConfigurationProperties properties;
    private SimpleHttpClientLogFormatter formatter;
    private Correlation correlation;
    private HttpRequest request;
    private HttpResponse response;

    @BeforeEach
    void setUp() {
        properties = HttpClientLoggingConfigurationProperties.builder()
                .maxLoggedRequestBodyLength(MAX_LOGGED_REQUEST_BODY_LENGTH)
                .maxLoggedResponseBodyLength(MAX_LOGGED_RESPONSE_BODY_LENGTH)
                .build();
        formatter = new SimpleHttpClientLogFormatter(properties);
        correlation = MockCorrelation.builder().build();
        request = MockHttpRequest.builder().build();
        response = MockHttpResponse.builder().build();
    }

    @Test
    void logRequestLine() throws IOException {
        // when
        String log = formatter.format(correlation, request);

        // then
        assertTrue(log.contains("GET http://localhost/ HTTP/1.1"));
    }

    @Test
    void logRequestHeaders() throws IOException {
        // when
        String log = formatter.format(correlation, request);

        // then
        assertTrue(log.contains("hdr: [value]"));
    }

    @Test
    void logRequestBody() throws IOException {
        //given
        request = MockHttpRequest.builder()
                .bodyAsString("body")
                .contentType("application/json")
                .build();

        // when
        String log = formatter.format(correlation, request);

        // then
        assertTrue(log.contains("body"));
        assertTrue(log.contains(" " + "body".length()));
    }

    @Test
    void excludeRequestBodyForNotMatchingContentType() throws IOException {
        //given
        request = MockHttpRequest.builder()
                .bodyAsString("body")
                .contentType("text/plain")
                .build();

        // when
        String log = formatter.format(correlation, request);

        // then
        assertFalse(log.contains("body"));
    }

    @Test
    void limitLoggedRequestBodyLength() throws IOException {
        //given
        String body = StringUtils.repeat("*", 20);
        request = MockHttpRequest.builder()
                .bodyAsString(body)
                .contentType("application/json")
                .build();

        // when
        String log = formatter.format(correlation, request);

        // then
        assertTrue(log.contains("**********"));
        assertFalse(log.contains("*************"));
    }

    @Test
    void logRequestTime() throws IOException {
        // when
        String log = formatter.format(correlation, response);

        // then
        assertTrue(log.contains("10ms"));
    }

    @Test
    void logResponseStatus() throws IOException {
        // when
        String log = formatter.format(correlation, response);

        // then
        assertTrue(log.contains("200"));
    }

    @Test
    void logResponseHeaders() throws IOException {
        // when
        String log = formatter.format(correlation, response);

        // then
        assertTrue(log.contains("hdr: [value]"));
    }

    @Test
    void logResponseBody() throws IOException {
        //given
        response = MockHttpResponse.builder()
                .bodyAsString("body")
                .contentType("application/json")
                .build();

        // when
        String log = formatter.format(correlation, response);

        // then
        assertTrue(log.contains("body"));
        assertTrue(log.contains(" " + "body".length()));
    }

    @Test
    void excludeResponseBodyForNotMatchingContentType() throws IOException {
        //given
        response = MockHttpResponse.builder()
                .bodyAsString("body")
                .contentType("text/plain")
                .build();

        // when
        String log = formatter.format(correlation, response);

        // then
        assertFalse(log.contains("body"));
    }

    @Test
    void limitLoggedResponseBodyLength() throws IOException {
        //given
        String body = StringUtils.repeat("*", 20);
        response = MockHttpResponse.builder()
                .bodyAsString(body)
                .contentType("application/json")
                .build();

        // when
        String log = formatter.format(correlation, response);

        // then
        assertTrue(log.contains("**********"));
        assertFalse(log.contains("*************"));
    }
}