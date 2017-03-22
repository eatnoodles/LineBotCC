package com.cc.bean;

import com.utils.NudoCCUtil;

/**
 * 
 * @author Caleb.Cheng
 *
 */
public class WowCharacterProfileParamBean extends BaseWOWParamBean{

	private static final String SERVICE = "character";
	
	/**
	 * server name
	 */
	private String realm;
	
	/**
	 * character name
	 */
	private String characterName;
	
	@Override
	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(getWowServer()).append(SERVICE).append(NudoCCUtil.SLASH)
		  .append(realm).append(NudoCCUtil.SLASH)
		  .append(characterName).append(NudoCCUtil.QUESTION_MARK)
		  .append("locale=").append(getLocale()).append("&apikey=").append(getApikey());
		return sb.toString();
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

}
