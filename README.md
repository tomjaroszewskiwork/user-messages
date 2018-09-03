# User Messages Demo Showcase

![CircleCI](https://circleci.com/gh/tomjaroszewskiwork/user-messages/tree/master.svg?style=svg)

## To build

Get JDK 1.8

```bash
./gradlew build
```

## To run jar

```bash
java -jar ./build/libs/user-messages-1.0.0
```

## To build docker image

```bash
./gradlew build docker
```

## To run docker image

```bash
docker run -p 8080:8080 user-messages
```

## API spec

Default port is 8080.

Sample API call: http://localhost:8080/v1/users/tom.j/messages

Please see swagger.json for full API spec details.

## Architecture Overview

![Architecture](/architecture.svg)

The app runs an embedded Jersey server listening in on port 8080.

JAX-RS is used to map REST end points from Jersey to method calls.

Persistence is provided by Hibernate ORM using HSQLDB as the store.

HSQLDB is a Java based file/in-memeory DB similar to sqllite.

Everything is configured using Spring Boot.

When deployed into Elastic Beanstalk in AWS there is a ngix load balancer in front of the application.
