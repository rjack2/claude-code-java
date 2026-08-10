# Running Netflux

Follow these steps to build and run the Netflux application.

## 1. Navigate to the Infrastructure Directory

Open a terminal and navigate to the `infrastructure` directory.

## 2. Build the Microservices

Run the following commands to build the JAR files for all three microservices.

```bash
mvn -f ../movie-service/pom.xml clean package
mvn -f ../user-service/pom.xml clean package
mvn -f ../recommendation-service/pom.xml clean package
```

## 3. Build and Start the Containers

Build the Docker images and start all services.

```bash
docker compose build
docker compose up
```

## 4. Access Netflux

Once all containers have started successfully, open your browser and navigate to

```text
http://localhost
```
