FROM python:3.7.0-alpine3.8

WORKDIR /usr/src/app
COPY server.pem ./
COPY requirements.txt ./

RUN pip install -r requirements.txt

COPY request-graph-server.py ./

CMD ["python", "-u", "./request-graph-server.py"]

