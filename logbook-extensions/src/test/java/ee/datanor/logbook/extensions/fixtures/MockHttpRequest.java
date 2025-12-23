package ee.datanor.logbook.extensions.fixtures;

import lombok.Builder;
import lombok.Data;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.Origin;
import org.zalando.logbook.attributes.HttpAttributes;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.zalando.logbook.Origin.REMOTE;

@Data
@Builder
public class MockHttpRequest implements HttpRequest {

    @Builder.Default
    String protocolVersion = "HTTP/1.1";
    @Builder.Default
    Origin origin = REMOTE;
    @Builder.Default
    String remote = "127.0.0.1";
    @Builder.Default
    String method = "GET";
    @Builder.Default
    String scheme = "http";
    @Builder.Default
    String host = "localhost";
    @Builder.Default
    Optional<Integer> port = Optional.of(80);
    @Builder.Default
    String path = "/";
    @Builder.Default
    String query = "";
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
    public HttpRequest withBody() {
        return this;
    }

    @Override
    public HttpRequest withoutBody() {
        bodyAsString = "";
        return this;
    }

    @Override
    public HttpAttributes getAttributes() {
        return httpAttributes;
    }
}
