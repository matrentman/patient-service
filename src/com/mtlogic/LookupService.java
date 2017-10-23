package com.mtlogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookupService {
	final Logger logger = LoggerFactory.getLogger(LookupService.class);
	
	public String lookup(String key) {
		logger.info(">>>ENTERED lookup(" + key + ")");
		Context envContext = null;
		Connection con = null;
		PreparedStatement preparedStatement = null;
		
		String selectMessageSQL = "select value from lookup where key = ?";
		String value = "";
		
		try {
			envContext = new InitialContext();
			Context initContext  = (Context)envContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)initContext.lookup("jdbc/claimstatus");
			//DataSource ds = (DataSource)envContext.lookup("java:/comp/env/jdbc/claimstatus");
			con = ds.getConnection();
						
			preparedStatement = con.prepareStatement(selectMessageSQL);
			preparedStatement.setString(1, key);
			
			ResultSet rs = preparedStatement.executeQuery();
			
			if (rs.next()) {
				value = rs.getString("value");
			}		
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("ERROR!!! : " + e.getMessage());
		} catch (NamingException e) {
			e.printStackTrace();
			logger.error("ERROR!!! : " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR!!! : " + e.getMessage());
		} finally {
		    try{preparedStatement.close();}catch(Exception e){};
		    try{con.close();}catch(Exception e){};
		}
		logger.info("<<<EXITED storeMessage(" + key + ")");
		return value;
	}
}
