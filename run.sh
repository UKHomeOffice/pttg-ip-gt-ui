#!/usr/bin/env bash
NAME=${NAME:-pttg-ip-gt-ui}

JAR=$(find . -name ${NAME}*.jar|head -1)
java -Dcom.sun.management.jmxremote.local.only=false -Djavax.net.ssl.trustStore=/data/truststore.jks -Djava.security.egd=file:/dev/./urandom -jar "${JAR}"
