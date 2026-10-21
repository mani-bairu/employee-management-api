# Employee Management API

Spring Boot REST API for managing employees and departments.

## Tech stack
- Java 17
- Spring Boot 4.1
- Maven
- H2 in-memory database
- Spring Data JPA
- Spring AOP (AspectJ) for request/response logging
- RestClient for the external API call
- JUnit 5, Mockito, MockMvc
- Lombok

## Data model
Department (1) --- (*) Employee

## Assumptions
- google.com returns HTML, so the external endpoint returns the status code, content length and response time instead of the body.
- H2 is in-memory, so data resets on every restart. Sample data is loaded from `data.sql`.
- Employee email is unique.

## Project structure
```
com.example.emp
  controller  - REST endpoints
  service     - business logic
  repository  - JPA repositories, join query
  entity      - JPA entities
  dto         - request/response records
  mapper      - entity to DTO conversion
  client      - external API client
  aspect      - logging aspect
  exception   - custom exceptions and global exception handler
  config      - RestClient configuration
```

## Endpoints
| Method | Path | Description |
|---|---|---|
| GET | /api/v1/employees/{id} | Get employee by id |
| POST | /api/v1/employees | Create employee |
| PUT | /api/v1/employees/{id} | Update employee |
| PATCH | /api/v1/employees/{id} | Partially update employee |
| DELETE | /api/v1/employees/{id} | Delete employee |
| GET | /api/v1/employees/search?name=&page=0&size=10&sort=name,asc | Search by name with pagination |
| GET | /api/v1/employees/department/{name} | Employees of a department (join query) |
| GET | /api/v1/external/google | Calls google.com |

Errors are returned in a common format with status codes 400 (validation), 404 (not found), 409 (duplicate email) and 502 (external service down).

## Running the application
```
./mvnw spring-boot:run
```
The application starts on http://localhost:8080.

H2 console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:empdb`
- Username: `test`
- Password: (empty)

## Running tests
```
./mvnw test
```
Tests include service unit tests (Mockito), controller tests (`@WebMvcTest`), repository tests (`@DataJpaTest`) and a test for the external API client using `MockRestServiceServer`.

## Postman
The collection is in `postman/Employee-API.postman_collection.json`. Import it into Postman; it uses a `baseUrl` variable set to `http://localhost:8080`.

## Notes
- Layered structure (controller, service, repository) with DTOs, so entities are not exposed in the API.
- Logging for all controllers is handled in one place with an aspect.
- The external API call is in a separate client class with connect/read timeouts.
- `@EntityGraph` is used on the search query to avoid N+1 queries when loading departments.
- `open-in-view` is disabled, so database access stays within service transactions.
