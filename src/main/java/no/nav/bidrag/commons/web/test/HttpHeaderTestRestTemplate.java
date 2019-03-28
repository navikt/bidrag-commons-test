package no.nav.bidrag.commons.web.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class HttpHeaderTestRestTemplate {

  private final Set<HeaderGenerator> headerGenerators = new HashSet<>();
  private final TestRestTemplate testRestTemplate;

  public HttpHeaderTestRestTemplate(TestRestTemplate testRestTemplate) {
    this.testRestTemplate = testRestTemplate;
  }

  public HttpHeaderTestRestTemplate(TestRestTemplate testRestTemplate, Collection<? extends HeaderGenerator> headerGenerators) {
    this(testRestTemplate);
    this.headerGenerators.addAll(headerGenerators);
  }

  public <T> ResponseEntity<T> exchange(String url, HttpMethod httpMethod, HttpEntity<?> httpEntity, Class<T> responseClass) {
    return testRestTemplate.exchange(url, httpMethod, newEntityWithaddedHeaders(httpEntity), responseClass);
  }

  public <T> ResponseEntity<T> exchange(String url, HttpMethod httpMethod, HttpEntity<?> httpEntity, ParameterizedTypeReference<T> typeReference) {
    return testRestTemplate.exchange(url, httpMethod, newEntityWithaddedHeaders(httpEntity), typeReference);
  }

  private HttpEntity<?> newEntityWithaddedHeaders(HttpEntity<?> httpEntity) {
    HttpHeaders tempHeaders = new HttpHeaders();
    headerGenerators.forEach(headerGenerator -> tempHeaders.add(headerGenerator.getHeaderName(), headerGenerator.value()));

    Optional.ofNullable(httpEntity).ifPresent(entity -> tempHeaders.putAll(entity.getHeaders()));

    return new HttpEntity<>(
        Optional.ofNullable(httpEntity).map(HttpEntity::getBody).orElse(null),
        tempHeaders
    );
  }

  public void add(HeaderGenerator headerGenerator) {
    headerGenerators.add(headerGenerator);
  }

  public static class HeaderGenerator {

    private final String headerName;
    private final ValueGenerator valueGenerator;

    public HeaderGenerator(String headerName, ValueGenerator valueGenerator) {
      this.headerName = headerName;
      this.valueGenerator = valueGenerator;
    }

    String value() {
      return valueGenerator.generate();
    }

    String getHeaderName() {
      return headerName;
    }
  }

  @FunctionalInterface
  public interface ValueGenerator {

    String generate();
  }
}
