#!/bin/sh

JMX_OPTS="-Dcom.sun.management.jmxremote=true
-Dcom.sun.management.jmxremote.port=7192
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.local.only=false
-Dcom.sun.management.jmxremote.rmi.port=7192
"

LD_PRELOAD=/usr/lib/aarch64-linux-gnu/libjemalloc.so.2

java ${JMX_OPTS} -cp /usr/share/java/app/*.jar dev.responsive.boundedmemoryexample.BoundedMemoryExample /app.properties
