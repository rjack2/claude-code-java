# CLAUDE.md

## System Overview
Netflux is a Netflix-inspired distributed movie streaming platform. It consists of 4 modules:

| Module | Responsibility |
|---|---|
| `movie-service` | Movie catalog: search, filter, retrieve movie data |
| `user-service` | User accounts, authentication (JWT), watchlist |
| `recommendation-service` | Stateless aggregator: calls movie-service and user-service, returns home page recommendations |
| `frontend` | Vanilla JS SPA: hash-based routing, fetch API, no framework |

## Architecture
- All client requests go through **Nginx**, which acts as API gateway and static file host for the UI.
- Modules communicate via **REST over HTTP**.
- `recommendation-service` calls `movie-service` and `user-service`. No other inter-service calls exist.

## Security Model
- `user-service` issues a **JWT (RS256)** on login, signed with a private key.
- **Nginx** validates the JWT using the public key, strips any client-supplied `X-User-Id` header, and sets it from the JWT `sub` claim.
- All downstream services trust the `X-User-Id` header. No Spring Security is needed outside `user-service`.
- `recommendation-service` forwards the `X-User-Id` header on every call to `user-service`.

## Available Agents
- Backend agents, for each backend service (`movie-service`, `user-service`, `recommendation-service`):
  - `backend-reporter`
  - `backend-planner`
  - `backend-implementer`
- Frontend agents, for `frontend`:
  - `frontend-reporter`
  - `frontend-planner`
  - `frontend-implementer`