package ee.datanor.logbook.extensions.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

import java.io.IOException;

public class DefaultHttpClientLogWriter implements HttpLogWriter {

    private final Logger requestLogger = LoggerFactory.getLogger("httpclient-request-log");
    private final Logger responseLogger = LoggerFactory.getLogger("httpclient-response-log");

    @Override
    public void write(Precorrelation precorrelation, String request) throws IOException {
        requestLogger.info(request);
    }

    @Override
    public void write(Correlation correlation, String response) throws IOException {
        responseLogger.info(response);
    }
}
