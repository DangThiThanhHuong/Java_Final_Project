package server;

import java.sql.*;

public class UserDB {
	public static void main(String[] args) {
		final String DB_URL = "jdbc:derby:UserDB;create=true";
		 try
	      {
	         // Create a connection to the database.
	         Connection conn =
	                DriverManager.getConnection(DB_URL);

						 
				// If the DB already exists, drop the tables.
				dropTable(conn);
				
				// Build the Coffee table.
				buildUsersTable(conn);

	         // Close the connection.
	         conn.close();
	      }
	      catch (Exception ex)
	      {
	         System.out.println("ERROR1: " + ex.getMessage());
	      }
	}
	public static void dropTable(Connection conn)
	{
		System.out.println("Checking for existing tables.");
		
		try
		{
			// Get a Statement object.
			Statement stmt  = conn.createStatement();;

			try
			{
	         // Drop the UnpaidOrder table.
	         stmt.execute("DROP TABLE Users");
				System.out.println("UnpaidOrder table dropped.");
			}
			catch(SQLException ex)
			{
				// No need to report an error.
				// The table simply did not exist.
			}

		}
  		catch(SQLException ex)
		{
	      System.out.println("ERROR2: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static void buildUsersTable(Connection conn)
	{
		try
		{
         // Get a Statement object.
         Statement stmt = conn.createStatement();
         
			// Create the table.
			stmt.execute("CREATE TABLE Users (" +
                      "UserName CHAR(20) NOT NULL PRIMARY KEY, " +
                      "Password CHAR(20) NOT NULL, " +
                      "Score Integer " +
                      ")");
							 
			// Insert row #1.
			stmt.execute("INSERT INTO Users VALUES ( " +
                      "'tuan', " +
                      "'tuan', " +
                      "0)" );
			// Insert row #2.
			stmt.execute("INSERT INTO Users VALUES ( " +
                    "'huong', " +
                    "'huong', " +
                    "0)" );

			
			System.out.println("Users table created.");
		}
		catch (SQLException ex)
      {
         System.out.println("ERROR3: " + ex.getMessage());
      }
	}

}
