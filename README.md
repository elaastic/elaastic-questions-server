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

### Update the `hosts` file
The following rules must be added to your hosts file to use local dev hostnames : 
```
127.0.0.1   iam.local idp.local
```

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

The generated autosigned certificate must be imported on the JDK used to launch elaastic.

From `JAVA_HOME`, run the following command :

```shell
.\bin\keytool.exe -importcert -cacerts -alias "elaastic-cas" -file <elaastic-questions-server>\docker-resources\cas\etc\cas\config\elaastic-cas-certificate.cer
```

### Launching the application

To launch the application in development mode:

You have to launch at least this container :

- elaastic-questions-db-8
- elaastic-mailhog
- auth-iam

````shell
docker compose up -d elaastic-questions-db-8 elaastic-mailhog auth-iam
````

Then, you can run the application with the following command:

````shell
gradle bootRun
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

## Development guide

The project _elaastic_ is composed of two modules:

1. `server`: The Spring Boot webapp developed in Kotlin
2. `ui-components`: A set of UI components developed in Vue 3

## Deploying the application

_elaastic_ can be deployed in _stand-alone_ mode (with an embedded Tomcat server) or in a Tomcat server.

### Packaging the application in _stand-alone_ mode

````shell
gradle bootJar
````

Get the `elaastic-questions-server.jar` file from the `build/libs` folder.

It is possible to test this packaged mode
by running the services in the [docker-compose.konsolidation.yml](docker-compose.production_example.yml) file.
The application will be available at `http://localhost:8080/elaastic-questions`.

### Packaging the application for Tomcat

````
gradle bootWar
````

Get the `elaastic-questions-server.war` file from the `build/libs`folder.

## Development guide

The project _elaastic_ is composed of two modules:

1. `server`: The Spring Boot webapp developed in Kotlin
2. `ui-components`: A set of UI components developed in Vue 3

You can follow the [README.md](ui-components/README.md) in the `ui-components` folder for more information
on how to use it in Elaastic.

## Authentication

### Authenticate on Elaastic using the Keycloak OIDC Identity & Access Manager

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
