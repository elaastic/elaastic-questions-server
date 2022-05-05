FROM apereo/cas
COPY etc/cas/* /etc/cas/
COPY etc/cas/config/* /etc/cas/config/
COPY etc/cas/services/* /etc/cas/services/
#RUN cd /etc/cas \
#  && keytool -genkey -noprompt -keystore thekeystore -storepass changeit -keypass changeit -validity 360 \
#             -keysize 2048 -keyalg RSA -alias elaastic-cas -dname "CN=localhost, OU=MyOU, O=MyOrg, L=Somewhere, S=VA, C=US" \
#  && keytool -export -alias elaastic-cas -storepass changeit -file config/elaastic-cas-certificate.cer -keystore thekeystore
RUN if [ -r /etc/cas/config/certificate.pem ]; then \
       keytool -noprompt -importcert -keystore /etc/ssl/certs/java/cacerts -storepass changeit \
               -file /etc/cas/config/elaastic-cas-certificate.cer -alias "elaastic-cas"; \
    fi


# The generated autosigned certificate must be imported on the JDK used to launch elaastic \
# from JAVA_HOME, run the following command
# .\bin\keytool.exe -importcert -cacerts -alias "elaastic-cas" -file <elaastic-questions-server>\docker-resources\cas\etc\cas\config\elaastic-cas-certificate.cer