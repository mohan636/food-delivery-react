# GourmetFlow - Online Food Order Processing System

A Maven multi-module microservices workspace built using Java 17, Spring Boot 3.x, Spring Data JPA, Hibernate, MySQL, Apache ActiveMQ, Camunda BPMN v7, and React.

## System Architecture

The project consists of 4 Java-based microservices and a single React-based single page application (SPA):

```
food-order-system (Parent Module)
├── order-service (Port 8081)    - Orchestrator using Camunda BPMN to drive food orders
├── payment-service (Port 8082)  - Simulates payment transaction authorizations
├── kitchen-service (Port 8083)  - Tracks prep orders and cooking ticket status
├── delivery-service (Port 8084) - Manages logistics and dispatch couriers
└── food-del (Port 5173 / Vite) - Dashboard to orchestrate, simulate, and monitor events
```

---

## Tech Stack Overview

- **Java**: Version 17
- **Build Tool**: Apache Maven
- **Spring Boot**: 3.3.0
- **Workflow Engine**: Camunda BPMN (v7.21.0) embedded inside `order-service`
- **Database**: MySQL (using 4 separate database schemas for database-per-service isolation)
- **Message Broker**: Apache ActiveMQ (JMS) for asynchronous events
- **Frontend**: React + Vite (Vanilla CSS design system)

---

## Prerequisites

Before running the application, make sure you have installed:

1. **JDK 17** (Ensure `JAVA_HOME` is set)
2. **Maven 3.8+** (Or use standard `mvnw` wrapper)
3. **MySQL Server**
4. **Node.js (v18+)** & **npm (v9+)** (Vite builds automatically in Maven using `frontend-maven-plugin`)
5. **Apache ActiveMQ** (Or use Docker to launch a broker)

---

## Configuration & Databases

Each service uses its own application configuration properties under `src/main/resources/application.properties`.

### Port Allocations

- **order-service**: `8081`
- **payment-service**: `8082`
- **kitchen-service**: `8083`
- **delivery-service**: `8084`
- **react-ui**: `5173` (Vite dev server)

### Database Creation

Run the following SQL queries in your MySQL instance to create the required databases before starting the services:

```sql
CREATE DATABASE IF NOT EXISTS food_order_db;
CREATE DATABASE IF NOT EXISTS food_payment_db;
CREATE DATABASE IF NOT EXISTS food_kitchen_db;
CREATE DATABASE IF NOT EXISTS food_delivery_db;
```

_Note: Database credentials default to `root` / `password`. Modify these in the respective `application.properties` files if necessary._

### ActiveMQ Broker

Ensure ActiveMQ is running and accepting TCP connections at:
`tcp://localhost:61616` (Default web console: `http://localhost:8161` with credentials `admin` / `admin`).

---

## Standard Project Build Steps

To compile, build, and bundle the entire workspace (including downloading Node, installing React dependencies, and compiling the static build bundle) run:

```bash
mvn clean install
```

This single command will:

1. Compile and build the java code in all 4 microservices.
2. Trigger the `frontend-maven-plugin` inside the `react-ui` directory to download Node/NPM local binaries, run `npm install`, and output a production bundle.

---

## Running the Services

### 1. Run the Backend Microservices

You can run the microservices by navigating to each directory and using the Spring Boot plugin or running the built JAR:

```bash
# Example for order-service
cd order-service
mvn spring-boot:run
```

Repeat the same steps for `payment-service`, `kitchen-service`, and `delivery-service`.

### 2. Run the React UI Dashboard

You can run the React UI in development mode with Hot Module Replacement (HMR) directly:

```bash
cd food-del
npm install
npm run dev
```

Open your browser to `http://localhost:5173` to explore the GourmetFlow console dashboard.

---

## BPMN & Messaging Choreography Flow

When an order is created, the system executes the following flow:

1. **order-service** starts a new BPMN workflow instance and publishes `ORDER_CREATED` to ActiveMQ.
2. **payment-service** processes payment and publishes `PAYMENT_PROCESSED`.
3. **kitchen-service** accepts the payment ticket, cooks the order, and publishes `ORDER_PREPARED`.
4. **delivery-service** dispatches a courier rider and publishes `ORDER_DELIVERED`.
5. **order-service** completes the BPMN instance in Camunda engine.
