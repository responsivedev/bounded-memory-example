#!/bin/sh

LD_PRELOAD=/usr/lib/aarch64-linux-gnu/libjemalloc.so.2
java -cp /usr/share/java/app/*.jar dev.responsive.boundedmemoryexample.BoundedMemoryExample /app.properties
