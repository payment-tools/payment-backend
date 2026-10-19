import java.sql.DriverManager;

/**
 * Crée le schéma Keycloak (KC_DB_SCHEMA) s'il n'existe pas : Keycloak partage la base
 * Postgres UAT avec payment-backend mais ne crée pas son schéma lui-même.
 */
public class CreateSchema {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://" + System.getenv("KC_DB_URL_HOST") + ":"
                + System.getenv().getOrDefault("KC_DB_URL_PORT", "5432") + "/" + System.getenv("KC_DB_URL_DATABASE");
        String schema = System.getenv("KC_DB_SCHEMA");
        if (!schema.matches("[a-z_][a-z0-9_]*")) throw new IllegalArgumentException("KC_DB_SCHEMA invalide : " + schema);
        try (var conn = DriverManager.getConnection(url, System.getenv("KC_DB_USERNAME"), System.getenv("KC_DB_PASSWORD"));
             var stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schema);
        }
        System.out.println("Schéma Keycloak prêt : " + schema);
    }
}
