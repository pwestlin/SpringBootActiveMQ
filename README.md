# Spring Boot med ActiveMQ
Testar Spring Boot med ActiveMQ.

Applikationen startar automatiskt en ActiveMQ mha Docker (Compose).

Applikationen använder både kö och topic. För topic används både non-durable och durable topic subscribers.

Kön har två lyssnare. Om man skickar meddelande till kön ser man att endast en lyssnare får meddelandet.

Topicen har tre lyssnare varav två är non-durable och en är durable. Om man skickar meddelande till kön ser man att alla lyssnarna får meddelandet.
## Endpoints
```bash
curl -X POST "http://localhost:8080/send/queue?message=HelloQueue"
curl -X POST "http://localhost:8080/send/topic?message=HelloTopic"
```
## ActiveMQ Admin console
[ActiveMQ Admin console](http://localhost:8161/admin/index.jsp)
