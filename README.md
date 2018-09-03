# User Messages Demo Showcase

![CircleCI](https://circleci.com/gh/tomjaroszewskiwork/user-messages/tree/master.svg?style=svg)

## To build

Get JDK 1.8

./gradlew build

## To run jar

java -jar ./build/libs/user-messages-1.0.0

## To build docker image

./gradlew build docker

## To run docker image

docker run -p 8080:8080 user-messages

## API spec

Default port is 8080. If need to change see application.properties

Sample API call: http://localhost:8080/v1/users/tom.j/messages

Please see swagger.json for full spec details

## Architecture Overview

![Architecture](/architecture.svg)

The app runs an embedded Jersey server listening in on port 8080.

JAX-RS is used to map REST end points from Jersey to method calls.

Persistence is provided by Hibernate ORM using HSQLDB as the store.

HSQLDB is a Java based file/in-memeory DB similar to sqllite.

Everying is configured and runs from using Spring Boot.

When deployed into Elastic Beanstalk in AWS there is a ngix load balancer in front of the application.

### Limitations

Since HSQLDB is file based the app is not scalable currently.

Because we are using an ORM it is simple matter of dropping in the connector for an external SQL database like Postgres.

Further scalability can be achived with read-only replication/sharding on the database layer.














