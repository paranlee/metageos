FROM postgres:latest

RUN sed -i 's/archive.ubuntu.com/mirror.kakao.com/g' /etc/apt/sources.list \
    && apt update \
    && apt install -y \ 
    language-pack-ko \
    tzdata \ 
    locales \
    postgresql-13-postgis-3 \
    postgresql-13-postgis-3-scripts \
    vim \
    python3 \
    && rm -rf /var/lib/apt/lists/*

RUN localedef -i ko_KR -f UTF-8 ko_KR.UTF-8

ENV TZ='Asia/Seoul' \
    LANGUAGE=ko_KR.UTF-8 \
    LANG=ko_KR.UTF-8 \
    LC_ALL=ko_KR.UTF-8 \
    LC_CTYPE=ko_KR.UTF-8 \
    LC_MESSAGES=ko_KR.UTF-8

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
    && echo $(date +%r)

ENV POSTGRES_USER paranlee
ENV POSTGRES_PASSWORD bluetooth
ENV POSTGRES_DB postgis
ENV POSTGRES_INITDB_ARGS "--encoding='ko_Kr.utf8' --locale='ko_Kr'"

WORKDIR /var/lib/postgresql

ADD *.sql ./
ADD *.sh ./

RUN bash db.sh
