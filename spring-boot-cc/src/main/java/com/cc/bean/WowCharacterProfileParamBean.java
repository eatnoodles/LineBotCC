package com.cc.bean;

import com.cc.enums.WowProfileFieldEnum;
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
	
	/**
	 * fields
	 */
	private WowProfileFieldEnum fields = WowProfileFieldEnum.NULL;
	
	@Override
	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(getWowServer()).append(SERVICE).append(NudoCCUtil.SLASH)
		  .append(realm).append(NudoCCUtil.SLASH)
		  .append(characterName).append(NudoCCUtil.QUESTION_MARK);
		
		if (fields != WowProfileFieldEnum.NULL) {
			sb.append("fields=").append(fields.getContext()).append("&");
		}
		
		sb.append("locale=").append(getLocale()).append("&apikey=").append(getApikey());
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

	public WowProfileFieldEnum getFields() {
		return fields;
	}

	public void setFields(WowProfileFieldEnum fields) {
		this.fields = fields;
	}

}
