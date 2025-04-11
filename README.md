
# user-service
Generic user-service for managing user data and providing OAuth2 authentication.

## Overview
This project provides a generic user-service that supports OAuth2 for authentication. It allows the creation, deletion, modification, and retrieval of user data.

### Key Features:
- OAuth2 authentication via client credentials
- User management: Create, Read, Update, Delete (CRUD)
- Configurable client and secret storage
- Easy integration with other services

---

## Test the OAuth2 token generation with (cURL):
```bash
clientId="<your client id configured in /src/main/resources/secret/clients.properties>"
clientSecret="<your secret plain text>"
serverPort=<Look up in application.properties>

# cURL with Base64 encoding for Authorization header
curl -X POST "http://localhost:$serverPort/oauth2/token" \
  -H "Authorization: Basic $(echo -n "$clientId:$clientSecret" | base64)" \
  -H "Content-Type: application/x-www-form-urlencoded; charset=utf-8" \
  -d "grant_type=client_credentials"
```

---

## Test the OAuth2 token generation with (Windows PowerShell):
```powershell
$clientId='<your client id configured in /src/main/resources/secret/clients.properties>'
$clientSecret='<your secret plain text>'
$serverPort=<Look up in application.properties>

$raw = "{0}:{1}" -f $clientId, $clientSecret
$base64 = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($raw))

$headers = @{
    Authorization = "Basic $([Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("$clientId`:$clientSecret")))"
    ContentType   = "application/x-www-form-urlencoded; charset=utf-8"
}

$response = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:$serverPort/oauth2/token" `
  -Headers $headers `
  -ContentType "application/x-www-form-urlencoded" `
  -Body "grant_type=client_credentials"

$response.access_token
```

---

## About the user-service

This **user-service** is a generic service designed to handle the management of users, with OAuth2 authentication integrated for secure access.

The service supports the following user management functionalities:

- **Create users**: Allows adding new users with necessary details.
- **Read users**: Enables retrieving user information by ID or other search parameters.
- **Update users**: Modify user data such as name, email, etc.
- **Delete users**: Removes users from the system.

### OAuth2 Authentication:
OAuth2 is used for secure authorization, where each client can authenticate using the `client_credentials` flow. The service expects client credentials to be configured, and upon successful authentication, a token is generated that can be used to access the API.

The client ID and secret must be stored securely in the `clients.properties` file under `src/main/resources/secret/`, and the service will validate them to allow access to the user-management endpoints.

### API Endpoints:
- `/oauth2/token`: Token endpoint for OAuth2 authentication.
- `/users`: Manage users (GET, POST, PUT, DELETE).

---

## Configuration
You can configure the client ID and secret in the file located at:
```
src/main/resources/secret/clients.properties
```

For the server port, you can check your `application.properties` file:
```
server.port=8080
```

Feel free to extend this service by adding additional endpoints or features as per your project needs.
