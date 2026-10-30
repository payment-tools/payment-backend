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

> Le jeu de données de démo (`db/data/insert-data.sql`) n'est chargé qu'en local (contexte Liquibase `dev`, par défaut). Le profil `uat` utilise le contexte `uat` : la base UAT démarre vide. Pour changer de contexte en local : `LIQUIBASE_CONTEXTS`.

1. Connecté en `super.admin` :
   1. une **entreprise** (avec au moins un module, ex. RESTAURATION) ;
   2. un **commerce** (type = un module de l'entreprise) ;
   3. un **partenariat** entreprise ↔ commerce (actif) ;
   4. les **profils** (« Inviter un utilisateur ») avec exactement les usernames Keycloak : `enterprise.admin` et `enterprise.finance` → l'entreprise, `sales.admin` et `sales.finance` → le commerce.
2. Connecté en `enterprise.admin` : la **configuration entreprise** (plafonds par module) puis un **employé** avec le username `client.test`.
3. Connecté en `sales.admin` : la **configuration du point de vente** (montants min/max) puis un **caissier** avec le username `cashier.test`.

> Pour les comptes de test ci-dessus, le username saisi doit être identique au compte Keycloak existant : la fiche est alors simplement **rattachée** à ce compte (aucun e-mail). Pour tout autre utilisateur, le compte Keycloak est **créé automatiquement** (voir ci-dessous).

### Comptes de connexion créés automatiquement

Créer un employé, un caissier, un profil entreprise ou un profil commerce (back-office ou API) crée aussi son **compte Keycloak** :

- même username et e-mail que la fiche, rôle realm correspondant (`CLIENT`, `CASHIER`, `ENTERPRISE_ADMIN`/`ENTERPRISE_FINANCE`, `SALES_ADMIN`/`SALES_FINANCE`) ;
- **mot de passe temporaire envoyé par e-mail** à l'adresse de la fiche — l'utilisateur choisit son mot de passe définitif à la première connexion ;
- modification de l'e-mail, du prénom ou du nom → reportée dans Keycloak ; suppression (logique) → compte **désactivé** ; réactivation (`active: true`) → compte réactivé ;
- le **username et le rôle ne sont plus modifiables** après création (422) : ils relient la fiche à son compte et en fixent les droits ;
- un profil entreprise n'accepte que `ENTERPRISE_ADMIN`/`ENTERPRISE_FINANCE`, un profil commerce que `SALES_ADMIN`/`SALES_FINANCE` (422 sinon) ; un employé est toujours `CLIENT`, un caissier toujours `CASHIER` ;
- si un compte Keycloak existe déjà avec ce username : rattaché s'il a déjà ce rôle, sinon refus (409) — une création de fiche ne peut jamais élever les droits d'un compte existant ;
- si l'enregistrement en base échoue, le compte Keycloak créé est supprimé ; si seul l'e-mail échoue, la fiche et le compte sont conservés (erreur dans les logs) : réinitialiser alors le mot de passe depuis la console Keycloak (Users → Credentials → Reset password, *Temporary* activé).

#### Configurer l'envoi d'e-mails (Brevo, gratuit)

Render bloque le SMTP sur les services gratuits : l'envoi passe par l'**API HTTP de Brevo** (300 e-mails/jour gratuits).

1. Créer un compte sur [brevo.com](https://www.brevo.com).
2. **Senders, Domains & Dedicated IPs → Senders → Add a sender** : ajouter l'adresse d'expédition (ex. votre Gmail) et la valider via le lien reçu.
3. **SMTP & API → API Keys → Generate a new API key**.
4. Sur Render, service `onepay-backend-uat` → **Environment** : `BREVO_API_KEY` = la clé, `MAIL_FROM_EMAIL` = l'expéditeur validé à l'étape 2 (`MAIL_PROVIDER` vaut déjà `brevo`). Optionnel : `MAIL_LOGIN_URL` = URL du back-office indiquée dans l'e-mail.

Sans configuration (`MAIL_PROVIDER=none`, valeur par défaut en local), les comptes sont créés mais aucun e-mail n'est envoyé (le mot de passe temporaire n'est jamais écrit dans les logs).

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
| E-mail d'identifiants non reçu | Vérifier les spams, puis les logs backend (`Failed to send credentials e-mail`) : clé `BREVO_API_KEY` invalide ou `MAIL_FROM_EMAIL` non validé dans Brevo. Réinitialiser le mot de passe depuis la console Keycloak |
| 409 « Un compte de connexion existe déjà… » à la création | Un compte Keycloak porte déjà ce username (avec un autre rôle) ou cet e-mail : choisir un autre username/e-mail |
| Écran « rôle non pris en charge » / profil introuvable | Profil métier (employé, caissier, profil) absent ou username différent du compte Keycloak — voir étape 1 |
| `Invalid redirect uri` sur le login web | Front servi ailleurs que `http://localhost:4200` : ajouter l'URL dans le client `onepay-client` (console Keycloak) |
| 400 à la création d'un employé, caissier ou profil | Téléphone absent ou hors format sénégalais : 9 chiffres commençant par 7 (`771234567`, `77 123 45 67` ou `+221 77 123 45 67`) |
| 409 « … déjà utilisé par un autre compte » | Username, e-mail ou téléphone déjà pris par un compte, tous types confondus (employé, caissier, profils) |
| Le backend ne démarre plus après déploiement (Liquibase, `phonenumber` de `cashier`) | Un caissier existant n'a pas de téléphone : le renseigner (pgAdmin) puis redéployer |

## Règles métier à connaître (front et mobile)

### Cumul consommé : remis à zéro chaque mois
Le plafond par module (`EnterpriseConfiguration.maxAmount*`) est **mensuel**. Le contrôle fait à chaque paiement et `GET /v1/onepay/payment/sum/{clientId}` (total, ou `?module=`) ne comptent que les paiements actifs du **mois calendaire en cours, en UTC** (heure de Dakar), du 1er à 00:00 au 1er du mois suivant. Aucune donnée n'est supprimée : l'historique reste consultable avec `GET /v1/onepay/payment`.

À adapter côté front et mobile : les libellés qui affichent `/sum` (« Total consommé », jauges par module) désignent maintenant **la consommation du mois**.

### Factures générées automatiquement chaque mois
- Le **1er de chaque mois à 00:05 UTC**, le backend crée les factures du mois écoulé : **une par partenariat actif** (entreprise ↔ commerce) ayant des paiements. Le montant est la somme des paiements actifs des employés de l'entreprise chez les caissiers du commerce. La facture est créée avec le statut `UNPAYED`, la période `YYYY-MM` et des dates du 1er à 00:00 au dernier jour à 23:59:59.
- Un partenariat sans paiement sur le mois n'a pas de facture.
- La génération peut être relancée sans risque : une seule facture active par partenariat et par mois, garantie aussi en base. Une création manuelle (`POST /v1/onepay/bills`) qui ferait doublon reçoit **409**.
- Le planning est réglable avec `onepay.bills.cron` (expression cron Spring, en UTC).

**PDF de la facture (frontend)** : `GET /v1/onepay/bills/{id}/details` renvoie l'en-tête à imprimer (`ref`, `period`, `startDate`, `endDate`, `billStatus`, `totalAmount`, `enterprise` {`name`, `address`}, `sales` {`name`, `address`, `type`}). Le PDF est généré côté front.

### Paiement d'une facture : confirmé par les deux parties
Une facture n'a que deux statuts : `UNPAYED` et `PAYED`. Elle passe à **`PAYED` seulement quand l'entreprise ET le commerce du partenariat ont chacun confirmé le paiement**.

- `PUT /v1/onepay/bills/{id}/payment-confirmation` : l'appelant confirme pour sa partie.
  - **`ENTERPRISE_FINANCE` de l'entreprise du partenariat** : confirmation côté entreprise.
  - **`SALES_FINANCE` du commerce du partenariat** : confirmation côté commerce.
  - Toute autre personne reçoit **403** : autre organisation, admin, `SUPER_ADMIN`, profil inactif.
  - La facture renvoyée indique qui a confirmé et quand : `enterprisePaymentConfirmedBy`/`At` et `salesPaymentConfirmedBy`/`At`.
  - Confirmer deux fois ne change rien.
- `DELETE /v1/onepay/bills/{id}/payment-confirmation` : l'appelant retire sa confirmation, **tant que l'autre partie n'a pas confirmé**. Une fois la facture `PAYED`, plus de retour arrière (**422**).
- Une facture est toujours créée `UNPAYED`, par le job mensuel comme par `POST /bills` : le statut envoyé est ignoré. `PUT /bills/{id}` ne modifie plus le statut.
- À brancher côté `payment-frontend` : un bouton « Marquer comme payée » / « Annuler » par partie dans la liste et la fiche des factures, et l'affichage des deux confirmations.

### Import Excel des employés
`POST /v1/onepay/client/import` (multipart : `file`, et `enterpriseId` pour le SUPER_ADMIN) crée un employé (rôle `CLIENT`) par ligne d'un fichier **`.xlsx`**.

- **Modèle à générer côté front** : première feuille, ligne 1 = en-têtes exactement `Prénom | Nom | Username | Email | Téléphone` (casse indifférente), une ligne par employé à partir de la ligne 2. Les lignes vides sont ignorées. Le téléphone suit le format sénégalais (`771234567`, `77 123 45 67` ou `+221 77 123 45 67`).
- **Qui importe où** : un `ENTERPRISE_ADMIN` importe toujours dans **sa** propre entreprise (un `enterpriseId` envoyé est ignoré). Le `SUPER_ADMIN` doit fournir `enterpriseId` (422 sinon). Les autres rôles reçoivent 403.
- **Lignes valides importées, les autres signalées** : la réponse `200` contient `totalRows`, `created` [{`row`, `id`, `username`}] et `errors` [{`row`, `message`}]. `row` est le numéro de ligne Excel. Les mêmes règles qu'une création unitaire s'appliquent : champs obligatoires, format du téléphone, username/e-mail/téléphone uniques sur tous les comptes.
- Chaque employé créé reçoit son **e-mail d'invitation** (mot de passe temporaire), comme pour une création unitaire. Avec l'offre gratuite Brevo (300 e-mails/jour), les e-mails au-delà de la limite ne partent pas, mais les comptes sont bien créés.
- Fichier illisible, non `.xlsx` ou en-têtes incorrects : **400** avec un message.
- Taille maximale du fichier : 1 Mo (limite Spring par défaut, plusieurs milliers de lignes).

### Notifications de paiement (push Firebase + historique)
À chaque **paiement** et à chaque **annulation**, le client et le caissier reçoivent une notification. Elle est enregistrée en base, pour la cloche, et envoyée en **push** sur leurs téléphones, que l'app soit ouverte ou fermée.

| Destinataire | Paiement | Annulation |
|---|---|---|
| Client | « Paiement de 2 500 FCFA chez {commerce} » | « Paiement de 2 500 FCFA chez {commerce} annulé » |
| Caissier | « Paiement reçu : 2 500 FCFA de {prénom nom} ({entreprise}) » | « Paiement de 2 500 FCFA de {prénom nom} ({entreprise}) annulé » |

**API** (`/v1/onepay/notification`, utilisateur connecté, uniquement ses propres données) :
- `POST /device-token` `{"token": "<jeton FCM>", "platform": "ANDROID" | "IOS"}` : enregistrer l'appareil (204) ;
- `DELETE /device-token/{token}` : à la déconnexion (204) ;
- `GET` (paginé, plus récentes d'abord, `?read=false` pour les non lues) ;
- `GET /unread-count` : pastille de la cloche ;
- `PUT /{id}/read` : marquer comme lue (404 si la notification appartient à un autre utilisateur) ;
- `PUT /read-all` : tout marquer comme lu.

**À faire dans `mobile-app`** :
1. Ajouter `firebase_core` et `firebase_messaging`, puis configurer Firebase (`flutterfire configure`).
2. Après la connexion, et dans `onTokenRefresh`, appeler `POST /device-token` avec le jeton FCM et la plateforme.
3. À la déconnexion, appeler `DELETE /device-token/{token}`.
4. App ouverte : `FirebaseMessaging.onMessage` affiche la notification (bannière ou snackbar) et rafraîchit la cloche (`/unread-count`). App fermée ou en arrière-plan : le système affiche le push.
5. Le push contient `data.type`, `data.paymentId` et `data.notificationId`, pour ouvrir le bon écran au tap.

#### Activer le push (Firebase, gratuit)
1. Créer un projet sur [console.firebase.google.com](https://console.firebase.google.com), puis y ajouter l'app **Android** (`com.sn.onepay`) et l'app **iOS** (bundle `com.sn.onepay`).
2. **iOS** : dans *Paramètres du projet → Cloud Messaging*, téléverser une **clé APNs** (fichier `.p8`). Elle se crée dans un compte **Apple Developer, payant (99 $/an)**. Sans elle, le push ne fonctionne que sur Android.
3. *Paramètres du projet → Comptes de service → Générer une nouvelle clé privée* : un fichier JSON est téléchargé. **Ne jamais le committer.**
4. Sur Render (`onepay-backend-uat` → Environment) : coller le **contenu** du JSON dans `FIREBASE_CREDENTIALS` et passer `PUSH_PROVIDER` à `firebase`.

Sans configuration (`PUSH_PROVIDER=none`, le défaut), les notifications sont seulement enregistrées en base et l'envoi est juste journalisé. Avec `PUSH_PROVIDER=firebase` et `FIREBASE_CREDENTIALS` vide, le backend refuse de démarrer, avec un message explicite.
