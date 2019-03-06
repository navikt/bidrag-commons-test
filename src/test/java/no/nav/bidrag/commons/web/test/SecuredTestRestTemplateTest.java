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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@DisplayName("SecuredTestRestTemplate")
class SecuredTestRestTemplateTest {

  @InjectMocks
  private SecuredTestRestTemplate securedTestRestTemplate;
  @Mock
  private TestRestTemplate testRestTemplateMock;

  @BeforeEach
  void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("skal legge på OIDC token før exchange-kall med TestRestTemplate")
  void skalLeggePaOidcToken() {
    securedTestRestTemplate.exchange("somewhere", null, new HttpEntity<>(null, null), Object.class);

    ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(testRestTemplateMock).exchange(anyString(), any(), entityCaptor.capture(), eq(Object.class));

    assertThat(entityCaptor.getValue()).isNotNull();

    var httpEntity = entityCaptor.getValue();

    assertAll(
        () -> assertThat(httpEntity.getHeaders()).as("headers").hasSize(1),
        () -> assertThat(Objects.requireNonNull(httpEntity.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0))
            .as("token").contains("Bearer ")
    );
  }

  @Test
  @DisplayName("skal ikke overskrive eksisterende headere")
  void skalIkkeOverskriveEksisterendeHeadere() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("something", "completely different");
    securedTestRestTemplate.exchange("somewhere", null, new HttpEntity<>(null, headers), Object.class);

    ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(testRestTemplateMock).exchange(anyString(), any(), entityCaptor.capture(), eq(Object.class));

    assertThat(entityCaptor.getValue()).isNotNull();
    assertThat(entityCaptor.getValue().getHeaders()).as("headers").hasSize(2);
  }

  @Test
  @SuppressWarnings("unchecked")
  @DisplayName("skal inititalisere ny HttpEntity når argument er null")
  void skalInitialisereNyHttpEntityNaarArgumentErNull() {
    securedTestRestTemplate.exchange("somewhere", null, null, new ParameterizedTypeReference<List<Object>>() {
    });

    ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(testRestTemplateMock).exchange(anyString(), any(), entityCaptor.capture(), any(ParameterizedTypeReference.class));

    assertThat(entityCaptor.getValue()).isNotNull();

    var httpEntity = entityCaptor.getValue();

    assertAll(
        () -> assertThat(httpEntity.getHeaders()).as("headers").hasSize(1),
        () -> assertThat(Objects.requireNonNull(httpEntity.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0))
            .as("token").contains("Bearer ")
    );
  }
}
