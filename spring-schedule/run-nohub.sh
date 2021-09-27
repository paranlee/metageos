#!/bin/bash

nohup $JAVA_HOME/bin/java -jar -Dspring.profiles.active=prod spring-schedule-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &
