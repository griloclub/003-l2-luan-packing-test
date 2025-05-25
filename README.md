# Packaging Optimization Service

This Spring Boot application provides an API endpoint for optimizing the packaging of products into predefined box sizes for "Seu Manoel's" online game store. Given a list of orders with products and their dimensions, the service determines the best way to pack these items, aiming to minimize the number of boxes used.

## Features

* Calculates optimal box selection for multiple orders.
* Considers product dimensions (height, width, length) and allows for 6-way rotation to fit items.
* Uses a heuristic approach to minimize the number of boxes.
* Identifies products that cannot fit into any available box size.
* Provides API documentation via Swagger/OpenAPI.

## Prerequisites

* Java 21 (or the JDK version specified in your `pom.xml`)
* Apache Maven (for building the project)
* Docker (optional, for running the application in a container)

## Building the Application

To build the project and create an executable JAR file, navigate to the project's root directory and run:

```bash
mvn clean package
```

## Running the Application
### Locally
Once the application is built, you can run it using the following command:

```bash
mvn spring-boot:run
```
(Replace packing_test-0.0.1-SNAPSHOT.jar with the actual name of your generated JAR file if it differs.)

The application will start, and the API will typically be available at http://localhost:8080.

### Using Docker
A Dockerfile is provided to containerize the application.

a. Build the Docker Image:
First, ensure you have built the application JAR using mvn clean package. Then, from the project's root directory, run:

Bash
```bash
docker build -t dev.genro.luan/packing-test .
```
(You can choose a different tag if you prefer.)

b. Run the Docker Container:

```bash
docker run -p 8080:8080 dev.genro.luan/packing-test
```

This will start the container, and the application will be accessible on port 8080 of your host machine.

## API Documentation (Swagger UI)
Once the application is running, you can access the Swagger UI for interactive API documentation and testing at:

http://localhost:8080/swagger-ui.html

This interface provides details about the API endpoint, request/response schemas, and allows you to send test requests directly from your browser.

API Endpoint: Packaging Optimization

The primary API endpoint for optimizing packaging is:

URL: /api/v1/packaging/optimize

Method: POST

Content-Type: application/json

### Request Payload

The endpoint expects a JSON payload representing a PackageOrderRequest, which contains a list of orders. Each order has an ID and a list of products with their respective dimensions.

Example Request (PackageOrderRequest):

```json5
{
  "orders": [
    {
      "order_id": 1,
      "products": [
        {
          "product_id": "PS5",
          "dimension": {
            "height": 40,
            "width": 10,
            "length": 25
          }
        },
        {
          "product_id": "Volante",
          "dimension": {
            "height": 40,
            "width": 30,
            "length": 30
          }
        }
      ]
    },
    {
      "order_id": 2,
      "products": [
        {
          "product_id": "Joystick",
          "dimension": {
            "height": 15,
            "width": 20,
            "length": 10
          }
        },
        {
          "product_id": "Fifa 24",
          "dimension": {
            "height": 10,
            "width": 30,
            "length": 10
          }
        },
        {
          "product_id": "Call of Duty",
          "dimension": {
            "height": 30,
            "width": 15,
            "length": 10
          }
        }
      ]
    },
    {
      "order_id": 3,
      "products": [
        {
          "product_id": "Cadeira Gamer",
          "dimension": {
            "height": 120,
            "width": 60,
            "length": 70
          }
        }
      ]
    }
  ]
}
```

### Response Payload
The API returns an OrdersResponse object, which contains a list of OrderBoxesResponse objects, one for each processed order.

Example Success Response (OrdersResponse):

```json5
{
  "orders": [
    {
      "order_id": 1,
      "boxes": [
        {
          "box_id": "Box 1",
          "products": [
            "Volante",
            "PS5"
          ]
        }
      ]
    },
    {
      "order_id": 2,
      "boxes": [
        {
          "box_id": "Box 1",
          "products": [
            "Call of Duty",
            "Joystick",
            "Fifa 24"
          ]
        }
      ]
    },
    {
      "order_id": 3,
      "boxes": [
        {
          "products": [
            "Cadeira Gamer"
          ],
          "observation": "Product does not fit in any available box."
        }
      ]
    }
  ]
}
```
(The observation field will be omitted for successfully packed boxes and will contain a message for products that could not be packed).

