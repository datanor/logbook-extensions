package ee.datanor.logbook.extensions.fixtures;

import lombok.Builder;
import lombok.Data;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.Precorrelation;

import java.time.Instant;

@Data
@Builder
public class MockPreCorrelation implements Precorrelation {

    @Builder.Default
    private String id = "corrId";
    @Builder.Default
    private Instant start = Instant.now();

    @Override
    public Correlation correlate() {
        return MockCorrelation.builder().id(id).start(start).build();
    }
}
