# SpringBootExample Project
Simple Micro-Services project based on SpringBoot and Docker

### Docker Configuration in pom.xml

Docker SpringBoot plugin documentation is located at the following URL:
https://spring.io/guides/gs/spring-boot-docker

- Provide the docker hub repository or other repository using the docker image prefix parameter

```
<docker.image.prefix>mycompany/myapp</docker.image.prefix>
```

- Make sure the tag contains the application's version number

```
<tag>${project.version}</tag>
```


### Build Docker Image
- First build the jar of spring boot application

```
./mvnw package
```
- Use maven to build docker image using the Dockerfile located at the project root

```
./mvnw dockerfile:build
```

### Run Docker Image

```
docker run -p 9090:9090 -t mycompany/myapp:version
```

### Push Docker Image

```
docker push mycompany/myapp:version
```

### Externalize SpringBoot Configuration using Env Variables

As recommended by SpringBoot documentation, application configuration can be externalized using SPRING_CONFIGURATION_JSON environment variable:

For example:

```
SPRING_APPLICATION_JSON={"user" : {"users" : {"url" : "http://172.18.0.2:7070/users"}}, "car" : {"cars" : {"url" : "http://172.18.0.3:9090/cars"}}}
```

This environment variable can be passed to docker container using the following command:

```
docker run --network=springbootexample_network -e 'SPRING_APPLICATION_JSON={"user" : {"users" : {"url" : "http://172.18.0.2:7070/users"}}, "car" : {"cars" : {"url" : "http://172.18.0.3:9090/cars"}}}'  -p 8080:8080 boussettahichem/myrepo:insurance0.1
```

### Communication between Docker Containers

To perform inter-docker-container communication, it is recommended to create a user-defined bridge network and to run docker containers within this network.
The following link gives more details on docker networking capabilities:
https://docs.docker.com/network/network-tutorial-standalone/

- Create a new custom network, use the following command:

```
docker network create --driver bridge springbootexample_network
```

- Inspect the newly created netwrok,

```
docker network inspect springbootexample_network
```

- List available docker networks

```
docker network ls
```

- Get IP address of a docker container running on the user-defined network

```
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' CONTAINER_ID
```


