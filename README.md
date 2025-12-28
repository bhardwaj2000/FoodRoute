# 🍔 FoodRoute – Food Delivery Platform
## Day-1: Service Discovery & API Gateway Setup

FoodRoute is a Spring Boot microservices–based food delivery platform (inspired by Swiggy/Zomato) designed to demonstrate real-world distributed system architecture, cloud-native patterns, and production-ready practices.

This repository currently contains the Day-1 platform foundation, which all future microservices will build upon.

---

## 🏗️ Day-1 Objective
Establish a solid microservices platform layer (service discovery + API gateway + routing) before adding any business logic.

### ✅ What we achieved
- Service Discovery using Eureka
- API Gateway using Spring Cloud Gateway (WebFlux + Netty)
- End-to-end routing verification with a dummy service
- Load-balanced, dynamic service resolution
- Clean separation of responsibilities

---

## 🧩 Modules in Day-1
```
FoodRoute
├── foodroute-discovery        # Eureka Server
├── foodroute-gateway          # API Gateway (WebFlux + Netty)
└── foodroute-dummy-service    # Test service for routing validation
```

---

## 🧠 Architecture Overview
Client  
↓  
API Gateway (Spring Cloud Gateway)  
↓  
Service Discovery (Eureka)  
↓  
Downstream Microservices (Dummy / Order / Payment / etc.)

### Key Principles Applied
- Single entry point via Gateway
- No hardcoded service URLs
- Dynamic service registration
- Load balancing via service name
- Non-blocking I/O (WebFlux + Netty)

---

## ⚙️ Tech Stack (Day-1)

Component | Technology
--- | ---
Language | Java 21
Framework | Spring Boot 3.x
Gateway | Spring Cloud Gateway (WebFlux)
Discovery | Eureka
Build Tool | Maven
Communication | HTTP (via Gateway)

---

## ▶️ Prerequisites
- Java 21 (JDK)
- Maven 3.8+
- Git (to clone repo)
- (Optional) IDE: IntelliJ / VS Code

---

## ▶️ How to Run (Local)

1️⃣ Start Eureka Server
```bash
cd foodroute-discovery
mvn spring-boot:run
```
Eureka Dashboard: http://localhost:8761

2️⃣ Start Dummy Service
```bash
cd foodroute-dummy-service
mvn spring-boot:run
```
This service:
- Runs on a dynamic port
- Registers itself with Eureka
- Exposes:
  - GET /dummy/hello

3️⃣ Start API Gateway
```bash
cd foodroute-gateway
mvn spring-boot:run
```
Gateway runs on: http://localhost:8080

---

## 🧪 Verify Routing (MOST IMPORTANT)
Call the dummy service via Gateway:
```
http://localhost:8080/foodroute-dummy-service/dummy/hello
```

### ✅ Expected Response
```
Hello from FoodRoute Dummy Service!
```

This confirms:
- Eureka registration works
- Gateway routing works
- Load balancer resolves service dynamically
- Netty-based WebFlux stack is active

---

## ⚠️ Known Warnings (Expected)
You may see warnings related to:
- Gateway starter deprecation
- LoadBalancer cache (Caffeine recommendation)
- Property migration warnings

These are non-blocking and expected with newer Spring versions; they will be addressed in later phases.

---

## 📌 Git Commit (Day-1)
`Day-1: Discovery + Gateway + routing working`

This commit represents a stable platform baseline.
