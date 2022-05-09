#!/bin/sh
set -e

docker service update --force habit_server
