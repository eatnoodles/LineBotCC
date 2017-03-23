/**
 * 
 */
package com.cc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;
import com.cc.service.INudoCCService;
import com.cc.service.IWowCharacterProfileService;

/**
 * @author Caleb-2109
 *
 */
@Component
public class NudoCCServiceImpl implements INudoCCService {

	@Autowired
	private IWowCharacterProfileService wowCharacterProfileService;
	
	private String[] realms = new String[]{"阿薩斯", "狂熱之刃", "地獄吼"};
	
	@Override
	public String findWowCharacterProfile(String name, String server) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		paramBean.setRealm(server);
		try {
			WowCharacterProfileResponse resp = wowCharacterProfileService.doSend(paramBean);
			return String.format("群組: %s, 等級: %s級", resp.getBattlegroup(), resp.getLevel());
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public String findWowCharacterProfileByName(String name) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		for (String realm : realms) {
			paramBean.setRealm(realm);
			try {
				WowCharacterProfileResponse resp = wowCharacterProfileService.doSend(paramBean);
				return String.format("群組: %s, 伺服器: %s, 等級: %s級", resp.getBattlegroup(), resp.getRealm(), resp.getLevel());
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}
}
