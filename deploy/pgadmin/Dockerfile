# pgAdmin UAT (Render) — accès à la base par le réseau privé Render, sans IP à autoriser
FROM dpage/pgadmin4:8.12
COPY --chmod=755 entrypoint.sh /render-entrypoint.sh
ENTRYPOINT ["/render-entrypoint.sh"]
