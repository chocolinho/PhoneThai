package context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3307/PhoneThaiDB?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    protected Connection connection;

    public DBContext() {
        try {
            String url = config("db.url", "DB_URL", DEFAULT_URL);
            String user = config("db.user", "DB_USER", DEFAULT_USER);
            String pass = config("db.password", "DB_PASSWORD", DEFAULT_PASSWORD);

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot connect to database", ex);
        }
    }

    private String config(String propertyName, String envName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(envName);
        }
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static void main(String[] args) {
        DBContext db = new DBContext();
        if (db.connection != null) {
            System.out.println("Database connection successful.");
        } else {
            System.out.println("Database connection failed.");
        }
    }
}
