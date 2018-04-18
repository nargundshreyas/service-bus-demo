# Developer Guide

## Pre-requisites
1. AZ CLI (2.0.22)
2. Kubectl (v1.8.5)
3. helm client (v2.6.2)
4. Docker

## How to install helm charts?
1. Login with az cli and place ssh keys used for creating ACS cluster at <user-home>/.shh/
2. Configure Kubectl using az cli
    ```
    az acs kubernetes get-credentials -g <resource-group> -n <acs-cluster-name>
    ```
3. Create name space, if required.
    ```
    kubectl create namespace <name-space-name>
    ```
4. Create a docker-registry secret, if required.
    ```
    kubectl create secret docker-registry <secret-name> --docker-server=<docker-registry-url> --docker-username=<user-name> --docker-password=<password> --docker-email=<email-id> -n <name-space>
    ```
5. Edit **sample-service\\values.yaml**
6. Go to **sample-service** folder and install heml chart.
    ```
    helm install .
    ```

### How to create docker image and push it?
```
docker build -t <image-repo-name> .

docker run -p 8080:8080 <image-repo-name>

docker tag <image-repo-name> <image-registry-uri>/<image-repo-name>:<tag>

docker login <image-registry-uri> -u <user-name> -p <password>

docker push <image-registry-uri>/<image-repo-name>:<tag>
```

### Other helm commands
1. List Installed Helm Charts
    ```
    helm list
    ```
2. Delete helm chart
    ```
    helm delete <release-name>
    ```
3. Upgrade Helm Chart
    ```
    helm upgrade <release-name> <path>
    ```
