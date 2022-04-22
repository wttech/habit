#!/bin/sh
set -e
export SERVER_IMAGE=${1}
export HABIT_HTTP_PORT=7080
docker stack deploy -c ./docker-compose.yml habit
