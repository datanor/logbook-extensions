package ee.datanor.logbook.extensions.access;

import ee.datanor.logbook.extensions.fixtures.MockCorrelation;
import ee.datanor.logbook.extensions.fixtures.MockPreCorrelation;
import ee.datanor.logbook.extensions.fixtures.TestLogAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.Precorrelation;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultAccessLogWriterTest {
    private static TestLogAppender appender;
    private static Logger requestLogger;
    private static Logger responseLogger;

    private final DefaultAccessLogWriter writer = new DefaultAccessLogWriter();

    @AfterAll
    static void teardown() {
        requestLogger.removeAppender(appender);
        responseLogger.removeAppender(appender);
    }

    @BeforeAll
    static void setUp() {
        if (appender == null) {
            appender = new TestLogAppender();
            requestLogger = (Logger) LogManager.getLogger("access-request-log");
            requestLogger.addAppender(appender);

            responseLogger = (Logger) LogManager.getLogger("access-response-log");
            responseLogger.addAppender(appender);
        }
        requestLogger.setLevel(Level.INFO);
        responseLogger.setLevel(Level.INFO);
    }

    @BeforeEach
    void before() {
        appender.messages.clear();
    }

    @Test
    void logRequest() throws IOException {
        //given
        Precorrelation preCorrelation = MockPreCorrelation.builder().build();

        // when
        writer.write(preCorrelation, "request");

        // then
        assertEquals(1, appender.messages.size());
    }

    @Test
    void logResponse() throws IOException {
        //given
        Correlation correlation = MockCorrelation.builder().build();

        // when
        writer.write(correlation, "response");

        // then
        assertEquals(1, appender.messages.size());
    }
}