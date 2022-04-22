#!/usr/bin/env bash
set -e
../gradlew :server:bootJar
./buildImage.sh ${1}
