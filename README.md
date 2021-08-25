# elaastic
_elaastic_ is an interactive system dedicated to the orchestration of formative assessment sequences during face-to-face or distance learning.        
_elaastic_ is developed in the context of research
conducted by the [TALENT team at IRIT](https://www.irit.fr/en/departement/dep-interaction-collective-intelligence/talent-team/) on the design and implementation of formative
assessment systems.

## Getting started
_elaastic_ is developed with the spring-boot framework and the Kotlin language.

### Prerequisites
* Java (version 11)
* Gradle
* Docker

### Launching the database
The `docker-compose` file is used to start the MySQL 5 database services used to run the application or to run tests. 

Launch the database for the application: 
````
docker-compose start elaastic-questions-db
````

Launch the database for the tests: 
````
docker-compose start elaastic-questions-db-test
```` 


### Launching the application
To launch the application in development mode:
````
gradle bootRun
````

The application is then accessible at `http://localhost:8080`.
                     

## Deploying the application
_elaastic_ can be deployed in _stand-alone_ mode (with an embedded Tomcat server) or in a Tomcat server.

### Packaging the application in _stand-alone_ mode
````
gradle bootJar
````
Get the `elaastic-questions-server.jar` file from the `build/libs` folder.

It is possible to test this packaged mode by running the services in the `docker-compose-standalone.yml` file.
The application will be available at `http://localhost:8081/elaastic-questions`.

### Packaging the application for Tomcat
````
gradle bootWar
````
Get the `elaastic-questions-server.war` file from the `build/libs`folder.

It is possible to test the package for Tomcat by running the services in the `docker-compose.tomcat.yml` file.
The application will be available at `http://localhost:8088`.

 
## Licence

Elaastic - formative assessment system
Copyright (C) 2019. Université Toulouse 1 Capitole, Université Toulouse 3 Paul Sabatier

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
