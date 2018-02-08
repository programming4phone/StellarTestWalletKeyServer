# Stellar Test Wallet Key Server 

This project demonstrates how to securely store the public key / secret seed associated with a Stellar account.  
 
The public key is stored as a hash and the secret seed is stored as encrytpted ciphertext. Neither key is _ever_ exposed as plaintext. Each web service invocation requires an Authorization header containing a Google Authentication token. The token is verified with Google Authentication Services before allowing any of the web services to continue processing.

Web services implement the following functions:
- **store keys** -  Store the account keys associated with a specific Stellar account.
- **get keys** - Retrieve the account keys associated with a specific Stellar account.
- **remove keys** - Remove the account keys associated with a specific Stellar account.

Also see the [Stellar Test Wallet Angular App](https://github.com/programming4phone/StellarTestWalletNgApp "Stellar Test Wallet Angular App") project.

For further details about Stellar accounts see <https://www.stellar.org/developers/guides/get-started/create-account.html>.

For further details about Google Authentication Services see <https://developers.google.com/identity/sign-in/web/sign-in>.

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
