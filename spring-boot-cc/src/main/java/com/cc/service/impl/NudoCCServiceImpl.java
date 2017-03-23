/**
 * 
 */
package com.cc.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;
import com.cc.enums.WowClassEnum;
import com.cc.enums.WowRaceEnum;
import com.cc.service.INudoCCService;
import com.cc.service.IWowCharacterProfileService;
import com.linecorp.bot.model.message.TextMessage;
import com.utils.NudoCCUtil;

/**
 * @author Caleb-2109
 *
 */
@Component
public class NudoCCServiceImpl implements INudoCCService {

	@Autowired
	private IWowCharacterProfileService wowCharacterProfileService;
	
	@Override
	public String findWowCharacterProfile(String name, String server) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		paramBean.setRealm(server);
		try {
			WowCharacterProfileResponse resp = wowCharacterProfileService.doSend(paramBean);
			if (StringUtils.isBlank(resp.getName())) {
				return null;
			}
			String race = WowRaceEnum.getEnumByValue(resp.getRace()).getContext();
			String clz = WowClassEnum.getEnumByValue(resp.getClz()).getContext();
			
			return String.format("群組: %s, 等級: %s級的<%s>是一隻%s%s，他殺了%s個人、有%s成就點數！",
					resp.getBattlegroup(), resp.getLevel(), resp.getName(), race, clz, resp.getTotalHonorableKills(), resp.getAchievementPoints());
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public String findWowCharacterProfileByName(String name) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		for (String realm : NudoCCUtil.REALMS) {
			paramBean.setRealm(realm);
			try {
				WowCharacterProfileResponse resp = wowCharacterProfileService.doSend(paramBean);
				if (StringUtils.isBlank(resp.getName())) {
					return null;
				}
				String race = WowRaceEnum.getEnumByValue(resp.getRace()).getContext();
				String clz = WowClassEnum.getEnumByValue(resp.getClz()).getContext();
				
				return String.format("群組: %s, 等級: %s級的<%s>是一隻%s%s，他殺了%s個人、有%s成就點數！",
						resp.getBattlegroup(), resp.getLevel(), resp.getName(), race, clz, resp.getTotalHonorableKills(), resp.getAchievementPoints());
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}

	@Override
	public String findWowCharacterImgPath(String name) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		for (String realm : NudoCCUtil.REALMS) {
			paramBean.setRealm(realm);
			try {
				WowCharacterProfileResponse resp = wowCharacterProfileService.doSend(paramBean);
				if (StringUtils.isBlank(resp.getName())) {
					return null;
				}
				return NudoCCUtil.WOW_IMG_BASE_PATH.concat(resp.getThumbnail());
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}
}
