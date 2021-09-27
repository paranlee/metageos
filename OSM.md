# OSM

South korea OSM pbf converted tile file.

You can use this osm service node as on-promise enviroment or self map hosting service.

[south-korea-latest.osm.pbf](https://download.geofabrik.de/asia/south-korea-latest.osm.pbf)

Create the named container volume.

```bash
$ podman volume create openstreetmap-tile-server
```

Initialize the osm container.

```bash
 $ podman run \
    -e DOWNLOAD_PBF=https://download.geofabrik.de/asia/south-korea-latest.osm.pbf \
    -e DOWNLOAD_POLY=https://download.geofabrik.de/asia/south-korea.poly \
    -v openstreetmap-tile-server:/var/lib/postgresql/12/main \
    overv/openstreetmap-tile-server \
    import
```

Run container service.

```bash
 $ podman run \
    -e ALLOW_CORS=1 \
    -p 28080:80 \
    -v openstreetmap-tile-server:/var/lib/postgresql/12/main \
    -d overv/openstreetmap-tile-server \
    run
```
