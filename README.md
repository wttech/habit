# Habit

Tool for testing Apache Httpd / Nginx / proxy server behaviour.

Build a testing environment according to the `habit.json` configuration. 
Execute test requests and verify if they were processed as expected.

## Documentation

[Documentation](wttech.github.io/habit)

## Local development

Prerequisites:
* install Docker
* `docker swarm init` - enable Swarm mode

Build current version:
* `gradlew :install` - install all Java artifacts
* `gradlew :publishToLocalDocker` - build all Docker images

Install Habit Server - current version
* `gradlew :dev:installServer`

Install Habit Server - released version
* `sh ./dev/install.sh <version_number>`

## Uninstall

* `sh dev/remove.sh`

## Modules

* client - Java Habit client library
* dev - local development helper
* environment-front-proxy - Nginx proxy for an environment
* front-proxy - Nginx proxy for the whole solution
* gradle-plugin - Gradle plugin, entrypoint to client library
* junit-plugin - JUnit plugin, entrypoint to client library
* mock-server - simplest HTTP server, Python / Docker
* request-graph-server - server handling test requests, Python / Docker
* request-persister - MitM proxy recording requests and responses, Python / Docker
* server - Spring Boot server with REST API

## Versioning

Semantic version scheme is used.

All Java / Docker / NPM artifacts use the same version number.

## Contributing

[Contributing rules](CONTRIBUTING.md)

## Code of conduct

[Code of conduct](CODE_OF_CONDUCT.md)

## License

Habit is licensed under [Apache License, Version 2.0](LICENSE).
