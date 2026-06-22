# GourmetFlow - Food Order Processing System

GourmetFlow is a modern, event-driven food order processing system designed using a microservices architecture. It automates and orchestrates the full lifecycle of a food order, from placement to delivery, using an embedded workflow engine and asynchronous event messaging.

---

## Project Overview

GourmetFlow is built as a Maven multi-module microservices application coupled with a dynamic React frontend dashboard. The project decouples core business operations—ordering, payment processing, kitchen preparation, and delivery dispatch—into dedicated microservices. An embedded Camunda BPMN engine orchestrates the order workflow, ensuring states are processed reliably and in the correct sequence. The services communicate asynchronously through JMS events via Apache ActiveMQ, and state changes are reflected in real-time on the React dashboard.

---

## Microservices Architecture

The system consists of the following components, each running as a decoupled service with its own dedicated database:

- **`order-service` (Port 8081)**: The core orchestrator embedding the Camunda BPMN engine. It coordinates the business process flow and manages the lifecycle of orders.
- **`payment-service` (Port 8082)**: Simulates the payment transaction processing for orders.
- **`kitchen-service` (Port 8083)**: Manages food preparation workflows and kitchen status.
- **`delivery-service` (Port 8084)**: Coordinates delivery driver assignment, dispatch, and final package handoff.
- **`food-del` (Port 5173)**: React dashboard user interface built with Vite, providing a visual representation of order tracking.

---

## Tech Stack

- **Backend Core**: Java 17, Spring Boot 3.x
- **Build Tool**: Maven (Multi-module structure)
- **Database**: MySQL (Separate database instance per microservice)
- **Persistence**: Spring Data JPA & Hibernate
- **Messaging**: Apache ActiveMQ (JMS Messaging)
- **Workflow Orchestration**: Camunda BPMN 7.21 (Embedded in `order-service`)
- **Frontend**: React, Vite

---

## Key Features

- **Microservices Architecture**: Domain-driven design isolating responsibilities into distinct Maven modules with isolated MySQL databases to prevent database sharing.
- **BPMN Workflow Orchestration**: Uses an embedded Camunda engine to model, run, and track the end-to-end order processing lifecycle.
- **Asynchronous JMS Messaging**: Decoupled, event-driven service communication utilizing Apache ActiveMQ to publish and consume order lifecycle events.
- **React Frontend Dashboard**: A sleek, responsive dashboard built with React and Vite to view order progress and statuses dynamically.

---

## Project Structure

```
food-order-system (Parent Project)
├── order-service/        # BPMN workflow orchestrator (Port 8081)
├── payment-service/      # Payment simulator (Port 8082)
├── kitchen-service/      # Food preparation manager (Port 8083)
├── delivery-service/     # Delivery and dispatch manager (Port 8084)
├── food-del/             # React dashboard UI (Vite) (Port 5173)
└── pom.xml               # Parent Maven POM
```

---

## Prerequisites

Before setting up and running the application, ensure you have the following installed:
- **Java Development Kit (JDK)**: Version 17
- **Apache Maven**: Version 3.x
- **MySQL Database**: Running instance with separate schemas configured for each service
- **Node.js & npm**: (For building and running the React frontend)
- **Apache ActiveMQ**: Message broker running and accessible

---

## How to Run

Follow these steps to run the complete GourmetFlow system locally:

### 1. Build the Parent Project
Run the Maven build command from the root directory (`food-order-system`) to compile and package all backend modules:
```bash
mvn clean install
```

### 2. Start the Backend Microservices
Run each Spring Boot microservice separately. Open a new terminal window/tab for each service directory and execute:

* **Order Service**:
  ```bash
  cd order-service
  mvn spring-boot:run
  ```
* **Payment Service**:
  ```bash
  cd payment-service
  mvn spring-boot:run
  ```
* **Kitchen Service**:
  ```bash
  cd kitchen-service
  mvn spring-boot:run
  ```
* **Delivery Service**:
  ```bash
  cd delivery-service
  mvn spring-boot:run
  ```

### 3. Run the React Frontend
Navigate to the frontend module, install dependencies, and start the Vite development server:
```bash
cd food-del
npm install
npm run dev
```
Open your browser and navigate to `http://localhost:5173` to access the dashboard.

---

## Workflow Explanation

GourmetFlow follows an event-driven workflow orchestrated by Camunda BPMN inside the `order-service`. The lifecycle proceeds as follows:

```
[Order Created] ➔ [Payment Processed] ➔ [Order Prepared] ➔ [Order Delivered] ➔ [Workflow Completed]
```

1. **Order Created**: An order is initiated via the React frontend. The `order-service` instantiates a new process instance in the embedded Camunda BPMN engine and publishes an `Order Created` JMS event to ActiveMQ.
2. **Payment Processed**: The `payment-service` consumes the `Order Created` event, processes/simulates the payment, and publishes a `Payment Processed` JMS event back to ActiveMQ.
3. **Order Prepared**: Upon receiving the payment confirmation, the workflow instructs the `kitchen-service` (via JMS) to begin preparation. Once cooking is finished, the `kitchen-service` publishes an `Order Prepared` event.
4. **Order Delivered**: The `delivery-service` receives the preparation event, dispatches a driver, and publishes an `Order Delivered` event once the food reaches the customer.
5. **Workflow Completed**: The `order-service` consumes the final delivery event, signaling the Camunda BPMN engine to successfully close the workflow instance.

---

## Future Improvements

To transition this project to a production-ready cloud deployment, the following enhancements are planned:
- **Dockerization**: Containerize all microservices and the React application using Docker.
- **Kubernetes Deployment**: Orchestrate, scale, and manage container deployments using Kubernetes.
- **API Gateway**: Introduce a centralized API Gateway to route frontend requests and handle cross-cutting concerns.
- **Authentication**: Secure endpoints and the frontend dashboard using Spring Security and JWT.
- **Observability**: Set up centralized logging and application monitoring for distributed tracing.

---

## 👨‍💻 Author

Mohan – Full-Stack Developer
