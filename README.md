# user-service
Generic user-service


## Test the OAuth2 token generation with (curl):
clientId="<your client id configured in /src/main/resources/secret/clients.properties"
clientSecret="<your secret plain text>"
serverPort=<Look up in application.properties>

# cURL with Base64 encoding for Authorization header
curl -X POST "http://localhost:$serverPort/oauth2/token" \
  -H "Authorization: Basic $(echo -n "$clientId:$clientSecret" | base64)" \
  -H "Content-Type: application/x-www-form-urlencoded; charset=utf-8" \
  -d "grant_type=client_credentials"


## Test the OAuth2 token generation with (windows powershell):

$clientId='<your client id configured in /src/main/resources/secret/clients.properties'
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
