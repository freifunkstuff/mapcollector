package de.ffle.mapcollector.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DataHelper {
	
	public static Long parseNBytes(String nBytes) {
		if (nBytes==null) {
			return null;
		}
		String[] bytesAndUnit=nBytes.trim().split("\\s+",2);
		long bytes=Long.parseLong(bytesAndUnit[0]);
		
		if (bytesAndUnit.length>1) {
			switch (bytesAndUnit[1].toLowerCase()) {
				case "b":
					break;
				case "kb":
					bytes=bytes * 1024;
					break;
				case "mb":
					bytes=bytes * 1024 * 1024;
					break;
				case "gb":
					bytes=bytes * 1024 * 1024 *1024;
					break;
				default:
					throw new IllegalArgumentException("Unimplemented unit: "+bytesAndUnit[1].toLowerCase());
			}
		}
		return bytes;
	}
	
	public static String encodeToString(ZonedDateTime dateTime) {
		if (dateTime==null) {
			return null;
		}
		return dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
	
	public static ZonedDateTime decodeZonedDateTime(String dateTime) {
		if (dateTime==null) {
			return null;
		}
		return ZonedDateTime.parse(dateTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	public static String encodeToString(Enum<?> e) {
		if (e==null) {
			return null;
		}
		return e.name();
	}

	public static <T extends Enum<T>> T decodeEnum(Class<T> type, String value) {
		if (value==null) {
			return null;
		}
		return Enum.valueOf(type, value);
	}

	public static String encodeToString(Boolean value) {
		if (value==null) {
			return null;
		}
		return value.booleanValue()?"1":"0";
	}

	public static Boolean decodeBoolean(String value) {
		if (value==null) {
			return null;
		}
		switch (value.toLowerCase()) {
			case "1":
			case "true":
				return true;
			case "0":
			case "false":
				return false;
		}
		throw new IllegalArgumentException("Invalid boolean value: "+value);
	}

	public static String encodeToString(Double value) {
		if (value==null) {
			return null;
		}
		return String.valueOf(value.doubleValue());
	}

	public static Double decodeDouble(String value) {
		if (value==null) {
			return null;
		}
		return Double.parseDouble(value);
	}

	public static String encodeToString(Integer value) {
		if (value==null) {
			return null;
		}
		return String.valueOf(value.intValue());
	}

	public static Integer decodeInteger(String value) {
		if (value==null) {
			return null;
		}
		return Integer.parseInt(value);
	}
	
}
