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

public class StorePatientDataService {
	final Logger logger = LoggerFactory.getLogger(StorePatientDataService.class);
	
	public static final String ELIGIBILITY_QA = "eligibility-qa";
	public static final String ELIGIBILITY_PROD = "eligibility-prod";
	
	public StorePatientDataService() {
		super();
	}

	public int storePatientInfo(String dataSource, 
			                    String subscriberId, 
			                    String firstName, 
			                    String lastName, 
			                    String dateOfBirth, 
			                    String qualifier,
			                    Boolean dependent) {
		logger.info(">>>ENTERED storePatientInfo()");
		
		Context envContext = null;
		Connection con = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		int patientIdentifier = -1;
		
		String insertMessageSQL = "insert into public.patient (subscriber_id, first_name, last_name, dob, identification_code_qualifier, dependent) values(?, ?, ?, ?, ?, ?)"; 
		String selectMessageSQL = "select patient_id from public.patient where subscriber_id = ? and first_name = ? and last_name = ? and dob = ? and identification_code_qualifier = ? and dependent = ?";
		
		try {
			envContext = new InitialContext();
			Context initContext  = (Context)envContext.lookup("java:/comp/env");
			DataSource ds = null;
			if (ELIGIBILITY_QA.equalsIgnoreCase(dataSource)) {
			    ds = (DataSource)initContext.lookup("jdbc/eligibility-qa");
			} else if (ELIGIBILITY_PROD.equalsIgnoreCase(dataSource)) {
				ds = (DataSource)initContext.lookup("jdbc/eligibility-prod");
			} else {
				ds = (DataSource)initContext.lookup("jdbc/claimstatus");
			}
			con = ds.getConnection();
			
			preparedStatement = con.prepareStatement(selectMessageSQL);
			preparedStatement.setString(1, subscriberId.toString());
			preparedStatement.setString(2, firstName.toString());
			preparedStatement.setString(3, lastName.toString());
			preparedStatement.setString(4, dateOfBirth.toString());
			preparedStatement.setString(5, qualifier.toString());
			preparedStatement.setBoolean(6, dependent);
			
			rs1 = preparedStatement.executeQuery();
			
			if (rs1.next()) {
				patientIdentifier = rs1.getInt("patient_id");
			} else {
				PreparedStatement insertStatement = null;
				try {
					insertStatement = con.prepareStatement(insertMessageSQL);
					insertStatement.setString(1, subscriberId.toString());
					insertStatement.setString(2, firstName.toString());
					insertStatement.setString(3, lastName.toString());
					insertStatement.setString(4, dateOfBirth.toString());
					insertStatement.setString(5, qualifier.toString());
					insertStatement.setBoolean(6, dependent);
					
					insertStatement.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("ERROR!!! : " + e.getMessage());
				} finally {
				    try{insertStatement.close();}catch(Exception e){};
				}
				
				PreparedStatement selectStatement = null;
				try {
					selectStatement = con.prepareStatement(selectMessageSQL);
					selectStatement.setString(1, subscriberId.toString());
					selectStatement.setString(2, firstName.toString());
					selectStatement.setString(3, lastName.toString());
					selectStatement.setString(4, dateOfBirth.toString());
					selectStatement.setString(5, qualifier.toString());
					selectStatement.setBoolean(6, dependent);
					
					rs2 = selectStatement.executeQuery();
					
					if (rs2.next()) {
						patientIdentifier = rs2.getInt("patient_id");
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("ERROR!!! : " + e.getMessage());
				} finally {
					try{rs2.close();}catch(Exception e){};
				    try{selectStatement.close();}catch(Exception e){};
				}
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
			try{rs1.close();}catch(Exception e){};
		    try{preparedStatement.close();}catch(Exception e){};
		    try{con.close();}catch(Exception e){};
		}
		
		logger.info("<<<EXITED storePatientInfo()");
		return patientIdentifier;
	}
	
}
