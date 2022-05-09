#!/bin/sh
set -e
export VERSION=${1}
export HABIT_HTTP_PORT=7080
export HABIT_HTTPS_PORT=7443

cd ../server
sh build.sh ${1}
cd -

./updateServerService.sh
