# 🚀 Dev-Connect - Professional Social Network Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-18-red.svg)](https://angular.io/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green.svg)](https://www.mongodb.com/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Driven-black.svg)](https://kafka.apache.org/)

A modern, scalable professional social networking platform built with **Microservices Architecture**, inspired by LinkedIn. Built with Spring Boot, Angular 18, MongoDB Atlas, and Apache Kafka.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Getting Started](#-getting-started)
- [Microservices](#-microservices)
- [API Documentation](#-api-documentation)
- [Contributing](#-contributing)

---

## 🌟 Overview

**Dev-Connect** is a comprehensive social networking platform designed for professionals. It provides:

- ✅ **Secure Authentication** with JWT and Google OAuth2
- ✅ **Rich User Profiles** with education, experience, and skills
- ✅ **Content Sharing** with posts, likes, comments, and shares
- ✅ **Professional Networking** with connection management
- ✅ **Real-time Features** with WebSocket and Kafka events
- ✅ **Responsive UI** with Angular Material Design

---

## 🏗️ Architecture

### Microservices Architecture

```
                    ┌─────────────────────┐
                    │   Angular Frontend  │
                    │     (Port 4200)     │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │    API Gateway      │
                    │     (Port 8768)     │
                    └──────────┬──────────┘
                               │
        ┏━━━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━┓
        ▼                      ▼                      ▼
┌───────────────┐    ┌───────────────┐      ┌───────────────┐
│ Auth Service  │    │Profile Service│      │ Other Services│
│  (Port 8080)  │    │  (Port 8081)  │      │  (Port 80XX)  │
└───────┬───────┘    └───────┬───────┘      └───────┬───────┘
        │                    │                       │
        └────────────────────┼───────────────────────┘
                             │
                  ┌──────────▼──────────┐
                  │  Eureka Discovery   │
                  │    (Port 8761)      │
                  └─────────────────────┘
        
        ┌──────────┐      ┌─────────┐      ┌─────────┐
        │ MongoDB  │      │  Kafka  │      │  Redis  │
        │  Atlas   │      │ Events  │      │  Cache  │
        └──────────┘      └─────────┘      └─────────┘
```

---

## 🛠️ Tech Stack

### Backend
- **Spring Boot 3.3.4** - Microservices framework
- **Spring Cloud Gateway** - API Gateway and routing
- **Spring Cloud Netflix Eureka** - Service discovery
- **Spring Security + JWT** - Authentication & authorization
- **Spring Data MongoDB** - Database operations
- **Spring Kafka** - Event-driven architecture
- **MongoDB Atlas** - Cloud NoSQL database

### Frontend
- **Angular 18** - Modern web framework
- **Angular Material** - UI component library
- **RxJS** - Reactive programming
- **TypeScript** - Type-safe development
- **SCSS** - Advanced styling

### DevOps
- **Docker** - Containerization
- **Kubernetes** - Orchestration (production ready)
- **GitHub Actions** - CI/CD pipeline
- **Nginx** - Reverse proxy

---

## ✨ Features

### Current Features
- 🔐 **Authentication**
  - Email/Password registration and login
  - Google OAuth2 integration
  - JWT token-based authentication
  - Secure password encryption with BCrypt

- 👤 **User Profiles**
  - Comprehensive profile management
  - Education and experience tracking
  - Skills and projects showcase
  - Profile picture upload

- 📝 **Posts & Feed**
  - Create text and image posts
  - Like, comment, and share functionality
  - Real-time feed updates
  - Post engagement tracking

- 🎨 **UI/UX**
  - LinkedIn-inspired responsive design
  - Material Design components
  - Dark/Light mode support
  - Mobile-responsive layout

### Upcoming Features
- 💬 Real-time messaging
- 🔔 Smart notifications
- 🤝 Connection requests
- 🔍 Advanced search
- 📊 Analytics dashboard
- 🎥 Video posts

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:

- **Java 17+** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **MongoDB Atlas Account** - [Sign Up](https://www.mongodb.com/cloud/atlas)
- **Apache Kafka** - [Download](https://kafka.apache.org/downloads)
- **Git** - [Download](https://git-scm.com/downloads)

### Quick Start

#### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/Dev-Connect.git
cd Dev-Connect
```

#### 2. Setup MongoDB Atlas

1. Create a MongoDB Atlas account
2. Create two databases: `auth_db` and `profile_db`
3. Get your connection strings
4. Whitelist your IP address

#### 3. Configure Environment Variables

Create `application.properties` for each service:

**Auth-Service** (`Auth-Service/Auth-Service/src/main/resources/application.properties`):

```properties
server.port=8080
spring.application.name=auth-service

# MongoDB
spring.data.mongodb.uri=mongodb+srv://user:pass@cluster.mongodb.net/auth_db

# JWT
jwt.secret=your-secret-key-change-in-production
jwt.expiration=86400000

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Google OAuth
google.clientId=your-google-client-id.apps.googleusercontent.com

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

**Profile-Service** (`ProfileService/src/main/resources/application.properties`):

```properties
server.port=8081
spring.application.name=profile-service

# MongoDB
spring.data.mongodb.uri=mongodb+srv://user:pass@cluster.mongodb.net/profile_db

# JWT
jwt.secret=your-secret-key-change-in-production

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=profile-service

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

#### 4. Start Kafka

```bash
# Start Zookeeper
bin\windows\zookeeper-server-start.bat config\zookeeper.properties

# Start Kafka Server (new terminal)
bin\windows\kafka-server-start.bat config\server.properties
```

#### 5. Start Backend Services

Open separate terminals for each service:

**Terminal 1 - Eureka Server:**
```bash
cd Eureka-Server\Eureka-Server
mvn spring-boot:run
```

**Terminal 2 - API Gateway:**
```bash
cd Api-Gateway\Api-Gateway
mvn spring-boot:run
```

**Terminal 3 - Auth Service:**
```bash
cd Auth-Service\Auth-Service
mvn spring-boot:run
```

**Terminal 4 - Profile Service:**
```bash
cd ProfileService
mvn spring-boot:run
```

#### 6. Start Frontend

```bash
cd dev-connect-frontend
npm install
ng serve
```

### Access the Application

- **Frontend:** http://localhost:4200
- **Eureka Dashboard:** http://localhost:8761
- **API Gateway:** http://localhost:8768

---

## 🎯 Microservices

### 1. Eureka Service Registry (Port: 8761)
Service discovery and registration for all microservices.

**Location:** `/Eureka-Server`

### 2. API Gateway (Port: 8768)
Single entry point for all client requests with routing and authentication.

**Location:** `/Api-Gateway`

**Features:**
- Request routing
- JWT validation
- CORS configuration
- Load balancing

### 3. Auth-Service (Port: 8080)
Handles user authentication and authorization.

**Location:** `/Auth-Service`

**Endpoints:**
```
POST /api/auth/register      - Register new user
POST /api/auth/login         - Login user
POST /api/auth/google        - Google OAuth login
DELETE /api/auth/delete/{id} - Delete account
```

### 4. Profile-Service (Port: 8081)
Manages user profiles and related data.

**Location:** `/ProfileService`

**Endpoints:**
```
GET  /api/v1/profile/me        - Get current user profile
PUT  /api/v1/profile/me        - Update profile
DELETE /api/v1/profile/me      - Delete profile
GET  /api/v1/profile/{userId}  - Get user profile by ID
```

### 5. Frontend (Port: 4200)
Angular-based responsive web application.

**Location:** `/dev-connect-frontend`

---

## 📚 API Documentation

### Authentication Flow

1. **Register:**
```http
POST http://localhost:8768/auth-service/api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "username": "johndoe"
}
```

2. **Login:**
```http
POST http://localhost:8768/auth-service/api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

3. **Access Protected Resource:**
```http
GET http://localhost:8768/profile-service/api/v1/profile/me
Authorization: Bearer <your-jwt-token>
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License.

---

## 👥 Authors

- **Your Name** - [GitHub](https://github.com/yourusername)

---

## 🙏 Acknowledgments

- LinkedIn for UI/UX inspiration
- Spring Boot and Angular communities
- MongoDB Atlas team
- Apache Kafka project

---

## 📞 Support

For support, email support@devconnect.com or open an issue on GitHub.

---

<div align="center">

**Built with ❤️ by the Dev-Connect Team**

⭐ Star us on GitHub — it motivates us a lot!

</div>
