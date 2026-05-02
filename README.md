# snip — temporary url shortener

A full stack web application for creating short, time-limited URLs with click tracking. Features a minimalist web interface served directly by the Spring Boot backend, built with Java 21 and vanilla HTML/CSS/JS.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Error Handling](#error-handling)
- [Database Schema](#database-schema)

---

## Overview

Snip is a full stack URL shortener with a built-in web interface. The frontend is served as a static file directly from Spring Boot — no separate server or build pipeline required. Every short link is assigned a unique 6-character alphanumeric hash and an expiration window chosen at creation time. Expired or non-existent links return structured JSON error responses rather than generic HTTP errors.

---

## Features

- **Minimalist web UI** — clean, responsive interface served at `/` directly by Spring Boot; no external frontend server needed
- **Short link generation** — produces a unique 6-character alphanumeric hash per URL
- **Instant redirection** — resolves and redirects to the original URL with `302 Found`
- **Enforced expiration** — links expire after a user-selected duration; expired links are rejected at resolution time
- **Click tracking** — increments an access counter on each successful redirect
- **URL lookup** — query any short link's stats (original URL, expiration, click count) directly from the interface without triggering a redirect
- **Structured error responses** — a global `@RestControllerAdvice` handler intercepts all exceptions and returns consistent JSON payloads

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL |
| Build Tool | Maven |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Typography | DM Sans, DM Mono |

---

## Architecture

The project follows a standard layered MVC architecture:

```
src/
├── main/
│   ├── java/com/ismael/
│   │   ├── controller/        # HTTP layer — handles requests and delegates to services
│   │   ├── service/           # Business logic — URL creation, expiration, redirect resolution
│   │   ├── repository/        # Data access layer — Spring Data JPA interfaces
│   │   ├── dto/               # Request and response transfer objects
│   │   ├── entity/            # JPA entities
│   │   └── exception/         # Global exception handler (@RestControllerAdvice)
│   └── resources/
│       ├── static/            # Frontend — index.html, style.css, script.js
│       └── application.properties
```

Each layer has a single, well-defined responsibility. Controllers delegate immediately to services; services own all business rules and interact with repositories through defined contracts; no persistence logic leaks outside the repository layer. The frontend is a set of static files under `src/main/resources/static`, served automatically by Spring Boot at the root path — no separate server or proxy required.

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- MySQL 8+

### Configuration

Create a schema in your MySQL instance and update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/urlshortener
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### Running

```bash
mvn clean install
mvn spring-boot:run
```

The application starts on `http://localhost:8080` by default. Open that address in a browser to access the web interface.

---

## API Reference

### Create a Short URL

Generates a new short link with a selected expiration window.

```
POST /api/urls/shorten
```

**Valid expiration values (minutes):** `15`, `30`, `60`, `300`, `1440`, `2880`

**Request body:**

```json
{
  "originalUrl": "https://www.example.com/some/very/long/path?query=value",
  "expirationMinutes": 60
}
```

**Response — `201 Created`:**

```json
{
  "shortCode": "aB3xYz",
  "originalUrl": "https://www.example.com/some/very/long/path?query=value",
  "shortUrl": "http://localhost:8080/aB3xYz",
  "expiresAt": "2025-01-15T14:30:00",
  "clickCount": 0
}
```

---

### Redirect

Resolves a short code and redirects to the original URL. Increments the click counter.

```
GET /{shortCode}
```

**Response — `302 Found`**

On success, the response carries a `Location` header pointing to the original URL. No response body is returned.

---

### Get URL Stats

Returns metadata for a given short code without triggering a redirect or incrementing the click counter.

```
GET /api/urls/{hash}
```

**Response — `200 OK`:**

```json
{
  "id": 1,
  "shortCode": "aB3xYz",
  "originalUrl": "https://www.example.com/some/very/long/path?query=value",
  "shortUrl": "http://localhost:8080/aB3xYz",
  "expiresAt": "2025-01-15T14:30:00",
  "clickCount": 14
}
```

---

## Error Handling

All errors are handled by a centralized `@RestControllerAdvice` and return a consistent JSON structure. No stack traces or framework-generated error pages are exposed to the client.

**Error response shape:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Short URL has expired.",
  "timestamp": "2025-01-15T15:00:00"
}
```

| Scenario | HTTP Status | Message |
|---|---|---|
| Short code not found | `400 Bad Request` | `Short URL not found.` |
| Short URL is expired | `400 Bad Request` | `Short URL has expired.` |
| Invalid expiration value | `400 Bad Request` | `Invalid expiration. Allowed values: 15, 30, 60, 300, 1440, 2880 minutes.` |
| Malformed request body | `400 Bad Request` | `Required field is missing or invalid.` |

> The decision to return `400` for expired and not-found links — rather than `404` or `410` — keeps the external contract intentionally opaque, avoiding enumeration of valid short codes.

---

## Database Schema

```sql
CREATE TABLE urls (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    short_code     VARCHAR(6)   NOT NULL UNIQUE,
    original_url   TEXT         NOT NULL,
    created_at     DATETIME     NOT NULL,
    expires_at     DATETIME     NOT NULL,
    click_count    BIGINT       NOT NULL DEFAULT 0
);
```

The schema is managed by Hibernate via `ddl-auto`. The `short_code` column carries a unique constraint enforced at both the database and application layers to handle hash collision edge cases safely.

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
