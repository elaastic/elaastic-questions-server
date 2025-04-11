# External Identity Provider SAML2

This folder contains, for testing purpose only, an Identity Provider working in SAML2.

The goal is to experiment with adding to the Elaastic authentication server (implemented with Keycloak)
an external Identity Provider using the SAML2 protocol.
This configuration allows us to explore the situation where user identity is provided by an external 
source such as the CAS server of an "Environnement Numérique de Travail" (ENT).

For sake of simplicity we will use another Keycloak server to implement this external Identity Provider
using SAML2.