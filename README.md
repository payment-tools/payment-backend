# payment-backend

## Environnement UAT (Render)

Déploiement décrit dans [`deploy/README.md`](deploy/README.md) (blueprint [`render.yaml`](render.yaml), plans gratuits).

| Service | URL |
|---|---|
| API | `https://onepay-backend-uat.onrender.com` (base API : `/v1/onepay`) |
| Swagger | `https://onepay-backend-uat.onrender.com/swagger-ui.html` |
| Keycloak | `https://onepay-keycloak-uat.onrender.com` (console : `/admin`) |
| pgAdmin | `https://onepay-pgadmin-uat.onrender.com` |

> Si Render a ajouté un suffixe aux noms de service, remplacer les URLs ci-dessous par celles affichées dans le dashboard Render.

### Plan gratuit : à savoir avant de tester

- Les services s'endorment après **15 min sans trafic**. Le premier appel peut prendre plusieurs minutes : **ouvrir d'abord l'URL Keycloak puis Swagger dans un navigateur** et attendre qu'elles répondent avant de lancer le front ou l'app.
- La base Postgres gratuite **expire au bout de 30 jours** (toutes les données de test sont alors perdues).

### Comptes de test

`super.admin`, `enterprise.admin`, `enterprise.finance`, `sales.admin`, `sales.finance`, `client.test`, `cashier.test` — mot de passe : la valeur de `UAT_USERS_PASSWORD` saisie à la création du blueprint (service Keycloak → Environment sur Render).

### 1. Initialiser les données (une seule fois, base vide)

Les comptes existent dans Keycloak, mais la base UAT est vide : il faut créer les entités métier rattachées à ces comptes, depuis le frontend web, **dans cet ordre** :

1. Connecté en `super.admin` :
   1. une **entreprise** (avec au moins un module, ex. RESTAURATION) ;
   2. un **commerce** (type = un module de l'entreprise) ;
   3. un **partenariat** entreprise ↔ commerce (actif) ;
   4. les **profils** (« Inviter un utilisateur ») avec exactement les usernames Keycloak : `enterprise.admin` et `enterprise.finance` → l'entreprise, `sales.admin` et `sales.finance` → le commerce.
2. Connecté en `enterprise.admin` : la **configuration entreprise** (plafonds par module) puis un **employé** avec le username `client.test`.
3. Connecté en `sales.admin` : la **configuration du point de vente** (montants min/max) puis un **caissier** avec le username `cashier.test`.

> Le username saisi dans le back-office doit être identique au compte Keycloak : c'est lui qui relie le token de connexion au profil métier (endpoints `/me`, app mobile). Pour un nouvel utilisateur, créer aussi son compte (et son rôle realm) dans la console Keycloak.

### 2. Frontend web (`payment-frontend`) en local contre l'UAT

Le client Keycloak `onepay-client` autorise déjà `http://localhost:4200`. Dans `src/environments/environment.ts`, **en local uniquement, sans commiter** :

```ts
baseUrl: 'https://onepay-backend-uat.onrender.com',
useMock: false,
keycloak: {
  url: 'https://onepay-keycloak-uat.onrender.com/',
  realm: 'onepay',
  clientId: 'onepay-client',
},
```

Puis `npm start` et ouvrir `http://localhost:4200`.

### 3. Application mobile (`mobile-app`) contre l'UAT

Aucun fichier à modifier, les URLs passent par `--dart-define` (même commande pour simulateur, émulateur et téléphone physique, en HTTPS) :

```bash
flutter run \
  --dart-define=API_BASE_URL=https://onepay-backend-uat.onrender.com/v1/onepay \
  --dart-define=KEYCLOAK_BASE_URL=https://onepay-keycloak-uat.onrender.com
```

> Les `--dart-define` sont figés à la compilation : sur simulateur iOS, rebuilder puis réinstaller l'app après un changement d'URL.

Connexion avec `client.test` (paiement, QR, historique) ou `cashier.test` (encaissement, tickets du jour).

### 4. Base de données (pgAdmin)

pgAdmin tourne sur Render et accède à la base par le réseau privé : **aucune IP à autoriser**, accessible depuis n'importe où.

1. Ouvrir `https://onepay-pgadmin-uat.onrender.com`.
2. Se connecter avec `admin@onepay.sn` / `PGADMIN_DEFAULT_PASSWORD` (service `onepay-pgadmin-uat` → Environment sur Render).
3. Le serveur **OnePay UAT** est déjà listé : saisir le mot de passe de la base (base `onepay-uat-db` → Connections sur Render).

- Tables OnePay : schéma `public`. Tables Keycloak : schéma `keycloak` — ne pas les modifier à la main, passer par la console Keycloak.
- Plan gratuit sans disque : à chaque réveil/redémarrage, pgAdmin oublie les mots de passe enregistrés et les préférences (le serveur, lui, est recréé automatiquement).

### Dépannage

| Symptôme | Cause probable |
|---|---|
| Timeout / chargement infini au premier appel | Services endormis : attendre le réveil (voir plus haut) |
| 401 sur tous les appels avec un token valide | `KEYCLOAK_ISSUER_URI` du backend ≠ URL Keycloak réelle (`https://…/realms/onepay`) |
| 403 | Rôle non autorisé pour cette action (matrice dans `SecurityConfig`) |
| Écran « rôle non pris en charge » / profil introuvable | Profil métier (employé, caissier, profil) absent ou username différent du compte Keycloak — voir étape 1 |
| `Invalid redirect uri` sur le login web | Front servi ailleurs que `http://localhost:4200` : ajouter l'URL dans le client `onepay-client` (console Keycloak) |
