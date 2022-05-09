#!/usr/bin/env bash
set -e
docker build -f ./Dockerfile -t ghcr.io/wttech/habit/habit-server:${1} --build-arg VERSION=${1} .
