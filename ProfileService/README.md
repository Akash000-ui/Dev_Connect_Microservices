# Profile-Service - Dev-Connect

## Overview
Profile-Service is a microservice that manages user profiles in the Dev-Connect LinkedIn clone project. It subscribes to user registration events from Auth-Service via Kafka and provides REST APIs for profile management.

## Features
- 🔔 **Kafka Consumer**: Listens to `auth-events` topic and creates default profiles
- 🔒 **JWT Authentication**: Validates JWT tokens for secured endpoints
- 📝 **CRUD Operations**: Complete profile management APIs
- 🗄️ **MongoDB Integration**: Stores profiles in `profile_db.userProfile` collection
- 📊 **Comprehensive Profile Data**: Education, experience, projects, skills, etc.

## Tech Stack
- **Spring Boot 3.5.6** (Java 17)
- **Spring Security** for JWT validation
- **Spring Data MongoDB** for database operations
- **Spring Kafka** for event consumption
- **Lombok** for boilerplate code reduction
- **Jackson** for JSON serialization

## Architecture

### Profile Model
```java
UserProfile {
    id, userId, email, username, firstName, lastName,
    about, profilePicUrl, location, website, phone,
    skills[], education[], experience[], projects[], interests[],
    createdAt, updatedAt
}
```

### Kafka Integration
- **Topic**: `auth-events`
- **Consumer Group**: `profile-service`
- **Event**: `UserRegisteredEvent` with userId, email, name, timestamp

## API Endpoints

### Public Endpoints
- `GET /api/v1/profile/health` - Health check

### Protected Endpoints (JWT Required)
- `GET /api/v1/profile/me` - Get current user's profile
- `PUT /api/v1/profile/me` - Update current user's profile
- `DELETE /api/v1/profile/me` - Delete current user's profile
- `GET /api/v1/profile/{userId}` - Get profile by userId

## Configuration

### application.properties
```properties
server.port=8081
spring.application.name=ProfileService

# MongoDB
spring.data.mongodb.uri=mongodb+srv://...

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=profile-service

# JWT
jwt.secret=your-secret-key
jwt.expiration=3600000
```

## How It Works

### 1. User Registration Flow
1. User registers in Auth-Service
2. Auth-Service publishes `UserRegisteredEvent` to `auth-events` topic
3. Profile-Service consumes the event
4. Creates default profile with basic info
5. Stores in MongoDB

### 2. Profile Management Flow
1. User authenticates and gets JWT from Auth-Service
2. Includes JWT in Authorization header: `Bearer <token>`
3. Profile-Service validates JWT and extracts userId
4. Performs CRUD operations on user's profile

### 3. Security
- JWT validation on all endpoints except health check
- User can only access/modify their own profile
- Security filter extracts userId from JWT and adds to request

## Data Flow

```
Auth-Service → Kafka (auth-events) → Profile-Service → MongoDB
     ↓                                       ↓
 JWT Token ←→ Frontend ←→ Profile APIs ←→ JWT Validation
```

## Running the Service

### Prerequisites
- Java 17
- Maven
- Kafka running on localhost:9092
- MongoDB Atlas connection

### Steps
1. **Start Kafka**: Ensure Kafka is running
2. **Build**: `mvn clean compile`
3. **Run**: `mvn spring-boot:run`
4. **Test**: Service runs on port 8081

### Verification
1. Check health: `GET http://localhost:8081/api/v1/profile/health`
2. Register user in Auth-Service (port 8080)
3. Check Kafka logs for profile creation
4. Use JWT from auth to access profile APIs

## Testing Profile APIs

### 1. Get Profile
```bash
curl -H "Authorization: Bearer <jwt-token>" \
     http://localhost:8081/api/v1/profile/me
```

### 2. Update Profile
```bash
curl -X PUT \
     -H "Authorization: Bearer <jwt-token>" \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "John",
       "lastName": "Doe",
       "about": "Full-stack developer",
       "skills": ["Java", "React", "MongoDB"]
     }' \
     http://localhost:8081/api/v1/profile/me
```

## Key Components

### 1. UserRegisteredEventConsumer
- Listens to Kafka `auth-events` topic
- Creates default profiles for new users
- Manual acknowledgment for reliability

### 2. ProfileService
- Business logic for profile operations
- Default profile creation
- CRUD operations with validation

### 3. JwtAuthenticationFilter
- Validates JWT tokens
- Extracts userId and sets in SecurityContext
- Adds userId to request attributes

### 4. ProfileController
- REST API endpoints
- JWT-protected operations
- Standardized API responses

## Security Features
- JWT token validation
- User isolation (users can only access their own profiles)
- Input validation with Bean Validation
- Global exception handling

## Next Steps for Integration
1. **Start Kafka**: Ensure Kafka is running locally
2. **Test Integration**: Register user in Auth-Service and verify profile creation
3. **API Testing**: Use Postman/curl to test profile APIs
4. **Frontend Integration**: Connect React frontend to profile APIs
5. **Image Upload**: Add Cloudinary integration for profile pictures

## Monitoring
- Kafka consumer logs for event processing
- API request/response logs
- Exception tracking with global handler
- Health endpoint for service monitoring