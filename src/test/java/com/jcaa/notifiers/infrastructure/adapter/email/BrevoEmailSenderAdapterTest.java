package com.jcaa.notifiers.infrastructure.adapter.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.jcaa.notifiers.domain.exception.EmailSenderException;
import com.jcaa.notifiers.domain.model.EmailDestinationModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@DisplayName("BrevoEmailSenderAdapter")
class BrevoEmailSenderAdapterTest {

  private static final String BASE_URL = "http://brevo.test";
  private static final String API_KEY = "xkeysib-test";
  private static final String EXPECTED_BODY =
      "{\"sender\":{\"email\":\"remitente@test.local\",\"name\":\"Gestion de Usuarios\"},"
          + "\"to\":[{\"email\":\"ana@gmail.com\",\"name\":\"Ana Perez\"}],"
          + "\"subject\":\"Bienvenida\",\"htmlContent\":\"<p>Hola</p>\"}";

  private MockRestServiceServer server;
  private BrevoEmailSenderAdapter adapter;
  private EmailDestinationModel destination;

  @BeforeEach
  void setUp() {
    final RestClient.Builder builder =
        RestClient.builder().baseUrl(BASE_URL).defaultHeader("api-key", API_KEY);
    server = MockRestServiceServer.bindTo(builder).build();
    adapter =
        new BrevoEmailSenderAdapter(
            builder.build(), new BrevoConfig("remitente@test.local", "Gestion de Usuarios"));
    destination =
        new EmailDestinationModel("ana@gmail.com", "Ana Perez", "Bienvenida", "<p>Hola</p>");
  }

  @Test
  @DisplayName("send() llama a la API de Brevo con la api-key y el cuerpo esperado")
  void shouldCallBrevoApi() {
    // Arrange
    server
        .expect(requestTo(BASE_URL + "/v3/smtp/email"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header("api-key", API_KEY))
        .andExpect(content().json(EXPECTED_BODY, true))
        .andRespond(
            withStatus(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"messageId\":\"<abc@brevo>\"}"));

    // Act & Assert
    assertDoesNotThrow(() -> adapter.send(destination));
    server.verify();
  }

  @Test
  @DisplayName("send() lanza EmailSenderException cuando Brevo rechaza la petición")
  void shouldThrowWhenBrevoRejects() {
    // Arrange
    server.expect(requestTo(BASE_URL + "/v3/smtp/email")).andRespond(withStatus(HttpStatus.UNAUTHORIZED));

    // Act & Assert
    assertThrows(EmailSenderException.class, () -> adapter.send(destination));
  }
}
