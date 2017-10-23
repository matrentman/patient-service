package com.mtlogic;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api")
public class StorePatientData {
	final Logger logger = LoggerFactory.getLogger(StorePatientData.class);
	
	@Path("/storePatientMetaData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response storePatientMetaData(String payload) throws JSONException 
	{	
		logger.info(">>>ENTERED storePatientMetaData()");
		
		Response response = null;
		int patientIdentifier = -1;
		StorePatientDataService eligibilityService = null;
		int responseCode = 200;
		
		try {
			final JSONObject obj = new JSONObject(payload);
			String dataSource = obj.getString("dataSource");
			String subscriberIdentifier = obj.getString("subscriberId");
			String firstName = obj.getString("firstName");
			String lastName = obj.getString("lastName");
			String dateOfBirth = obj.getString("dateOfBirth");
			String qualifier = obj.getString("qualifier");
			Boolean dependent = obj.getBoolean("dependent");
			eligibilityService = new StorePatientDataService();
			patientIdentifier = eligibilityService.storePatientInfo(dataSource, subscriberIdentifier, firstName, lastName, dateOfBirth, qualifier, dependent);
		} catch (Exception e) {
			logger.error("Message could not be processed: " + e.getMessage());
			e.printStackTrace();
			response = Response.status(422).entity("Message could not be processed: " + e.getMessage()).build();
		}
		
		if (response == null) {
			response = Response.status(responseCode).entity(patientIdentifier).build();
		}
		
		logger.info("<<<EXITED storePatientMetaData()");
		return response;
	}
	
}