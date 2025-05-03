# External Identity Provider SAML2 (idp-saml2)

## Description

This folder contains, for testing purpose only, an Identity Provider working in SAML2.

The goal is to experiment with adding to the Elaastic authentication server (implemented with Keycloak)
an external Identity Provider using the SAML2 protocol.
This configuration allows us to explore the situation where user identity is provided by an external
source such as the CAS server of an "Environnement Numérique de Travail" (ENT).

For the sake of simplicity, we will use another Keycloak server to implement this external Identity Provider
using SAML2.

## realms

### master realm

#### Users

`admin` / ${KEYCLOAK_ADMIN_PASSWORD}

### test-external-idp-saml2 realm

This realm is loaded when creating the container from `./realms/test-external-idp-saml2-realm.json`.

Users predefined in the realm:

| username | password |  role   | Comment |
|----------|----------|:-------:|---------|
| demo     | demo     |         |         |
| admin    | secret   |  ADMIN  |         |
| eleve    | secret   |  Eleve  |         |
| student  | secret   | STUDENT |         |
| teacher  | secret   | TEACHER |         |

Roles in the reaml:

- `STUDENT` : map to `STUDENT` in `auth-iam-1`
- `TEACHER` : map to `TEACHER` in `auth-iam-1`
- `ADMIN` : map to `ADMIN` in `auth-iam-1`
- `Eleve` : map to `STUDENT` in `auth-iam-1`
- `Professeur` : map to `TEACHER` in `auth-iam-1`

A client:

- clientId: `http://host.docker.internal:8081/realms/elaastic-keycloak` => For SAML2 Identity brokering from
  Elaastic IAM

## Common operations

### Export the test-external-idp-saml2 realm

```bash
docker compose exec auth-idp-saml2 /opt/keycloak/bin/kc.sh export --dir=/opt/keycloak/data/import/ --realm test-external-idp-saml2 --users realm_file --optimized
```
