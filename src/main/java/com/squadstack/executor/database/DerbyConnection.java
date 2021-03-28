package com.squadstack.executor.database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.springframework.stereotype.Component;

import com.squadstack.executor.abstraction.DBConnection;

@Component
public class DerbyConnection implements DBConnection{

	 @Override
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
    	 Driver derbyEmbeddedDriver = new EmbeddedDriver();
         DriverManager.registerDriver(derbyEmbeddedDriver);
         Connection conn = DriverManager.getConnection("jdbc:derby:testdb1;create=true", "pass123",null);
         conn.setAutoCommit(true);
         return conn;
	}


	@Override
	public void closeConnection(Connection conn) throws SQLException {
		// TODO Auto-generated method stub
		conn.close();
	}
    
    

}
