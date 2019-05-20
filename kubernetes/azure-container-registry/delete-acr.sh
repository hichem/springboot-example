#!/bin/sh

# Connect to azure cli
az login

# Delete resource group DemoContainerRegistryResourceGroup
az group delete --name DemoContainerRegistryResourceGroup --yes --no-wait
