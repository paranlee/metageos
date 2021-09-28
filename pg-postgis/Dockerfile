FROM postgres:latest

RUN apt-get update -y \
    && apt-get install -y \ 
        tzdata \ 
        locales \
        locales-all \
        postgresql-13-postgis-3 \
        postgresql-13-postgis-3-scripts \
        vim \
        python3 \
    && rm -rf /var/lib/apt/lists/*

RUN localedef -i ko_KR -f UTF-8 ko_KR.UTF-8

ENV TZ 'Asia/Seoul'
ENV LANGUAGE ko_KR.UTF-8
ENV LANG ko_KR.UTF-8
ENV LC_ALL ko_KR.UTF-8
ENV LC_CTYPE ko_KR.UTF-8
ENV LC_MESSAGES ko_KR.UTF-8

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone \
    && echo $(date +%r)

ENV POSTGRES_USER paranlee
ENV POSTGRES_PASSWORD bluetooth
ENV POSTGRES_DB postgis
ENV POSTGRES_INITDB_ARGS " --locale='ko_Kr' "

WORKDIR /var/lib/postgresql

ADD *.sql ./
ADD *.sh ./

RUN chmod +x *.sh \
    ./db.sh
