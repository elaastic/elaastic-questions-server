# elaastic

_elaastic_ is an interactive system dedicated to the orchestration of formative assessment sequences during face-to-face
or distance learning.        
_elaastic_ is developed in the context of research
conducted by
the [TALENT team at IRIT](https://www.irit.fr/en/departement/dep-interaction-collective-intelligence/talent-team/) on
the design and implementation of formative
assessment systems.

## Getting started

_elaastic_ is developed with the spring-boot framework and the Kotlin language.

### Prerequisites

* Java (version 17)
* Gradle
* Docker

### Launch the services required by Elaastic

Those services are containerized and can be launched with docker compose:

````
docker-compose up -d [<service>]
````

| Service name                 | Description                                                     |
|:-----------------------------|:----------------------------------------------------------------| 
| elaastic-questions-db-8      | mySQL 8 Database used for running elaastic                      |
| elaastic-questions-db-test-8 | mySQL 8 Database used for running integration tests             |
| cas                          | a CAS server just for testing CAS integration in dev mode       |
| cas-2                        | another CAS server for testing multiple CAS servers integration |
| elaastic-mailhog             | a mail server for testing email sending                         |
| auth-iam                     | a keycloak server to connect with OpenID Connect                |
| auth-idp-saml2               | identity provider using SAML2 protocole                         |

### Set up the dockerized CAS servers in dev mode

The optional `cas` and `cas-2` services allow local CAS authentication testing.
Before starting them, follow the [CAS development setup](docker-resources/cas/README.md)
to generate local TLS files and configure the JVM running elaastic.

### Launching the application

To launch the application in development mode:

You have to launch at least these containers :
- elaastic-questions-db-8
- elaastic-mailhog

````shell
docker compose up -d elaastic-questions-db-8 elaastic-mailhog
````

Then, you can run the application with the following command:

````shell
./gradlew :server:bootRun
````

The application is then accessible at `http://localhost:8080`.

You can access the MailHog web interface at `http://localhost:8025` to check the emails sent by the application.

To manage the authentification service Keycloak, you can access the web interface at `http://localhost:8081/`.
To connect to the console as `admin`, use the password set in the [.env](.env) file
(see [.env.template](.env.template)).
More information about the Keycloak server can be found in the [README.md](authentication/elaastic-iam/README.md) file.

### Test users

| Login | Password | role    | 
|:------|:---------|:--------| 
| fsil  | 1234     | teacher |
| tsil  | 1234     | student | 
| jtra  | 1234     | student | 
| admin | admin    | admin   |

## Project structure

### Directory layout

```text
.
├── .github/workflows/          # CI workflows for testing, analysis, docs, and image publishing
├── .env.template               # Example environment variables for local setup
├── settings.gradle.kts         # Includes the server module
├── gradle/
│   └── libs.versions.toml       # Dependency and plugin version catalog
├── gradlew, gradlew.bat        # Gradle wrapper scripts for Unix and Windows
├── docker-compose.yml          # Services for local development
├── docker-compose.production_example.yml
│                               # Example Docker Compose configuration for production
├── docker-resources/           # Container configuration and supporting files
├── authentication/             # Keycloak and SAML identity provider setup
├── user-docs/                  # Jekyll source for the user documentation
└── server/                     # Kotlin and Spring Boot web application
    ├── build.gradle.kts        # Dependencies, build tasks, and packaging
    └── Dockerfile              # Builds the application container image
```

## Deploying the application

_elaastic_ can be deployed in _stand-alone_ mode (with an embedded Tomcat server) or in a Tomcat server.

### Packaging the application in _stand-alone_ mode

````shell
./gradlew :server:bootJar
````

Get the `server-<version>.jar` file from the `server/build/libs` folder.

To build the Docker image, run this command from the project root.

```shell
docker build -f server/Dockerfile .
```

It is possible to test this packaged mode by running the services in the `docker-compose-standalone.yml` file.
The application will be available at `http://localhost:8080/elaastic-questions`.

### Packaging the application for Tomcat

````
gradle bootWar
````

Get the `elaastic-questions-server.war` file from the `build/libs`folder.

It is possible to test the package for Tomcat by running the services in the `docker-compose.tomcat.yml` file.
The application will be available at `http://localhost:8088`.

## Authentication

### Authenticate on Elaastic using the Keycloak OIDC Identity & Access Manager

Enable the `oidc` Spring profile (for example with `SPRING_PROFILES_ACTIVE=oidc`) and start
`auth-iam` and its reverse proxy as described in [authentication](authentication/README.md).
The default provider settings are in `server/src/main/resources/application-oidc.properties`;
replace the issuer URI and client credentials for other environments. The identity provider must
be available when the application starts.

OIDC is disabled by default. Set `elaastic.oidc.enabled=true` (or `ELAASTIC_OIDC_ENABLED=true`)
and supply the `spring.security.oauth2.client.*` provider settings to enable it without a profile.
The `oidc` profile sets this property to `true` and provides the development settings above.
Set `ELAASTIC_OIDC_ENABLED=false` to disable OIDC even with that profile active.
Changing this setting requires restarting the application.
When disabled, OIDC login, callbacks and `oidc_hint` redirects are inactive, the login button is hidden,
and no OIDC configuration or available identity provider is required, even if provider settings remain
in the configuration. Local and CAS login and logout remain available.

The login page exposes a Keycloak button. Each external identity is associated with its Elaastic
account through `UserLink`; first login creates the account. The Keycloak `realm_access.roles`
claim must contain exactly one of `student`, `teacher` or `admin` (other realm roles are ignored).
A role mismatch with an existing Elaastic account rejects login. Logout also ends the OIDC session.

One can add to an Elaastic URL the `oidc_hint` parameter. When set to an OAuth2 server, it will be used for
authentication.
With the development config and the Keycloak test service, you can log into Elaastic using Keycloak with
http://localhost:8080/home?oidc_hint=keycloak


## Licence

Elaastic - formative assessment system
Copyright (C) 2019. Université Toulouse 1 Capitole, Université Toulouse 3 Paul Sabatier

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
