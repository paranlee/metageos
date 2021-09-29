# PG with PostGIS

Checkout adhoc pg cluster node.

## Using termial

We can do interactive shell.

```bash
 $ docker-compose exec pg-postgis-0 /bin/bash
 root@-:/var/lib/postgresql# psql -U paranlee -d postgis
```

show

```cmd
 postgis=# \l
                            데이터베이스 목록
   이름    |  소유주  | 인코딩 | Collate | Ctype |      액세스 권한
-----------+----------+--------+---------+-------+-----------------------
 postgis   | paranlee | EUC_KR | ko_Kr   | ko_Kr |
 postgres  | paranlee | EUC_KR | ko_Kr   | ko_Kr |
 template0 | paranlee | EUC_KR | ko_Kr   | ko_Kr | =c/paranlee          +
           |          |        |         |       | paranlee=CTc/paranlee
 template1 | paranlee | EUC_KR | ko_Kr   | ko_Kr | =c/paranlee          +
           |          |        |         |       | paranlee=CTc/paranlee
```