package cz.fs.proto1;

public class ConfigUtil {
	
	public static String getConfig(String key) {
		String prop = System.getProperty(key);
		
		// try system environment as well
		if(prop == null) {
			prop = System.getenv(key);
		}
		
		if(prop == null) {
			String msg = String.format("Property %s is null! Please run configuration script.", key);
			throw new NullPointerException(msg);
		}
		return prop;
	}
	
}
