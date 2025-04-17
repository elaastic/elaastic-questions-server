# External Identity Provider SAML2

## Description

This folder contains, for testing purpose only, an Identity Provider working in SAML2.

The goal is to experiment with adding to the Elaastic authentication server (implemented with Keycloak)
an external Identity Provider using the SAML2 protocol.
This configuration allows us to explore the situation where user identity is provided by an external 
source such as the CAS server of an "Environnement Numérique de Travail" (ENT).

For sake of simplicity we will use another Keycloak server to implement this external Identity Provider
using SAML2.

## Common operations

### Export the test-external-idp-saml2 realm
```bash
docker compose exec auth-idp-saml2 /opt/keycloak/bin/kc.sh export --dir=/opt/keycloak/data/import/ --realm test-external-idp-saml2 --users realm_file --optimized
```
