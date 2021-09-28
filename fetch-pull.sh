#!/bin.bash

for v in cockroachdb-docker-strategies geoserver-cloud openstreetmap-tile-server ; do

    cd ${v}
    git fetch && git pull
    cd ..

done
