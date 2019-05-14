# SpringBootExample Project
Simple Micro-Services project based on SpringBoot and Docker

## SpringBoot Apps REST API
### User App
Endpoint: http://$HOSTNAME/users

- GET Users

```
curl http://localhost:7000/users
```

- GET User by ID

```
curl http://localhost:7000/users/1
```

- ADD User

```
curl -X POST -H "Content-Type: application/json" -d '{"id":4,"firstname":"firstname4","lastname":"lastname4"}' http://localhost:7000/users
```

- DEL User by ID

```
curl -X DELETE http://localhost:7000/users/4
```
### Car App
Endpoint: http://$HOSTNAME/cars

- GET Cars

```
curl http://localhost:7001/cars
```

- GET Car by ID

```
curl http://localhost:7001/cars/1
```

- ADD Car

```
curl -X POST -H "Content-Type: application/json" -d '{"id":4,"model":"model4","serialNumber":"SN_4"}' http://localhost:7001/cars
```

- DEL Car by ID

```
curl -X DELETE http://localhost:7001/cars/4
```



### Insurance App
Endpoint: http://$HOSTNAME/contracts

- GET Contracts

```
curl http://localhost:7002/contracts
```

- GET Contract by ID

```
curl http://localhost:7002/contracts/1
```

- ADD Contract

```
curl -X POST -H "Content-Type: application/json" -d '{"id":4,"userId":4,"carId":4}' http://localhost:7002/contracts
```

- DEL Contract by ID

```
curl -X DELETE http://localhost:7002/contracts/4
```

- GET Report for contract by ID (this api requires the insurance app to collect data from car and user apps)

```
curl http://localhost:7002/reports/1
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
docker run -p 7001:7001 -t mycompany/myapp:version
```

### Push Docker Image

```
docker push mycompany/myapp:version
```

### Externalize SpringBoot Configuration using Env Variables

As recommended by SpringBoot documentation, application configuration can be externalized using SPRING_CONFIGURATION_JSON environment variable:

For example:

```
SPRING_APPLICATION_JSON={"user" : {"users" : {"url" : "http://172.18.0.2:7000/users"}}, "car" : {"cars" : {"url" : "http://172.18.0.3:7001/cars"}}}
```

This environment variable can be passed to docker container using the following command:

```
docker run --network=springbootexample_network -e 'SPRING_APPLICATION_JSON={"user" : {"users" : {"url" : "http://172.18.0.2:7000/users"}}, "car" : {"cars" : {"url" : "http://172.18.0.3:7001/cars"}}}'  -p 7002:7002 boussettahichem/myrepo:insurance0.1
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
        - containerPort: 7000
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
kubectl port-forward app-user 7000:7000
```

- Call rest api of the pod

```
curl http://localhost:7000/cars
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
  - port: 7000
    protocol: TCP
    targetPort: 7000
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

- Simulate Load Balancer in minikube. Run the below command in a separate terminal because it is blocking. Provide the root password because the tunnel functionality requires root privileges

```
minikube tunnel
```

- Display the URL of the service

```
minikube service list
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

- Services can be discovered based on their DNS entries or Environment variables created by Kubernetes when the service is started. Refer to the following link:

https://kubernetes.io/docs/concepts/services-networking/service/#discovering-services

### Create Deployment Manually

- Create a deployment using the following command

```
kubectl run app-user --image boussettahichem/myrepo:user0.1 --port 7000 --image-pull-policy Never
```

- Verify the deployment

```
kubectl get deployment
```

- Expose the deployment using a Service

```
kubectl expose deployment app-user --type=NodePort
```

The --type=NodePort option makes the Service available from outside of the cluster. It will be available at <NodeIP>:<NodePort>, i. e. the service maps any request incoming at <NodePort> to port 7002 of its assigned Pods.

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
service/app-user     NodePort    10.97.201.57   <none>        7000:32460/TCP   7m40s
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
            - containerPort: 7000

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
              value: '{"user" : {"users" : {"url" : "http://app-user-lb:7000/users"}}, "car" : {"cars" : {"url" : "http://app-car-lb:7001/cars"}}}'
          ports:
            - containerPort: 7002

