FROM apereo/cas
COPY etc/cas/config/* /etc/cas/config/
RUN cd /etc/cas \
  && keytool -genkey -noprompt -keystore thekeystore -storepass changeit -keypass changeit -validity 360 \
             -keysize 2048 -keyalg RSA -alias thekeystore -dname "CN=localhost, OU=MyOU, O=MyOrg, L=Somewhere, S=VA, C=US"
RUN if [ -r /etc/cas/config/certificate.pem ]; then \
       keytool -noprompt -importcert -keystore /etc/ssl/certs/java/cacerts -storepass changeit \
               -file /etc/cas/config/certificate.pem -alias "casclient"; \
    fi


