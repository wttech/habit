#!/bin/sh
pids=""

# run processes and store pids in array
for port in ${MOCK_PORTS}
do
    python /usr/src/app/mock-server.py ${port} &
    if [ -z "${pids}" ]; then
      pids="$!"
    else
      pids="${pids} $!"
    fi

done

# wait for all pids
for pid in ${pids}
do
    wait ${pid}
done