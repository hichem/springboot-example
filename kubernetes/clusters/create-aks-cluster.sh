#!/bin/sh

# Connect to Azure Console
az login

# Create Resource group DemoKubernetes for our AKS cluster
az group create --name DemoKubernetes --location francecentral

# Create AKS cluster within the Resource Group
az aks create \
--resource-group DemoKubernetes \
--name kamereon-k8s \
--node-count 1 \
--enable-addons monitoring \
--generate-ssh-keys

# Get credentials necessary for kubectl to authenticate to aks
az aks get-credentials --resource-group DemoKubernetes --name kamereon-k8s

# Enable aks dashboard insecure mode (for dev / test)
kubectl create clusterrolebinding kubernetes-dashboard --clusterrole=cluster-admin --serviceaccount=kube-system:kubernetes-dashboard

