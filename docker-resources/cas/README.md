# Local CAS test servers

Docker Compose provides two optional CAS 6.6.9 servers, `cas` and `cas-2`, for
running elaastic on the host at `http://localhost:8080`. They share a local test
certificate, but run separately on ports 8443 and 8444.

## 1. Generate local TLS files

Prerequisites: Docker Compose, Bash (Git Bash on Windows), and the JDK used to
run elaastic (Java 17 as specified in the root README). Run the following from
the repository root, setting `JAVA_HOME` to that JDK's actual location:

```bash
export JAVA_HOME="/path/to/jdk-17"
bash docker-resources/cas/generate-dev-tls.sh
```

The script generates these Git-ignored files under `docker-resources/cas/generated/`:

- `cas-keystore.p12`: private key and self-signed certificate for the CAS servers.
- `cas-certificate.pem`: public certificate, also usable for browser trust.
- `elaastic-truststore.p12`: a copy of the JDK's trusted authorities, plus the CAS certificate.

All store passwords are `changeit`. These files and credentials are only for local
testing; never reuse them in a deployed environment or commit the private key.
The script does not change the JDK's `cacerts`. If its password differs from
`changeit`, set `CAS_DEV_CACERTS_PASSWORD` before running the script.

The certificate is valid for 365 days and has SANs for `localhost`, `127.0.0.1`,
`cas` and `cas-2`. Re-running the script keeps existing files.

## 2. Start the two CAS servers

From the repository root:

```bash
docker compose up -d --build cas cas-2
docker compose logs -f cas cas-2
```

Wait for both servers to finish starting, then open:

- ENT_1: <https://localhost:8443/cas/login>
- ENT_2: <https://localhost:8444/cas/login>

Compose mounts only the server keystore, read-only. TLS material is excluded
from the Docker build context. Generate the files before starting the services;
Compose fails if the keystore is missing.

Browsers have their own certificate trust settings: the JVM truststore does not
remove their self-signed certificate warning. For local testing, accept the
warning at both URLs, or import `generated/cas-certificate.pem` into your
browser's certificate trust settings. Do not disable TLS validation in elaastic.

## 3. Configure and run elaastic

In IntelliJ, add these **VM options** to the Spring Boot application run
configuration, replacing the path with the absolute path to this checkout:

```text
-Djavax.net.ssl.trustStore=/absolute/path/to/elaastic-questions-server/docker-resources/cas/generated/elaastic-truststore.p12
-Djavax.net.ssl.trustStorePassword=changeit
-Djavax.net.ssl.trustStoreType=PKCS12
```

Quote the path if it contains spaces. These options belong to the application
JVM, not the program arguments or just the Gradle daemon.

Alternatively, from the repository root in Bash, use `JAVA_TOOL_OPTIONS` for
this command so that Gradle's `bootRun` child JVM also receives the settings:

```bash
JAVA_TOOL_OPTIONS="-Djavax.net.ssl.trustStore=\"$PWD/docker-resources/cas/generated/elaastic-truststore.p12\" -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.trustStoreType=PKCS12" \
  ./gradlew :server:bootRun
```

Start the database and other normal application dependencies as described in
the [root README](../../README.md). Open <http://localhost:8080> and select a CAS
provider. The default application configuration maps ENT_1 to port 8443 and
ENT_2 to port 8444; ENT_3 points to port 8445, for which Compose provides no server.
No OIDC profile is needed for CAS.

The static CAS accounts are defined in `etc/cas/config/cas.properties` and their
attributes in `etc/cas/attribute-repository.json`:

| Login | Password | Suggested provider |
| --- | --- | --- |
| `fsil` | `1234` | ENT_1 (Kosmos, teacher attributes) |
| `kcas` | `1234` | ENT_1 (Kosmos, student attributes) |
| `casuser` | `P@ssword00` | ENT_2 (Edifice, firstName/lastName attributes) |

The registered CAS service allows callbacks to `http://localhost:8080/.*`.
If you change elaastic's host, port or context path, also update
`etc/cas/services/elaastic-88.json` and rebuild the CAS images.

## Renewal and troubleshooting

To renew an expired certificate, change its names, or refresh the trusted
public authorities after changing JDK, stop CAS and delete only the generated
TLS directory, then regenerate:

```bash
docker compose stop cas cas-2
rm -rf docker-resources/cas/generated
bash docker-resources/cas/generate-dev-tls.sh
docker compose up -d --force-recreate cas cas-2
```

Restart elaastic to load the new truststore, and accept/import the new browser
certificate if necessary. Customize the script's SAN list before regenerating
if you need other hostnames.

- `PKIX path building failed`: check the application JVM's truststore options
  and verify it uses the newly generated truststore.
- Hostname validation error: use a name included in the certificate's SANs.
- Missing keystore mount: run the generation script before Compose.
- To stop the test servers: `docker compose stop cas cas-2`.
