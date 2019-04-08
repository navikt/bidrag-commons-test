package no.nav.bidrag.commons.web.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;


@DisplayName("HttpHeaderTestRestTemplateTest")
class HttpHeaderTestRestTemplateTest {

  private HttpHeaderTestRestTemplate httpHeaderTestRestTemplate;
  @Mock
  private TestRestTemplate testRestTemplateMock;

  @BeforeEach
  void initClassToTestWithCustomHeader() {
    MockitoAnnotations.initMocks(this);
    httpHeaderTestRestTemplate = new HttpHeaderTestRestTemplate(testRestTemplateMock);
    httpHeaderTestRestTemplate.add("X-Custom", () -> "my custom header");
  }

  @Test
  @DisplayName("skal legge ved custom header verdi ved bruk")
  void skalLeggeVedCustomHeaderVerdiVedBruk() {
    httpHeaderTestRestTemplate.exchange("somewhere", null, new HttpEntity<>(null, null), Object.class);

    ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(testRestTemplateMock).exchange(anyString(), any(), entityCaptor.capture(), eq(Object.class));

    assertThat(entityCaptor.getValue()).isNotNull();

    var httpEntity = entityCaptor.getValue();

    assertAll(
        () -> assertThat(httpEntity.getHeaders()).as("headers").hasSize(1),
        () -> assertThat(Objects.requireNonNull(httpEntity.getHeaders().get("X-Custom")).get(0))
            .as("headerValue").contains("my custom header")
    );
  }

  @Test
  @DisplayName("skal ikke overskrive eksisterende headere")
  void skalIkkeOverskriveEksisterendeHeadere() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("something", "completely different");
    httpHeaderTestRestTemplate.exchange("somewhere", null, new HttpEntity<>(null, headers), Object.class);

    ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(testRestTemplateMock).exchange(anyString(), any(), entityCaptor.capture(), eq(Object.class));

    assertThat(entityCaptor.getValue()).isNotNull();
    assertThat(entityCaptor.getValue().getHeaders()).as("headers").hasSize(2);
  }

  @Test
  @DisplayName("skal inititalisere ny HttpEntity når argument er null")
  void skalInitialisereNyHttpEntityNaarArgumentErNull() {
    ParameterizedTypeReference<List<Object>> typeReference = new ParameterizedTypeReference<>() {
    };

    httpHeaderTestRestTemplate.exchange("somewhere", null, null, typeReference);

    ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(testRestTemplateMock).exchange(anyString(), any(), entityCaptor.capture(), eq(typeReference));

    assertThat(entityCaptor.getValue()).isNotNull();

    var httpEntity = entityCaptor.getValue();

    assertAll(
        () -> assertThat(httpEntity.getHeaders()).as("headers").hasSize(1),
        () -> assertThat(Objects.requireNonNull(httpEntity.getHeaders().get("X-Custom")).get(0))
            .as("headerValue").contains("my custom header")
    );
  }
}
