package com.mars.azure;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class ZoomLevelWizzard extends MSSQLSource{
	/**
	 * @throws IOException
	 * @throws SQLException
	 */
	
	private int bestCountMin = 6;
	private int bestCountMax = 16;
	
	public ZoomLevelWizzard() throws IOException {
		super();
		
		bestCountMin = Integer.valueOf(properties.getProperty("best.count.min"));
		bestCountMax = Integer.valueOf(properties.getProperty("best.count.max"));
	}
	
	@FunctionName("zoomlevel")
	public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
			HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context) throws IOException, SQLException {

		context.getLogger().info("Java HTTP trigger processed a request.");

		String lat = request.getQueryParameters().get("latitude");
		String longi = request.getQueryParameters().get("longitude");
		
		if (lat == null || longi == null) 
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
				.body("Please pass a latitude and longitude as query parameters").build();
		
		final Double latitude;
		final Double longitude;
		// Parse query parameters
		try {
			latitude = Double.valueOf(lat);
			longitude = Double.valueOf(longi);
		} catch (NumberFormatException e) {
			return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
					.body("Please pass a latitude and longitude as correct numbers").build();
		}
		
		//1D is for 1 mile as a starting delta
		//in zoom params there is logic for adjusting long degrees to lat
	
		ZoomLevelResponse zr = execZoom(latitude, longitude, bestCountMin, bestCountMax, 1D);
		
		return request.createResponseBuilder(HttpStatus.OK).body(zr).build();

	}
	
	private ZoomLevelResponse execZoom(Double latitude, Double longitude, int min, int max, Double startingMileDelta) throws SQLException {
		
		StringBuilder statement = new StringBuilder("exec zoom ")
				.append(Double.toString(latitude)).append(",")
				.append(Double.toString(longitude)).append(",")
				.append(Integer.toString(min)).append(",")
				.append(Integer.toString(max)).append(",")
				.append(Double.toString(startingMileDelta)).append(";");
		
		log.info("Statement: "+statement.toString());
		
		PreparedStatement readStatement = cpds.getConnection().prepareStatement(statement.toString());
		ResultSet resultSet = readStatement.executeQuery();
		resultSet.next();
		ZoomLevelResponse zlr = new ZoomLevelResponse();
		zlr.setCount(resultSet.getInt("doors_count"));
		zlr.setSquareSideInMiles(resultSet.getDouble("square_size_in_miles"));
		zlr.setDefaultZoomLevel(getZoomLevel(zlr.getSquareSideInMiles(),zlr.getCount()));
		return zlr;
	}
	
	private int getZoomLevel(Double squareSideInMiles, int count) {
		DecimalFormat df = new DecimalFormat( "##0.000" );
		String square = df.format(squareSideInMiles);
		log.info("squareSideInMiles: "+square);
		
		//if square size goes beyond our configuration matrix get min or max zoom level 
		if (squareSideInMiles < Double.valueOf(properties.getProperty("square.min")))
			return  Integer.valueOf(properties.getProperty("zoom.level.max"));
		
		if (squareSideInMiles > Double.valueOf(properties.getProperty("square.max")))
			return  Integer.valueOf(properties.getProperty("zoom.level.min"));
		
		//if count is in upper half get upper half zoom level, otherwise get lower half zoom
		if (count > (bestCountMax - (bestCountMax - bestCountMin)/2))
			return Integer.valueOf(properties.getProperty("zoom.level.for."+square+".upper.half"));
		else
			return Integer.valueOf(properties.getProperty("zoom.level.for."+square+".lower.half"));
	}
	
}
