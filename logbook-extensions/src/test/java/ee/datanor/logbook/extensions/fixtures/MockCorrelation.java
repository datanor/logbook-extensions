package ee.datanor.logbook.extensions.fixtures;

import lombok.Builder;
import lombok.Data;
import org.zalando.logbook.Correlation;

import java.time.Duration;
import java.time.Instant;

@Data
@Builder
public class MockCorrelation implements Correlation {

    @Builder.Default
    private String id = "corrId";
    @Builder.Default
    private Instant start = Instant.now();

    @Builder.Default
    private Instant end = Instant.now();

    @Builder.Default
    private Duration duration = Duration.ofMillis(10);
}
