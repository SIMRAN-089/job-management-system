 #Job Application and Interview Management System

A Spring Boot REST API for a small job portal. This version preserves the existing User, Job and Application layers and adds JWT login, BCrypt password hashing, recruiter ownership checks and interview scheduling.

## Technology

- Java 27 and Spring Boot 4.1.1
- Spring MVC, Spring Data JPA, Spring Security
- JWT (JJWT 0.12.6) and BCrypt
- H2 for local development; MySQL driver is included for later configuration
- Maven and Lombok

## Run locally

1. Open this folder in IntelliJ as a Maven project.
2. Ensure IntelliJ is configured to use a JDK compatible with Java 27.
3. Reload Maven and run `JobManagementApplication`.
4. The API runs at `http://localhost:8080`; H2 console is at `http://localhost:8080/h2-console`.

The default H2 database is in-memory and resets when the app stops. The JWT secret in `application.properties` is for local development only. Set `APP_JWT_SECRET` to a private secret of at least 32 bytes before sharing/deploying the application.

## Important security note

This is a learning/demo project. Registration accepts a `role` field so both candidate and recruiter flows can be tested locally. A production system must not let anyone self-assign `RECRUITER`; recruiter accounts should be approved or provisioned by an administrator. Use HTTPS, a strong environment-provided JWT secret, rate limiting, and production database credentials before deployment.

## API workflow (Postman)

All requests use `Content-Type: application/json` unless otherwise noted.

### 1. Register a candidate or recruiter

`POST /api/users`

```json
{
  "name": "Candidate One",
  "email": "candidate@example.com",
  "password": "ChangeMe123!",
  "role": "CANDIDATE"
}
```

Use `"role": "RECRUITER"` to create a local recruiter test account. Passwords are stored as BCrypt hashes; the response never includes the password.

### 2. Log in

`POST /api/auth/login`

```json
{
  "email": "candidate@example.com",
  "password": "ChangeMe123!"
}
```

Copy `token` from the response. For protected requests, add:

`Authorization: Bearer YOUR_TOKEN_HERE`

### 3. Browse jobs (public)

- `GET /api/jobs`
- `GET /api/jobs/{id}`
- `GET /api/jobs/search?title=developer&location=remote`
- `GET /api/jobs/salary?minSalary=50000`
- `GET /api/jobs/location?location=remote`
- `GET /api/jobs/paginated?page=0&size=5`

### 4. Recruiter creates a job

Log in as a recruiter, then `POST /api/jobs` with the recruiter token:

```json
{
  "title": "Junior Java Developer",
  "company": "Example Company",
  "description": "Entry-level backend role",
  "location": "Bengaluru",
  "salary": 500000
}
```

The server derives the recruiter from the token; `recruiterId` supplied by the client is not trusted. A recruiter can update/delete only jobs they own.

### 5. Candidate applies

Log in as a candidate, then `POST /api/applications`:

```json
{
  "jobId": 1
}
```

The candidate is derived from the token. Duplicate applications return HTTP 409. New applications start at `APPLIED`.

### 6. Recruiter manages applications

With the owning recruiter's token:

- `GET /api/applications/my` (candidate's own applications)
- `GET /api/applications/job/{jobId}`
- `GET /api/applications/job/{jobId}/status?status=SHORTLISTED`
- `PUT /api/applications/{applicationId}/status`

```json
{ "status": "SHORTLISTED" }
```

Allowed transitions: `APPLIED -> SHORTLISTED/REJECTED`, `SHORTLISTED -> INTERVIEW/REJECTED`, `INTERVIEW -> SELECTED/REJECTED`. `SELECTED` and `REJECTED` are terminal states.

### 7. Interview management (recruiter)

An application must be `SHORTLISTED` or `INTERVIEW` before scheduling.

`POST /api/interviews`

```json
{
  "applicationId": 1,
  "scheduledAt": "2026-10-20T10:30:00",
  "meetingLink": "https://example.com/meeting"
}
```

- `GET /api/interviews/application/{applicationId}`
- `PUT /api/interviews/{interviewId}` to update schedule, meeting link, feedback or status (`SCHEDULED`, `COMPLETED`, `CANCELLED`).

Recruiters can manage interviews only for applications to their own jobs.

## Access overview

| Feature | Access |
|---|---|
| Register and log in | Public |
| Browse/search jobs | Public |
| Create/update/delete jobs | Recruiter; owner required for update/delete |
| Apply to a job | Candidate; candidate identity comes from token |
| Read/update applications | Recruiter; job owner required |
| Schedule/update interviews | Recruiter; job owner required |
| Read all user profiles | Recruiter |
| Read one user profile | User themselves or recruiter |

## Known development limitations

- H2 is configured as an in-memory database; configure MySQL separately before deployment.
- The AI resume-to-job matching feature is not implemented yet; it was kept as a future enhancement rather than adding a misleading placeholder.
- A production-grade recruiter approval workflow and refresh-token/logout revocation are not implemented.
