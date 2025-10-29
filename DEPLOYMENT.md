# 🚀 Deployment Guide - Dev-Connect

## Quick Deployment Options

### Option 1: Local Development Setup ✅ CURRENT

**Status:** Ready to use
**Environment:** Local development

```powershell
# Start all services (requires 4-5 terminals)

# Terminal 1 - Eureka Server
cd Eureka-Server\Eureka-Server
mvn spring-boot:run

# Terminal 2 - API Gateway
cd Api-Gateway\Api-Gateway
mvn spring-boot:run

# Terminal 3 - Auth Service
cd Auth-Service\Auth-Service
mvn spring-boot:run

# Terminal 4 - Profile Service
cd ProfileService
mvn spring-boot:run

# Terminal 5 - Frontend
cd dev-connect-frontend
ng serve
```

**Access:**
- Frontend: http://localhost:4200
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8768

---

### Option 2: Docker Compose (Coming Soon)

**Status:** Recommended for team development

```yaml
# docker-compose.yml
version: '3.8'

services:
  eureka-server:
    build: ./Eureka-Server/Eureka-Server
    ports:
      - "8761:8761"
    
  api-gateway:
    build: ./Api-Gateway/Api-Gateway
    ports:
      - "8768:8768"
    depends_on:
      - eureka-server
    
  auth-service:
    build: ./Auth-Service/Auth-Service
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
    environment:
      - SPRING_DATA_MONGODB_URI=${MONGODB_AUTH_URI}
      - JWT_SECRET=${JWT_SECRET}
    
  profile-service:
    build: ./ProfileService
    ports:
      - "8081:8081"
    depends_on:
      - eureka-server
    environment:
      - SPRING_DATA_MONGODB_URI=${MONGODB_PROFILE_URI}
      - JWT_SECRET=${JWT_SECRET}
    
  frontend:
    build: ./dev-connect-frontend
    ports:
      - "80:80"
    depends_on:
      - api-gateway
```

**Usage:**
```powershell
docker-compose up -d
docker-compose logs -f
docker-compose down
```

---

### Option 3: Kubernetes Deployment (Production)

**Status:** For production/staging environments

#### Prerequisites
- Kubernetes cluster (AWS EKS, GCP GKE, Azure AKS, or local Minikube)
- kubectl installed and configured
- Docker images pushed to container registry

#### Deployment Steps

```powershell
# 1. Create namespace
kubectl create namespace dev-connect-prod

# 2. Create secrets
kubectl create secret generic dev-connect-secrets \
  --from-literal=mongodb-auth-uri='your-mongodb-uri' \
  --from-literal=mongodb-profile-uri='your-mongodb-uri' \
  --from-literal=jwt-secret='your-jwt-secret' \
  --from-literal=google-client-id='your-google-client-id' \
  -n dev-connect-prod

# 3. Apply configurations
kubectl apply -f k8s/production/

# 4. Verify deployment
kubectl get pods -n dev-connect-prod
kubectl get services -n dev-connect-prod

# 5. Access application
kubectl port-forward service/frontend 8080:80 -n dev-connect-prod
```

---

### Option 4: Cloud Platform Deployment

#### AWS Deployment

**Services Used:**
- **ECS/EKS** - Container orchestration
- **RDS** - MongoDB Atlas connection
- **ElastiCache** - Redis cache
- **MSK** - Managed Kafka
- **ALB** - Load balancing
- **CloudWatch** - Monitoring

```powershell
# Using AWS Copilot
copilot init --app dev-connect
copilot env init --name production
copilot svc deploy
```

#### Azure Deployment

**Services Used:**
- **AKS** - Kubernetes service
- **Azure Cosmos DB** - MongoDB API
- **Event Hubs** - Kafka-compatible
- **Application Gateway** - Load balancing
- **Monitor** - Application insights

```powershell
# Using Azure CLI
az login
az aks create --resource-group dev-connect-rg --name dev-connect-cluster
az aks get-credentials --resource-group dev-connect-rg --name dev-connect-cluster
kubectl apply -f k8s/production/
```

#### GCP Deployment

**Services Used:**
- **GKE** - Kubernetes Engine
- **Cloud Firestore** - NoSQL database
- **Pub/Sub** - Messaging
- **Cloud Load Balancing**
- **Cloud Monitoring**

