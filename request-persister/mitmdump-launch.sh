#!/bin/sh

for port in ${SSL_PORTS}
do
    if [ "${port}" -lt "1025" ]; then
        destination=$(expr 64000 + ${port})
    else
        destination=${port}
    fi
    PORT=${port} mitmdump -p ${destination} --mode "reverse:https://${ORIGIN_DOMAIN}" -k --scripts /request-persister.py --set ssl=true &
    echo "Started SSL proxy on port ${destination} for target port ${port}"
done

for port in ${PORTS}
do
    if [ "${port}" -lt "1025" ]; then
        destination=$(expr 64000 + ${port})
    else
        destination=${port}
    fi
    PORT=${port} mitmdump -p ${destination} --mode "reverse:http://${ORIGIN_DOMAIN}" -k --scripts /request-persister.py &
    echo "Started non SSL proxy on port ${destination} for target port ${port}"
done


