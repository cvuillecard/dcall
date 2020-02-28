#!/bin/sh

# $1 is prefix filename
# $2 is keystore.p12 password
# $3 is keystore.jks password
# $4 is truststore.jks password

DEST_DIRECTORY=/var/lib/dcall/ssl

# 1 - CREATE PRIVATE AND PUBLIC CERTIFICATES
echo "*** Generating public ($1-pub_cert.pem) and private key ($1-priv_key.pem) ***"
openssl req -newkey rsa:2048 -x509 -keyout $1-priv_key.pem -out $1-pub_cert.pem -days 3650

# 2 - CREATE PKCS12 / JKS FILE
echo "*** Generating $1-keystore.p12 keystore with alias entry $1-keystore ***"
openssl pkcs12 -export -in $1-pub_cert.pem -inkey $1-priv_key.pem -out $1-keystore.p12 -name "$1-keystore"

# 3 - CONVERTING THE PKCS12 / JKS TO KEYSTORE
echo "*** converting $1-keystore.p12 to JKS $1-keystore.jks ***"
keytool -importkeystore -destkeystore $1-keystore.jks -deststorepass  $2 -srckeystore $1-keystore.p12 -srcstoretype PKCS12 -srcstorepass $2

# 4 - CREATE A TRUSTSTORE : import a public certificate in a JKS keystore using
echo "*** Adding public key ($1-pub_cert.pem) to JKS file ($1-truststore.jks) ***"
keytool -import -file $1-pub_cert.pem -keystore $1-truststore.jks -storepass $3

# 5 - COPYING JKS TRUSTSTORE AND KEYSTORE FILES IN ${project.extern.resources.directory}/ssl
echo "*** Moving $1-keystore.jks and $1-truststore.jks in : /var/lib/dcall/ssl ***"
mv $1-keystore.jks ${DEST_DIRECTORY}/$1-keystore.jks
mv $1-truststore.jks ${DEST_DIRECTORY}/$1-truststore.jks

# USAGE : ./generateKey.sh "prefix_filename" "password_keystore_jks_p12" "password_truststore_jks"


######################
# CHECK CERTIFICATES #
######################

# - check the public certificate :
#   openssl x509 -in $1-pub_cert.pem -noout -text

# - check the private key
#   openssl rsa -in $1-priv_key.pem -check

# - check certificate pkcs#12
#   openssl pkcs12 -info -in $1-keystore.p12

# - check jks file without alias entry
#   keytool -list -v -keystore $1-keystore.jks

# - check jks file with alias entry (defined by -name in PKCS12)
#   keytool -list -v -keystore $1-keystore.jks -alias $1-keystore

# - concatenate the private key and public certificate into a pem file (which is required for many web-servers) :
#   cat $1-priv_key.pem $1-pub_cert.pem > server.pem

# SOURCES : https://blogs.oracle.com/blogbypuneeth/steps-to-create-a-self-signed-certificate-using-openssl