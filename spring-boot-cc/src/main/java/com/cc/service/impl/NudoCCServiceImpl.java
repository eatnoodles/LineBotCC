/**
 * 
 */
package com.cc.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.bean.WowCharacterProfileItemResponse;
import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;
import com.cc.bean.WowCommandBean;
import com.cc.enums.WowClassEnum;
import com.cc.enums.WowEventEnum;
import com.cc.enums.WowProfileFieldEnum;
import com.cc.enums.WowRaceEnum;
import com.cc.service.INudoCCService;
import com.cc.service.IWowCharacterProfileService;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.utils.NudoCCUtil;

/**
 * @author Caleb-2109
 *
 */
@Component
public class NudoCCServiceImpl implements INudoCCService {

	@Autowired
	private IWowCharacterProfileService wowCharacterProfileService;
	
	/**
	 * 以name、server搜尋角色基本資料
	 * 
	 * @param name :角色名稱
	 * @param server :伺服器名稱
	 * @return
	 */
	@Override
	public TextMessage findWowCharacterProfile(String name, String server) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		paramBean.setRealm(server);
		try {
			WowCharacterProfileResponse resp = wowCharacterProfileService.doSendProfile(paramBean);
			if (StringUtils.isBlank(resp.getName())) {
				return null;
			}
			String race = WowRaceEnum.getEnumByValue(resp.getRace()).getContext();
			String clz = WowClassEnum.getEnumByValue(resp.getClz()).getContext();
			
			return new TextMessage(String.format("群組: %s, 等級: %s級的<%s>是一隻%s%s，他殺了%s個人、有%s成就點數！",
					resp.getBattlegroup(), resp.getLevel(), resp.getName(), race, clz, resp.getTotalHonorableKills(), resp.getAchievementPoints()));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 以name搜尋角色基本資料
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	@Override
	public TextMessage findWowCharacterProfileByName(String name) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		for (String realm : NudoCCUtil.REALMS) {
			paramBean.setRealm(realm);
			try {
				WowCharacterProfileResponse resp = wowCharacterProfileService.doSendProfile(paramBean);
				if (StringUtils.isBlank(resp.getName())) {
					return null;
				}
				String race = WowRaceEnum.getEnumByValue(resp.getRace()).getContext();
				String clz = WowClassEnum.getEnumByValue(resp.getClz()).getContext();
				
				return new TextMessage(String.format("群組: %s, 等級: %s級的<%s>是一隻%s%s，他殺了%s個人、有%s成就點數！",
						resp.getBattlegroup(), resp.getLevel(), resp.getName(), race, clz, resp.getTotalHonorableKills(), resp.getAchievementPoints()));
				
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}
	
	/**
	 * 以name搜尋角色大頭照
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	@Override
	public ImageMessage findWowCharacterImgPath(String name) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		for (String realm : NudoCCUtil.REALMS) {
			paramBean.setRealm(realm);
			try {
				WowCharacterProfileResponse resp = wowCharacterProfileService.doSendProfile(paramBean);
				if (StringUtils.isBlank(resp.getThumbnail())) {
					return null;
				}
				String imgPath = NudoCCUtil.WOW_IMG_BASE_PATH.concat(resp.getThumbnail());
				return new ImageMessage(imgPath, imgPath);
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}
	
	/**
	 * 處理前端傳來的wow命令列成bean
	 * 
	 * @param command :命令列
	 * @return
	 */
	@Override
	public WowCommandBean processCommand(String command) {
		if (StringUtils.isBlank(command) || !command.startsWith(NudoCCUtil.WOW_COMMAND)) {
			return null;
		}
		command = command.replaceAll(NudoCCUtil.WOW_COMMAND, StringUtils.EMPTY).trim();
		WowCommandBean bean = new WowCommandBean();
		String name = null;
		if (command.startsWith(NudoCCUtil.WOW_COMMAND_IMG)) {
			bean.setEventEnum(WowEventEnum.IMG);
			name = command.replaceAll(NudoCCUtil.WOW_COMMAND_IMG, StringUtils.EMPTY).trim();
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_TEST)) {
			bean.setEventEnum(WowEventEnum.TEST);
			name = command.replaceAll(NudoCCUtil.WOW_COMMAND_TEST, StringUtils.EMPTY).trim();
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_ITEM)) {
			bean.setEventEnum(WowEventEnum.ITEM);
			String[] array = command.replaceAll(NudoCCUtil.WOW_COMMAND_ITEM, StringUtils.EMPTY).trim().split(";");
			if (array.length != 2) {
				bean.setErrorMsg(NudoCCUtil.WOW_ITEM_PARAM_ERROR_MSG);
				return bean;
			}
			name = array[0];
			String realm = array[1];
			if (Arrays.binarySearch(NudoCCUtil.ALL_REALMS, realm) < 0) {
				bean.setErrorMsg(NudoCCUtil.WOW_ITEM_PARAM_ERROR_MSG);
				return bean;
			}
			bean.setRealm(realm);
		} else {
			bean.setEventEnum(WowEventEnum.PROFILE);
			name = command;
		}
		if (!checkWowName(name)) {
			bean.setErrorMsg(NudoCCUtil.WOW_NAME_ERROR_MSG);
		} else {
			bean.setName(name);
		}
		return bean;
	}
	
