# simple-system-task
## About

Service to register todo items and track their statuses. Shecduled job checks NOT_DONE items every 5 misn to mark them as PAST_DUE 

#### tech stack used
 -spring boot<br />
 -java-17<br />
 -h2 database(jdbc:h2:mem:testdb)<br />
 -rabbitmq<br />
 -open api /swagger

## How to:

#### building service:
Run "mvn package -DskipTests" under project root

#### run automatic tests:
Run "mvn test" under project root

#### run the service locally:
mvn package -DskipTests<br />
docker compose up

#### API Documantation
	http://localhost:8080/swagger-ui/index.html
