To build, first open `app/docker/files/app.properties` and fill in your broker and auth properties. Then:
```bash
$ ./gradlew build
$ cd app
$ docker build . -f docker/Dockerfile -t test-bounded-memory
