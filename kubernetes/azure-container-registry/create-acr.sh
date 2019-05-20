#!/bin/sh

# Login to Azure Console
az login

# Create a dedicated resource group for the container registry in francecentral region
az group create --name DemoContainerRegistryResourceGroup --location francecentral

# Create the container registry within the created resource group
az acr create --resource-group DemoContainerRegistryResourceGroup --name ACVContainerRegistry --sku Basic

# Connect to the container registry
az acr login --name ACVContainerRegistry
