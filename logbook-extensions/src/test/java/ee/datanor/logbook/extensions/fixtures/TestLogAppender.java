package ee.datanor.logbook.extensions.fixtures;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

import java.util.ArrayList;
import java.util.List;

public class TestLogAppender extends AbstractAppender {

    public List<String> messages = new ArrayList<>();

    public TestLogAppender() {
        super("MockedAppender", null, null, true, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent event) {
        messages.add(event.getMessage().getFormattedMessage());
    }
}

