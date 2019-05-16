package no.nav.bidrag.commons.web.test;

import no.nav.security.oidc.test.support.jersey.TestTokenGeneratorResource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;

public class SecuredTestRestTemplate extends HttpHeaderTestRestTemplate {

  public SecuredTestRestTemplate(TestRestTemplate testRestTemplate) {
    super(testRestTemplate);
    add(HttpHeaders.AUTHORIZATION, SecuredTestRestTemplate::generateTestToken);
  }

  private static String generateTestToken() {
    TestTokenGeneratorResource testTokenGeneratorResource = new TestTokenGeneratorResource();
    return "Bearer " + testTokenGeneratorResource.issueToken("localhost-idtoken");
  }
}
