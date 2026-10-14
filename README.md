# notifiers-service

Microservicio **notifiers** del parcial.

1. **Paso 3** – Consumidor Kafka que escucha el tópico `notificaciones` (grupo `notifiers`).
2. **Paso 4** – Por cada mensaje envía el correo con la **API HTTP de Brevo** (no SMTP: Render bloquea SMTP).

Formato del mensaje que lee (lo escriben `users` y `accounts`):

```json
{ "email": "ana@gmail.com", "nombre": "Ana Pérez", "asunto": "Bienvenida a Gestión de Usuarios", "mensaje": "<p>Hola Ana, tu usuario fue creado.</p>" }
```

- Si Brevo falla se reintenta 3 veces (cada 5 s) y luego se descarta el mensaje con un log de error.
- Un mensaje mal formado se descarta sin reintentos.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` o `/health` | Responde `{"service":"notifiers","status":"UP"}`. **Abrir antes de probar** para despertar el servicio en Render |
| GET | `/swagger-ui.html` | Swagger |

> En el plan gratuito Render duerme el servicio tras 15 min sin tráfico y dormido no lee Kafka.
> Los mensajes no se pierden: se envían cuando despierta.

## Variables de entorno (Render)

| Variable | Ejemplo |
|---|---|
| `BREVO_API_KEY` | `xkeysib-...` |
| `BREVO_FROM_ADDRESS` | correo remitente **verificado** en Brevo |
| `BREVO_FROM_NAME` | `Gestion de Usuarios` |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka-xxxx.aivencloud.com:12345` *(puerto SASL)* |
| `KAFKA_USERNAME` | `avnadmin` |
| `KAFKA_PASSWORD` | *(Aiven)* |
| `KAFKA_CA_CERT` | contenido completo de `ca.pem` |

## Ejecutar en local

```bash
./mvnw test
./mvnw spring-boot:run   # puerto 8082
```

## Despliegue en Render

New → Web Service → conectar este repositorio → **Runtime: Docker** → agregar las variables de arriba → Deploy.
