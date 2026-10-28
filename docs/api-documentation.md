# Gateway's API documentation

## Table of contents

- [Service details](#service-details)
- [Endpoints](#endpoints)
  - [Authentication](#authentication)

## Service details

- **Service name**: api-gateway
- **Service port**: 8080
- **Network**: inkwell (via docker compose)

## Endpoints

### Authentication

#### 1. Login

- **Method**: POST
- **Path**: /api/auth/login
- **Type**: public

Request Payload:

```json
{
    "email": "example@mail.com",
    "password": "*****"
}
```

A successfull login request returns a payload of format:

```json
{
    "jwt": "joijsd58e4qf9f2af3...."
}
```

#### 2. Create account

- **Method**: POST
- **Path**: /api/auth/login
- **Type**: public

#### 3. Refresh access token

- **Method**: POST
- **Path**: /api/auth/login
- **Type**: public

### Customer

#### 1. Profile

A profile holds a collection of information about a customer user. This endpoint holds lists information about an authenticated user.

##### List profile

- **Method**: GET
- **Path**: /api/customer/me/profile
- **Type**: authenticated
- **Credentials**: access token (jwt)

The following json represents a full profile resource representation:

```json
{
    "firstName": "john",
    "lastName": "doe",
    "photo": "some-url",
    "dateOfBirth": "2026-01-03",
    "email": "example@mail.com"
}
```

This endpoint supports the optional attribute `attributes`, to query selective properties from profile:

| attribute         | downstream service*                                                               |
| ----------------- | --------------------------------------------------------------------------------- |
| first_name        |  [customer-service](https://github.com/inkwell-store/customer-service)            |
| last_name         |  [customer-service](https://github.com/inkwell-store/customer-service)            |
| photo             |  [customer-service](https://github.com/inkwell-store/customer-service)            |
| date_of_birth     |  [customer-service](https://github.com/inkwell-store/customer-service)            |
| email             |  [authorization-service](https://github.com/inkwell-store/authorization-service)  |

*these are the services api gateway calls to fetch the properties.

##### Update profile

- **Method**: PATCH
- **Path**: /api/customer/me/profile
- **Type**: authenticated
- **Credentials**: access token (jwt)

The following json represents a full profile resource representation:
