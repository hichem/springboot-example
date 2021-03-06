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

## Operate Kubernetes

This section covers the following:
- Install Kubernetes cluster locally (minikube)
- Create / Delete Pod
- Create / Delete Service
- Create deployment & Service manually
- Create deployment using YAML file
- Install an Azure Kubernetes Service

### Install Minikube

- Download minikube binary from the following location (Mac version, install other versions depending on your environment):
https://storage.googleapis.com/minikube/releases/latest/minikube-darwin-amd64

- Grant execution permission on minikube executable

```
chmod a+x minikube-darwin-amd64
```

- Create a symlink to minikube binary so it is added to system path

```
ln -s /usr/local/bin/minikube minikube-darwin-amd64
```

- Start minikube

```
minikube start
```

- Check minikube status

```
minikube status
```

returns

```
host: Running
kubelet: Running
apiserver: Running
kubectl: Correctly Configured: pointing to minikube-vm at 192.168.99.101
```

- Start minikube dashboard

```
minikube dashboard
```

- Stop minikube using the following command 

```
minikube stop
```

- To delete all resources of minikube cluster (purge the cluster), run the following command

```
minikube delete
```

### Install Kubectl

Download & Install kubectl following this link
https://kubernetes.io/docs/tasks/tools/install-kubectl/
 

### Create / Delete Pods
- Create the Pod YAML descriptor

```yaml
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

### Create / Delete Service

- A service can be created for a labeled pod using the a YAML manifest (here service-user.yaml). We named the service app-user-lb because it is of type load balancer.

```yaml
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

```yaml
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

```yaml
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

### Create Ingress
* An ingress controller is mandatory to create ingress routes to services hosted inside kubernetes cluster
* Many ingress controllers providers are supported by kubernetes. Here is an exhaustive list:
https://kubernetes.io/docs/concepts/services-networking/ingress-controllers/
* We will be using next nginx ingress controller

#### Install Nginx Ingress Controller
Install Nginx controller using its official helm chart.
Doc: https://kubernetes.github.io/ingress-nginx/deploy/#azure

* Install Nginx Controller helm chart

```
helm install stable/nginx-ingress --name my-nginx
```

* (Optional) If the kubernetes cluster has RBAC enabled, then run:

```
helm install stable/nginx-ingress --name my-nginx --set rbac.create=true
```

#### Create Ingress Configuration

Google Doc:
https://cloud.google.com/kubernetes-engine/docs/how-to/ingress-multi-ssl

* Ingress Fanout Configuration (URL-based routing) Example

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: my-ingress-fanout
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /$1
spec:
  rules:
  - host: example.com
    http:
      paths:
      - path: /car/(.*)
        backend:
          serviceName: car
          servicePort: 80
      - path: /user/(.*)
        backend:
          serviceName: user
          servicePort: 80
      - path: /insurance/(.*)
        backend:
          serviceName: insurance
          servicePort: 80

```

* Ingress Name-based Virtual Hosting Example

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: my-ingress-virtual-hosts
spec:
  rules:
  - host: car.example.com
    http:
      paths:
      - backend:
          serviceName: car
          servicePort: 80
  - host: user.example.com
    http:
      paths:
      - backend:
          serviceName: user
          servicePort: 80
  - host: insurance.example.com
    http:
      paths:
      - backend:
          serviceName: insurance
          servicePort: 80


```

* Ingress TLS Configuration
This ingress requires creating:
** Ingress with TLS definition
** Secret with TLS certificate

Generate the key and certificate using openssl

```
openssl req -x509 -newkey rsa:4096 -keyout car-key.pem -out car-cert.pem -days 365 -nodes -subj '/CN=car.example.com'
openssl req -x509 -newkey rsa:4096 -keyout user-key.pem -out user-cert.pem -days 365 -nodes -subj '/CN=user.example.com'
openssl req -x509 -newkey rsa:4096 -keyout insurance-key.pem -out insurance-cert.pem -days 365 -nodes -subj '/CN=insurance.example.com'
```

Create secret files with tls key and cert (key and certificate must be BASE64 encoded). Example:

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: car-domain-tls
  namespace: default
data:
  tls.crt: LS0tLS1CRUdJTiBDR....
  tls.key: LS0tLS1CRUdJ...
type: kubernetes.io/tls
```

Update ingress definition with tls configuration (host to tls certificates mapping)

```yaml
apiVersion: extensions/v1beta1
kind: Ingress
metadata:
  name: my-ingress-virtual-hosts
spec:
  tls:
  - hosts:
    - example.com
    secretName: main-domain-tls
  - hosts:
    - car.example.com
    secretName: car-domain-tls
  - hosts:
    - user.example.com
    secretName: user-domain-tls
  - hosts:  
    - insurance.example.com
    secretName: insurance-domain-tls
  rules:
  - host: car.example.com
    http:
      paths:
      - backend:
          serviceName: car
          servicePort: 80
  - host: user.example.com
    http:
      paths:
      - backend:
          serviceName: user
          servicePort: 80
  - host: insurance.example.com
    http:
      paths:
      - backend:
          serviceName: insurance
          servicePort: 80
```

Create the secret and the ingress, then  check the ingress properties using **kubectl describe** command

```
kubectl describe ingress my-ingress-fanout

Name:             my-ingress-fanout
Namespace:        default
Address:          
Default backend:  default-http-backend:80 (<none>)
TLS:
  main-domain-tls terminates example.com
Rules:
  Host         Path  Backends
  ----         ----  --------
  example.com  
               /car/(.*)         car:80 (<none>)
               /user/(.*)        user:80 (<none>)
               /insurance/(.*)   insurance:80 (<none>)
```

```
Name:             my-ingress-virtual-hosts
Namespace:        default
Address:          
Default backend:  default-http-backend:80 (<none>)
TLS:
  main-domain-tls terminates example.com
  car-domain-tls terminates car.example.com
  user-domain-tls terminates user.example.com
  insurance-domain-tls terminates insurance.example.com
Rules:
  Host                   Path  Backends
  ----                   ----  --------
  car.example.com        
                            car:80 (<none>)
  user.example.com       
                            user:80 (<none>)
  insurance.example.com  
                            insurance:80 (<none>)
```

#### Test Ingress

* Test Fanout Routing

```
curl -H 'Host: example.com' http://40.89.129.131/car/cars
curl -H 'Host: example.com' http://40.89.129.131/user/users
curl -H 'Host: example.com' http://40.89.129.131/inurance/contracts
```

* Test Name-based Virtual Hosting

```
curl -H 'Host: car.example.com' http://40.89.129.131/cars
curl -H 'Host: user.example.com' http://40.89.129.131/users
curl -H 'Host: insurance.example.com' http://40.89.129.131/contracts
```

* Test TLS Configuration

Create /etc/hosts dns entry if no domain name is available for testing.
Use the browser or any other tools to check https call is working

## Manage Kubernetes Namespaces
Kubernetes namespace are useful to create separate virtual environments/clusters on kubernetes physical cluster.
A typical application would be to create development, staging, preproductiona and production environments.

### Create Namespace
- Create a YAML namespace-dev.yaml manifest for the namespace. We will call the namespace "development" and give it the same label.

```yaml
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

```yaml
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
az acr create --resource-group DemoKubernetes --name ACVContainerRegistry --sku Basic
```

returns the following:

```json
{
  "adminUserEnabled": false,
  "creationDate": "2019-05-13T14:05:40.005536+00:00",
  "id": "/subscriptions/79947dfa-0d6a-45c4-b6dc-9fe738f59300/resourceGroups/DemoKubernetes/providers/Microsoft.ContainerRegistry/registries/acvcontainerregistry",
  "location": "francecentral",
  "loginServer": "acvcontainerregistry.azurecr.io",
  "name": "ACVContainerRegistry",
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

The full name of the created registry is "acvcontainerregistry.azurecr.io"

- Connect to the container registry

```
az acr login --name ACVContainerRegistry
```

- In order to send our local images to Azure container registry, we must first tag them for azure registry hostname

```
docker tag boussettahichem/myrepo:car0.1 acvcontainerregistry.azurecr.io/car:0.1
docker tag boussettahichem/myrepo:user0.1 acvcontainerregistry.azurecr.io/user:0.1
docker tag boussettahichem/myrepo:insurance0.1 acvcontainerregistry.azurecr.io/insurance:0.1
```

- Push the images to azure registry container

```
docker push acvcontainerregistry.azurecr.io/car:0.1
docker push acvcontainerregistry.azurecr.io/user:0.1
docker push acvcontainerregistry.azurecr.io/insurance:0.1
```

- Display the images on azure registry container

```
az acr repository list --name ACVContainerRegistry --output table
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
az acr repository show-tags --name ACVContainerRegistry --repository car --output table
```

returns

```
Result
--------
v0.1
```

- Delete image from azure container registry

```
az acr repository delete --name ACVContainerRegistry --image acvcontainerregistry.azurecr.io/car:v0.1
```

### Authenticate with Azure Container Registry from AKS

Refer to the following link for more information:
https://docs.microsoft.com/bs-latn-ba/azure/container-registry/container-registry-auth-aks

2 options are available: AD or Kubernetes Secrets (Image Pull Secrets)

We will use option 2 that consists in using kubernetes secrets to store the container registry account credentials.

- Run **create-azure-service-principal.sh** to get principal ID and password

- Create the secret containing the container registry credentials

```
kubectl create secret docker-registry acr-auth --docker-server acvcontainerregistry.azurecr.io --docker-username cf6296f1-78c7-4995-a330-da845834cd61 --docker-password 1bf594f0-3999-4acb-9430-e830448055c0 --docker-email hichem.boussetta@alliance-rnm.com
```

- Pod deployment specification files must be updated with the image pull secrets

```yaml
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

### Get Azure Container Registry for use with Maven

Refer to links:
https://docs.microsoft.com/fr-fr/java/azure/spring-framework/deploy-spring-boot-java-app-using-fabric8-maven-plugin?view=azure-java-stable
https://github.com/Azure/azure-docs-sdk-java/blob/master/docs-ref-conceptual/spring-framework/deploy-spring-boot-java-app-from-container-registry-using-maven-plugin.md

- Enable admin mode on the repository

```
az acr update -n ACVContainerRegistry --admin-enabled true
```

- Get the password for accessing the registry

```
az acr credential show --name ACVContainerRegistry --query passwords[0]
```

returns 

```json
{
  "name": "password",
  "value": "PASSWORDQJGDQSDVB?QSBDJGYJZE"
}
```

- Update maven **settings.xml*** file in ~/.m2/settings.xml with the following

```xml
<server>
      <id>acvcontainerregistry.azurecr.io</id>
      <username>ACVContainerRegistry</username>
      <password>PASSWORDQJGDQSDVB</password>
</server>
```

- In project's pom.xml, if using spotify dockerfile maven plugin, add the following settings

```xml
<serverId>${mycontainerregistry}</serverId>
<registryUrl>https://${mycontainerregistry}</registryUrl>
<useMavenSettingsForAuth>true</useMavenSettingsForAuth>
```

### Use Azure Container Registry with Minikube
- Retrieve the credentials of the registry
- Create a credentials secret in the minikube cluster

```
kubectl create secret docker-registry secret-acr --docker-server=acvcontainerregistry.azurecr.io --docker-username=ACVContainerRegistry --docker-password=**** --docker-email=hichem.boussetta@alliance-rnm.com
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

## Vault Integration (Storing Application Secrets)

- Install Vault docker image

```
docker pull vault
```

- Run vault docker image

```
docker run --cap-add=IPC_LOCK -d --name=dev-vault vault
```

- The following link gives instructions about configuring Kubernetes with Vault
https://github.com/hashicorp/vault-guides/tree/master/identity/vault-agent-k8s-demo

- Create Kubernetes service account

```
# Create a service account, 'vault-auth'
kubectl create serviceaccount vault-auth