```

- Each service with the cluster can be resolved by its name as kube-dns creates a dns entry for each created service. Containers can communicate with a service using its dns entry.

## Manage Kubernetes Namespaces
Kubernetes namespace are useful to create separate virtual environments/clusters on kubernetes physical cluster.
A typical application would be to create development, staging, preproductiona and production environments.

### Create Namespace
- Create a YAML namespace-dev.yaml manifest for the namespace. We will call the namespace "development" and give it the same label.

```
apiVersion: v1
kind: Namespace
metadata:
  name: development
  labels:
    name: development
```

- Create the development namespace using the following command

```
kubectl create -f namespace-dev.yaml
```

- Display namespaces and their labels

```
kubectl get namespaces --show-labels
```

### Create Kubernetes Contexts to manage Namespaces

- Display current context configuration

```
kubectl config view
```

will return the following since we are using minikube

```
apiVersion: v1
clusters:
- cluster:
    certificate-authority: /Users/ay51547-dev/.minikube/ca.crt
    server: https://192.168.99.100:8443
  name: minikube
contexts:
- context:
    cluster: minikube
    user: minikube
  name: minikube
current-context: minikube
kind: Config
preferences: {}
users:
- name: minikube
  user:
    client-certificate: /Users/ay51547-dev/.minikube/client.crt
    client-key: /Users/ay51547-dev/.minikube/client.key
