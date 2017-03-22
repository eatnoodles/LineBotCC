/**
 * 
 */
package com.cc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Caleb-2109
 *
 */
public class DataCache {

	public static Map<String, String> cacheMap = new HashMap<>();
	static {
		ResourceBundle resource = ResourceBundle.getBundle("system");
		Enumeration<String> resourceKeys = resource.getKeys();
		while (resourceKeys.hasMoreElements()) {
			String key = resourceKeys.nextElement();
			String value = resource.getString(key);
			cacheMap.put(key, value);
		}
	}
	
	public static void setCataCache(String key, String value) {
		cacheMap.put(key, value);
	}
	
	public static void main(String[] args){
		System.out.println(cacheMap.values());
	}
	
}
