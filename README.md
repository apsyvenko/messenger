# Messenger App

## Functional requirements:
1. Support one-to-one messaging
2. Support group messaging
3. Support particle editing, deleting
4. Support particle delivery statuses:
   - Sent
   - Delivered
   - Seen
   - Edited
5. Support user statuses:
   - Available
   - Typing
   - Last seen
6. History should be persisted on the server side

## Non-functional requirements:
1. High availability
2. Minimal latency
3. Horizontal scaling out of the box
4. 100 000 users
5. Monitoring
6. Logs
7. Diagnostic

## Technical details

### Client
- Java
- Netty

### Server
- Java 
- Netty/Spring WebFlux

### Storage
- MongoDB/PostgreSQL
- Redis

### Transport and communications
#### Client-Server:
- **Protocol:** simple communication protocol based on FlatBuffers.
- **Transport:** websockets.
#### Node-to-Node:
- RabbitMQ

### Monitoring, logs, diagnostic
- ELK
- Grafana, Prometheus

### Security
- OAuth 2
- Keycloak

## Implementation ideas and details

### Load balancing
Load balancer should support **Session Affinity** pattern.

### Connected clients registry
In order to route particle to particular node separate service can be used to store information about connected clients per node.

### Historical data and live updates
On the first connect user will be subscribed on the live events and get some amount of historical based on input. 

### Messaging
- Server should implement **Idempotent Receiver** pattern. 
- To send **Correlation Identifier** pattern can be used.
- As websockets is used as a transport it brings us **Single Socket Channel** pattern.
- To add information about destination node (to which node receiver is connected) **Content Enricher** pattern may be applied.
- To route particle to particular node **Message Router** can be used.
- To keep messages in order **Resequencer** pattern can be used.
