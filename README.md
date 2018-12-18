
# Call Center Application

Call Center application has been implemented using following technologies:

  - **Spring Boot** as a backend REST micro-service.
  - **MongoDB** as a persistence storage
  - **RabbitMQ** for queuing calls.

Pre-requisites:
- [Install Docker](https://docs.docker.com/install/)
- [Install Docker Compose](https://docs.docker.com/compose/install/#prerequisites)


# System Design (Internal Components)

The micro-service contains the following components:

- **Employee Controller** - Allows for creation and fetch details of employees
- **Employee Service** - Business logic to process controller requests
- **Employee Repository** - Persistence layer for storing domain objects.
- **Call Handler Controller** - Allows for dispatching and ending call
- **Call Handler Service** - Business logic to process incoming calls
- **Call Info Repository** - Persistence layer to store call logs


![diagram](./images/diagram.png)

1. User makes a `POST` request to create an employee of one of the allowed types - `DIRECTOR`, `MANAGER`, `RESPONDENT`
2. Employee service handles the call and processes the request
3. Employee object is persisted to mongodb's collection
4. Caller makes a `POST` request to place a call to call center
5. Call handler service creates an entry in the database of the with the call information and marks `queued` boolean properties as `true`.
6. A scheduler job runs at a configurable time checking for available employee. If an employee is availed to handle the call. 
  6A) Call handler service will `dequeue` the call from the queue and proceed to assign it to the employee. This process will also mark the employee's status as "ON_CALL".

### Tech Stack

Call center application uses a number of open source projects:

* [Spring Boot](https://spring.io/projects/spring-boot) - Creating spring based application for the web!
* [RabbitMQ](https://www.rabbitmq.com/) - Messaging queue for internet scale systems
* [MongoDB](https://www.mongodb.com/) - NoSQL data storage.
* [Docker](https://www.docker.com) - great for containerzing your app to be ran on multiple platforms without worry for underlying OS.


### Running the application locally using Docker

**Running the application requires docker installed locally.** 
The application also uses docker compose to run multi-container. See [Install Docker Compose | Docker Documentation](https://docs.docker.com/compose/install/#prerequisites)

Given that the application uses MongoDB and RabbitMQ broker, it has been packaged with docker compose file which starts up docker container for both of those dependencies. The application is setup to talk to those two containers. 


Navigate to the root of the project
```sh
$ cd callcenter
```

Cleaning and building the project also runs unit test and integration test for the application.

For integration test, it spins up an embedded mongodb instance and a Apache QPid AMQP broker with in-memory store for queueing the calls.
This command below also bundles up the whole application as fat jar to be ran standalone. NOTE: this doesn't bundle up mongo and rabbitmq.

```sh
$ ./gradlew clean build
```

In order to start up all the three containers: callcenter, mongodb, rabbitmq. The command below starts all the required dependencies before starting the application. 
```sh
$ docker compose up -d
```

Once the container is up and running. It will be available on:

```sh
$ localhost:8080
```

### APIs

- Creating an employee

  Request
  ```sh
  curl --request POST \
    --url http://localhost:8080/callcenter/employees/create \
    --header 'accept: application/json' \
    --header 'content-type: application/json' \
    --data '{
    "firstName": "John",
    "lastName": "Doe",
    "role": "DIRECTOR"
  }'
  ```

  Response
  ```json
  {
    "id": "5b1c4bf32ab79c000134b9b1",
    "firstName": "John",
    "lastName": "Doe",
    "role": "DIRECTOR",
    "status": "AVAILABLE"
  }
  ```
  
- Getting a list of employees by role
  - Supported types: `RESPONDENT`, `MANAGER`, and `DIRECTOR`

  Request
  ```sh
  curl --request GET \
  --url 'http://localhost:8080/callcenter/employees?role=DIRECTOR'
  ```
  Response
  ```json
    {
    "content": [
      {
        "id": "5b19c58b2ab79c0001378b30",
        "firstName": "098769fd-cae0-4ef0-b9ec-f5b5529df5ed",
        "lastName": "Patel",
        "role": "DIRECTOR",
        "status": "AVAILABLE"
      },
      {
        "id": "5b19c58c2ab79c0001378b31",
        "firstName": "c157437c-6ee8-4d39-960b-554c8ee5db65",
        "lastName": "Patel",
        "role": "DIRECTOR",
        "status": "AVAILABLE"
      },
      {
        "id": "5b1be3fe2ab79c00019401b2",
        "firstName": "c40343e8-27dd-495b-af87-8b765ccd9a8b",
        "lastName": "Patel",
        "role": "DIRECTOR",
        "status": "AVAILABLE"
      },
      {
        "id": "5b1be3fe2ab79c00019401b3",
        "firstName": "b041b016-8eff-436a-b1b9-b125bae1940e",
        "lastName": "Patel",
        "role": "DIRECTOR",
        "status": "AVAILABLE"
      },
      {
        "id": "5b1c4bf32ab79c000134b9b1",
        "firstName": "John",
        "lastName": "Doe",
        "role": "DIRECTOR",
        "status": "AVAILABLE"
      }
    ],
    "pageable": {
      "sort": {
        "sorted": false,
        "unsorted": true
      },
      "pageNumber": 0,
      "pageSize": 20,
      "offset": 0,
      "unpaged": false,
      "paged": true
    },
    "totalElements": 5,
    "totalPages": 1,
    "last": true,
    "sort": {
      "sorted": false,
      "unsorted": true
    },
    "first": true,
    "numberOfElements": 5,
    "size": 20,
    "number": 0
  }
  ```

- Dispatch a call
  Response
  ```sh
  curl --request POST \
  --url http://localhost:8080/callcenter/callhandler/dispatchCall \
  --header 'content-type: application/json' \
  --data '{
	"callerId":"+f064e60d-35b1-482c-a567-d3779cecd53d",
	"callerName": "CALLNAME-35e8b8b0-6cb3-11e8-91cf-974ff98e0897"
  }'
  ```

  Response
  
  ```json
    {
      "id": "5b1be4272ab79c00019401b4"
    }
  ```
  
  - Get queued calls
  Request
  ```sh
  curl --request GET \
  --url http://localhost:8080/callcenter/callhandler/queuedCalls
  ```
  
  Response
  ```json
    {
      "content": [
        {
          "id": "5b1d2a432ab79c0001e63741"
        }
      ],
      "pageable": {
        "sort": {
          "sorted": false,
          "unsorted": true
        },
        "pageSize": 20,
        "pageNumber": 0,
        "offset": 0,
        "unpaged": false,
        "paged": true
      },
      "last": true,
      "totalPages": 1,
      "totalElements": 1,
      "sort": {
        "sorted": false,
        "unsorted": true
      },
      "first": true,
      "numberOfElements": 1,
      "size": 20,
      "number": 0
    }
  ```
  
  - End a call
  Request
  ```sh
      curl --request GET \
      --url http://localhost:8080/callcenter/callhandler/endcall/5b1d2cb62ab79c000171e27f
  ```
  
  Response
  ```json
    {
      "duration": "1 minutes",
      "callerId": "+485cca3b-a64d-418b-9e66-ed9b460cd284",
      "callerName": "CALLNAME-3a995d90-6cb5-11e8-91cf-974ff98e0897"
    }
  ```
  
  
## Project Structure
  
The proect uses [Gradle](https://gradle.org) as a build tool. It uses the gradle wrapper functionality so that the user does not have to have gradle installed on their system. 

#### Application Code
  
  
  ```sh
|-- main
|   |-- java
|   |   `-- com
|   |       `-- callcenter
|   |               |-- ApplicationBootstrap.java
|   |               |-- Constants.java
|   |               |-- RabbitMQConfiguration.java
|   |               |-- controller
|   |               |   |-- CallController.java
|   |               |   |-- EmployeeController.java
|   |               |   |-- request
|   |               |   |   |-- CallRequest.java
|   |               |   |   `-- EmployeeRequest.java
|   |               |   `-- response
|   |               |       |-- CallEndedResponse.java
|   |               |       |-- CallResponse.java
|   |               |       `-- EmployeeResponse.java
|   |               |-- dao
|   |               |   |-- DataModelConstants.java
|   |               |   |-- model
|   |               |   |   |-- CallInfo.java
|   |               |   |   `-- Employee.java
|   |               |   `-- repository
|   |               |       |-- CallInfoRepository.java
|   |               |       `-- EmployeeRepository.java
|   |               |-- exception
|   |               |   |-- CallCenterServiceException.java
|   |               |   `-- RestResponseEntityExceptionHandler.java
|   |               `-- service
|   |                   |-- CallHandlerService.java
|   |                   |-- EmployeeService.java
|   |                   `-- impl
|   |                       |-- DefaultCallHandlerService.java
|   |                       `-- DefaultEmployeeService.java
|   `-- resources
|       `-- application.yml
```

  - package: `com.callcenter` root dir contains `ApplicationBootstrap.java` which runs the application
  - package: `com.callcenter`'s `RabbitMQConfiguration.java` contains rabbitmq broker connection configuration to be used by `spring-data-amqp`
  - package: `com.callcenter.controller` contains the controller class for the application. These are the main entry points for rest calls. The subpackages for contains `request` and `response` object types.
  - package: `com.callcenter.service` contains the service layer, which encapsulates the business logic for the application. This is where the application puts the call on rabbitmq queue, assigns a call to available employee etc. 
  - package: `com.callcenter.dao` contains the dao layer of the application. The exit point for data sinks. This is where `spring-data-mongodb` comes in to play. 
  - package: `com.callcenter.exception` contains utils class for exceptions thrown by the service. It uses spring's `@ControllerAdvice` to translate exceptions thrown by the service into a http status code. 
  - `src/main/resources` contains `application.yml` which stores the application configuration properties. All the properties are externalized so that they can be set by passing via environment variables.


          
### Test Code

```sh
`-- test
    |-- java
    |   `-- com
    |       `-- callcenter
    |               `-- test
    |                   |-- integration
    |                   |   |-- BaseIntegrationTest.java
    |                   |   |-- dao
    |                   |   |   `-- repository
    |                   |   |       |-- CallInfoRepositoryTest.java
    |                   |   |       `-- EmployeeRepositoryTest.java
    |                   |   |-- service
    |                   |   |   |-- CallHandlerServiceIntegrationTest.java
    |                   |   |   |-- EmployeeServiceIntegrationTest.java
    |                   |   |   `-- HandleQueuedCallsSchedulerTest.java
    |                   |   `-- utils
    |                   |       `-- EmbeddedAMQPBroker.java
    |                   `-- unit
    |                       |-- controller
    |                       |   |-- CallControllerTest.java
    |                       |   `-- EmployeeControllerTest.java
    |                       `-- service
    |                           `-- impl
    |                               |-- DefaultCallHandlerServiceTest.java
    |                               `-- DefaultEmployeeServiceTest.java
    `-- resources
        |-- initial-config.json
        `-- integration-test.properties
  ```

  - package: `com.callcenter.test.integration` contains integrations test for the application. The integration test runs an embedded mongodb instance and an embedded amqp broker for running end to end test on the service. Each component is test individuals in isolation, plus it has been tested in integration with others.
  - package: `com.callcenter.test.unit` contains unit test for each component. This package contains mock test for all the components.
  - `src/test/resources` contains `integration-test.properties` which is used to override environment configuration properties for the application. It also contains `initial-config.json` which is used by the embedded AMQP broker to configure itself.


---------
