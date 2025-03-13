To build, first open `app/docker/files/app.properties` and fill in your broker and auth properties. Then:
```bash
$ ./gradlew buildDocker
```

That will give you a docker image tagged "bounded-memory-example"
