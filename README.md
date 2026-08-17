# 💸 Task Manager API

![CI](https://github.com/elVato-ops/task-manager/actions/workflows/ci.yml/badge.svg)

## Overview
REST API for managing users, projects, and tasks, with JWT-based authentication and role-based authorization.

## Features
- User registration and JWT-based login
- Create and manage projects and tasks
- Role-based authorization (multi-user access control)
- Assign users to tasks
- Validation & business rules
- Global error handling

## Tech stack
- Java 21
- Spring Boot
- PostgreSQL
- Spring Data JPA
- Spring Security (JWT)
- Docker & Docker Compose

## How to run
### With Docker (recommended)
1. Copy the environment template and fill in your own values:
```bash
   cp .env.example .env
```
2. Build and start the app + database:
```bash
   docker compose up --build
```
3. The API will be available at `http://localhost:8080`

### Without Docker
### Without Docker
Requires a local PostgreSQL instance and the environment variables in `.env.example` set manually (e.g. via your IDE's run configuration).

```bash
mvn spring-boot:run
```
## API docs
Once running, interactive API docs are available at:
http://localhost:8080/swagger-ui/index.html

## CI/CD
Every push and pull request runs the test suite automatically via GitHub Actions. Merges to `master` are blocked unless tests pass.