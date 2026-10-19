# pgAdmin UAT (Render) — accès à la base par le réseau privé Render, sans IP à autoriser
FROM dpage/pgadmin4:8.12
USER root
# Render lance les conteneurs avec « no new privileges » : un binaire portant une capability
# (ici cap_net_bind_service, pour écouter sur le port 80) n'y est plus exécutable.
# On retire la capability et pgAdmin écoute sur un port non privilégié.
RUN setcap -r "$(readlink -f /venv/bin/python3)"
USER pgadmin
ENV PGADMIN_LISTEN_PORT=8080
COPY --chmod=755 entrypoint.sh /render-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/render-entrypoint.sh"]