## Authentication (JWT)
This API uses JWT (JSON Web Token) Bearer token authentication to secure its endpoints (except for the /api/v1/authenticate endpoint itself).

To access secured endpoints like /api/v1/packaging/optimize, you first need to obtain a JWT by authenticating with valid credentials, and then include this JWT in the Authorization header of your subsequent requests.

### Obtaining a JWT Token
   You can get a JWT by sending a POST request to the /api/v1/authenticate endpoint with your username and password.

Endpoint: POST /api/v1/authenticate

Request Body:

JSON

{
"username": "user",
"password": "password"
}

(Note: The default credentials are username user and password password as per the current implementation)

Successful Response (200 OK):

```json5
{
"jwt": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjc4ODg2NDAwLCJleHAiOjE2Nzg4ODk0MDB9.exampleTokenSignature"
}
```
Copy the jwt value received. This is your Bearer token.

## Using the JWT to Access Secured Endpoints
### Manual Authentication using curl
Once you have the JWT, you need to include it in the Authorization header for requests to secured endpoints, prefixed with Bearer.

Example curl request to the secured /api/v1/packaging/optimize endpoint:

Replace <YOUR_JWT_HERE> with the actual token you obtained. Replace the example request body with your actual packaging data.

```bash
curl -X POST http://localhost:8080/api/v1/packaging/optimize \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <YOUR_JWT_HERE>" \
-d '{
"orders": [
{
"order_id": "ORD001_SECURED",
"products": [
{
"product_id": "PROD_ITEM_S",
"dimension": {
"height": 10,
"width": 10,
"length": 10
}
}
]
}
]
}'
```
If the token is valid and not expired, you will receive the normal response from the endpoint (e.g., a 200 OK with the packaging solution). If the token is missing, invalid, or expired, you will receive a 401 Unauthorized error.

### Using JWT Authentication in Swagger UI
The Swagger UI is configured to support JWT Bearer authentication, making it easy to test secured endpoints.

Open Swagger UI: Navigate to http://localhost:8080/swagger-ui.html in your browser.

Authenticate (Globally for Swagger UI):
Look for an "Authorize" button, usually located near the top right of the page. Click it.
A dialog will appear, prompting you for "bearerAuth" (or the name you configured for your SecurityScheme).

In the "Value" field, paste your full JWT token (the long string you received from the /api/v1/authenticate endpoint).
Note: Some Swagger UI versions might require you to prefix it with Bearer (e.g., Bearer eyJ...), while others will add the "Bearer " prefix automatically. Check the instructions in the dialog or try both if one doesn't work.
Click "Authorize" in the dialog, and then "Close".

### Available Box Sizes

Seu Manoel has the following predefined cardboard box sizes (Height x Width x Length, in centimeters): 

Caixa 1: 30 x 40 x 80

Caixa 2: 80 x 50 x 40

Caixa 3: 50 x 80 x 60

The service uses these dimensions to determine the best fit.

## Packing Heuristic Overview

The packing algorithm implemented uses a heuristic approach to optimize space and minimize the number of boxes: 

Product Sorting: For each order, products are sorted by their volume in descending order. Larger items are considered first.

Fit into Existing Boxes: The algorithm attempts to place each product into an already "opened" box for the current order if it fits (considering remaining volume and dimensional compatibility with the box type, including 6-way rotation).

Open New Smallest Box: If a product cannot fit into any existing open box, a new box is "opened." The smallest available BoxType that can accommodate the product (dimensionally and by volume, considering rotation) is chosen. 

Unpackable Products: If a product cannot fit into any available empty box type, it's marked as unpackable and listed with an appropriate observation in the response for that order.

## Testing the Application
Unit Tests
To run the unit tests defined in the project, use the Maven command:

```bash
mvn test
```
This will execute all test classes found in src/test/java.

Manual API Testing

You can test the /api/v1/packaging/optimize endpoint using tools like curl, Postman, or the integrated Swagger UI.

Example using curl:

```bash
curl -X POST http://localhost:8080/api/v1/packaging/optimize \
-H "Content-Type: application/json" \
-d '{
    "orders": [
        {
            "order_id": 1,
            "products": [
                {
                    "product_id": "itemA",
                    "dimension": { "height": 10, "width": 10, "length": 10 }
                },
                {
                    "product_id": "itemB_unpackable",
                    "dimension": { "height": 200, "width": 200, "length": 200 }
                }
            ]
        }
    ]
}'
```
