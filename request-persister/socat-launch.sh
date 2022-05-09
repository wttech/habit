#!/bin/sh
for port in ${SSL_PORTS}
do
    if [ "${port}" -lt "1025" ]; then
        forkTo=$(expr 64000 + ${port})
        socat "TCP4-LISTEN:${port},fork" "TCP4:127.0.0.1:${forkTo}" &
    fi
done

for port in ${PORTS}
do
    if [ "${port}" -lt "1025" ]; then
        forkTo=$(expr 64000 + ${port})
        socat "TCP4-LISTEN:${port},fork" "TCP4:127.0.0.1:${forkTo}" &
    fi
done
