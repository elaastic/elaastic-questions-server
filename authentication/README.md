# Authentication with Keycloak

This folder contains the configuration files for the Keycloak server.

## Setup environment
You must set the Keycloak admin password using the environment variable `KEYCLOAK_ADMIN_PASSWORD`.
This can be achieved by defining your own `.env` file at the project root (it will be used by docker-compose).
See `.env.template` at the root of the project.

## `/realms`

This folder contains the configuration files for the realms in Keycloak.

For now the single configuration file is `elaastic-keycloak-realm.json`.

It describes the realm `elaastic-keycloak` with the following configuration:

- Some users:

| username | password | role | Comment                         |
|----------|----------|:----:|---------------------------------|
| brice    | secret   | NICE |                                 |
| igor     | secret   |  /   |                                 |
| janedoe  | secret   |  /   | Has the same email as `johndoe` |
| johndoe  | secret   |  /   | Has the same email as `janedoe` |

- A client : 
  - name: `elaastic`


## `/themes`

This folder contains the files that describe a theme that can be used in Keycloak.

For now, we have a single theme called `elaastic`. It can only be applaid to the `login` page.