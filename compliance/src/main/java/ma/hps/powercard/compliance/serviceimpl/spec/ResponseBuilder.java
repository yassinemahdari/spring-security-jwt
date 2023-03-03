package ma.hps.powercard.compliance.serviceimpl.spec;

import java.util.Map;

import javax.ws.rs.core.Response;

import com.google.gson.JsonObject;

public class ResponseBuilder {

	public static Response createResponse( Response.Status status  ) {
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty( "message", status.toString() );
		return Response.status( status ).entity( jsonObject.toString() ).build();
	}

	public static Response createResponse( Response.Status status, String message ) {
		
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty( "message", message ); 
		return Response.status( status ).entity( jsonObject.toString() ).build();
	}

	public static Response createResponse( Response.Status status, Map<String,Object> map ) {
		
		JsonObject jsonObject = new JsonObject();
		for( Map.Entry<String,Object> entry : map.entrySet() ) {
			jsonObject.addProperty( entry.getKey(), (String) entry.getValue() );
		}
		return Response.status( status ).entity( jsonObject.toString() ).build();
	}

	/*
	public static Response createResponse( Response.Status status, JsonSerializable json ) throws JSONException {
		return Response.status( status ).entity( json.toJson().toString() ).build();
	}

	public static Response createResponse( Response.Status status, List<JsonSerializable> json ) throws JSONException {
		JSONArray jsonArray = new JSONArray();

		for( int i = 0; i < json.size(); i++ ) {
			jsonArray.put( json.get(i).toJson() );
		}

		return Response.status( status ).entity( jsonArray.toString() ).build();
	}
	 */
}
