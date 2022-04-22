#!/bin/sh
set -e

/socat-launch.sh

MITMPROXY_PATH="/home/mitmproxy/.mitmproxy"

mkdir -p "${MITMPROXY_PATH}"
chown -R mitmproxy:mitmproxy "${MITMPROXY_PATH}"

su-exec mitmproxy /mitmdump-launch.sh

tail -f /dev/null