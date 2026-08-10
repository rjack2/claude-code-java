# User Service

## Objective & Scope
- Manage user accounts, authentication, and watchlist.

## Out of Scope
- Token validation
- Refresh tokens
- Password reset and forgot-password flows.

## API

### Auth

#### `POST /api/users/register`

- Request body
```json
{ "username": "", "password": "", "name": "" }
```
- Returns 201 Created, no body.
- Password stored hashed (BCrypt).
- Returns 409 if username already exists.

#### `POST /api/users/login`

- Request body
```json
{ "username": "", "password": "" }
```
- Response body
```json
{ "token": "<jwt>", "name": "" }
```
- JWT signed with the RS256 private key.
- `sub` claim contains the userId.
- Returns 401 if credentials are invalid.

### Watchlist
All endpoints read `userId` from the `X-User-Id` header.

#### `GET /api/users/watchlist`

- Response body
```json
[123, 456]
```
- movieIds in the user's watchlist.

#### `POST /api/users/watchlist`

- Request body
```json
{ "movieId": 123 }
```
- Returns 201 Created.

#### `DELETE /api/users/watchlist/{movieId}`
- Returns 204 No Content.

## Cryptography & Key Management
- Algorithm: RS256
- Private key is loaded from the filesystem. Path is configured via the `rsa.private-key` property.

### Testing
- Generate a dummy RSA key pair at test startup, write the private key PEM to `target/`, and point `rsa.private-key` to that path.

## Data Model

### DB

**Table: `users`**

| Field    | Type   |
|----------|--------|
| id       | Long   |
| username | String |
| password | String |
| name     | String |

**Table: `watchlist`**

| Field   | Type |
|---------|------|
| id      | Long |
| userId  | Long |
| movieId | Long |
