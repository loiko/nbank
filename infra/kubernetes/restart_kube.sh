#!/bin/bash

minikube start --driver=docker

kubectl delete configmap postgres-init-scripts --ignore-not-found=true
kubectl delete configmap selenoid-config --ignore-not-found=true

kubectl create configmap postgres-init-scripts --from-file=init.sql=./nbank-chart/files/01-init-db.sql

kubectl create configmap selenoid-config --from-file=browsers.json=./nbank-chart/files/browsers.json

helm install nbank ./nbank-chart

kubectl get svc

kubectl get pods

kubectl logs deployment/backend

kubectl port-forward svc/frontend 3000:80 > /dev/null 2>&1 &
kubectl port-forward svc/backend 4111:4111 > /dev/null 2>&1 &
kubectl port-forward svc/selenoid 4444:4444 > /dev/null 2>&1 &
kubectl port-forward svc/selenoid-ui 8080:8080 > /dev/null 2>&1 &
kubectl port-forward svc/postgres 5433:5432 > /dev/null 2>&1 &