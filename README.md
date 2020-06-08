# glovo-api-mock

Mock to simulate the integration with Glovo API

## Getting Started

### Prerequisites

What things you need to install the software and how to install them:

```
	- Java 11 or higher
 	- Maven 3.6 or higher
 	- curl
```

### My Environment

I've working with this tools

```
	- OS: Ubuntu 19.04
	- Java 11: OpenJDK Runtime Environment GraalVM CE 20.0.0 (build 11.0.6+9-jvmci-20.0-b02)
	- IDE: Eclipse 2020-03
	- Docker Engine: Docker Engine - Community: 19.03.8
	- Docker Compose: docker-compose version 1.21.0, build unknown
	- curl 7.64.0
	- browser: Google Chrome Versión 81.0.4044.129 (Build oficial) (64 bits)
	- Postman for Linux: Postman for Linux Version 7.24.0
```

### Installing

To run the application you have to do the following steps:

```
	./mvnw spring-boot:run
```

### Playing

The default port is 8080, so the url of the swagger interface is [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)


You can configure some parameters in the *application.properties* like as:

```
	
	# The server port... nothing more to say
	server.port=8080
	
	
	# Credentials
	basic.auth.user=grsm-poc
	basic.auth.pass=grsm-poc-password

```


## Mock Responses

- (GET) /b2b/orders 
- (POST) /b2b/orders
- (POST) ​/b2b​/orders​/estimate
- (GET) /b2b/orders/{orderId}
- (POST) /b2b/orders/{orderId}/cancel
- (GET) /b2b/orders/{orderId}/courier-contact
- (GET) /b2b/orders/{orderId}/tracking
- (GET) /b2b/working-areas

#### (GET) /b2b/orders

We get an order list, with all the data randomly.

Request example:

```
GET /b2b/orders?limit=<number>&offset=<number>
```

 
#### (POST) /b2b/orders -> Create Order

Validate that the data is correct:

- Planning time: null or future
- Description not empty
- Addresses:
	- Between 2 and 9
	- At least 1 pickup and 1 delivery
	- At most 1 pickup or 1 delivery

Answers:
The order you have received with the following specifications:

- Only one delivery address:
	- Only one pickup address: OrderCode with prefix "OWO"
	- More than one collection address: OrderCode with prefix "MPUO"

- 2 delivery addresses:
	- One of the delivery addresses is the same as the pickup address (it is a return): OrderCode with prefix "RO"
	- None of the delivery addresses is the same as the pickup: It is the same as "Multiple delivery addresses"
- Multiple delivery addresses:
	- OrderCode with prefix "MDOO"
	
Create an order:
	- Create a One Way order: Create an order. If you want the order to be scheduled, provide a scheduleTime in milliseconds (see Order). Otherwise, it'll be scheduled for immediate delivery.
	- Create a Return order: Create a return order by adding a second DELIVERY address that is identical to the PICKUP (label, lat, lon must be the same).
	- Create a Multiple Pick-Up order: Create a multiple pick-up order by creating up to eight PICKUP addresses and one DELIVERY address.
	- Create Multiple Drop-Off Order: Create a multiple drop-off order by adding one PICKUP and up to eight DELIVERY addresses.	

##### Create One Way Order

Request example:

```
POST /b2b/orders
 {
   "scheduleTime": null || 1568879245000,
   "description": "A 30cm by 30cm box",
   "reference": {
     "id": "your internal reference"
   },
   "addresses": [
     {
       "type": "PICKUP",
       "lat": 0.1,
       "lon": 0.3,
       "label": "Calle la X, 29",
       "details": "2nd Floor",
       "contactPhone": "+34622334455",
       "contactPerson": "Sam Romero",
       "instructions": "Use the stairs to access this address"
     },
     {
       "type": "DELIVERY",
       "lat": 0.1,
       "lon": 0.3,
       "label": "Calle la X, 30",
       "details": "Blue button of the intercom",
       "contactPhone": "+34622334455",
       "contactPerson": "Alex Smith",
       "instructions": "If recipient is unavailable leave next door"
     }
   ]
 }
```

Response example:

```

{
   "id": 123456789,
   "code": "AA12AA1BB",
   "state": "SCHEDULED",
   "scheduleTime": 1568879245000,
   "description": "A 30cm by 30cm box",
   "reference": {
     "id": "your internal reference"
   },
   "addresses": [
     {
       "type": "PICKUP",
       "lat": 0.1,
       "lon": 0.3,
       "label": "Calle la X, 29",
       "details": "2nd Floor",
       "contactPhone": "+34622334455",
       "contactPerson": "Sam Romero",
       "instructions": "Use the stairs to access this address"
     },
     {
       "type": "DELIVERY",
       "lat": 0.1,
       "lon": 0.3,
       "label": "Calle la X, 30",
       "details": "Blue button of the intercom",
       "contactPhone": "+34622334455",
       "contactPerson": "Alex Smith",
       "instructions": "If recipient is unavailable leave next door"
     }
   ]
 }

```

