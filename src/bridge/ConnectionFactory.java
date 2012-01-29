package bridge;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {
	private Connection con;
	private static final String DB_CONNECTIONSTRING = "jdbc:mysql://localhost:3306/world";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "admin";

	public Connection getConnection() {
		if (con != null) {
			return con;
		} else {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(DB_CONNECTIONSTRING,
						DB_USERNAME, DB_PASSWORD);
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Exception Occured: " + ex);
			}
			return con;
		}
	}
}
