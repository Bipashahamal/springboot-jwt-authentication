# Employee Management System

Spring Boot REST API for managing employees with JWT authentication, role-based access, file upload/download, and rate limiting.

---

## Features

- User registration and login with JWT token authentication
- Role-based access control (SYSTEM_ADMIN, USER_ADMIN, EMPLOYEE_VIEWER)
- CRUD operations for employees
- File upload/download (per-user and global)
- Rate limiting by user role
- Exception handling and validation
- Swagger/OpenAPI documentation

---

## Technologies Used

- Java 17+ (compatible with higher versions)
- Spring Boot 3.x
- Spring Security 6.x
- JWT (io.jsonwebtoken)
- MySQL 8.x
- Maven
- Lombok

---

## Setup Instructions

1. **MySQL Setup**
   - Ensure MySQL is running on `localhost:3306`.
   - Create a database (default: `employee_db`).
   - Update `src/main/resources/application.properties` with your DB name, user, and password.

2. **Build & Run**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
   The app runs at: [http://localhost:8080](http://localhost:8080)

---

## Authentication & Authorization

All endpoints (except `/api/auth/**`, `/api/public/**`, `/swagger-ui/**`, `/v3/api-docs/**`) require a valid JWT token.

### 1. Register
`POST /api/auth/register`

Request:
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "password123",
  "role": "SYSTEM_ADMIN" // or USER_ADMIN, EMPLOYEE_VIEWER
}
```

### 2. Login
`POST /api/auth/login`

Request:
```json
{
  "email": "jane@example.com",
  "password": "password123"
}
```
Response:
```json
{
  "accessToken": "<JWT_TOKEN>",
  "tokenType": "Bearer"
}
```

---

## Using the API

1. **Register** a user (see above)
2. **Login** to get a JWT token
3. **Add header to all requests:**
   ```
   Authorization: Bearer <your_access_token>
   ```

### Example cURL
```bash
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer <your_access_token>"
```

---

## Main API Endpoints

### Auth
- `POST /api/auth/register` — Register new user
- `POST /api/auth/login` — Login and get JWT

### Employees
- `GET /api/employees` — List all employees
- `GET /api/employees/{id}` — Get employee by ID
- `POST /api/employees` — Create employee (SYSTEM_ADMIN only)
- `PUT /api/employees/{id}` — Update employee (SYSTEM_ADMIN only)
- `DELETE /api/employees/{id}` — Delete employee (SYSTEM_ADMIN only)

### Files
- `POST /api/files/upload` — Upload file (per user)
- `GET /api/files/{id}` — Download file by ID
- `GET /api/files` — List all files (admin/global)

### Public
- `GET /api/public/info` — Public info (no auth required)

---

## Screenshots

<details>
<summary>Register API</summary>

![Register](screenshots/register.png)
</details>

<details>
<summary>Login API</summary>

![Login](screenshots/login.png)
</details>

<details>
<summary>Access Secured API</summary>

![Secured API](screenshots/secured-api.png)
</details>

---

## Security Architecture

### JWT Authentication
- Stateless: No server-side session
- Token generated on login, sent in `Authorization` header
- Token contains user email, roles, expiration, and is signed

### Filter Chain
1. **RateLimitingFilter** — Enforces per-role request limits
2. **JwtAuthFilter** — Validates JWT, sets authentication
3. **Spring Security** — Enforces `@PreAuthorize` and role checks

### Role-Based Access
- SYSTEM_ADMIN: Full access (CRUD, file, admin endpoints)
- USER_ADMIN: Limited admin
- EMPLOYEE_VIEWER: Read-only

### Method Security
`@PreAuthorize` annotations restrict controller methods by role

---

## Additional Notes

- **Rate Limiting:** Each user role has a request-per-minute limit (see `RateLimitingFilter`)
- **File Uploads:** Use `multipart/form-data` for file endpoints
- **Validation:** All endpoints use request validation and global exception handling
- **Swagger UI:** API docs at `/swagger-ui/index.html`

---

## License

MIT License. See [LICENSE](LICENSE) for details.
