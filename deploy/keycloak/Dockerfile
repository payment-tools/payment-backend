# Keycloak UAT (Render) — realm onepay + thème de connexion
FROM quay.io/keycloak/keycloak:23.0.7 AS builder
ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
# Instance unique (plan gratuit) : pas de cluster Infinispan
ENV KC_CACHE=local
COPY themes/onepay /opt/keycloak/themes/onepay
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:23.0.7
COPY --from=builder /opt/keycloak/ /opt/keycloak/
COPY realm/onepay-realm.json /opt/keycloak/data/import/onepay-realm.json
COPY CreateSchema.java /opt/keycloak/bin/CreateSchema.java
COPY --chmod=755 entrypoint.sh /opt/keycloak/bin/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/opt/keycloak/bin/entrypoint.sh"]
