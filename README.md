# SpringBootExample Project
Simple Micro-Services project based on SpringBoot and Docker

## SpringBoot Apps REST API
### Car App
Endpoint: http://$HOSTNAME/cars

- GET Cars

```
curl http://localhost:9090/cars
```

- GET Car by ID

```
curl http://localhost:9090/cars/1
```

- ADD Car

```
curl -X POST -H "Content-Type: application/json" -d '{"id":4,"model":"model4","serialNumber":"SN_4"}' http://localhost:9090/cars
```

- DEL Car by ID

```
curl -X DELETE http://localhost:9090/cars/4
```

### User App
Endpoint: http://$HOSTNAME/users

- GET Users

```
curl http://localhost:7070/users
```

- GET User by ID

```
curl http://localhost:7070/users/1
```

- ADD User

```
curl -X POST -H "Content-Type: application/json" -d '{"id":4,"firstname":"firstname4","lastname":"lastname4"}' http://localhost:7070/users
```

- DEL User by ID

```
curl -X DELETE http://localhost:7070/users/4
```

### Insurance App
Endpoint: http://$HOSTNAME/contracts

- GET Contracts

```
curl http://localhost:8080/contracts
```

- GET Contract by ID

```
curl http://localhost:8080/contracts/1
```

- ADD Contract

```
curl -X POST -H "Content-Type: application/json" -d '{"id":4,"userId":4,"carId":4}' http://localhost:8080/contracts
```

- DEL Contract by ID

```
curl -X DELETE http://localhost:8080/contracts/4
```

- GET Report for contract by ID (this api requires the insurance app to collect data from car and user apps)

```
curl http://localhost:8080/reports/1
```

## Docker
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

## Kubernetes
This section covers the following:
- Create / Delete Pod
- Create / Delete Service
- Create deployment & Service manually
- Create deployment using YAML file

### Create Pods
- Create the Pod YAML descriptor

```
apiVersion: v1
kind: Pod
metadata:
  name: app-user
  labels:
    app: app-user
spec:
  containers:
    - image: boussettahichem/myrepo:user0.1
      name: app-user
      ports:
        - containerPort: 7070
```

- Create the pod

```
kubectl create -f pod-user.yaml
```

- Check the pod is running

```
kubectl get pods
```

this will return

```
NAME       READY   STATUS    RESTARTS   AGE
app-user   1/1     Running   0          11s
```

- Expose the port used by the application of the pod so we can interact with it from the host

```
kubectl port-forward app-user 7070:7070
```

- Call rest api of the pod

```
curl http://localhost:7070/cars
```

- Get the logs of the pod

```
kubectl log app-user
```

- Delete Pod

Display all kubernetes running resources

```
kubectl get all
```

this will return

```
NAME           READY   STATUS    RESTARTS   AGE
pod/app-user   1/1     Running   0          66m

NAME                 TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   5d21h
```

Delete the pod pod/app-user

```
kubectl delete pod/app-user
```

### Create Service

- A service can be created for a labeled pod using the a YAML manifest (here service-user.yaml). We named the service app-user-lb because it is of type load balancer.

```
apiVersion: v1
kind: Service
metadata:
  name: app-user-lb
spec:
  type: LoadBalancer
  ports:
  - port: 7070
    protocol: TCP
    targetPort: 7070
  selector:
    app: app-user

```

- Create the service

```
kubectl create -f service-user.yaml
```

- Run the sevice

```
minikube service app-user-lb
```

- Check the service status

```
kubectl get services
```

On minikube which is a local kubernetes installation, the service will hang on the pending state. If executed however on cloud platforms (Azure, GCP...), it will get a public IP address.

- Display the URL of the service

```
kubectl service list
```

This will return

```
|-------------|----------------------|-----------------------------|
|  NAMESPACE  |         NAME         |             URL             |
|-------------|----------------------|-----------------------------|
| default     | app-user-lb          | http://192.168.99.100:30618 |
| default     | kubernetes           | No node port                |
| kube-system | kube-dns             | No node port                |
| kube-system | kubernetes-dashboard | No node port                |
|-------------|----------------------|-----------------------------|
```

