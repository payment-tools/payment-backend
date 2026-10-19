#!/bin/bash
# Render expose l'URL publique du service dans RENDER_EXTERNAL_HOSTNAME :
# on l'utilise comme hostname Keycloak (=> issuer des JWT) sauf si KC_HOSTNAME est fourni.
export KC_HOSTNAME="${KC_HOSTNAME:-$RENDER_EXTERNAL_HOSTNAME}"
set -e
if [ -n "$KC_DB_SCHEMA" ]; then
  java -cp "$(ls /opt/keycloak/lib/lib/main/org.postgresql.postgresql-*.jar)" /opt/keycloak/bin/CreateSchema.java
fi
# --import-realm n'importe le realm que s'il n'existe pas encore en base.
exec /opt/keycloak/bin/kc.sh start --optimized --import-realm "$@"
