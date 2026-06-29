# Following_package_system
Following_package_system is a backend application developed using Java Spring Boot.  
The project demonstrates the implementation of a following package system and provides REST API endpoints.

The application uses PostgreSQL as a database and supports running both locally and inside Docker containers.  
The API was tested using Postman.

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Gradle
- Docker
- Docker Compose
- Postman

## Features

- Insert, search, update and import operations
- Data persistence using PostgreSQL
- REST API 
- Dockerized application and database
- Environment-based configuration
- API testing using Postman

## Project Structure

```text
src/
 └── main/
     ├── java/
     │   └── com/example/demo/
     │       ├── controller/
     │       ├── entity/
     │       ├── repository/
     │       ├── service/
     │       └── DemoApplication.java
     └── resources/
         └── application.properties
Environment Configuration

The application uses environment variables for database configuration.

Example .env file for Docker:

DB_URL=jdbc:postgresql://db:5432/red_black_tree
DB_USERNAME=postgres
DB_PASSWORD=postgres
SERVER_PORT=8080

Example .env file for local execution:

DB_URL=jdbc:postgresql://localhost:5432/red_black_tree
DB_USERNAME=postgres
DB_PASSWORD=postgres
SERVER_PORT=8080

If PostgreSQL is running locally on a different port, for example 5433, the database URL should be changed like this:

DB_URL=jdbc:postgresql://localhost:5433/red_black_tree
DB_USERNAME=postgres
DB_PASSWORD=postgres
SERVER_PORT=8080
How to Run the Application

The application can be started in two ways:

Locally, using Gradle
Using Docker and Docker Compose
Running the Application Locally

Before running the application locally, make sure that you have installed:

Java 17
PostgreSQL
Gradle, or use the included Gradle wrapper

First, create a PostgreSQL database named:

red_black_tree

Then configure the .env file:

DB_URL=jdbc:postgresql://localhost:5432/red_black_tree
DB_USERNAME=postgres
DB_PASSWORD=postgres
SERVER_PORT=8080

After that, run the application.

On Linux or macOS:

./gradlew bootRun

On Windows:

.\gradlew bootRun

After successful startup, the application will be available at:

http://localhost:8080
Running the Application with Docker

The project can also be started using Docker Compose.
This will start both the Spring Boot application and the PostgreSQL database inside Docker containers.

Before running the project, make sure that Docker or Rancher Desktop is started.

To build and start the containers, run:

docker compose up --build

The application will be available at:

http://localhost:8080

To stop the containers, run:

docker compose down

To stop the containers and remove database volumes, run:

docker compose down -v

The command docker compose down -v removes the database volume as well, so all database data stored inside Docker will be deleted.

Database

The project uses a PostgreSQL database named:

red_black_tree

Hibernate/JPA is used for mapping Java entities to database tables.

When the application is running inside Docker, the Spring Boot application connects to the PostgreSQL container using the database service name from docker-compose.yml.

Example Docker database URL:

DB_URL=jdbc:postgresql://db:5432/red_black_tree

When the application is running locally, the database URL should point to localhost:

DB_URL=jdbc:postgresql://localhost:5432/red_black_tree
API Testing with Postman

The API was tested using Postman.

Base URL:

http://localhost:8080
Example API Requests

1. Insert a package

Method:

POST

Endpoint:

/posiljka

Request body:

{
  "serijskiBroj": "POS-001",
  "ukupanIznos": 1200.50,
  "opisSadrzaja": "Laptop i punjac",
  "napomenaIzmene": "Prva kreirana posiljka",
  "korisnik": {
    "ime": "Petar Petrovic",
    "jmbg": "1234567890123",
    "adresa": "Bulevar 1"
  }
}

2. Get All packages

Method:

GET

Endpoint:

/posiljka

3. Filter package by user, date of creation or status

Method:

GET

Endpoint:
1. /posiljka/filter?korisnikId=1
2. /posiljka/filter?status=U_TRANSPORTU
3. /posiljka/filter?datumKreiranja=2026-06-28
4. /posiljka/filter?korisnikId=1&status=U_TRANSPORTU
5. /posiljka/filter?korisnikId=1&status=U_TRANSPORTU&datumKreiranja=2026-06-28

4. Update a status of package

Method:

PUT

Endpoint:

/posiljka

Request body:

{
  "serijskiBroj": "POS-001",
  "status": "U_TRANSPORTU",
  "napomenaIzmene": "Posiljka je poslata u transport"
}


5. Get package history

Method:

GET

Endpoint:

/posiljka/history/{serijskiBroj}

6. Import package from a CSV/XLSX File

Method

POST

Endpoint:

/posiljka/import

In Postman:

Body: form-data
key: file
type: File
value: select a .csv or .xlsx file

The file header must be:

serijskiBroj,ukupanIznos,opisSadrzaja,napomenaIzmene,ime,jmbg,adresa

Example CSV row:

POS-001,1200.50,Laptop and charger,First shipment,Petar Petrovic,1234567890123,Bulevar 1

For XLSX import, the Excel format is supported.
