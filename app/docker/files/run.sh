#!/bin/bash

JMX_OPTS="-Dcom.sun.management.jmxremote.port=7192
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
"

LD_PRELOAD=/usr/lib/x86_64-linux-gnu/libjemalloc.so.2

echo "starting application"

java -Dlog4j2.configurationFile=file:/log4j2.properties ${JMX_OPTS} -cp "/usr/share/java/app/*" dev.responsive.boundedmemoryexample.BoundedMemoryExample /app.properties
