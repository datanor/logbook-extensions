package ee.datanor.logbook.extensions.access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLoggingConfigurationProperties {
    @Builder.Default
    private String correlationIdHeaderName = "X-Correlation-ID";
    @Builder.Default
    private int maxLoggedRequestBodyLength = 1024;
    @Builder.Default
    private int maxLoggedResponseBodyLength = 1024;
    @Builder.Default
    private Set<String> includedRequestBodyMediaSubtypes = Set.of("json", "xml");
    @Builder.Default
    private Set<String> includedResponseBodyMediaSubtypes = Set.of("json", "xml");
    @Builder.Default
    private String requestLinePrefix = "Request:";
    @Builder.Default
    private String responseLinePrefix = "Response:";
}
