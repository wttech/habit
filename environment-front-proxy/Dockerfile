FROM nginx:1.15.10-alpine

RUN apk update && apk add openssl curl

COPY generate-self-signed-ssl.sh /opt/
COPY run.sh /opt/
COPY ./configuration/main.conf /etc/nginx/conf.d/main.conf

RUN chmod a+x /opt/run.sh /opt/generate-self-signed-ssl.sh

CMD ["/opt/run.sh"]