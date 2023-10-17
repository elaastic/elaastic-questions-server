# CAS Container

This ``DockerFile``, associated to config files stored into ``./cas/etc`` allows to
build and run a CAS server in development mode.

## Setup the CAS certificate
The CAS server will use the auto-signed certificate ``./etc/cas/config/cas-certificate.cer``.

This certificate must be loaded into the JVM used for running this Spring Boot webapp :
```
 <JAVA_HOME>\bin\keytool -importcert -cacerts -alias "cas-certificate" -file docker-resources/cas/etc/cas/config/cas-certificate.cer
```

The password for this autosigned testing certificate is : ``changeit``.

## Generate an new self-signed certificate

Step 1 : Generate the certificate
```
 <JAVA_HOME>\bin\keytool -genkey -noprompt -keystore thekeystore -storepass changeit -keypass changeit -validity 3650 \
            -keysize 2048 -keyalg RSA -alias cas-certificate -dname "CN=localhost, OU=MyOU, O=MyOrg, L=Somewhere, S=VA, C=US" \
```

Step 2 : export it
```
 <JAVA_HOME>\bin\keytool -export -alias cas-certificate -storepass changeit -file cas-certificate.cer -keystore thekeystore
```

Step 3 : 
Move the keystore into ``./etc/cas/thekeystore`` and the certificate into ``./etc/cas/config/cas-certificate.cer``. 