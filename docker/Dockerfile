FROM openjdk:8-jdk-slim

RUN apt-get update && \
    apt-get clean && \
    useradd -s /bin/sh -m -d /nodes -u 2000 nodes && \
    chmod 700 /nodes && \
    mkdir -p /nodes /backup

COPY nodes.jar /nodes/
ADD https://github.com/krallin/tini/releases/download/v0.18.0/tini /usr/bin/tini

RUN chown nodes:nodes -R /nodes && \
    chmod 755 /usr/bin/tini

VOLUME /backup

WORKDIR /nodes

ENTRYPOINT ["tini", "--", "java", "-jar", "nodes.jar", "-p", "/backup/backup.bin"]