# Update the 'vault-auth' service account
kubectl apply -f vault-auth-service-account.yaml
```

- Run the setup-k8s-auth.sh script to set up the kubernetes auth method on your Vault server.

```
./setup-k8s-auth.sh
```

## Helm Installation

Helm is the configuration management tool for Kubernetes applications.

- Download helm from official website
- Run the command below to initialize helm CLI and install Tiller into the cluster (can be minikube or other)

```
helm init --service-account tiller  --history-max 200
```

- Update helm repo

```
helm repo update
```

- Install a stable chart

```
helm install stable/mysql
```

```
NAME:   wintering-rodent
LAST DEPLOYED: Thu Oct 18 14:21:18 2018
NAMESPACE: default
STATUS: DEPLOYED

RESOURCES:
==> v1/Secret
NAME                    AGE
wintering-rodent-mysql  0s

==> v1/ConfigMap
wintering-rodent-mysql-test  0s

==> v1/PersistentVolumeClaim
wintering-rodent-mysql  0s

==> v1/Service
wintering-rodent-mysql  0s

==> v1beta1/Deployment
wintering-rodent-mysql  0s

==> v1/Pod(related)

NAME                                    READY  STATUS   RESTARTS  AGE
wintering-rodent-mysql-6986fd6fb-988x7  0/1    Pending  0         0s
```

- List installed charts

```
helm ls
```

- Uninstall a release by its name (name of mysql chart is wintering-rodent)

```
helm delete wintering-rodent
```

- We can still request information about the deleted app

```
helm status wintering-rodent
```

```
LAST DEPLOYED: Thu Oct 18 14:21:18 2018
NAMESPACE: default
STATUS: DELETED
```

- Undelete a release

```
helm rollback
```

### Create New Chart

- Create an app-user chart

```
helm create app-user
```

- In **values.yaml**, provide general parameters for pod and service like repository, image version, port, load balancer...

- Verify chart (lint / static analysis) to make sure there are no errors

```
helm lint app-user
```

- Install the chart using the following command so it can be deployed

```
helm install --name user app-user/
```

- To update an existing deployment

```
helm upgrade user app-user/
```

- To fully delete an installed chart

```
helm delete --purge app-user
```

- Package app-user chart (helm chart packaged in .tgz format)

```
helm package app-user
```

### Helm Repo

A helm repo is just a server folder containing an **index.yaml** file having the following structure:

```yaml
apiVersion: v1
entries:
  app-user:
  - apiVersion: v1
    appVersion: "0.1"
    created: "2019-05-20T17:09:23.250775+02:00"
    description: Helm char for User Microservice
    digest: bb113dd12f7190ff45d9b1b750f3ca0a763a6f3580ab6a74e0a217bea6d57c23
    name: app-user
    urls:
    - https://hichem.github.io/springboot-example/app-user-0.3.0.tgz
    version: 0.3.0
