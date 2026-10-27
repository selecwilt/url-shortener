# URL Shortener

A REST service for shortening links, built with Java and Spring Boot. It accepts a long URL, returns a short code, and redirects to the original address when the short link is opened.

This is a learning project: layered architecture (controller → service → repository → database), input validation, a unified error format, and tests.

## Tech stack

- Java 21 (17+ works too)
- Spring Boot (Web, Data JPA, Validation)
- PostgreSQL 16 (in Docker)
- Maven
- JUnit 5, Mockito, MockMvc

## Features

- Create a short link from a long URL
- Redirect by short code (HTTP 302)
- URL validation: only `http` and `https` with a domain are accepted (`javascript:`, `ftp:` and similar are rejected)
- Unified JSON error format (400, 404)
- Unit tests for the service and tests for the web layer

## Requirements

- JDK 17 or newer
- Docker Desktop (for PostgreSQL)
- Git

You don't need to install Maven: the project includes the Maven Wrapper (`mvnw`).

## Quick start

1. Clone the repository and go to its folder:

   ```bash
   git clone <repository-url>
   cd url-shortener
   ```

2. Start the database:

   ```bash
   docker compose up -d
   ```

3. Start the application:

   ```bash
   # Mac / Linux / Git Bash
   ./mvnw spring-boot:run

   # Windows (cmd / PowerShell)
   mvnw spring-boot:run
   ```

   The application will be available at `http://localhost:8080`.

4. Stop the database when you're done:

   ```bash
   docker compose down
   ```

## Configuration

The main settings are in `src/main/resources/application.properties`:

| Property | Default value |
|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/shortener` |
| `spring.datasource.username` | `shortener` |
| `spring.datasource.password` | `shortener` |
| `spring.jpa.hibernate.ddl-auto` | `update` |

The database login and password are for local development only. Do not use them in production.

## API

### Create a short link

`POST /api/links`

Request body:

```json
{ "url": "https://github.com" }
```

Example (Mac / Linux / Git Bash):

```bash
curl -X POST http://localhost:8080/api/links \
  -H "Content-Type: application/json" \
  -d '{"url": "https://github.com"}'
```

Example (Windows cmd):

```bat
curl -X POST http://localhost:8080/api/links -H "Content-Type: application/json" -d "{\"url\": \"https://github.com\"}"
```

Response `200 OK`:

```json
{
  "shortCode": "aB3xK9q",
  "shortUrl": "http://localhost:8080/aB3xK9q",
  "originalUrl": "https://github.com"
}
```

### Follow a short link

`GET /{code}`

Returns `302 Found` with a `Location` header containing the original address. In a browser, just open `http://localhost:8080/aB3xK9q`.

### Error format

All errors are returned in the same shape:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Link with code 'abc' not found",
  "timestamp": "2026-10-27T12:00:00.000Z"
}
```

| Code | When |
|---|---|
| 400 | The URL is empty, longer than 2048 characters, or not `http`/`https` |
| 404 | The short code was not found |

## Tests

```bash
# Mac / Linux / Git Bash
./mvnw test

# Windows
mvnw test
```

- `LinkServiceTest`: unit tests for the service logic (the repository is replaced with a mock, Mockito)
- `LinkControllerTest`: web layer tests (`@WebMvcTest`, MockMvc) covering validation, status codes, and the redirect
- `ShortenerApplicationTests`: checks that the whole application starts (requires a running database)

## Project structure

```
src/main/java/com/maksym_solodukhin/shortener/
├── ShortenerApplication.java
├── controller/   # HTTP endpoints
├── service/      # business logic
├── repository/   # database access (Spring Data JPA)
├── entity/       # classes mapped to tables
├── dto/          # request and response objects of the API
├── validation/   # custom @ValidHttpUrl annotation
└── exception/    # custom exceptions and the global error handler
```

## Design decisions

**Code generation.** A code is 7 random characters (`a-z`, `A-Z`, `0-9`), which gives about 3.5 trillion combinations. Before saving, the service checks that the code isn't taken yet and generates a new one on a collision. Pros: simple, and codes are hard to guess. Cons: an extra database query on every creation and a theoretical chance of collisions.

Alternative: encode the numeric `id` in base62. No uniqueness check is needed, but codes become sequential and predictable.

**Redirect with 302, not 301.** Browsers cache 301 and stop contacting the server. To count clicks (planned), every visit must reach the service.

**Only http/https.** Schemes like `javascript:` and `file:` are dangerous in a link shortener, so they are rejected at the input by a custom validator.

## Roadmap

- [ ] Click tracking and visit statistics
- [ ] Link expiration and click limit
- [ ] Users and authentication (JWT, Spring Security)
- [ ] Database migrations (Flyway)
- [ ] API documentation (Swagger / OpenAPI)
- [ ] Running the application itself in Docker
