[![Build Status](https://travis-ci.com/danielmkraus/transfer-service.svg?branch=master)](https://travis-ci.com/danielmkraus/transfer-service)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.danielmkraus%3Atransfer-service&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.danielmkraus%3Atransfer-service)


# Transfer Service

## Objective

- This project is a sample web service to receive concurrent account transfer requests
- Detailed use case scenarios are in [Feature description](./src/test/resources/stories/Transfer.feature)

## Prerequisites

- Java 11 +

## Building 

- to build the project you need to execute the following command
```./mvnw clean package```


## Running


### Running

- To run in an IDE, you just need to start the ```org.danielmkraus.transfer.TransferServer```

- To run it as a separated java executable, you can run it using command 
```java -jar transfer-service-1.0.0-jar-with-dependencies.jar```, to get this jar you can do one of these options: 
    - build it locally and get the executable from ```./target/transfer-service-1.0.0-jar-with-dependencies.jar```
    - download executable bundle from [Github Releases](https://github.com/danielmkraus/transfer-service/releases)

## Endpoints

### POST /rest/accounts/{accountId}

- Register a new account with balance 0
    - path parameters: 
        - accountId
    - return
        - status 400 if found any account with given accountId 
        - status 204 if account was successfully created
        
#### Testing 
```
curl -X POST -v http://localhost:8080/rest/accounts/1
 [...]
< HTTP/1.1 204 No Content
```
    
    
### PUT /rest/accounts/{accountId}?balance={balance}

- Set a new balance to an account 
    - path parameter: 
        - accountId
    - query parameter:
        - balance
    - return
        - status 204 if account was successfully created or updated
        
#### Testing 
 ```
 curl -X PUT -v http://localhost:8080/rest/accounts/2?balance=100
  [...]
<  HTTP/1.1 204 No Content
 ```
 
### POST /rest/transfers

- transfer money between accounts
    - request body - json with following format:
         ```
         {
             "fromAccountId" : "20000",
             "toAccountId": "10000",
             "amount": "10.12"
         }
         ```
    - return
         - status 400 if validation error occurs
         - status 404 if not found any account with one of given accountIds
         - status 409 if concurrent modifications on this account occurred
         - status 204 if transfer was done

#### Testing 
```
curl -H "Content-Type: application/json" -d '{"fromAccountId" : "2", "toAccountId": "1", "amount":"100.00"}' -v http://localhost:8080/rest/transfers 
[...]
< HTTP/1.1 204 No Content
```

### GET /rest/accounts/{accountId}

- Get an account balance 
    - path parameter: 
        - accountId
    - return
        - status 404 if not found any account with given accountId 
        - status 200 with json containing accountId and balance

#### Testing 
```
curl -X GET http://localhost:8080/rest/accounts/2
{"id":"2","balance":0}
```

# TODO

- Create a mechanism to get a random port for integration tests (today is fixed on /test/resources/application.properties)
- Configure pipeline to deploy on cloud