##### Create Return Order

Request example:

```
POST /b2b/orders
 {
   "scheduleTime": null,
   "description": "A 30cm by 30cm box",
   "reference": {
     "id": "your internal reference"
   },
   "addresses": [
     {
       "type": "PICKUP",
       "lat": 0.1,
       "lon": 0.3,
       "label": "Calle la X, 29",
       "details": "2nd Floor",
       "contactPhone": "+34622334455",
       "contactPerson": "Sam Romero",
       "instructions": "Use the stairs to access this address"
     },
     {
       "type": "DELIVERY",
       "lat": 1.2,
       "lon": 4.5,
       "label": "Calle la Y, 30",
       "details": "Blue button of the intercom",
       "contactPhone": "+34622334466",
       "contactPerson": "Alex Smith",
       "instructions": "If recipient is unavailable leave next door"
     },
     {
       "type": "DELIVERY",
       "lat": 0.1,
       "lon": 0.3,
       "label": "Calle la X, 29",
       "details": "2nd Floor",
       "contactPhone": "+34622334455",
       "contactPerson": "Sam Romero",
       "instructions": "Use the stairs to access this address"
     }
   ]
 }

```

##### Create Multiple Pickup Order

Request example:

```
POST /b2b/orders
 {
     "scheduleTime": null,
     "description": "A 30cm by 30cm box",
     "reference": {
       "id": "your internal reference"
     },
     "addresses": [
         {
           "type": "PICKUP",
           "lat": 0.1,
           "lon": 0.3,
           "label": "Calle la X, 29",
           "details": "2nd Floor",
           "contactPhone": "+34622334455",
           "contactPerson": "Sam Romero",
           "instructions": "Use the stairs to access this address"
         },
         {
           "type": "PICKUP",
           "lat": 0.11,
           "lon": 0.31,
           "label": "Calle la J, 40",
           "details": "3rd Floor",
           "contactPhone": "+34622334466",
           "contactPerson": "Rosa Smith",
           "instructions": "If recipient is unavailable leave at reception"
         },
         {
           "type": "PICKUP",
           "lat": 0.12,
           "lon": 0.32,
           "label": "Calle la Y, 56",
           "details": "Ground floor",
           "contactPhone": "+34622334477",
           "contactPerson": "Ana Feliz",
           "instructions": "Ask for Ramiro"
         },
         {
           "type": "DELIVERY",
           "lat": 0.13,
           "lon": 0.33,
           "label": "Calle la Z, 30",
           "details": "Blue button of the intercom",
           "contactPhone": "+34622334488",
           "contactPerson": "Alex Rossi",
           "instructions": "If recipient is unavailable leave next door"
         }
     ]
 }

```

##### Create Multiple Drop Off Order

Request example:

```
POST /b2b/orders
 {
     "scheduleTime": null,
     "description": "A 30cm by 30cm box",
     "reference": {
       "id": "your internal reference"
     },
     "addresses": [
         {
           "type": "PICKUP",
           "lat": 0.1,
           "lon": 0.3,
           "label": "Calle la J, 29",
           "details": "2nd Floor",
           "contactPhone": "+34622334455",
           "contactPerson": "Sam Romero",
           "instructions": "Use the stairs to access this address"
         },
         {
           "type": "DELIVERY",
           "lat": 0.11,
           "lon": 0.31,
           "label": "Calle la X, 40",
           "details": "3rd Floor",
           "contactPhone": "+34622334466",
           "contactPerson": "Rosa Smith",
           "instructions": "If recipient is unavailable leave next door"
         },
         {
           "type": "DELIVERY",
           "lat": 0.12,
           "lon": 0.32,
           "label": "Calle la Y, 56",
           "details": "Ground floor",
           "contactPhone": "+34622334477",
           "contactPerson": "Ana Feliz",
           "instructions": "Leave the box near the entrance"
         },
         {
           "type": "DELIVERY",
           "lat": 0.14,
           "lon": 0.35,
           "label": "Calle la Z, 30",
           "details": "Blue button of the intercom",
           "contactPhone": "+34622334488",
           "contactPerson": "Alex Smith",
           "instructions": "If recipient is unavailable leave at reception"
         }
     ]
 }
```

#### (POST) ​/b2b​/orders​/estimate

Validate that the data is correct:

