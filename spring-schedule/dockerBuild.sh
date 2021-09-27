#!/bin/bash

IMAGE_NAME="spring-schedule" 
NEXUS3_PATH=
DATE_VERSION=$(date +%Y.%m.%d.%H.%M)

# MAIN_TAG=$NEXUS3_PATH/$IMAGE_NAME
MAIN_TAG=$IMAGE_NAME
DATE_TAG=$MAIN_TAG:$DATE_VERSION
LOCAL_TAG=$IMAGE_NAME:latest
LATEST_TAG=$MAIN_TAG:latest

echo "BUILD ${DATE_TAG} IMAGE"

# build maven jar file
cd $1

bash ./mvnw clean package

# docker build
docker build --tag $LOCAL_TAG .
docker build --tag $DATE_TAG .
docker build --tag $LATEST_TAG .

# Tag된 image를 nexus저장소로 push 한다.
# docker push $DATE_TAG
# docker push $LATEST_TAG