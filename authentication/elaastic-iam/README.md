# Authentication with Keycloak

This folder contains the configuration files for the Keycloak server.

## Setup environment

You must set the Keycloak admin password using the environment variable `KEYCLOAK_ADMIN_PASSWORD`.
This can be achieved by defining your own `.env` file at the project root (it will be used by docker-compose).
See `.env.template` at the root of the project.

## `/realms`

This folder contains the configuration files for the realms in Keycloak.

### elaastic-keycloak

For now the single configuration file is `elaastic-keycloak-realm.json`.

It describes the realm `elaastic-keycloak` with the following configuration:

- Some realm roles :
    - `STUDENT` : can log in to the `elaastic` client
    - `TEACHER` : can log in to the `elaastic` client
    - `ADMIN` : can log in to the `elaastic` client
    - `NICE` : **CAN'T** log in to the `elaastic` client

- Some users:

| username | password |      role       | Comment                                              |
|----------|----------|:---------------:|------------------------------------------------------|
| brice    | secret   |      NICE       | Didn't have an Elaastic role so it can't login to it |
| igor     | secret   |        /        | Didn't have an Elaastic role so it can't login to it |
| janedoe  | secret   |     STUDENT     | Has the same email as `johndoe`                      |
| johndoe  | secret   |     STUDENT     | Has the same email as `janedoe`                      |
| alice    | secret   |      ADMIN      |                                                      |
| steve    | secret   |     STUDENT     |                                                      |
| tom      | secret   |     TEACHER     |                                                      |
| two_role | secret   | STUDENT,TEACHER | Has two roles so he can't login to Elaastic          |

- A client: ⇒ For the elaastic application
    - name: `elaastic`

If a user has exactly one role that can log into the `elaastic` client, then he can log in to it.

- An Identity Provider:
    - alias: `saml` in `./keaycloak/README.md`

## `/themes`

This folder contains the files that describe a theme that can be used in Keycloak.

For now, we have a single theme called `elaastic`. It can only be applaid to the `login` page.

## Common operations

### Export the elaastic-keycloak realm

```bash
docker compose exec auth-iam /opt/keycloak/bin/kc.sh export --dir=/opt/keycloak/data/import/ --realm elaastic-keycloak --users realm_file --optimized
```
