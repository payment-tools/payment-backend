#!/bin/sh
# Pré-enregistre le serveur Postgres UAT (hôte interne fourni par Render).
# Le plan gratuit n'a pas de disque : la config pgAdmin repart de zéro à chaque redémarrage.
cat > /tmp/servers.json <<JSON
{
  "Servers": {
    "1": {
      "Name": "OnePay UAT",
      "Group": "Servers",
      "Host": "${DB_HOST}",
      "Port": ${DB_PORT:-5432},
      "MaintenanceDB": "${DB_NAME}",
      "Username": "${DB_USER}",
      "SSLMode": "prefer"
    }
  }
}
JSON
export PGADMIN_SERVER_JSON_FILE=/tmp/servers.json
exec /entrypoint.sh "$@"
