# Authentication

This directory contains all the services related to authentication for Elaastic.

**TODO**: Create modules here for the CAS servers.

## Authentication modules

### `elaastic-iam`

This directory contains the "Identity and Access Management" service for Elaastic.
It is implemented by a Keycloak server.
Elaastic is registered as an OpenID Connect client of `elaastic-iam` service.

### `idp-saml2`

This directory contains an Identity Provider that uses SAML2 protocol.
It's implemented by another Keycloak server.

`idp-saml2` is registered as an Identity Provider for `elaastic-iam`.

## Authentication scenarios

Up to now, Elaastic supports the following authentication workflows.

### Local database

Users directly registered on the elaastic application can log onto the Elaastic login form.

### CAS server

Elaastic supports the CAS protocol; many CAS servers can be configured on Elaastic.
For each configured CAS, an external authentication link is provided on the Elaastic login form.
Using this link, a user can authenticate against the CAS server and open a session on Elaastic.

### OpenID Connect with `elaastic-iam`

The `elaastic-iam` service holds its own users referential.
When activated on elaastic (through `application.properties`), this new authentication source is presented on the
elaastic login form.
Users can authenticate on `elaastic-iam` to open a session on Elaastic.

### OpenID Connect with `elaastic-iam` + Identity brokering with `idp-saml2`

On the login form of `elaastic-iam`, there is a link to `idp-saml2` allow users to authenticate on `idp-saml2`,
and then opens a session on `elaastic-iam` using SAML2,
which turns to open a session on `elaastic` using OpenID Connect.