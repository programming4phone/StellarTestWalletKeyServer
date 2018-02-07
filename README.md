# Stellar Test Wallet Key Server 

This is currently a work in progress.

## Development stack

This project was developed using Java 8, Spring Boot, Spring Data Redis, Eclipse Oxygen, and Maven. 

## Prerequisites

The integration tests within this project require a Redis instance running on localhost. Testing was done on Windows 10 Professional with Docker installed. The Docker container is started using these commands from a Windows Command prompt:

`docker pull redis`

`docker run --name some-redis –p 6379:6379 -d redis`

When finished, the Docker container is terminated using these commands:

`docker stop some-redis`

`docker rm some-redis`

Also, to avoid possible problems make sure that the Docker settings use a fixed DNS Server of `8.8.8.8`

## Build

Run `mvn clean install` to build the project and run the supplied integration tests. The build artifacts will be stored in the `target/` directory. 

## Running the Spring Boot container
Change to the source code directory and run `java -jar target/wallet.api-0.0.1-SNAPSHOT.jar`.
