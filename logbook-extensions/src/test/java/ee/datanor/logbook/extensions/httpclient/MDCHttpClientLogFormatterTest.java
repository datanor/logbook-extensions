package ee.datanor.logbook.extensions.httpclient;

import ee.datanor.logbook.extensions.fixtures.MockCorrelation;
import ee.datanor.logbook.extensions.fixtures.MockHttpRequest;
import ee.datanor.logbook.extensions.fixtures.MockHttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MDCHttpClientLogFormatterTest {
    private static final int MAX_LOGGED_REQUEST_BODY_LENGTH = 10;
    private static final int MAX_LOGGED_RESPONSE_BODY_LENGTH = 10;

    private HttpClientLoggingConfigurationProperties properties;
    private MDCHttpClientLogFormatter formatter;
    private Correlation correlation;
    private HttpRequest request;
    private HttpResponse response;

    @BeforeEach
    void setUp() {
        properties = HttpClientLoggingConfigurationProperties.builder()
                .maxLoggedRequestBodyLength(MAX_LOGGED_REQUEST_BODY_LENGTH)
                .maxLoggedResponseBodyLength(MAX_LOGGED_RESPONSE_BODY_LENGTH)
                .build();
        formatter = new MDCHttpClientLogFormatter(properties);
        correlation = MockCorrelation.builder().build();
        request = MockHttpRequest.builder().build();
        response = MockHttpResponse.builder().build();
        MDC.clear();
    }

    @Test
    void generateRequestHash() throws IOException {
        // when
        formatter.format(correlation, request);

        // then
        assertNotNull(MDC.get("HC_REQUEST_HASH"));
    }

    @Test
    void logRequestLine() throws IOException {
        // when
        formatter.format(correlation, request);

        // then
        assertEquals("GET http://localhost/ HTTP/1.1", MDC.get("HC_REQUEST_LINE"));
    }

    @Test
    void logRequestHeaders() throws IOException {
        // when
        formatter.format(correlation, request);

        // then
        assertEquals("hdr: [value]", MDC.get("HC_REQUEST_HEADERS"));
    }

    @Test
    void logRequestBody() throws IOException {
        //given
        request = MockHttpRequest.builder()
                .bodyAsString("body")
                .contentType("application/json")
                .build();

        // when
        formatter.format(correlation, request);

        // then
        assertEquals("body", MDC.get("HC_REQUEST_BODY"));
        assertEquals("" + "body".length(), MDC.get("HC_REQUEST_BODY_LENGTH"));
    }

    @Test
    void excludeRequestBodyForNotMatchingContentType() throws IOException {
        //given
        request = MockHttpRequest.builder()
                .bodyAsString("body")
                .contentType("text/plain")
                .build();

        // when
        formatter.format(correlation, request);

        // then
        assertEquals("-", MDC.get("HC_REQUEST_BODY"));
        assertEquals("-", MDC.get("HC_REQUEST_BODY_LENGTH"));
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
        formatter.format(correlation, request);

        // then
        assertEquals(MAX_LOGGED_REQUEST_BODY_LENGTH, MDC.get("HC_REQUEST_BODY").length());
        assertEquals("" + body.length(), MDC.get("HC_REQUEST_BODY_LENGTH"));
    }

    @Test
    void logRequestTime() throws IOException {
        // when
        formatter.format(correlation, response);

        // then
        assertNotNull(MDC.get("HC_REQUEST_TIME"));
    }

    @Test
    void logResponseStatus() throws IOException {
        // when
        formatter.format(correlation, response);

        // then
        assertEquals("200", MDC.get("HC_RESPONSE_STATUS"));
    }

    @Test
    void logResponseHeaders() throws IOException {
        // when
        formatter.format(correlation, response);

        // then
        assertEquals("hdr: [value]", MDC.get("HC_RESPONSE_HEADERS"));
    }

    @Test
    void logResponseBody() throws IOException {
        //given
        response = MockHttpResponse.builder()
                .bodyAsString("body")
                .contentType("application/json")
                .build();

        // when
        formatter.format(correlation, response);

        // then
        assertEquals("body", MDC.get("HC_RESPONSE_BODY"));
        assertEquals("" + "body".length(), MDC.get("HC_RESPONSE_BODY_LENGTH"));
    }

    @Test
    void excludeResponseBodyForNotMatchingContentType() throws IOException {
        //given
        response = MockHttpResponse.builder()
                .bodyAsString("body")
                .contentType("text/plain")
                .build();

        // when
        formatter.format(correlation, response);

        // then
        assertEquals("-", MDC.get("HC_RESPONSE_BODY"));
        assertEquals("-", MDC.get("HC_RESPONSE_BODY_LENGTH"));
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
        formatter.format(correlation, response);

        // then
        assertEquals(MAX_LOGGED_RESPONSE_BODY_LENGTH, MDC.get("HC_RESPONSE_BODY").length());
        assertEquals("" + body.length(), MDC.get("HC_RESPONSE_BODY_LENGTH"));
    }
}