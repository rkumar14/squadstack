package com.squadstack.executor.abstraction;

import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.stereotype.Component;

@Component
public interface DBConnection {

	public Connection getConnection() throws SQLException ;
	
	public void closeConnection(Connection conn) throws SQLException;
}
