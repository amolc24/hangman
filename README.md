# Introduction
This is a distributed hangman backend api service which exposes 3 api's to play the game. The documentation of these API's are available in swagger.yaml file in docs folder.

The game starts by calling the newgame api which returns the uuid of the game. Using the UUID of the game the other members can join the game as well.
The game has a limit of 10 guess overall irrespective of each player.


# Pre-requisites:
- Maven 3.x
- Java 11

# Download and Build
`git clone https://github.com/amolc24/hangman.git`
`mvn clean install`

# Run
` mvn spring-boot:run`

The application will start on port **9090**

# API Docs
[Swagger API Documentation](docs/swagger.yaml)


# System Design

![System Design](docs/system_design.png)

![Database Schema Design](docs/cassandra_schema.png)

![Distributed Cache Design](docs/redis_cache.png)

# Notes and Design diagrams raw images

![Rough1](docs/rough_note1.png)

![Rough2](docs/rough_note2.png)
