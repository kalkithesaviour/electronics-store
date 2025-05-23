# Electronics Store Application
Back-end application built with Java, Spring Boot, and MySQL, designed to efficiently manage products, users, and orders for an e-commerce electronics store through a comprehensive set of RESTful APIs exposed via Swagger UI.

The application supports essential e-commerce functionalities including user registration, login, product listing, shopping cart management, and order placement. Admin users are empowered with additional capabilities such as product management and order tracking, all secured via Spring Security and JWT for robust authentication and authorization.

The system is designed with modular services and follows clean architecture principles, utilizing Spring Data JPA for seamless database operations. User and admin interactions are fully validated, logged, and managed through layered exception handling and service orchestration.

To ensure smooth operation and traceability, the application implements centralized logging with SLF4J and maintains code clarity and maintainability using Project Lombok. The project structure supports easy scalability and future integration with external services such as payment gateways or inventory systems.

Comprehensive unit testing is performed using JUnit and Mockito to ensure code reliability and correctness. Swagger integration provides an interactive API documentation interface, simplifying development and testing.

#### Spring Boot Docker image: https://hub.docker.com/repository/docker/vizzzard/electronicsstore-1.0/tags

### Tech Stack

- Java 17
- Spring Boot 3.3
- Spring Data JPA
- Spring Security
- JWT & OAuth 2.0
- MySQL
- Hibernate
- Docker
- Project Lombok
- SLF4J Logging
- JUnit & Mockito
- Swagger UI
- Maven