generated: "2019-05-20T17:09:23.250161+02:00"
```

- Update the index when you generate a new helm chart package using the following command (the repository is hosted here on github pages. tgz packages are therefore placed in the *docs* folder)

```
helm repo index docs --url https://hichem.github.io/springboot-example/
```

- Commit and push the files to github

- Add the repo to helm (name it my-helm-repo)

```
helm repo add my-helm-repo https://hichem.github.io/springboot-example/
```

- Install the chart now from remote (github) helm repository

```
helm install my-helm-repo/app-user --name user
```

### Using Azure Helm Repo from Azure CLI

The documentation on how to use azure container repository as a Helm repo is available at this link:
https://docs.microsoft.com/fr-fr/azure/container-registry/container-registry-helm-repos

- Connect to azure console

```
az login
```

- Configure the azure CLI with the container registry

```
az configure --defaults acr=ACVContainerRegistry
```

- Add Azure container registry helm repo to the local helm client

```
az acr helm repo add
```

- Push chart to Azure container registry

```
az acr helm push app-user-1.7.tgz
```

- List the charts in the repository

```
az acr helm list
```
```json
{
"user-api": [
{
"acrMetadata": {
"manifestDigest": "sha256:0c379180ba6a80987309854f8ceedb4292b71d108bf574de6cc76754753c5511"
},
"apiVersion": "v1",
"appVersion": "1.7",
"created": "2019-05-21T10:50:18.3175964Z",
"description": "Helm char for User Microservice",
"digest": "5fe930176ee618be5100f382174a5373b28edf41718faf7376acbae52bca0dc3",
"name": "user-api",
"urls": [
"_blobs/user-api-1.7.tgz"
],
"version": "1.7"
}
]
}
```

- Install helm chart from repository

```
az acr helm install acvcontainerregistry.azurecr.io/àpp-user:1.7
```

- Install a helm chart to a namespace

```
helm install --name car target/app-user-1.7.0.tgz --namespace development
```

### Use Helm/Tiller on AKS to Deploy Applications

Doc: https://docs.microsoft.com/fr-fr/azure/aks/kubernetes-helm

* Create a service account to grant Tiller admin role on the cluster
* On the deployment pipeline (release pipeline), initialize Helm / Tiller. Add the following tasks:
    * Helm Install (install Helm and Tiller)
    * Helm Init (initialize helm/tiller on the kubernetes cluster)
        * Create and use Azure Resource Manager connection as described in the following link:
        https://docs.microsoft.com/en-us/azure/devops/pipelines/library/connect-to-azure?view=azure-devops
        * Initialize Tiller with argument *--service-account tiller* (additional tls security can be applied at this stage)
        * Set Tiller namespace to *kube-system* in advanced settings
    * Helm Upgrade (deploy the application based on the last helm chart)
        * Provide the chart path or name to be used (could be the char file .tgz built and copied to artifact staging drop directory)

### Create Release Pipeline in Azure DevOps

This topic is covered in this page: https://docs.microsoft.com/en-us/azure/devops/pipelines/apps/cd/deploy-aks?view=azure-devops

### Scaling AKS Cluster

Doc: https://docs.microsoft.com/fr-fr/azure/aks/scale-cluster

* Scale the node pool

```
az aks scale --resource-group DemoKubernetes --name kamereon-k8s --node-count 2
```

* Get the name of the attributes of the node pool

```
az aks show --resource-group DemoKubernetes --name kamereon-k8s --query agentPoolProfiles
```
```json
[
{
"count": 2,
"maxPods": 110,
"name": "nodepool1",
"osDiskSizeGb": 100,
"osType": "Linux",
"storageProfile": "ManagedDisks",
"vmSize": "Standard_DS2_v2"
}
]
```

### Scaling Deployment - Replica
* Use the following command
```
kubectl scale -n development deployment car-deployment-dev --replicas=2
```
* To force restart a pod, we can for example decrease the replica count to 0 then to 2



## Azure Service Broker API

Doc:
https://github.com/Azure/open-service-broker-azure/blob/master/docs/quickstart-aks.md

### Installation
- Create Service Principal for Open Service Broker for Azure (OSBA)

```
az ad sp create-for-rbac --name osba-kamereon -o table

AppId                                 DisplayName    Name                  Password                              Tenant
------------------------------------  -------------  --------------------  ------------------------------------  ------------------------------------
d23809fb-85ea-4601-a646-c3713177c63a  osba-kamereon  http://osba-kamereon  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx  7bfa8236-cdfb-45d1-98fe-ee4ce511f142
```

- Export the service principal variables to be used later

```
export AZURE_TENANT_ID=<Tenant>
export AZURE_CLIENT_ID=<AppId>
export AZURE_CLIENT_SECRET=<Password>
```

- Get Azure subscription ID

```
az account list -o table

