# Dockerisation of deployment

## 1. Dockerfile adaptation

The docker file is adpated to allow:
- the load of the configuration file from outside the container only when it is launched,
- the use of the datastore as a docker volume to facilitate the share with other docker services.
