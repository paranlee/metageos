#!/bin/sh

set -e

POSTGRES_USER=paranlee

# Perform all actions as $POSTGRES_USER
export PGUSER="$POSTGRES_USER"

# Create the 'template_postgis' template db
psql -U postgis <<- EOSQL
CREATE DATABASE parandb WITH owner='paranlee' ENCODING='utf8' LC_COLLATE='ko_KR.utf8' LC_CTYPE='ko_KR.utf8' template=template_postgis;
EOSQL

# Load EXTENSION to template_postgis, $POSTGRES_DB
for DB in template_postgis "$POSTGRES_DB"; do

psql --dbname="$DB" <<- EOSQL
    CREATE EXTENSION IF NOT EXISTS pg_stat_statements;
    CREATE EXTENSION IF NOT EXISTS pg_trgm;
    CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
    CREATE EXTENSION IF NOT EXISTS fuzzystrmatch;

    CREATE EXTENSION IF NOT EXISTS postgis;
    CREATE EXTENSION IF NOT EXISTS postgis_topology;
    CREATE EXTENSION IF NOT EXISTS postgis_tiger_geocoder;
EOSQL

done
