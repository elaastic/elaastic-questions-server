# elaastic
_elaastic_ est un EIAH (Environnement Informatique pour l’Apprentissage Humain) dédiée à l’orchestration de séquences d’évaluations formatives pendant les cours en face à face ou à distance.
          
## Pour commencer
_elaastic_ est développé avec le framework spring-boot et le langage Kotlin.

### Prérequis
* Java (version 11)
* Gradle
* Docker

### Lancer la base de données
Le fichier `docker-compose` permet de lancer les services de base de données MySQL 5 utilisés pour lancer l'application ou pour exécuter les tests. 

Lancer la base de données pour l'application :  
````
docker-compose start elaastic-questions-db
````

Lancer la base de données pour les tests : 
````
docker-compose start elaastic-questions-db-test
```` 


### Lancer l'application
Pour lancer l'application en mode développement :
````
gradle bootRun
````
            
L'application est alors accessible sur `http://localhost:8080`.
                     

## Déployer l'application
_elaastic_ peut être déployé en mode _stand-alone_ (avec un serveur Tomcat embarqué) ou bien dans un serveur Tomcat.

### Packager l'application en mode _stand-alone_
````
gradle bootJar
````
Récupérer le fichier `elaastic-questions-server.jar` dans le dossier `build/libs`.

Il est possible de tester ce mode packagé en lançant les services du fichier `docker-compose-standalone.yml`.
L'application sera disponible sur `http://localhost:8081/elaastic-questions`.

### Packager l'application pour Tomcat
````
gradle bootWar
````
Récupérer le fichier `elaastic-questions-server.war` dans le dossier `build/libs`.

Il est possible de tester le package pour Tomcat en lançant les services du fichier `docker-compose.tomcat.yml`.
L'application sera disponible sur `http://localhost:8088`.

 
## Licence

Elaastic - formative assessment system
Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier

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
