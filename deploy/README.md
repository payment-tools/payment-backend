# Déploiement UAT sur Render

Blueprint : [`render.yaml`](../render.yaml) — Postgres + Keycloak 23.0.7 + payment-backend, région Frankfurt, branche `develop`.

## Première mise en place

1. Render → **New → Blueprint** → choisir le repo `payment-backend`.
2. Saisir les variables demandées :
   - `UAT_USERS_PASSWORD` : mot de passe des 7 comptes de test (`super.admin`, `enterprise.admin`, `enterprise.finance`, `sales.admin`, `sales.finance`, `client.test`, `cashier.test`).
   - `KEYCLOAK_ISSUER_URI` : `https://<url keycloak>/realms/onepay`
   - `KEYCLOAK_SERVER_URL` : `https://<url keycloak>`

   L'URL Keycloak est normalement `onepay-keycloak-uat.onrender.com`. Si Render ajoute un suffixe (nom déjà pris), corriger ces deux variables sur le service backend puis redéployer.
3. Console d'admin Keycloak : `https://<url keycloak>/admin`, utilisateur `admin`, mot de passe = `KEYCLOAK_ADMIN_PASSWORD` (généré, visible dans Environment du service Keycloak).

## À savoir

- Keycloak partage la base Postgres du backend, dans le schéma `keycloak` (créé au démarrage par `deploy/keycloak/CreateSchema.java`).
- Le realm (`deploy/keycloak/realm/onepay-realm.json`) n'est importé **que s'il n'existe pas encore** : une modification ultérieure du fichier (ou de `UAT_USERS_PASSWORD`) n'est pas appliquée automatiquement — passer par la console d'admin.
- `FRONTEND_URL` (optionnel, sur Keycloak) : URL du front UAT autorisée en redirection pour `onepay-client`, en plus de `http://localhost:4200`. Même règle : lue uniquement au premier import.
- Le thème `deploy/keycloak/themes/onepay` est une copie de `keycloak-23.0.7/themes/onepay` (hors git) : le recopier en cas de modification.
- Le health check Actuator est réservé à `SUPER_ADMIN` : le service backend n'a pas de `healthCheckPath`, Render vérifie seulement que le port répond.
