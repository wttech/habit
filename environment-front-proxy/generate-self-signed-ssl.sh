#!/bin/sh
# generate self signed ssl cert

mkdir -p /etc/ssl
cd /etc/ssl

rm ./key.pem
rm ./cert.pem
rm ./ssl.crt
rm ./key.pem.orig
rm ./cert.csr

# Generating signing SSL private key
openssl genrsa -des3 -passout pass:xxxx -out key.pem 2048

# Removing passphrase from private key
cp key.pem key.pem.orig
openssl rsa -passin pass:xxxx -in key.pem.orig -out key.pem

# Generating certificate signing request
openssl req -new -key key.pem -out cert.csr -subj "/C=PL"

# Generating self-signed certificate
openssl x509 -req -days 3650 -in cert.csr -signkey key.pem -out cert.pem

