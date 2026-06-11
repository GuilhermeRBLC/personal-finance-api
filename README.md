Markdown
# Personal Finance API 💰

RESTful API for a personal finance control system. This project was developed using architectural best practices, security with Spring Security + JWT, and database versioning.

The project is fully dockerized and ready to run alongside the frontend.

## 🚀 Technologies Used

* **Java 21** & **Spring Boot 3**
* **Spring Security** & **JWT (JSON Web Token)** (Stateless Authentication)
* **PostgreSQL** (Relational Database)
* **Flyway** (Database migrations and versioning)
* **Springdoc OpenAPI (Swagger)** (API Documentation)
* **Lombok** & **Jakarta Validation**

## 📐 Architecture & Best Practices
* **DTO Pattern:** Complete isolation between database entities and data transferred over the network.
* **Security Context:** Logged-in user identification retrieved directly from the Spring Security context (`@AuthenticationPrincipal`), avoiding the exposure of sensitive IDs in transaction URLs.
* **Automated Migrations:** Flyway automatically manages the creation and evolution of tables (`tb_users`, `tb_accounts`, `tb_transactions`).

## 🧪 Running Tests

To run the unit tests locally via Maven, execute the following command in the backend root directory:

```bash
mvn test
```

We use JUnit 5 and Mockito to ensure service layer isolation and test business rules against unexpected behavior.