```

- Get current context config

```
kubectl config current-context
```

will return

```
minikube
```

- Create dev and prod contexts

```
kubectl config set-context dev --namespace=development --cluster=minikube --user=minikube
kubectl config set-context prod --namespace=production --cluster=minikube --user=minikube
```

- Use dev context

```
kubectl config use-context dev
```

### Create Cluster in Namespace
- Create a cluster in development namespace using the manifest cluster.yaml. This will create 3 pods (app-user, app-car, app-insurance) and 3 services (app-user-lb, app-car-lb, app-insurance-lb)

```
kubectl --namespace=development create -f cluster.yaml
```

- Activate the 3 services

```
minikube -n development service app-user-lb
minikube -n development service app-car-lb
minikube -n development service app-insurance-lb
```

- Display the running services

```
minikube --namespace=development service list
```

will return

```
|-------------|------------------|-----------------------------|
|  NAMESPACE  |       NAME       |             URL             |
|-------------|------------------|-----------------------------|
| development | app-car-lb       | http://192.168.99.100:32096 |
| development | app-insurance-lb | http://192.168.99.100:32277 |
| development | app-user-lb      | http://192.168.99.100:30358 |
|-------------|------------------|-----------------------------|
```

- Delete cluster

```
kubectl --namespace=development delete pod/app-car pod/app-insurance pod/app-user service/app-car-lb service/app-insurance-lb service/app-user-lb
```

## Istio Activation
- Download Istio
- Install Istio Pods

```
for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl apply -f $i; done
```

- Install demo profile to enable permissive mutual TLS

```
kubectl apply -f install/kubernetes/istio-demo.yaml
```

- Verify the installation

```
kubectl get svc -n istio-system
```

must return

```
NAME                     TYPE           CLUSTER-IP       EXTERNAL-IP     PORT(S)                                                                                                                   AGE
grafana                  ClusterIP      172.21.211.123   <none>          3000/TCP                                                                                                                  2m
istio-citadel            ClusterIP      172.21.177.222   <none>          8060/TCP,9093/TCP                                                                                                         2m
istio-egressgateway      ClusterIP      172.21.113.24    <none>          80/TCP,443/TCP                                                                                                            2m
istio-galley             ClusterIP      172.21.132.247   <none>          443/TCP,9093/TCP                                                                                                          2m
istio-ingressgateway     LoadBalancer   172.21.144.254   52.116.22.242   80:31380/TCP,443:31390/TCP,31400:31400/TCP,15011:32081/TCP,8060:31695/TCP,853:31235/TCP,15030:32717/TCP,15031:32054/TCP   2m
istio-pilot              ClusterIP      172.21.105.205   <none>          15010/TCP,15011/TCP,8080/TCP,9093/TCP                                                                                     2m
istio-policy             ClusterIP      172.21.14.236    <none>          9091/TCP,15004/TCP,9093/TCP                                                                                               2m
istio-sidecar-injector   ClusterIP      172.21.155.47    <none>          443/TCP                                                                                                                   2m
istio-telemetry          ClusterIP      172.21.196.79    <none>          9091/TCP,15004/TCP,9093/TCP,42422/TCP                                                                                     2m
jaeger-agent             ClusterIP      None             <none>          5775/UDP,6831/UDP,6832/UDP                                                                                                2m
jaeger-collector         ClusterIP      172.21.135.51    <none>          14267/TCP,14268/TCP                                                                                                       2m
jaeger-query             ClusterIP      172.21.26.187    <none>          16686/TCP                                                                                                                 2m
kiali                    ClusterIP      172.21.155.201   <none>          20001/TCP                                                                                                                 2m
prometheus               ClusterIP      172.21.63.159    <none>          9090/TCP                                                                                                                  2m
tracing                  ClusterIP      172.21.2.245     <none>          80/TCP                                                                                                                    2m
zipkin                   ClusterIP      172.21.182.245   <none>          9411/TCP                                                                                                                  2m
```

- Ensure Istio pods are in running state (this may take some time to complete)

```
kubectl get pods -n istio-system
```

will return

```
NAME                                                           READY   STATUS      RESTARTS   AGE
grafana-f8467cc6-rbjlg                                         1/1     Running     0          1m
istio-citadel-78df5b548f-g5cpw                                 1/1     Running     0          1m
istio-cleanup-secrets-release-1.1-20190308-09-16-8s2mp         0/1     Completed   0          2m
istio-egressgateway-78569df5c4-zwtb5                           1/1     Running     0          1m
istio-galley-74d5f764fc-q7nrk                                  1/1     Running     0          1m
istio-grafana-post-install-release-1.1-20190308-09-16-2p7m5    0/1     Completed   0          2m
istio-ingressgateway-7ddcfd665c-dmtqz                          1/1     Running     0          1m
istio-pilot-f479bbf5c-qwr28                                    2/2     Running     0          1m
istio-policy-6fccc5c868-xhblv                                  2/2     Running     2          1m
istio-security-post-install-release-1.1-20190308-09-16-bmfs4   0/1     Completed   0          2m
istio-sidecar-injector-78499d85b8-x44m6                        1/1     Running     0          1m
istio-telemetry-78b96c6cb6-ldm9q                               2/2     Running     2          1m
istio-tracing-69b5f778b7-s2zvw                                 1/1     Running     0          1m
kiali-99f7467dc-6rvwp                                          1/1     Running     0          1m
prometheus-67cdb66cbb-9w2hm                                    1/1     Running     0          1m
```

- Inject Istio namespace in the applications' namespace

```
kubectl label namespace <namespace> istio-injection=enabled
```

- Create the application / cluster using their YAML specification file

```
kubectl create -n development -f cluster.yaml
```

- If the application / cluster is already deployed on namespaces without Istio injection label, Istio containers can be injected using the following commands

```
istioctl kube-inject -f <your-app-spec>.yaml | kubectl apply -f -
```

- Delete Istio using the following commands

```
kubectl delete -f install/kubernetes/istio-demo.yaml
for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl delete -f $i; done
```

## Create Kubernetes Cluster on Azure Cloud

- Login to azure CLI

```
az login
```

- Display available account locations

```
az account list-locations
```

- Create dedicated resource group in Central France region

```
az group create --name DemoKubernetes --location francecentral
```

- Create Azure Kubernetes Service (AKS) Cluster

```
az aks create \
    --resource-group DemoKubernetes \
    --name kamereon-k8s \
    --node-count 1 \
    --enable-addons monitoring \
    --generate-ssh-keys
