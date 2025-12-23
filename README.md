# Logbook extensions

An opinionated set of extensions for [Logbook](https://github.com/zalando/logbook).

- [Access log formatter](src/main/java/ee/datanor/logbook/access/MDCAccessLogFormatter.java) that breaks log entry to separate MDC fields for structured logging.
- [Http client log formatter ](src/main/java/ee/datanor/logbook/httpclient/MDCHttpClientLogFormatter.java)  that breaks log entry to separate MDC fields for structured logging.

For detailed configuration of Logbook, see [Logbook git repo](https://github.com/zalando/logbook).

## Usage

### Gradle dependency

```
implementation 'ee.datanor.logbook:logbook-extensions:3.12.3.1'
```

### HttpClient usage

#### HttpClient4 example

```
HttpClientLoggingConfigurationProperties properties = HttpClientLoggingConfigurationProperties
    .builder()
    .maxLoggedRequestBodyLength(1024)
    .requestLinePrefix("Request:")
    .includedRequestBodyMediaSubtypes(Set.of("json", "xml"))
    .maxLoggedResponseBodyLength(1024)
    .includedResponseBodyMediaSubtypes(Set.of("json", "xml"))
    .responseLinePrefix("Response:")
    .build();

Logbook logbook = Logbook.builder()
    .sink(new DefaultSink(
        new MDCHttpClientLogFormatter(properties),
        new DefaultHttpClientLogWriter()
    ))
    .build();

HttpClientBuilder builder = HttpClients.custom();
    builder.addInterceptorFirst(new LogbookHttpRequestInterceptor(logbook));
    builder.addInterceptorFirst(new LogbookHttpResponseInterceptor());

```

#### HttpClient5 example

```
HttpClientLoggingConfigurationProperties properties = HttpClientLoggingConfigurationProperties
    .builder()
    .maxLoggedRequestBodyLength(1024)
    .requestLinePrefix("Request:")
    .includedRequestBodyMediaSubtypes(Set.of("json", "xml"))
    .maxLoggedResponseBodyLength(1024)
    .includedResponseBodyMediaSubtypes(Set.of("json", "xml"))
    .responseLinePrefix("Response:")
    .build();

Logbook logbook = Logbook.builder()
    .sink(new DefaultSink(
        new MDCHttpClientLogFormatter(properties),
        new DefaultHttpClientLogWriter()
    ))
    .build();

CloseableHttpClient client = HttpClientBuilder.create()
    .addExecInterceptorFirst("Logbook", new LogbookHttpExecHandler(logbook))
    .build();
```


### Spring Boot Starter

#### Gradle Dependencies

```
implementation 'org.zalando:logbook-spring-boot-starter:3.12.3'
implementation 'ee.datanor.logbook:logbook-extensions-spring-boot-starter:3.12.3.1'
// optional for httpclient5 logging
// implementation 'org.zalando:logbook-httpclient5:3.12.3'
```

#### Spring YAML Configuration Example

```
logbook:
  filter:
    form-request-mode: OFF
  predicate:
    exclude:
      - path: /actuator/health
  obfuscate:
    headers:
      - Authorization
      - MD-Access-Key
      - Set-Cookie
      - Cookie
    json-body-fields:
      - password
      - access_token
      - refresh_token
      - id_token
      - googleIdToken
      - appleIdToken
      - confirmPassword
    parameters:
      - access_token
      - password
logbook-extensions:
  access-logging:
    correlation-id-header-name: X-Correlation-ID
    max-logged-request-body-length: 1024
    max-logged-response-body-length: 1024
    included-request-body-media-subtypes:
      - json
      - xml
    included-response-body-media-subtypes:
      - json
      - xml
  http-client:
    max-logged-request-body-length: 1024
    max-logged-response-body-length: 1024
    included-request-body-media-subtypes:
      - x-www-form-urlencoded
      - json
      - xml
    included-response-body-media-subtypes:
      - x-www-form-urlencoded
      - json
      - xml

```

#### Log4j2 Configuration Example

```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="INFO">

    <Properties>
        <Property name="httpclient-request-log-pattern">
            CREQ\t%date{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}\t%X{AL_CORRELATION_ID_HASH}\t%X{AL_REQUEST_HASH}\t%X{HC_REQUEST_HASH}\t%X{userId}\t%X{HC_REQUEST_LINE}\t%X{HC_REQUEST_HEADERS}\t%X{HC_RESPONSE_BODY_LENGTH}\t%replace{%X{HC_REQUEST_BODY}}{^$}{-}%n
        </Property>
        <Property name="httpclient-response-log-pattern">
            CRES\t%date{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}\t%X{AL_CORRELATION_ID_HASH}\t%X{AL_REQUEST_HASH}\t%X{HC_REQUEST_HASH}\t%X{userId}\t%X{HC_REQUEST_LINE}\t%X{HC_RESPONSE_STATUS}\t%X{HC_RESPONSE_HEADERS}\t%X{HC_RESPONSE_BODY_LENGTH}\t%X{HC_RESPONSE_BODY}%n
        </Property>
        <Property name="access-request-log-pattern">
            REQ\t%date{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}\t%X{AL_CORRELATION_ID_HASH}\t%X{AL_REQUEST_HASH}\t%X{userId}\t%X{AL_REQUEST_LINE}\t%X{AL_CLIENT_IP}\t%X{AL_REQUEST_HEADERS}\t%X{AL_REQUEST_BODY_LENGTH}\t%replace{%X{AL_REQUEST_BODY}}{^$}{-}%n
        </Property>
        <Property name="access-response-log-pattern">
            RES\t%date{yyyy-MM-dd'T'HH:mm:ss.SSSXXX}\t%X{AL_CORRELATION_ID_HASH}\t%X{AL_REQUEST_HASH}\t%X{userId}\t%X{AL_REQUEST_LINE}\t%X{AL_PROCESSING_TIME}ms\t%X{AL_RESPONSE_STATUS}\t%X{AL_RESPONSE_HEADERS}\t%X{AL_RESPONSE_BODY_LENGTH}\t%X{AL_RESPONSE_BODY}%n
        </Property>
    </Properties>

    <Appenders>
        <Console name="httpclient-request-logger-console" target="SYSTEM_OUT">
            <PatternLayout pattern="${httpclient-request-log-pattern}"/>
        </Console>
        <Console name="httpclient-response-logger-console" target="SYSTEM_OUT">
            <PatternLayout pattern="${httpclient-response-log-pattern}"/>
        </Console>
        <Console name="access-request-logger-console" target="SYSTEM_OUT">
            <PatternLayout pattern="${access-request-log-pattern}"/>
        </Console>

        <Console name="access-response-logger-console" target="SYSTEM_OUT">
            <PatternLayout pattern="${access-response-log-pattern}"/>
        </Console>
    </Appenders>

    <Loggers>
        <Logger name="httpclient-request-log" additivity="false" level="INFO">
            <AppenderRef ref="httpclient-request-logger-console"/>
        </Logger>
        <Logger name="httpclient-response-log" additivity="false" level="INFO">
            <AppenderRef ref="httpclient-response-logger-console"/>
        </Logger>
        <Logger name="access-request-log" additivity="false" level="INFO">
            <AppenderRef ref="access-request-logger-console"/>
        </Logger>

        <Logger name="access-response-log" additivity="false" level="INFO">
            <AppenderRef ref="access-response-logger-console"/>
        </Logger>
    </Loggers>
</Configuration>
```