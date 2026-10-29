# Gateway's API documentation

## Table of contents

- [Service details](#service-details)
- [Endpoints](#endpoints)
  - [Authentication](#authentication)
    - [1. Login](#1-login)
    - [2. Create account](#2-create-account)
    - [3. Refresh access token](#3-refresh-access-token)
  - [Customer](#customer)
    - [1. Profile](#1-profile)
      - [List](#list-profile)
      - [Update](#update-profile)
    - [2. Address](#2-address)
      - [List](#list-address)
      - [Update](#update-address)
      - [Delete](#delete-address)

## Service details

- **Host name**: api-gateway
- **Service port**: 8080

## Endpoints

### Authentication

Authentication throughout the application is stateless and processed by submiting a form represented with the following json:

```json
{
    "email": "example@mail.com",
    "password": "*****"
}
```

#### 1. Login

- **Method**: `POST`
- **Path**: /api/auth/login
- **Type**: public
- **Description**: authenticates and returns a set of tokens for the user. The request body must follow the format specified in [authentication](#authentication).

The access token is returned in the response body and follows the format:

```json
{
    "jwt": "joijsd58e4qf9f2af3...."
}
```

The refresh token is set as an HttpOnly cookie.

#### 2. Create account

- **Method**: `POST`
- **Path**: /api/auth/login
- **Type**: public
- **Description**: Creates a new account (set of credentials) for a user and authenticates this user. The request body must follow the format specified in [authentication](#authentication).

The access token is returned in the response body and follows the format:

```json
{
    "jwt": "joijsd58e4qf9f2af3...."
}
```

The refresh token is set as an HttpOnly cookie.

#### 3. Refresh access token

- **Method**: `POST`
- **Path**: /api/auth/login
- **Type**: public
- **Description**: uses refresh token inside the HttpOnly cookie to request a new access token.

### Customer

#### 1. Profile

A profile holds a collection of information about a customer user. This resource holds information about a user of type customer.

The following json shows a full profile resource representation:

```json
{
    "firstName": "john",
    "lastName": "doe",
    "photo": "http://some-url.example",
    "dateOfBirth": "2018-01-03",
    "email": "example@mail.com"
}
```

#### List profile

- **Method**: `GET`
- **Path**: /api/customers/me/profile
- **Type**: authenticated
- **Credentials**: access token (jwt)
- **Description**: returns a representation of the [profile resource](#1-profile) for the authenticated user.

This endpoint supports the optional query parameter: `attributes`. It allows to query selected properties from profile:

| attribute         | downstream service*                                                               |
| ----------------- | --------------------------------------------------------------------------------- |
| first_name        |  [customer-service](https://github.com/inkwell-store/customer-service)            |
| last_name         |  [customer-service](https://github.com/inkwell-store/customer-service)            |
| photo             |  [customer-service](https://github.com/inkwell-store/customer-service)            |
| date_of_birth     |  [customer-service](https://github.com/inkwell-store/customer-service)            |
| email             |  [authorization-service](https://github.com/inkwell-store/authorization-service)  |

*these are the services api gateway calls to fetch the properties.

#### Update profile

- **Method**: `PATCH`
- **Path**: /api/customers/me/profile
- **Type**: authenticated
- **Credentials**: access token (jwt)
- **Description**: updates a user's profile. The request's body may contain any of the properties from the [resource's representation](#1-profile). If the request is successfull it returns the full [resource's representation](#1-profile) in the response body.

#### 2. Address

An address is a resource that represents information about a user's physical address, which can be used for shipping, billing or both.

The following json shows a full address resource representation:

```json
{
    "id": 1,
    "name": "My house",
    "street": "Main Street",
    "number": 742,
    "city": "Austin",
    "state": "TEXAS",
    "postalCode": "78701",
    "line": "Apartment 4B",
    "type": "SHIPPING"
}
```

#### List address

- **Method**: `GET`
- **Path**: /api/customers/me/addresses
- **Type**: authenticated
- **Credentials**: access token (jwt)
- **Description**: returns an array of all addresses belonging to the authenticated user. Each item in this array follows the [address resource representation](#2-address).

#### Update address

- **Method**: `PATCH`
- **Path**: /api/customers/me/addresses/{addressId}
- **Type**: authenticated
- **Credentials**: access token (jwt)
- **Description**: updates the specified address. The request body supports any of the attributes specified in [address resource representation](#2-address). If the request is successfull it returns the full [updated address](#2-address) in the response body.

#### Delete address

- **Method**: `DELETE`
- **Path**: /api/customers/me/addresses/{addressId}
- **Type**: authenticated
- **Credentials**: access token (jwt)
- **Description**: deletes the specificed address *only* if there's at least one billing address remaining in the database.

#### Create address

- **Method**: `POST`
- **Path**: /api/customers/me/addresses
- **Type**: authenticated
- **Credentials**: access token (jwt)
- **Description**: creates a new address for the authenticated user. The request body should contain all the fields specified in the [address resource representation](#2-address) with the exception of `name`. If the request is successfull it returns the full [creatred address](#2-address) in the response body.
