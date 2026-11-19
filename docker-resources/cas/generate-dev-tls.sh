#!/usr/bin/env bash
# Local development only. Run with the JDK used to launch elaastic.
set -euo pipefail
umask 077

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
output_dir="$script_dir/generated"
if [[ -z "${JAVA_HOME:-}" || ! -x "$JAVA_HOME/bin/keytool" ]]; then
  echo "Set JAVA_HOME to the JDK used to run elaastic (see README.md)." >&2
  exit 1
fi
keytool="$JAVA_HOME/bin/keytool"

if [[ -d "$output_dir" ]]; then
  for file in cas-keystore.p12 cas-certificate.pem elaastic-truststore.p12; do
    if [[ ! -s "$output_dir/$file" ]]; then
      echo "Incomplete TLS setup: $output_dir. See README.md for regeneration." >&2
      exit 1
    fi
  done
  echo "TLS files already exist in $output_dir; keeping them. See README.md to regenerate."
  exit 0
fi

# Build everything in a private temporary directory; publish only on success.
work_dir="$(mktemp -d "$script_dir/.tls-XXXXXX")"
trap 'rm -rf -- "$work_dir"' EXIT

"$keytool" -genkeypair -noprompt -alias cas-dev -keyalg RSA -keysize 2048 \
  -validity 365 -dname "CN=localhost, O=Elaastic development" \
  -ext "SAN=dns:localhost,ip:127.0.0.1,dns:cas,dns:cas-2" \
  -ext "EKU=serverAuth" -storetype PKCS12 \
  -keystore "$work_dir/cas-keystore.p12" -storepass changeit -keypass changeit
"$keytool" -exportcert -rfc -alias cas-dev \
  -keystore "$work_dir/cas-keystore.p12" -storepass changeit \
  -file "$work_dir/cas-certificate.pem"

# Preserve the JDK's normal HTTPS trust, without modifying its cacerts.
# Override the source password if the local JDK uses a non-default password.
"$keytool" -importkeystore -noprompt -srckeystore "$JAVA_HOME/lib/security/cacerts" \
  -srcstorepass "${CAS_DEV_CACERTS_PASSWORD:-changeit}" \
  -destkeystore "$work_dir/elaastic-truststore.p12" \
  -deststoretype PKCS12 -deststorepass changeit > "$work_dir/import.log" 2>&1 || {
    cat "$work_dir/import.log" >&2
    exit 1
  }
"$keytool" -importcert -noprompt -alias elaastic-cas-dev \
  -file "$work_dir/cas-certificate.pem" \
  -keystore "$work_dir/elaastic-truststore.p12" -storetype PKCS12 -storepass changeit
rm "$work_dir/import.log"
# CAS may run as a different user inside Docker; only the test key is world-readable.
chmod 644 "$work_dir/cas-keystore.p12" "$work_dir/cas-certificate.pem"
chmod 755 "$work_dir"
mv -- "$work_dir" "$output_dir"
echo "Created local development TLS files in $output_dir (valid for 365 days)."
