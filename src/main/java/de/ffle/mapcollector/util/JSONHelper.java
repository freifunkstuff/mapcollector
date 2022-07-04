package de.ffle.mapcollector.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONHelper {
	
	protected static final ObjectMapper om=new ObjectMapper()
			.setSerializationInclusion(Include.NON_NULL)
			.findAndRegisterModules();
	
	public static String toJson(Object o) {
		try {
			return om.writeValueAsString(o);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static <T> T fromJson(Class<T> type, String json) {
		if (json==null) {
			return null;
		}
		try {
			return om.readValue(json, type);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	

}
