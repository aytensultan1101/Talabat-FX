import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens JDBC connections to the MySQL database.
 *
 * <p>Settings are resolved in this order (first match wins):
 * <ol>
 *   <li>environment variables {@code TALABAT_DB_URL}, {@code TALABAT_DB_USER}, {@code TALABAT_DB_PASSWORD}</li>
 *   <li>a {@code db.properties} file in the working directory (see {@code db.properties.example})</li>
 *   <li>built-in defaults: local MySQL, database {@code talabat_db}, user {@code root}, empty password</li>
 * </ol>
 */
public final class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/talabat_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";
    private static final Path CONFIG_FILE = Path.of("db.properties");

    private static final Properties CONFIG = loadConfig();

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                setting("TALABAT_DB_URL", "db.url", DEFAULT_URL),
                setting("TALABAT_DB_USER", "db.user", DEFAULT_USER),
                setting("TALABAT_DB_PASSWORD", "db.password", DEFAULT_PASSWORD));
    }

    private static String setting(String envVar, String key, String fallback) {
        String value = System.getenv(envVar);
        return value != null ? value : CONFIG.getProperty(key, fallback);
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        if (Files.isRegularFile(CONFIG_FILE)) {
            try (Reader in = Files.newBufferedReader(CONFIG_FILE)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Could not read " + CONFIG_FILE.toAbsolutePath() + ": " + e.getMessage());
            }
        }
        return props;
    }
}
