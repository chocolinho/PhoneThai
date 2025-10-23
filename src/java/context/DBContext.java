package context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {

    protected Connection connection;

    public DBContext() {
        try {
            
            String url = "jdbc:mysql://localhost:3306/PhoneThaiDB";
            String user = "root"; 
            String pass = "";     
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
      
        DBContext db = new DBContext();
        if (db.connection != null) {
            System.out.println("Kết nối CSDL thành công!");
        } else {
            System.out.println("Kết nối CSDL thất bại.");
        }
    }
}