Name                           CloudName    SubscriptionId                        State    IsDefault
-----------------------------  -----------  ------------------------------------  -------  -----------
ACV_Architecture_Subscription  AzureCloud   79947dfa-0d6a-45c4-b6dc-9fe738f59300  Enabled  True
```

- Export subscription ID as env variable

```
export AZURE_SUBSCRIPTION_ID=79947dfa-0d6a-45c4-b6dc-9fe738f59300
```

- (Optional) Helm installation if not already done

```
kubectl create -f https://raw.githubusercontent.com/Azure/helm-charts/master/docs/prerequisities/helm-rbac-config.yaml
helm init --service-account tiller
```

- Deploy Service Catalog on the Cluster

```
helm repo add svc-cat https://svc-catalog-charts.storage.googleapis.com
helm install svc-cat/catalog --name catalog --namespace catalog \
--set apiserver.storage.etcd.persistence.enabled=true \
--set apiserver.healthcheck.enabled=false \
--set controllerManager.healthcheck.enabled=false \
--set apiserver.verbosity=2 \
--set controllerManager.verbosity=2
```

- Wait for the service catalog to get ready

```
kubectl get pods --namespace=catalog --watch
```

- Deploy the Open Service Broker on the cluster

```
helm repo add azure https://kubernetescharts.blob.core.windows.net/azure
helm install azure/open-service-broker-azure --name osba --namespace osba \
--set azure.subscriptionId=$AZURE_SUBSCRIPTION_ID \
--set azure.tenantId=$AZURE_TENANT_ID \
--set azure.clientId=$AZURE_CLIENT_ID \
--set azure.clientSecret=$AZURE_CLIENT_SECRET
```

### Create Azure Postgres DB
Doc:
https://docs.microsoft.com/fr-fr/azure/postgresql/quickstart-create-server-up-azure-cli

- Install db-up extension in azure CLI

```
az extension add --name db-up
```

- Create the postgres database (we create it in the same resource group as aks for test purpose)

```
az postgres up -g DemoKubernetes --database-name kamereon --admin-user kamereon --admin-password kamere0n*

{
"connectionStrings": {
"ado.net": "Server=server904188840.postgres.database.azure.com;Database=kamereon;Port=5432;User Id=kamereon@server904188840;Password=kamere0n*;",
"jdbc": "jdbc:postgresql://server904188840.postgres.database.azure.com:5432/kamereon?user=kamereon@server904188840&password=kamere0n*",
"jdbc Spring": "spring.datasource.url=jdbc:postgresql://server904188840.postgres.database.azure.com:5432/kamereon  spring.datasource.username=kamereon@server904188840  spring.datasource.password=kamere0n*",
"node.js": "var client = new pg.Client('postgres://kamereon@server904188840:kamere0n*@server904188840.postgres.database.azure.com:5432/kamereon');",
"php": "host=server904188840.postgres.database.azure.com port=5432 dbname=kamereon user=kamereon@server904188840 password=kamere0n*",
"psql_cmd": "psql --host=server904188840.postgres.database.azure.com --port=5432 --username=kamereon@server904188840 --dbname=kamereon",
"python": "cnx = psycopg2.connect(database='kamereon', user='kamereon@server904188840', host='server904188840.postgres.database.azure.com', password='kamere0n*', port='5432')",
"ruby": "cnx = PG::Connection.new(:host => 'server904188840.postgres.database.azure.com', :user => 'kamereon@server904188840', :dbname => 'kamereon', :port => '5432', :password => 'kamere0n*')",
"webapp": "Database=kamereon; Data Source=server904188840.postgres.database.azure.com; User Id=kamereon@server904188840; Password=kamere0n*"
},
"host": "server904188840.postgres.database.azure.com",
"password": "kamere0n*",
"username": "kamereon@server904188840"
}
```
