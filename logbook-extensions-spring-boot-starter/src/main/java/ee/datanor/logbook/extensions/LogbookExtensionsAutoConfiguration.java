package ee.datanor.logbook.extensions;

import ee.datanor.logbook.extensions.access.AccessLoggingConfigurationProperties;
import ee.datanor.logbook.extensions.access.DefaultAccessLogWriter;
import ee.datanor.logbook.extensions.access.MDCAccessLogFormatter;
import ee.datanor.logbook.extensions.httpclient.HttpClientLoggingConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Logbook.class)
public class LogbookExtensionsAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "logbook-extensions.access-logging")
    public AccessLoggingConfigurationProperties accessLoggingConfigurationProperties() {
        return new AccessLoggingConfigurationProperties();
    }

    @Bean
    public HttpLogFormatter httpLogFormatter(AccessLoggingConfigurationProperties properties) {
        return new MDCAccessLogFormatter(properties);
    }

    @Bean
    public HttpLogWriter httpLogWriter() {
        return new DefaultAccessLogWriter();
    }

    @Bean
    @ConfigurationProperties(prefix = "logbook-extensions.http-client")
    public HttpClientLoggingConfigurationProperties httpClientLoggingConfigurationProperties() {
        return new HttpClientLoggingConfigurationProperties();
    }

}
