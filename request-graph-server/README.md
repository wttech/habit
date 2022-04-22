# Proxy

Acts as a proxy for the Apache server. Handles redirects with configured domains.

Returns full information about the request, response and redirect history.

Requests are proxied to domain found in the Host header so it is required to have a Apache container connected to the same network as the proxy with these domains as aliases.

## Authors

Tomasz Krug (<tomasz.krug@gmail.com>)
