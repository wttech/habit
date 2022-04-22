FROM mitmproxy/mitmproxy:4.0.4

RUN apk update && apk upgrade
RUN apk add curl vim libcap socat
RUN mkdir -p /opt/request-persister/logs
RUN chmod a+rw /opt/request-persister/logs

VOLUME /opt/request-persister/logs

COPY request-persister.py ./
COPY mitmdump-launch.sh ./
COPY socat-launch.sh ./
COPY run.sh ./

RUN chmod a+x ./run.sh ./request-persister.py ./mitmdump-launch.sh ./socat-launch.sh

ENTRYPOINT ["/run.sh"]
