package com.cc.bean;

import com.utils.NudoCCUtil;

public class WowItemParamBean extends BaseWOWParamBean {

	private static final String SERVICE = "item";
	
	/**
	 * server itemId
	 */
	private String itemId;
	
	@Override
	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(getWowServer()).append(SERVICE).append(NudoCCUtil.QUESTION_MARK).append(itemId).append("&");
		sb.append("locale=").append(getLocale()).append("&apikey=").append(getApikey());
		return sb.toString();
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

}
