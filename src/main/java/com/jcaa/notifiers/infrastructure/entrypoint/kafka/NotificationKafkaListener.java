package com.jcaa.notifiers.infrastructure.entrypoint.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcaa.notifiers.application.port.in.SendNotificationUseCase;
import com.jcaa.notifiers.domain.exception.InvalidNotificationException;
import com.jcaa.notifiers.infrastructure.entrypoint.kafka.dto.NotificationMessage;
import com.jcaa.notifiers.infrastructure.entrypoint.kafka.mapper.NotificationKafkaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Paso 3 del diagrama: escucha el tópico y entrega cada mensaje al caso de uso de envío. */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

  private static final String LOG_RECEIVED =
      "[NotificationKafkaListener] mensaje recibido. partition={} offset={}";

  private final SendNotificationUseCase sendNotificationUseCase;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group-id}")
  public void onMessage(final ConsumerRecord<String, String> record) {
    log.info(LOG_RECEIVED, record.partition(), record.offset());
    final NotificationMessage message = parse(record.value());
    sendNotificationUseCase.execute(NotificationKafkaMapper.toCommand(message));
  }

  private NotificationMessage parse(final String payload) {
    try {
      return objectMapper.readValue(payload, NotificationMessage.class);
    } catch (final JsonProcessingException | IllegalArgumentException exception) {
      throw InvalidNotificationException.becauseMessageIsUnreadable(exception);
    }
  }
}
