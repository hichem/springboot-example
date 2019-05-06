# SpringBoot Example
Simple microservices spring boot application.

### Docker Configuration in pom.xml
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