- Planning time: null or future
- Description not empty
- Addresses:
	- Greater than 2 and less than 9
	- There must be at least one pickup and one delivery
	- There may be more than one pickup
	- There may be more than one delivery
	- There cannot be more than one pickup and more than one delivery at a time
	- The latitudes have to fit into one of the work areas (working-areas service)
	
**Devuelve el precio estimado en centimos**

Request example:

```
POST /b2b/orders/estimate
  {
    "scheduleTime": 1568879245000,
    "description": "A 30cm by 30cm box",
    "addresses": [
      {
        "type": "PICKUP",
        "lat": 0.1,
        "lon": 0.3,
        "label": "Calle la X, 29"
      },
      {
        "type": "DELIVERY",
        "lat": 0.1,
        "lon": 0.3,
        "label": "Calle la X, 30"
      }
    ]
  }
```

Response example:

```
{
    "total": {
      "amount": 590,
      "currency": "EUR"
    }
}
```


#### (GET) /b2b/orders/{orderId}

We obtain an Order with the following characteristics:

- OrderId: the past as a parameter
- OrderCode with prefix "SO"
- Rest of data ... random

Request example:

```
GET /b2b/orders/:id
```

Response example:

```
{
   "id": 123456789,
   "code": "AA12AA1BB",
   "state": "SCHEDULED",
   "scheduleTime": 1568879245000,
   "description": "A 30cm by 30cm box",
   "reference": {
     "id": "your internal reference"
   },
   "addresses": [
     {
       "type": "PICKUP",
       "lat": 0.1,
       "lon": 0.3,
       "label": "Calle la X, 29",
       "details": "2nd Floor",
       "contactPhone": "+34622334455",
       "contactPerson": "Sam Romero",
       "instructions": "Use the stairs to access this address"
     },
     {
       "type": "DELIVERY",
       "lat": 0.2,
       "lon": 0.4,
       "label": "Calle la Y, 30",
       "details": "Blue button of the intercom",
       "contactPhone": "+34622334455",
       "contactPerson": "Alex Smith",
       "instructions": "If recipient is unavailable leave next door"
     }
   ]
 }

```

#### (POST) /b2b/orders/{orderId}/cancel

Cancel an order.

- If the orderId is even, cancel it.
- If the orderId is odd it does not cancel it.

```
EYE, we do not know how the glovo API responds with cancellations ... I assumed it could be in this way

```

Request example:

```
POST /b2b/orders/:id/cancel
```

Response example:

```

 {
   "id": 123456789,
   "code": "AA12AA1BB",
   "state": "CANCELED",
   "scheduleTime": 1568879245000,
   "description": "A 30cm by 30cm box",
   "reference": {
     "id": "your internal reference"
   },
   "addresses": [
     {
       "type": "PICKUP",
       "lat": 0.1,
       "lon": 0.3,
       "label": "Calle la X, 29",
       "details": "2nd Floor",
       "contactPhone": "+34622334455",
       "contactPerson": "Sam Romero",
       "instructions": "Use the stairs to access this address"
     },
     {
       "type": "DELIVERY",
       "lat": 0.2,
       "lon": 0.4,
       "label": "Calle la Y, 30",
       "details": "Blue button of the intercom",
       "contactPhone": "+34622334455",
       "contactPerson": "Alex Smith",
       "instructions": "If recipient is unavailable leave next door"
     }
   ]
 }

```


#### (GET) /b2b/orders/{orderId}/courier-contact

Returns the details of the order delivery:

- Name: Niarudni Leugim
- Telephone: +34666123123

Request example:

```
GET /b2b/orders/:id/courier-contact
```

Response example:

```
{
   "courier": "Niarudni Leugim",
   "phone": "+34666123123"
 }
```

#### (GET) /b2b/orders/{orderId}/tracking

Returns the coordinates of where an order is located.
It always returns:
- Latitude: 40.4170769
- Length: -3.7033539

Request example:

```
GET /b2b/orders/:id/tracking
```

Response example:

```
{
   "lat": 40.4170769,
   "lon": -3.7033539
 }
```


#### (GET) /b2b/working-areas

Get Working Areas
 Returns the characteristics of our working areas.
 Use this data to check for valid pickup and delivery locations and times on your side.
 A way of doing this can be found in com.google.maps.android.PolyUtil::containsLocation
 <p>
 We recommend you to use aggressive caching for the results of this endpoint in order to avoid unnecessary
 server-to-server traffic that could make you activate rate limiting.

Request example:

```
 GET /b2b/working-areas
```

Response example showing a single WorkingArea:

```
 {
   "workingAreas": [
     {
       "code": "BCN",
       "polygons": ["<ENCODED POLYLINE>", "<ENCODED POLYLINE>"],
       "workingTime": { "from": "09:00", "duration": 120 }
     }
   ]
 }
```
