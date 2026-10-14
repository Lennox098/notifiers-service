package com.jcaa.notifiers.infrastructure.entrypoint.rest.controller;

import com.jcaa.notifiers.infrastructure.entrypoint.rest.dto.response.HealthRestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Render (plan gratuito) duerme el servicio tras 15 min sin tráfico y dormido no lee Kafka.
 * Abrir esta URL lo despierta; los mensajes pendientes se envían al arrancar.
 */
@RestController
@Tag(name = "Health", description = "Estado del microservicio notifiers")
public class HealthRestController {

  private static final String SERVICE_NAME = "notifiers";
  private static final String STATUS_UP = "UP";

  @GetMapping({"/", "/health"})
  @Operation(summary = "Despertar / verificar el servicio")
  public HealthRestResponse health() {
    return new HealthRestResponse(SERVICE_NAME, STATUS_UP);
  }
}
