#!/bin/sh
/opt/generate-self-signed-ssl.sh
nginx -g "daemon off;"
