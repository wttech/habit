FROM python:3.7.0-alpine3.8

WORKDIR /usr/src/app
COPY run.sh ./
COPY mock-server.py ./

RUN chmod a+x ./run.sh

CMD ["./run.sh"]
