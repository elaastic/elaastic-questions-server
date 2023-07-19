VERSION=$(./gradlew properties -q | grep "version:" | awk '{print $2}')
# docker run -v /path/to/config:/config -p 8080:8080 elaastic-questions:${VERSION}
docker run -p 8080:8080 elaastic-questions:${VERSION}
