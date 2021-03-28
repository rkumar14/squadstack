package com.squadstack.executor.abstraction;

import java.sql.Connection;
import java.sql.SQLException;

public interface GenericStatus {

	public void parkingLotstatusGeneric(String input,Connection conn) throws SQLException ;
	public void dropInMemoryTable(String dbName,Connection conn) throws SQLException ;
	
}