```

- If kubectl is not installed locally, it can be installed with the following command (optional)

```
az aks install-cli
```

- To configure kubectl to connect to your Kubernetes cluster, use the az aks get-credentials command. This command downloads credentials and configures the Kubernetes CLI to use them.

```
az aks get-credentials --resource-group DemoKubernetes --name kamereon-k8s
```

To verify the connection to your cluster, use the kubectl get command to return a list of the cluster nodes.

```
kubectl get nodes
```

will return

```
NAME                       STATUS   ROLES   AGE     VERSION
aks-nodepool1-31718369-0   Ready    agent   6m44s   v1.9.11
```

- Display kubernetes dashboard

```
az aks browse --resource-group DemoKubernetes --name kamereon-k8s
```

- If access to kubernetes cluster is secured using role based authentication, the dashboard will display many error and access to the cluster information won't be possible. To authorize the access to the dashboard in  a non secure way, use the following command

```
kubectl create clusterrolebinding kubernetes-dashboard --clusterrole=cluster-admin --serviceaccount=kube-system:kubernetes-dashboard
```
## Create a Container Registry in Azure

A container registry holds the docker images.
Refer to: https://docs.microsoft.com/fr-fr/azure/container-registry/container-registry-get-started-azure-cli

- Create the container registry using this command (we used the same resource group as before)

```
az acr create --resource-group DemoKubernetes --name KamereonContainerRegistry --sku Basic
```

returns the following:

```
{
  "adminUserEnabled": false,
  "creationDate": "2019-05-13T14:05:40.005536+00:00",
  "id": "/subscriptions/79947dfa-0d6a-45c4-b6dc-9fe738f59300/resourceGroups/DemoKubernetes/providers/Microsoft.ContainerRegistry/registries/KamereonContainerRegistry",
  "location": "francecentral",
  "loginServer": "kamereoncontainerregistry.azurecr.io",
  "name": "KamereonContainerRegistry",
  "provisioningState": "Succeeded",
  "resourceGroup": "DemoKubernetes",
  "sku": {
    "name": "Basic",
    "tier": "Basic"
  },
  "status": null,
  "storageAccount": null,
  "tags": {},
  "type": "Microsoft.ContainerRegistry/registries"
}
```

The full name of the created registry is "kamereoncontainerregistry.azurecr.io"

- Connect to the container registry

```
az acr login --name KamereonContainerRegistry
```

- In order to send our local images to Azure container registry, we must first tag them for azure registry hostname

```
docker tag boussettahichem/myrepo:car0.1 kamereoncontainerregistry.azurecr.io/car:v0.1
docker tag boussettahichem/myrepo:user0.1 kamereoncontainerregistry.azurecr.io/user:v0.1
docker tag boussettahichem/myrepo:insurance0.1 kamereoncontainerregistry.azurecr.io/insurance:v0.1
```

- Push the images to azure registry container

```
docker push kamereoncontainerregistry.azurecr.io/car:v0.1
docker push kamereoncontainerregistry.azurecr.io/user:v0.1
docker push kamereoncontainerregistry.azurecr.io/insurance:v0.1
```

- Display the images on azure registry container

```
az acr repository list --name KamereonContainerRegistry --output table
```

returns

```
Result
---------
car
insurance
user
```

- Display the tag of a given image

```
az acr repository show-tags --name KamereonContainerRegistry --repository car --output table
```

returns

```
Result
--------
v0.1
```

- Delete image from azure container registry

```
az acr repository delete --name KamereonContainerRegistry --image kamereoncontainerregistry.azurecr.io/car:v0.1
```

- Authenticate with Azure Container Registry from AKS

Refer to the following link for more information:
https://docs.microsoft.com/bs-latn-ba/azure/container-registry/container-registry-auth-aks

2 options are available: AD or Kubernetes Secrets (Image Pull Secrets)

We will use option 2 that consists in using kubernetes secrets to store the container registry account credentials.

- Run **create-azure-service-principal.sh** to get principal ID and password

- Create the secret containing the container registry credentials

```
kubectl create secret docker-registry acr-auth --docker-server kamereoncontainerregistry.azurecr.io --docker-username cf6296f1-78c7-4995-a330-da845834cd61 --docker-password 1bf594f0-3999-4acb-9430-e830448055c0 --docker-email hichem.boussetta@alliance-rnm.com
```

- Pod deployment specification files must be updated with the image pull secrets

```
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: acr-auth-example
spec:
  template:
    metadata:
      labels:
        app: acr-auth-example
    spec:
      containers:
      - name: acr-auth-example
        image: myacrregistry.azurecr.io/acr-auth-example
      imagePullSecrets:
      - name: acr-auth
```


### Create the Pods

- Create the cluster pods using the following command

```
kubectl -n development apply -f cluster-azure.yaml
```

- Exposing the load-balanced services may take some time to complete (several minutes). You can monitor the progress use the following command

```
kubectl -n development get service app-car-lb --watch
```

returns

```
NAME         TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
app-car-lb   LoadBalancer   10.0.245.169   <pending>     7001:31343/TCP   2m10s
app-car-lb   LoadBalancer   10.0.245.169   40.89.161.63   7001:31343/TCP   3m9s
```

