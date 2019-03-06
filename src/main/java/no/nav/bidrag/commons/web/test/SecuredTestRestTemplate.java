package no.nav.bidrag.commons.web.test;

import java.util.Optional;
import no.nav.security.oidc.test.support.jersey.TestTokenGeneratorResource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class SecuredTestRestTemplate {

  private final TestRestTemplate testRestTemplate;
  private String testBearerToken;

  SecuredTestRestTemplate(TestRestTemplate testRestTemplate) {
    this.testRestTemplate = testRestTemplate;
  }

  public <T> ResponseEntity<T> exchange(String url, HttpMethod httpMethod, HttpEntity<?> httpEntity, Class<T> responseClass) {
    return testRestTemplate.exchange(url, httpMethod, secure(httpEntity), responseClass);
  }

  public <T> ResponseEntity<T> exchange(String url, HttpMethod httpMethod, HttpEntity<?> httpEntity, ParameterizedTypeReference<T> typeReference) {
    return testRestTemplate.exchange(url, httpMethod, secure(httpEntity), typeReference);
  }

  private HttpEntity<?> secure(HttpEntity<?> httpEntity) {
    HttpHeaders tempHeaders = new HttpHeaders();

    tempHeaders.add(
        HttpHeaders.AUTHORIZATION,
        Optional.ofNullable(testBearerToken).orElseGet(this::generateTestBearerToken)
    );

    Optional.ofNullable(httpEntity).ifPresent(entity -> tempHeaders.putAll(entity.getHeaders()));

    return new HttpEntity<>(
        Optional.ofNullable(httpEntity).map(HttpEntity::getBody).orElse(null),
        tempHeaders
    );
  }

  private String generateTestBearerToken() {
    TestTokenGeneratorResource testTokenGeneratorResource = new TestTokenGeneratorResource();
    testBearerToken = "Bearer " + testTokenGeneratorResource.issueToken("localhost-idtoken");

    return testBearerToken;
  }
}