```powershell
# Using gcloud
gcloud container clusters create dev-connect-cluster
gcloud container clusters get-credentials dev-connect-cluster
kubectl apply -f k8s/production/
```

---

## Environment Variables

### Required for All Deployments

```properties
# MongoDB
MONGODB_AUTH_URI=mongodb+srv://user:pass@cluster.mongodb.net/auth_db
MONGODB_PROFILE_URI=mongodb+srv://user:pass@cluster.mongodb.net/profile_db

# JWT
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters

# Google OAuth (optional)
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Service Discovery
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
```

---

## Health Checks

All services expose health endpoints:

```bash
# Eureka Server
curl http://localhost:8761/actuator/health

# API Gateway
curl http://localhost:8768/actuator/health

# Auth Service
curl http://localhost:8080/actuator/health

# Profile Service
curl http://localhost:8081/actuator/health
```

---

## Monitoring & Logging

### Option 1: ELK Stack (Elasticsearch, Logstash, Kibana)

```yaml
# docker-compose.monitoring.yml
services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    ports:
      - "9200:9200"
  
  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    ports:
      - "5000:5000"
  
  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    ports:
      - "5601:5601"
```

### Option 2: Prometheus + Grafana

```yaml
services:
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
  
  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
```

---

## Scaling Strategies

### Horizontal Scaling

```powershell
# Kubernetes
kubectl scale deployment auth-service --replicas=5 -n dev-connect-prod
kubectl scale deployment profile-service --replicas=5 -n dev-connect-prod

# Auto-scaling
kubectl autoscale deployment auth-service --cpu-percent=70 --min=3 --max=10
```

### Load Balancing

- **NGINX** - Reverse proxy
- **AWS ALB** - Application Load Balancer
- **Kubernetes Ingress** - Built-in load balancing

---

## Backup & Disaster Recovery

### MongoDB Atlas Automated Backups
- Continuous backup enabled
- Point-in-time recovery
- Cross-region backups

### Git Repository Backups
- GitHub automatic backups
- Mirror to GitLab/Bitbucket (optional)

---

## Security Checklist

- [ ] Environment variables in secrets (not in code)
- [ ] HTTPS/TLS enabled for all services
- [ ] MongoDB authentication enabled
- [ ] JWT secret is strong (32+ characters)
- [ ] CORS properly configured
- [ ] Rate limiting implemented
- [ ] Security headers configured
- [ ] Regular dependency updates
- [ ] Container vulnerability scanning
- [ ] Network policies in Kubernetes

---

## Troubleshooting

### Common Issues

**Service not registering with Eureka:**
```powershell
# Check Eureka URL
echo $EUREKA_CLIENT_SERVICEURL_DEFAULTZONE

# Check service logs
kubectl logs <pod-name> -n dev-connect-prod
```

**MongoDB connection failed:**
```powershell
# Test connection
mongosh "your-mongodb-uri"

# Check IP whitelist in MongoDB Atlas
```

**JWT validation errors:**
```powershell
# Verify JWT secret matches across services
# Check token expiration time
```

---

## Performance Optimization

1. **Enable Caching** - Redis for session management
2. **Database Indexing** - MongoDB indexes for frequently queried fields
3. **CDN for Frontend** - CloudFlare, AWS CloudFront
4. **Compress Responses** - Gzip compression
5. **Connection Pooling** - MongoDB connection pools
6. **Async Processing** - Kafka for non-blocking operations

---

## Cost Optimization

### Free Tier Options
- **MongoDB Atlas** - 512MB free cluster
- **Heroku** - Free dynos (with limitations)
- **AWS Free Tier** - 12 months free for many services
- **GitHub Actions** - 2,000 minutes/month free

### Paid Recommendations
- **MongoDB Atlas M10** - $57/month (production ready)
- **AWS ECS Fargate** - Pay per use
- **DigitalOcean** - $12/month for 2GB droplet

---

## Support & Documentation

- **GitHub Issues**: Report bugs and request features
- **Wiki**: Detailed documentation
- **Slack/Discord**: Team communication
- **Email**: support@devconnect.com

---

**Last Updated:** October 2025
**Version:** 1.0.0
