# Employee Management System

## Setup Instructions

1. Ensure you have **MySQL** running on `localhost:3306`.

2. Create a database named `employee_db` or update the database URL in `src/main/resources/application.properties` to point to your existing database. Update your MySQL username and password accordingly.

3. Requirements:

   * JDK 17 (or compatible Java version)
   * Maven (or use the included Maven wrapper `mvnw`)

4. Build the project:

```bash
./mvnw clean install
```

5. Run the application:

```bash
./mvnw spring-boot:run
```

The application will start on:
`http://localhost:8080`

---

# How to Generate Token

The project uses **JWT (JSON Web Token)** to secure endpoints.
To generate a token, you first need to **register a user and then log in**.

## 1. Register a New User

Send a `POST` request to:

```
/api/auth/register
```

Request Body:

```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "password123",
  "role": "USER"
}
```

Available roles:

```
USER
ADMIN
```

---

## 2. Login

Send a `POST` request to:

```
/api/auth/login
```

Request Body:

```json
{
  "email": "jane@example.com",
  "password": "password123"
}
```

Success Response:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

---

# How to Test Secured APIs

After login, copy the **accessToken** and include it in the request header.

Header format:

```
Authorization: Bearer <your_access_token>
```

### Example using cURL

```bash
curl -X GET http://localhost:8080/api/employees \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Secured Endpoint Examples

```
GET /api/employees
POST /api/employees
GET /api/employees/{id}
PUT /api/employees/{id}
DELETE /api/employees/{id}
```

---

# Screenshots

## Register API

![Register](screenshots/register.png)

## Login API

![Login](screenshots/login.png)

## Access Secured API

![Secured API](screenshots/secured-api.png)

---

# How JWT (JSON Web Token) Works in This Application

JWT is used for **stateless authentication**, meaning the server does not store session information.
Instead, the client sends a token with every request.

### 1. Token Generation (Login)

When a user sends credentials to:

```
/api/auth/login
```

the request goes to **AuthController**.

Spring's `AuthenticationManager` verifies the credentials.

If authentication is successful, the application calls:

```
jwtUtil.generateToken(user.getEmail())
```

The **JwtUtil** class generates the token containing:

* User email (subject)
* Issued time
* Expiration time
* Digital signature using `jwt.secret`

This ensures the token **cannot be modified by the client**.

---

### 2. Intercepting Requests

When a user calls a secured endpoint such as:

```
/api/employees
```

the request first passes through **JwtAuthFilter**.

The filter performs these steps:

* Allows requests to `/api/auth/*` (login and register)
* Checks the `Authorization` header
* Extracts the token from:

```
Authorization: Bearer <token>
```

---

### 3. Token Validation

The filter then:

1. Extracts the email from the token
2. Verifies the token signature
3. Checks whether the token has expired

This is done using methods like:

```
jwtUtil.extractEmail()
jwtUtil.validateToken()
```

If valid, the system loads user details using:

```
userDetailsService.loadUserByUsername()
```

Then it stores the authentication object inside:

```
SecurityContextHolder
```

This tells Spring Security that the request is **authenticated**.

---

# How Role-Based Security is Implemented

Role-based security ensures **only authorized users can access specific APIs**.

---

## 1. Enabling Role-Based Security

In `SecurityConfig.java`:

```java
@EnableMethodSecurity
```

This enables method-level security annotations such as:

```
@PreAuthorize
```

---

## 2. Assigning Roles to Users

During registration:

```
POST /api/auth/register
```

The user provides a role:

```
USER
ADMIN
```

The role is stored in the database and later converted into Spring Security authorities such as:

```
ROLE_USER
ROLE_ADMIN
```

---

## 3. Securing Controller Methods

In `EmployeeController`, access is restricted using `@PreAuthorize`.

### Read Access (USER and ADMIN)

```java
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@GetMapping
```

Both **USER** and **ADMIN** can view employees.

---

### Administrative Access (ADMIN Only)

```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
```

Only **ADMIN** users can:

* Create employees
* Update employees
* Delete employees

---

## 4. Security Enforcement

When a request is made:

1. The request passes through `JwtAuthFilter`
2. The token is validated
3. The user's role is loaded
4. Spring Security checks the `@PreAuthorize` annotation

If the user does not have the required role, the system returns:

```
403 Forbidden
```