	/**
	 * 產生角色的template訊息
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	@Override
	public TemplateMessage buildCharacterTemplate(String name) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		for (String realm : NudoCCUtil.REALMS) {
			paramBean.setRealm(realm);
			try {
				WowCharacterProfileResponse resp = wowCharacterProfileService.doSendProfile(paramBean);
				if (StringUtils.isBlank(resp.getName())) {
					return null;
				}
				String race = WowRaceEnum.getEnumByValue(resp.getRace()).getContext();
				String clz = WowClassEnum.getEnumByValue(resp.getClz()).getContext();
				String imgPath = NudoCCUtil.WOW_IMG_BASE_PATH.concat(resp.getThumbnail());
				MessageAction messageAction = this.genItemMessageAction(resp.getName(), resp.getRealm());
				List<Action> actions = new ArrayList<>();
				actions.add(messageAction);
				String alt = String.format("群組: %s, 等級: %s級的<%s>是一隻%s%s，他殺了%s個人、有%s成就點數！",
						resp.getBattlegroup(), resp.getLevel(), resp.getName(), race, clz, resp.getTotalHonorableKills(), resp.getAchievementPoints());
				
				String title = String.format("群組-%s %s-%s %s級%s%s", resp.getBattlegroup(), resp.getName(),
						resp.getRealm(), resp.getLevel(), race, clz);
				
				String text = String.format("殺了%s個人、有%s成就點數！", resp.getTotalHonorableKills(), resp.getAchievementPoints());
				
				ButtonsTemplate buttonsTemplate = new ButtonsTemplate(imgPath, title, text, actions);
				TemplateMessage result = new TemplateMessage(alt, buttonsTemplate);
				return result;
			} catch (Exception e) {
				continue;
			}
		}
		return null;
	}
	
	/**
	 * 取得角色裝備資訊
	 * 
	 * @param name :角色名稱
	 * @param realm :伺服器名稱
	 * @return
	 */
	@Override
	public TextMessage findWowCharacterItem(String name, String realm) {
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		paramBean.setRealm(realm);
		paramBean.setFields(WowProfileFieldEnum.ITEMS);
		try {
			WowCharacterProfileItemResponse resp = wowCharacterProfileService.doSendItem(paramBean);
			if (StringUtils.isBlank(resp.getName())) {
				return null;
			}
			String result = null;
			if (resp.getItems().getAverageItemLevel() >= 900) {
				result = "豪可怕!! 背包裝等%s, 穿在身上的裝等居然%s!!";
			} else if (resp.getItems().getAverageItemLevel() <= 860){
				result = "好費... 背包裝等%s, 穿在身上的裝等....%s...額";
			} else {
				result = "背包裝等%s, 穿在身上的裝等%s";
			}
			return new TextMessage(String.format(result, resp.getItems().getAverageItemLevel(), resp.getItems().getAverageItemLevel()));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * generate 裝備資訊 message action
	 * 
	 * @param name :角色名稱
	 * @param realm :伺服器
	 * @return
	 */
	private MessageAction genItemMessageAction(String name, String realm) {
		String command = "-wow -i ".concat(name).concat(";").concat(realm);
		return new com.linecorp.bot.model.action.MessageAction(WowEventEnum.ITEM.getContext(), command);
	}

	/**
	 * check wow name
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	private boolean checkWowName(String name) {
		Pattern patternCh = Pattern.compile(NudoCCUtil.PATTERN_CH);
		Pattern patternEn = Pattern.compile(NudoCCUtil.PATTERN_EN);
	    Matcher matcherCh = patternCh.matcher(name);
	    Matcher matcherEn = patternEn.matcher(name);
	    return (matcherCh.matches() && name.length() <= 6) || (matcherEn.matches() && name.length() <= 12);
	}
	
}
