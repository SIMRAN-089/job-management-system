# Job Application and Interview Management System

A Spring Boot REST API for managing job postings, candidate applications, recruiter access, and interview scheduling. The application includes JWT-based authentication, BCrypt password hashing, role-based authorization, recruiter ownership checks, and application status management.

## Technology

- Java 27 and Spring Boot 4.1.1
- Spring MVC, Spring Data JPA, Spring Security
- JWT authentication using JJWT 0.12.6
- BCrypt password hashing
- H2 database for local development
- MySQL driver for future database configuration
- Maven and Lombok

## Features

- User registration and login
- Candidate and recruiter roles
- JWT-based authentication
- Secure password storage using BCrypt
- Job creation, listing, searching, filtering, and pagination
- Candidate job applications and duplicate-application prevention
- Application status workflow
- Interview scheduling, updates, and feedback
- Recruiter ownership validation
- DTO-based request and response handling
- Input validation and global exception handling

## Run Locally

1. Open the project in IntelliJ IDEA as a Maven project.
2. Configure a compatible JDK for Java 27.
3. Reload the Maven project.
4. Run `JobManagementApplication`.
5. The API runs at `http://localhost:8080`.
6. The H2 console is available at `http://localhost:8080/h2-console`.

**Note:** The default H2 database is in-memory and resets when the application stops. Configure a persistent database before deploying the application.

## API Workflow (Postman)

Use `Content-Type: application/json` for JSON requests.

### 1. Register a User

**Endpoint:** `POST /api/users`

Example candidate request:

```json
{
  "name": "Candidate One",
  "email": "candidate@example.com",
  "password": "ChangeMe123!",
  "role": "CANDIDATE"
}
```

For local recruiter testing, use `"role": "RECRUITER"`.

Passwords are stored as BCrypt hashes and are not returned in API responses.

### 2. Log In

**Endpoint:** `POST /api/auth/login`

```json
{
  "email": "candidate@example.com",
  "password": "ChangeMe123!"
}
```

Copy the returned JWT token. For protected endpoints, configure Postman Authorization as **Bearer Token** and paste the token into the field.

### 3. Browse Jobs

- `GET /api/jobs`
- `GET /api/jobs/{id}`
- `GET /api/jobs/search?title=developer&location=remote`
- `GET /api/jobs/salary?minSalary=50000`
- `GET /api/jobs/location?location=remote`
- `GET /api/jobs/paginated?page=0&size=5`

### 4. Create a Job

Log in as a recruiter and use the recruiter token.

**Endpoint:** `POST /api/jobs`

```json
{
  "title": "Junior Java Developer",
  "company": "Example Company",
  "description": "Entry-level backend development role",
  "location": "Bengaluru",
  "salary": 500000
}
```

The server identifies the recruiter from the authenticated user rather than trusting a client-supplied recruiter ID.

### 5. Apply for a Job

Log in as a candidate.

**Endpoint:** `POST /api/applications`

```json
{
  "jobId": 1
}
```

The candidate is identified from the authentication token. Duplicate applications return HTTP `409 Conflict`. New applications begin with the `APPLIED` status.

### 6. Manage Applications

Using the appropriate authenticated account:

- `GET /api/applications/my` — view the candidate's applications
- `GET /api/applications/job/{jobId}` — view applications for a job
- `GET /api/applications/job/{jobId}/status?status=SHORTLISTED` — filter applications by status
- `PUT /api/applications/{applicationId}/status` — update application status

Example request body:

```json
{
  "status": "SHORTLISTED"
}
```

Supported transitions:

- `APPLIED` → `SHORTLISTED` or `REJECTED`
- `SHORTLISTED` → `INTERVIEW` or `REJECTED`
- `INTERVIEW` → `SELECTED` or `REJECTED`

`SELECTED` and `REJECTED` are terminal states.

### 7. Manage Interviews

An application must have an eligible status before an interview can be scheduled.

**Endpoint:** `POST /api/interviews`

```json
{
  "applicationId": 2,
  "scheduledAt": "2026-10-20T10:30:00",
  "meetingLink": "https://example.com/meeting"
}
```

Use an existing eligible application ID in your request.

Other endpoints:

- `GET /api/interviews/application/{applicationId}` — list interviews for an application
- `PUT /api/interviews/{interviewId}` — update interview details, feedback, or status

Supported interview statuses include `SCHEDULED`, `COMPLETED`, and `CANCELLED`.

Recruiters can manage interviews only for applications associated with their own jobs.

## Access Overview

| Feature | Access |
|---|---|
| Register and log in | Public |
| Browse and search jobs | Public |
| Create jobs | Recruiter |
| Update or delete jobs | Owning recruiter |
| Apply for jobs | Candidate |
| View and update applications | Authorized recruiter, subject to ownership checks |
| Schedule and update interviews | Owning recruiter |
| Access user profiles | Subject to configured authorization rules |

## Known Development Limitations

- H2 uses an in-memory database and resets when the application stops.
- MySQL configuration is required for persistent database deployment.
- Recruiter approval and refresh-token/logout revocation are not implemented.
- Production deployment requires a secure environment-provided JWT secret, HTTPS, appropriate database credentials, and additional security testing.
- Registration currently permits role selection for local testing. A production system should restrict recruiter account creation to an approved process.

## Future Improvements

- Add automated integration tests for all API endpoints.
- Configure a persistent MySQL database.
- Improve API documentation and deployment configuration.
- Add broader security testing and production monitoring.
