package com.jcaa.notifiers.infrastructure.adapter.email;

import com.jcaa.notifiers.application.port.out.EmailSenderPort;
import com.jcaa.notifiers.domain.exception.EmailSenderException;
import com.jcaa.notifiers.domain.model.EmailDestinationModel;
import com.jcaa.notifiers.infrastructure.adapter.email.dto.BrevoEmailRequest;
import com.jcaa.notifiers.infrastructure.adapter.email.dto.BrevoEmailRequest.BrevoContact;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Envía el correo con la API HTTP de Brevo (no SMTP: Render bloquea los puertos SMTP).
 * La clave API (xkeysib-...) la agrega el RestClient configurado en BrevoSpringConfig.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BrevoEmailSenderAdapter implements EmailSenderPort {

  private static final String SEND_EMAIL_PATH = "/v3/smtp/email";
  private static final String LOG_SENT = "[BrevoEmailSenderAdapter] correo enviado con Brevo.";

  private final RestClient brevoRestClient;
  private final BrevoConfig brevoConfig;

  @Override
  public void send(final EmailDestinationModel destination) {
    final BrevoEmailRequest request =
        new BrevoEmailRequest(
            new BrevoContact(brevoConfig.fromAddress(), brevoConfig.fromName()),
            List.of(
                new BrevoContact(
                    destination.getDestinationEmail(), destination.getDestinationName())),
            destination.getSubject(),
            destination.getBody());
    try {
      brevoRestClient
          .post()
          .uri(SEND_EMAIL_PATH)
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .body(request)
          .retrieve()
          .toBodilessEntity();
      log.info(LOG_SENT);
    } catch (final RestClientException restClientException) {
      throw EmailSenderException.becauseProviderFailed(restClientException);
    }
  }
}
