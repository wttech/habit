#!/usr/bin/env bash
set -e
docker build -f ./Dockerfile -t habitester/habit-server:${1} --build-arg VERSION=${1} .
