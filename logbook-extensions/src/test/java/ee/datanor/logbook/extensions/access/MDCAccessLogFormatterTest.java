package ee.datanor.logbook.extensions.access;

import ee.datanor.logbook.extensions.fixtures.MockCorrelation;
import ee.datanor.logbook.extensions.fixtures.MockHttpRequest;
import ee.datanor.logbook.extensions.fixtures.MockHttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MDCAccessLogFormatterTest {

    private static final int MAX_LOGGED_REQUEST_BODY_LENGTH = 10;
    private static final int MAX_LOGGED_RESPONSE_BODY_LENGTH = 10;
    private AccessLoggingConfigurationProperties properties;
    private MDCAccessLogFormatter formatter;
    private Correlation correlation;
    private HttpRequest request;
    private HttpResponse response;

    @BeforeEach
    void setUp() {
        properties = AccessLoggingConfigurationProperties.builder()
                .maxLoggedRequestBodyLength(MAX_LOGGED_REQUEST_BODY_LENGTH)
                .maxLoggedResponseBodyLength(MAX_LOGGED_RESPONSE_BODY_LENGTH)
                .build();
        formatter = new MDCAccessLogFormatter(properties);
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
        assertNotNull(MDC.get("AL_REQUEST_HASH"));
    }

    @Test
    void usesExistingCorrelationId() throws IOException {
        // given
        String correlationId = "existing1234567890";
        HttpHeaders headers = HttpHeaders.of(properties.getCorrelationIdHeaderName(), correlationId);
        request = MockHttpRequest.builder()
                .headers(headers)
                .build();
        // when
        formatter.format(correlation, request);

        // then
        assertEquals(correlationId, MDC.get("AL_CORRELATION_ID_HASH"));
    }

    @Test
    void logRemoteIp() throws IOException {
        // when
        formatter.format(correlation, request);

        // then
        assertEquals(request.getRemote(), MDC.get("AL_CLIENT_IP"));
    }

    @Test
    void logRequestHeaders() throws IOException {
        // when
        formatter.format(correlation, request);

        // then
        assertEquals("hdr: [value]", MDC.get("AL_REQUEST_HEADERS"));
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
        assertEquals("body", MDC.get("AL_REQUEST_BODY"));
        assertEquals("" + "body".length(), MDC.get("AL_REQUEST_BODY_LENGTH"));
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
        assertEquals("-", MDC.get("AL_REQUEST_BODY"));
        assertEquals("-", MDC.get("AL_REQUEST_BODY_LENGTH"));
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
        assertEquals(MAX_LOGGED_REQUEST_BODY_LENGTH, MDC.get("AL_REQUEST_BODY").length());
        assertEquals("" + body.length(), MDC.get("AL_REQUEST_BODY_LENGTH"));
    }

    @Test
    void logProcessingTime() throws IOException {
        // when
        formatter.format(correlation, response);

        // then
        assertNotNull(MDC.get("AL_PROCESSING_TIME"));
    }

    @Test
    void logResponseStatus() throws IOException {
        // when
        formatter.format(correlation, response);

        // then
        assertEquals("200", MDC.get("AL_RESPONSE_STATUS"));
    }

    @Test
    void logResponseHeaders() throws IOException {
        // when
        formatter.format(correlation, response);

        // then
        assertEquals("hdr: [value]", MDC.get("AL_RESPONSE_HEADERS"));
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
        assertEquals("body", MDC.get("AL_RESPONSE_BODY"));
        assertEquals("" + "body".length(), MDC.get("AL_RESPONSE_BODY_LENGTH"));
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
        assertEquals("-", MDC.get("AL_RESPONSE_BODY"));
        assertEquals("-", MDC.get("AL_RESPONSE_BODY_LENGTH"));
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
        assertEquals(MAX_LOGGED_RESPONSE_BODY_LENGTH, MDC.get("AL_RESPONSE_BODY").length());
        assertEquals("" + body.length(), MDC.get("AL_RESPONSE_BODY_LENGTH"));
    }

}