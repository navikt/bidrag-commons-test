package no.nav.bidrag.commons.web.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class HttpHeaderTestRestTemplate {

  private final Stack<String[]> headersForSingleCallbacks = new Stack<>();
  private final Map<String, ValueGenerator> valueGenerators = new HashMap<>();
  private final TestRestTemplate testRestTemplate;

  public HttpHeaderTestRestTemplate(TestRestTemplate testRestTemplate) {
    this.testRestTemplate = testRestTemplate;
  }

  public <T> ResponseEntity<T> exchange(String url, HttpMethod httpMethod, HttpEntity<?> httpEntity, Class<T> responseClass) {
    return testRestTemplate.exchange(url, httpMethod, newEntityWithaddedHeaders(httpEntity), responseClass);
  }

  public <T> ResponseEntity<T> exchange(String url, HttpMethod httpMethod, HttpEntity<?> httpEntity, ParameterizedTypeReference<T> typeReference) {
    return testRestTemplate.exchange(url, httpMethod, newEntityWithaddedHeaders(httpEntity), typeReference);
  }

  private HttpEntity<?> newEntityWithaddedHeaders(HttpEntity<?> httpEntity) {
    HttpHeaders tempHeaders = new HttpHeaders();
    valueGenerators.forEach((key, value) -> tempHeaders.add(key, value.generate()));

    while (!headersForSingleCallbacks.empty()) {
      String[] headerWithValue = headersForSingleCallbacks.pop();
      tempHeaders.add(headerWithValue[0], headerWithValue[1]);
    }

    Optional.ofNullable(httpEntity).ifPresent(entity -> tempHeaders.putAll(entity.getHeaders()));

    return new HttpEntity<>(
        Optional.ofNullable(httpEntity).map(HttpEntity::getBody).orElse(null),
        tempHeaders
    );
  }

  public void add(String headerName, ValueGenerator valueGenerator) {
    valueGenerators.put(headerName, valueGenerator);
  }

  public void addHeaderForSingleHttpEntityCallback(String headerName, String headerValue) {
    String[] headerWithValue = {headerName, headerValue};
    headersForSingleCallbacks.push(headerWithValue);
  }

  @FunctionalInterface
  public interface ValueGenerator {

    String generate();
  }
}
