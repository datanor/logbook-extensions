package ee.datanor.logbook.extensions.fixtures;

import lombok.Builder;
import lombok.Data;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Origin;
import org.zalando.logbook.attributes.HttpAttributes;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.zalando.logbook.Origin.LOCAL;

@Data
@Builder
public class MockHttpResponse implements HttpResponse {

    @Builder.Default
    String protocolVersion = "HTTP/1.1";
    @Builder.Default
    Origin origin = LOCAL;
    @Builder.Default
    int status = 200;
    @Builder.Default
    HttpHeaders headers = HttpHeaders.of("hdr", "value");
    @Builder.Default
    String contentType = "text/plain";
    @Builder.Default
    Charset charset = StandardCharsets.UTF_8;
    @Builder.Default
    String bodyAsString = "";
    @Builder.Default
    HttpAttributes httpAttributes = HttpAttributes.EMPTY;

    @Override
    public byte[] getBody() {
        return bodyAsString.getBytes(charset);
    }

    @Override
    public HttpResponse withBody() {
        return this;
    }

    @Override
    public HttpResponse withoutBody() {
        bodyAsString = "";
        return this;
    }

    @Override
    public HttpAttributes getAttributes() {
        return httpAttributes;
    }
}
