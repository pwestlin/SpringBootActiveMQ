# Spring Boot med ActiveMQ
Testar Spring Boot med ActiveMQ.

Applikationen startar automatiskt en ActiveMQ mha Docker (Compose).

Applikationen använder både queue och topic. För topic används både non-durable och durable topic subscribers.
## Endpoints
```bash
curl -X POST "http://localhost:8080/send/queue?message=HelloQueue"
curl -X POST "http://localhost:8080/send/topic?message=HelloTopic"
```
## ActiveMQ Admin console
[ActiveMQ Admin console](http://localhost:8161/admin/index.jsp)
