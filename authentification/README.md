# Authentification with Keycloak

This folder contains the configuration files for the Keycloak server.

## Setup environment
You must set the Keycloak admin password using the environment variable `KEYCLOAK_ADMIN_PASSWORD`.
This can be achieved by defining your own `.env` file at the project root (it will be used by docker-compose).
See `.env.template`.

## `/realms`

This folder contains the configuration files for the realms in Keycloak.

For now the single configuration file is `baeldung-keycloak-realm.json`.

It describes the realm `baeldung-keycloak` with the following configuration:
- Two users:
  - username: `brice` | password: | role: `NICE`
  - username: `igor` | password:
- A client : 
  - name: `baeldung-keycloak-confidential`


## `/themes`

This folder contains the files that describe a theme that can be used in Keycloak.

For now, we have a single theme called `elaastic`. It can only be applaid to the `login` page.