- Test the service using curl command on the url given by kubernetes

```
curl http://192.168.99.100:30618/users
```

- Delete the service

```
kubectl delete service/app-user-lb
```

### Create Deployment Manually

- Create a deployment using the following command

```
kubectl run app-user --image boussettahichem/myrepo:user0.1 --port 7070 --image-pull-policy Never
```

- Verify the deployment

```
kubectl get deployment
```

- Expose the deployment using a Service

```
kubectl expose deployment app-user --type=NodePort
```

The --type=NodePort option makes the Service available from outside of the cluster. It will be available at <NodeIP>:<NodePort>, i. e. the service maps any request incoming at <NodePort> to port 8080 of its assigned Pods.

We use the expose command, so NodePort will be set by the cluster automatically (this is a technical limitation), the default range is 30000-32767. To get a port of our choice, we can use a configuration file, as we’ll see in the next section.

- Display the service

```
kubectl get services
```

- Call the service using the command below. The browser will be launched on port 32460 to give access to the application.

```
minikube service app-user
```

- Delete Service and Deployment

Display all running resources

```
kubectl get all
```

this will return

```
NAME                            READY   STATUS    RESTARTS   AGE
pod/app-user-58c8675b99-xgxwq   1/1     Running   0          28m

NAME                 TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
service/app-user     NodePort    10.97.201.57   <none>        7070:32460/TCP   7m40s
service/kubernetes   ClusterIP   10.96.0.1      <none>        443/TCP          5d21h

NAME                       READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/app-user   1/1     1            1           28m

NAME                                  DESIRED   CURRENT   READY   AGE
replicaset.apps/app-user-58c8675b99   1         1         1       28m
```

Delete the service and deployment

```
kubectl delete service/app-user deployment.apps/app-user
```

### Create Deployment using YAML File

- Create a deployment YAML file for user app. the YAML will contain the following command (set the replicas and any other parameter according to your needs)

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-user
spec:
  selector:
    matchLabels:
      app: app-user
  replicas: 1
  minReadySeconds: 15
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  template:
    metadata:
      labels:
        app: app-user
    spec:
      containers:
        - image: boussettahichem/myrepo:user0.1
          imagePullPolicy: Never							# Set to Always to force pulling the image at each deployment
          name: app-user
          ports:
            - containerPort: 7070

```

- Create the deployment 

```
kubectl apply -f deployment-user.yaml
```

- Display the created resources

```
kubectl get all
```

This will display

```
NAME                            READY   STATUS    RESTARTS   AGE
pod/app-user-585596c48f-w845f   1/1     Running   0          34s

NAME                 TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)   AGE
service/kubernetes   ClusterIP   10.96.0.1    <none>        443/TCP   5d22h

NAME                       READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/app-user   1/1     1            1           34s

NAME                                  DESIRED   CURRENT   READY   AGE
replicaset.apps/app-user-585596c48f   1         1         1       34s
```

- Provide environment variables to container in deployment manifest. Example, we provide Spring Boot configuration with required URL to insurance app

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-insurance
spec:
  selector:
    matchLabels:
      app: app-insurance
  replicas: 1
  minReadySeconds: 15
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  template:
    metadata:
      labels:
        app: app-insurance
    spec:
      containers:
        - image: boussettahichem/myrepo:insurance0.1
          imagePullPolicy: Never							# Set to Always to force pulling the image at each deployment
          name: app-insurance
          env:
            - name: SPRING_APPLICATION_JSON
              value: '{"user" : {"users" : {"url" : "http://app-user-lb:7070/users"}}, "car" : {"cars" : {"url" : "http://app-car-lb:9090/cars"}}}'
          ports:
            - containerPort: 8080

```

- Each service with the cluster can be resolved by its name as kube-dns creates a dns entry for each created service. Containers can communicate with a service using its dns entry.