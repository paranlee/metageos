# Spring Cloud Gateway with WMS BBOX Dynamic Routing

Request parameter based Dynamic roting with BBOX Coordinate on WMS Protocol.

## Example Description

When `xmin: 196605.0` then, Route to `http://localhost:8800`.

```bash
 $ curl "http://localhost:9010/geoserver/pg-postgis/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&STYLES&LAYERS=pg-postgis%3ABuildingMutiPolygonPG&exceptions=application%2Fvnd.ogc.se_inimage&SRS=EPSG%3A5174&WIDTH=769&HEIGHT=727&BBOX=196605.0118673314%2C444064.86867325875%2C203934.75211558922%2C451003.30768430483"
```

We can find that routed request go to **localhost:8800** server.

```bash
 $ python3 -m http.server
Serving HTTP on :: port 8800 (http://[::]:8800/) ...
::ffff:127.0.0.1 - - [16/Sep/2021 10:08:22] "GET /geoserver/wms?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%252Fpng&TRANSPARENT=true&LAYERS=myLayer%253AMyGrid&TILED=true&viewparams=msrDate%253A20210401%253Bmsr%253AA%253Bmnc%253A1%253BkpiValue%253Apoor_sinr%253A%253Anumeric%253Bterm%253Amm%253BuseMsrDate%253A1%253BuseMsr%253A1%253BuseMnc%253A1%253BuseKpiValue%253A1%253BuseTerm%253A1%253BgpotSize%253A150%253BpartxMin%253A1414%253BpartxMax%253A1414%253Bminx%253A14146114.833080746%253Bminy%253A4489366.42764472%253Bmaxx%253A14147815.556960093%253Bmaxy%253A4491067.151524066%253B&env=c1%253A%2523ff0000%253Bv1%253A1%253B&WIDTH=256&HEIGHT=256&CRS=EPSG%253A3857&STYLES=&BBOX=20000000.833080746%252C4489366.42764472%252C14147815.556960093%252C4491067.151524066 HTTP/1.1"
```

When `xmin: 202655.0` then, Route to `http://localhost:8801`.

```bash
 $ curl "http://localhost:9010/geoserver/pg-postgis/wms?SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&FORMAT=image%2Fpng&TRANSPARENT=true&STYLES&LAYERS=pg-postgis%3ABuildingMutiPolygonPG&exceptions=application%2Fvnd.ogc.se_inimage&SRS=EPSG%3A5174&WIDTH=769&HEIGHT=727&BBOX=202655.86514519007%2C444522.9774387749%2C209985.60539344788%2C451461.416449821"
```

We can find that routed request go to **localhost:8801** server.

```bash
 $ python3 -m http.server 8801
Serving HTTP on :: port 8801 (http://[::]:8801/) ...
::ffff:127.0.0.1 - - [16/Sep/2021 10:08:50] "GET /geoserver/wms?SERVICE=WMS&VERSION=1.3.0&REQUEST=GetMap&FORMAT=image%252Fpng&TRANSPARENT=true&LAYERS=myLayer%253AMyGrid&TILED=true&viewparams=msrDate%253A20210401%253Bmsr%253AA%253Bmnc%253A1%253BkpiValue%253Apoor_sinr%253A%253Anumeric%253Bterm%253Amm%253BuseMsrDate%253A1%253BuseMsr%253A1%253BuseMnc%253A1%253BuseKpiValue%253A1%253BuseTerm%253A1%253BgpotSize%253A150%253BpartxMin%253A1414%253BpartxMax%253A1414%253Bminx%253A14146114.833080746%253Bminy%253A4489366.42764472%253Bmaxx%253A14147815.556960093%253Bmaxy%253A4491067.151524066%253B&env=c1%253A%2523ff0000%253Bv1%253A1%253B&WIDTH=256&HEIGHT=256&CRS=EPSG%253A3857&STYLES=&BBOX=10000000.833080746%252C4489366.42764472%252C14147815.556960093%252C4491067.151524066 HTTP/1.1"
```

At Window OS. then we can use `Invoke-WebRequest` on powershell.

So, we can running on adhoc geoserver cluster on `docker-compose.yml` almost same pattern.

## Reference

[A Web Map Service (WMS) is a standard protocol developed by the Open Geospatial Consortium](https://en.wikipedia.org/wiki/Web_Map_Service)
