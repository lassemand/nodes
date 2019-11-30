FROM openjdk:8-jdk-slim

RUN apt-get update && \
    apt-get clean && \
    useradd -s /bin/sh -m -d /nodes -u 2000 nodes && \
    chmod 700 /nodes && \
    mkdir -p /nodes

COPY nodes.jar /nodes/
COPY su-exec /usr/bin/
ADD https://github.com/krallin/tini/releases/download/v0.18.0/tini /usr/bin/tini

RUN chown nodes:nodes -R /nodes && \
    chmod 755 /usr/bin/su-exec /usr/bin/tini

WORKDIR /nodes

ENTRYPOINT ["tini", "--", "nodes.jar", "", "/backup.bin"]
