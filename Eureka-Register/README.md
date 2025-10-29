# Eureka Service Registry - Dev-Connect

## Overview
Eureka Server acts as the service discovery and registration component for the Dev-Connect microservices architecture. All microservices register with Eureka Server, enabling dynamic service discovery and load balancing.

## Features
- 🔍 **Service Discovery**: Automatic service registration and discovery
- 📊 **Dashboard**: Web-based Eureka dashboard for monitoring services
- 🏥 **Health Monitoring**: Real-time service health checking
- 🔄 **Load Balancing**: Client-side load balancing capabilities
- 📈 **Scaling**: Support for multiple instances of the same service

## Configuration

### Port & Access
- **Server Port**: 8761
- **Dashboard URL**: http://localhost:8761
- **Eureka REST API**: http://localhost:8761/eureka/

### Key Properties
```properties
# Server Configuration
server.port=8761
eureka.instance.hostname=localhost

# Eureka Server Settings
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.server.enable-self-preservation=false
```

## Registered Services

### Auth-Service
- **Service Name**: `auth-service`
- **Port**: 8080
- **Instance ID**: `auth-service:random-value`

### Profile-Service
- **Service Name**: `profile-service`  
- **Port**: 8081
- **Instance ID**: `profile-service:random-value`

## Service Registration Process

1. **Service Startup**: Microservices start and register with Eureka
2. **Health Checks**: Eureka monitors service health via heartbeats
3. **Service Discovery**: Other services can discover registered services
4. **Load Balancing**: Multiple instances automatically load balanced

## Architecture Benefits

### Service Discovery
- Services find each other dynamically using service names
- No need for hardcoded URLs or IP addresses
- Automatic failover when services go down

### Scalability
- Multiple instances of same service automatically registered
- Client-side load balancing distributes requests
- Easy horizontal scaling of services

### Resilience
- Self-healing architecture with health monitoring
- Failed services automatically removed from registry
- New instances automatically discovered

## Running the Eureka Server

### Prerequisites
- Java 17
- Maven

### Steps
1. **Build**: `mvn clean compile`
2. **Run**: `mvn spring-boot:run`
3. **Access**: Open http://localhost:8761

### Verification
1. **Dashboard**: Check Eureka dashboard for registered services
2. **Health**: Verify all services show as "UP"
3. **REST API**: Query `/eureka/apps` for service registry

## Service Registration Order

**Recommended startup sequence:**
1. **Eureka Server** (port 8761) - Start first
2. **Auth-Service** (port 8080) - Registers as 'auth-service'
3. **Profile-Service** (port 8081) - Registers as 'profile-service'
4. **API Gateway** (port 8080) - Will register as 'api-gateway'

## Monitoring & Troubleshooting

### Dashboard Information
- **Service Status**: UP/DOWN status of all services
- **Instance Info**: Service URLs, ports, and metadata
- **Last Heartbeat**: When service last communicated with Eureka

### Common Issues
- **Service Not Registering**: Check eureka.client.service-url.defaultZone
- **Service Shows as DOWN**: Verify service health endpoints
- **Multiple Instances**: Each instance needs unique instance-id

### Health Endpoints
- **Eureka Health**: http://localhost:8761/actuator/health
- **Service Registry**: http://localhost:8761/eureka/apps

## Next Steps
1. **API Gateway**: Implement Spring Cloud Gateway for routing
2. **Config Server**: Centralized configuration management
3. **Circuit Breaker**: Add resilience patterns with Hystrix/Resilience4j
4. **Distributed Tracing**: Implement with Zipkin/Jaeger

## API Gateway Integration
Once API Gateway is implemented, it will:
- Register with Eureka as 'api-gateway'
- Route requests to services using service names
- Provide load balancing across multiple instances
- Act as single entry point for all client requests