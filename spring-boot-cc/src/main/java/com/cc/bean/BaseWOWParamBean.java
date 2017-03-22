/**
 * 
 */
package com.cc.bean;

/**
 * @author Caleb.Cheng
 *
 */
public abstract class BaseWOWParamBean {

	private String wowServer = "https://tw.api.battle.net/wow/";
	
	private String locale = "zh_TW";

	private String apikey = System.getenv("WOWApiKey");
	
	public String getWowServer() {
		return wowServer;
	}
	
	protected String getLocale() {
		return locale;
	}

	protected String getApikey() {
		return apikey;
	}

	public abstract String getUrl();
}
