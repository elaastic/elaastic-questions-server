FROM apereo/cas:6.6.9
COPY etc/cas/* /etc/cas/
COPY etc/cas/config/* /etc/cas/config/
COPY etc/cas/services/* /etc/cas/services/

RUN if [ -r /etc/cas/config/certificate.pem ]; then \
       keytool -noprompt -importcert -keystore /etc/ssl/certs/java/cacerts -storepass changeit \
               -file /etc/cas/config/cas-certificate.cer -alias "cas-certificate"; \
    fi