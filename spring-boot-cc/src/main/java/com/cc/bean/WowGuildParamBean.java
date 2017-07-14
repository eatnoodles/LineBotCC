package com.cc.bean;

import com.cc.enums.WowGuildEnum;
import com.utils.NudoCCUtil;

public class WowGuildParamBean extends BaseWOWParamBean {

	private static final String SERVICE = "guild";
	
	/**
	 * server name
	 */
	private String realm;
	
	
	/**
	 * server itemId
	 */
	private String guild;
	
	private WowGuildEnum fields = WowGuildEnum.NEWS;
	
	@Override
	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(getWowServer()).append(SERVICE).append(NudoCCUtil.SLASH)
		  .append(realm).append(NudoCCUtil.SLASH).append(guild).append(NudoCCUtil.QUESTION_MARK);
		
		sb.append("fields=").append(fields.getContext()).append("&");
		
		sb.append("locale=").append(getLocale()).append("&apikey=").append(getApikey());
		
		return sb.toString();
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getGuild() {
		return guild;
	}

	public void setGuild(String guild) {
		this.guild = guild;
	}

	public WowGuildEnum getFields() {
		return fields;
	}

	public void setFields(WowGuildEnum fields) {
		this.fields = fields;
	}


}
