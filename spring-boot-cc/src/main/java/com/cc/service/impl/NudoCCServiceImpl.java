/**
 * 
 */
package com.cc.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.Application;
import com.cc.bean.WowCommandBean;
import com.cc.dao.UserTalkLevelDao;
import com.cc.dao.WoWCharacterMappingDao;
import com.cc.entity.UserTalkLevel;
import com.cc.entity.WoWCharacterMapping;
import com.cc.entity.key.UserTalkLevelKey;
import com.cc.enums.WowClassEnum;
import com.cc.enums.WowEventEnum;
import com.cc.enums.WowItemPartsEnum;
import com.cc.enums.WowRaceEnum;
import com.cc.service.INudoCCService;
import com.cc.wcl.client.WarcraftLogsClient;
import com.cc.wcl.client.WarcraftLogsClientImpl;
import com.cc.wcl.client.WarcraftLogsService;
import com.cc.wcl.client.WarcraftLogsServiceBuilder;
import com.cc.wcl.rank.CharacterRankResponse;
import com.cc.wcl.rank.Spec;
import com.cc.wcl.rank.WarcraftLogsClass;
import com.cc.wow.boss.BossMaster;
import com.cc.wow.character.Appearance;
import com.cc.wow.character.CharacterItemsResponse;
import com.cc.wow.character.CharacterProfileResponse;
import com.cc.wow.character.ItemParts;
import com.cc.wow.client.WoWCommunityClient;
import com.cc.wow.client.WoWCommunityClientImpl;
import com.cc.wow.client.WoWCommunityService;
import com.cc.wow.client.WoWCommunityServiceBuilder;
import com.cc.wow.client.exception.WoWCommunityException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientImpl;
import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.utils.NudoCCUtil;

/**
 * @author Caleb.Cheng
 *
 */
@Component
public class NudoCCServiceImpl implements INudoCCService {
	
	private static BossMaster wowBossMaster;
	
	private static List<WarcraftLogsClass> wclClasses;
	
	private static final Logger LOG = LoggerFactory.getLogger(NudoCCServiceImpl.class);
	
	private LineMessagingClient lineMessagingClient;
	{
		LineMessagingService lineMessagingService = LineMessagingServiceBuilder.create(System.getenv("LINE_BOT_CHANNEL_TOKEN")).build();
		lineMessagingClient = new LineMessagingClientImpl(lineMessagingService);
	}
	
	private WoWCommunityClient wowCommunityClient;
	{
		WoWCommunityService wowCommunityService = WoWCommunityServiceBuilder.create(System.getenv("WOWApiKey")).build();
		wowCommunityClient = new WoWCommunityClientImpl(wowCommunityService);
	}
	
	private WarcraftLogsClient warcraftLogsClient;
	{
		WarcraftLogsService warcraftLogsService = WarcraftLogsServiceBuilder.create(System.getenv("WCLApiKey")).build();
		warcraftLogsClient = new WarcraftLogsClientImpl(warcraftLogsService);
	}
	
	static {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			wowBossMaster = mapper.readValue(Application.class.getResourceAsStream("/wowBoss.json"), new TypeReference<BossMaster>(){});
			wclClasses = mapper.readValue(Application.class.getResourceAsStream("/spec.json"), new TypeReference<List<WarcraftLogsClass>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Autowired
	private WoWCharacterMappingDao wowCharacterMappingDao;
	
	@Autowired
	private UserTalkLevelDao userTalkLevelDao;
	
	/**
	 * 以name、server搜尋角色基本資料
	 * 
	 * @param name :角色名稱
	 * @param server :伺服器名稱
	 * @return
	 */
	@Override
	public TextMessage getWoWCharacterProfile(String name, String server) {
		try {
			CharacterProfileResponse resp = wowCommunityClient.getCharacterProfile(server, name).get();
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
	public TextMessage getWoWCharacterProfileByName(String name) {
		for (String realm : NudoCCUtil.REALMS) {
			try {
				CharacterProfileResponse resp = wowCommunityClient.getCharacterProfile(realm, name).get();
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
	public ImageMessage getWoWCharacterImgPath(String name) {
		for (String realm : NudoCCUtil.REALMS) {
			try {
				CharacterProfileResponse resp = wowCommunityClient.getCharacterProfile(realm, name).get();
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
	public WowCommandBean processWowCommand(String command) {
		if (StringUtils.isBlank(command)) {
			return null;
		}
		WowCommandBean bean = new WowCommandBean();
		if (!command.startsWith(NudoCCUtil.WOW_COMMAND)) {
			bean.setWowCommand(false);
			return bean;
		}
		command = command.replaceAll(NudoCCUtil.WOW_COMMAND, StringUtils.EMPTY).trim();
		String name = null;
		
		if (command.equalsIgnoreCase(NudoCCUtil.WOW_COMMAND_HELP)) {
			bean.setEventEnum(WowEventEnum.HELP);
			return bean;
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_IMG)) {
			bean.setEventEnum(WowEventEnum.IMG);
			name = command.replaceAll(NudoCCUtil.WOW_COMMAND_IMG, StringUtils.EMPTY).trim();
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_TEST)) {
			bean.setEventEnum(WowEventEnum.TEST);
			name = command.replaceAll(NudoCCUtil.WOW_COMMAND_TEST, StringUtils.EMPTY).trim();
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_ITEM)) {
			bean.setEventEnum(WowEventEnum.CHARACTER_ITEM);
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
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_CHECK_ENCHANTS)) {
			bean.setEventEnum(WowEventEnum.CHECK_ENCHANTS);
			String[] array = command.replaceAll(NudoCCUtil.WOW_COMMAND_CHECK_ENCHANTS, StringUtils.EMPTY).trim().split(";");
			if (array.length != 2) {
				bean.setErrorMsg(NudoCCUtil.WOW_ENCHANTS_PARAM_ERROR_MSG);
				return bean;
			}
			name = array[0];
			String realm = array[1];
			if (Arrays.binarySearch(NudoCCUtil.ALL_REALMS, realm) < 0) {
				bean.setErrorMsg(NudoCCUtil.WOW_ITEM_PARAM_ERROR_MSG);
				return bean;
			}
			bean.setRealm(realm);
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_WCL)) {
			bean.setEventEnum(WowEventEnum.WCL);
			String[] array = command.replaceAll(NudoCCUtil.WOW_COMMAND_WCL, StringUtils.EMPTY).trim().split(";");
			if (array.length != 4 && array.length != 5) {
				bean.setErrorMsg("要更多資訊喔~");
				return bean;
			}
			name = array[0];
			
			String realm = array[1];
			if (Arrays.binarySearch(NudoCCUtil.ALL_REALMS, realm) < 0) {
				bean.setErrorMsg("沒有這個server喔~");
				return bean;
			}
			bean.setRealm(realm);
			
			String location = array[2];
			if (Arrays.binarySearch(NudoCCUtil.LOCATIONS, location) < 0) {
				bean.setErrorMsg("沒有這個地區喔~請輸入 [US, EU, KR, TW, CN] 其中一個");
				return bean;
			}
			bean.setLocation(location);
			
			String metric = array[3];
			if (!metric.equalsIgnoreCase("dps")
				&& !metric.equalsIgnoreCase("hps")
				&& !metric.equalsIgnoreCase("bossdps")
				&& !metric.equalsIgnoreCase("tankhps")
				&& !metric.equalsIgnoreCase("playerspeed")) {
				bean.setErrorMsg("沒有這個選項喔~請輸入 [dps, hps, bossdps, tankhps, playerspeed] 其中一個");
				return bean;
			}
			bean.setMetric(metric);
			if (array.length == 5) {
				String mode = array[4];
				if (!mode.equalsIgnoreCase("N")
					&& !mode.equalsIgnoreCase("H")
					&& !mode.equalsIgnoreCase("M")) {
					bean.setErrorMsg("沒有這個模式喔~請輸入 [N,H,M] 其中一個");
					return bean;
				}
				bean.setMode(mode);
			}
			
		}  else if (command.startsWith(NudoCCUtil.WOW_COMMAND_SAVE)) {
			bean.setEventEnum(WowEventEnum.MAPPING_A);
			String[] array = command.replaceAll(NudoCCUtil.WOW_COMMAND_SAVE, StringUtils.EMPTY).trim().split(";");
			if (array.length != 3) {
				bean.setErrorMsg("要更多資訊喔~");
				return bean;
			}
			name = array[0];
			
			String realm = array[1];
			if (Arrays.binarySearch(NudoCCUtil.ALL_REALMS, realm) < 0) {
				bean.setErrorMsg("沒有這個server喔~");
				return bean;
			}
			bean.setRealm(realm);
			
			String location = array[2];
			if (Arrays.binarySearch(NudoCCUtil.LOCATIONS, location) < 0) {
				bean.setErrorMsg("沒有這個地區喔~請輸入 [US, EU, KR, TW, CN] 其中一個");
				return bean;
			}
			bean.setLocation(location);
			
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
		for (String realm : NudoCCUtil.REALMS) {
			try {
				CharacterProfileResponse resp = wowCommunityClient.getCharacterProfile(realm, name).get();
				if (StringUtils.isBlank(resp.getName())) {
					return null;
				}
				String race = WowRaceEnum.getEnumByValue(resp.getRace()).getContext();
				String clz = WowClassEnum.getEnumByValue(resp.getClz()).getContext();
				String imgPath = NudoCCUtil.WOW_IMG_BASE_PATH.concat(resp.getThumbnail());
				PostbackAction postbackAction1 = this.genItemPostbackAction(resp.getName(), resp.getRealm());
				PostbackAction postbackAction2 = this.genCheckEnchantsPostbackAction(resp.getName(), resp.getRealm());
				
				List<Action> actions = new ArrayList<>();
				actions.add(postbackAction1);
				actions.add(postbackAction2);
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
	public TextMessage getWoWCharacterItems(String name, String realm) {
		try {
			CharacterProfileResponse resp = wowCommunityClient.getCharacterItems(realm, name).get();
			
			if (StringUtils.isBlank(resp.getName())) {
				return null;
			}
			
			StringBuilder sb = new StringBuilder();
			if (resp.getItems().getAverageItemLevel() >= 900) {
				sb.append("豪可怕!! 背包裝等%s, 穿在身上的裝等居然%s!!");
			} else if (resp.getItems().getAverageItemLevel() <= 860){
				sb.append("好費... 背包裝等%s, 穿在身上的裝等....%s...額");
			} else {
				sb.append("背包裝等%s, 穿在身上的裝等%s");
			}
			
			CharacterItemsResponse items = resp.getItems();
			sb.append("\r\n\r\n");
			sb.append("<").append(name).append("-").append(realm).append(">的詳細資訊→\r\n");
			
			for (WowItemPartsEnum partsEnum :WowItemPartsEnum.values()) {
				if (partsEnum == WowItemPartsEnum.NULL) {
					continue;
				}
				String partsName = partsEnum.getContext();
				ItemParts itemParts = (ItemParts)PropertyUtils.getProperty(items, partsEnum.getValue());
				if (itemParts == null) {
					continue;
				}
				sb.append(String.format("　%s－%s %s", partsName, itemParts.getItemLevel(), itemParts.getName())).append("\r\n");
			}
			sb.append("------------------------------");
			
			return new TextMessage(String.format(sb.toString(), resp.getItems().getAverageItemLevel(), resp.getItems().getAverageItemLevelEquipped()));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 檢核裝備有無附魔
	 * 
	 * @param name :角色名稱
	 * @param realm :伺服器名稱
	 * @return
	 */
	@Override
	public TextMessage checkCharacterEnchants(String name, String realm) {
		try {
			CharacterProfileResponse resp = wowCommunityClient.getCharacterItems(realm, name).get();
			
			if (StringUtils.isBlank(resp.getName())) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			
			CharacterItemsResponse items = resp.getItems();
			
			for (WowItemPartsEnum partsEnum : NudoCCUtil.enchantsParts) {
				ItemParts itemParts = (ItemParts)PropertyUtils.getProperty(items, partsEnum.getValue());
				if (itemParts == null) {
					continue;
				}
				Appearance appearance = itemParts.getAppearance();
				if (appearance == null || appearance.getEnchantDisplayInfoId() == null) {
					if (sb.length() > 0) {
						sb.append("\r\n");
					}
					sb.append(String.format("%s-%s", partsEnum.getContext(), itemParts.getName()));
				}
			}
			
			if (sb.length() > 0) {
				sb.append(String.format("\r\n。。%s-%s沒有腹膜。。", name, realm));
				return new TextMessage(sb.toString());
			} else {
				return new TextMessage(String.format("%s-%s都有好好附魔~", name, realm));
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * generate 裝備資訊 postback action
	 * 
	 * @param name :角色名稱
	 * @param realm :伺服器
	 * @return
	 */
	private PostbackAction genItemPostbackAction(String name, String realm) {
		String command = "-wow -i ".concat(name).concat(";").concat(realm);
		return new PostbackAction(WowEventEnum.CHARACTER_ITEM.getContext(), command, String.format("我想知道<%s-%s>的裝等o.o", name, realm));
	}
	
	/**
	 * generate 檢查未附魔裝備資訊 postback action
	 * 
	 * @param name
	 * @param realm
	 * @return
	 */
	private PostbackAction genCheckEnchantsPostbackAction(String name, String realm) {
		String command = "-wow -ec ".concat(name).concat(";").concat(realm);
		return new PostbackAction(WowEventEnum.CHECK_ENCHANTS.getContext(), command, String.format("我想知道<%s-%s>有沒有沒附魔的莊o.o", name, realm));
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
	
	/**
	 * 取得協助
	 * 
	 * @return
	 */
	@Override
	public TextMessage getHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("995: -wow -help\r\n");
		sb.append("查詢角色基本資訊: -wow 角色名稱\r\n");
		sb.append("查詢角色大頭貼: -wow -img 角色名稱\r\n");
		sb.append("查詢角色裝備: -wow -i 角色名稱;伺服器名稱\r\n");
		sb.append("查詢角色裝備有無附魔: -wow -ec 角色名稱;伺服器名稱\r\n");
		sb.append("查詢角色WCL: -wow -wcl 角色名稱;伺服器名稱;地區(TW);(hps/dps/bossdps/tankhps)\r\n");
		return new TextMessage(sb.toString());
	}
	
	/**
	 * 根據request傳來的command回傳message
	 * 
	 * @param event
	 * @return
	 */
	@Override
	public Message processCommand(MessageEvent<TextMessageContent> event) {
		
		String mesg = event.getMessage().getText();
        String senderId = event.getSource().getSenderId();
        String userId = event.getSource().getUserId();
        
        if (StringUtils.isBlank(mesg)) {
        	return null;
        }
        
		WowCommandBean commandBean = this.processWowCommand(mesg);
    	if (commandBean.isWowCommand()) {
    		//wow command
    		if (StringUtils.isNotBlank(commandBean.getErrorMsg())) {
        		return new TextMessage(commandBean.getErrorMsg());
        	} else {
        		switch (commandBean.getEventEnum()) {
        			case HELP:
        				return this.getHelp();
					case PROFILE:
						return this.buildCharacterTemplate(commandBean.getName());
					case IMG:
						return this.getWoWCharacterImgPath(commandBean.getName());
					case CHARACTER_ITEM:
						return this.getWoWCharacterItems(commandBean.getName(), commandBean.getRealm());
					case CHECK_ENCHANTS:
						return this.checkCharacterEnchants(commandBean.getName(), commandBean.getRealm());
					case WCL:
						return this.getCharacterWCL(commandBean.getName(), commandBean.getRealm(), commandBean.getLocation(), commandBean.getMetric(), commandBean.getMode());
					case MAPPING_A:
						return this.saveCharacter(commandBean.getName(), commandBean.getRealm(), commandBean.getLocation(), userId);
					case TEST:
						//TODO ...
					default:
						return null;
				}
        	}
    	} else {
    		Pattern pattern = Pattern.compile(NudoCCUtil.WCL_USER_COMMANDS);
            
    		//other command
    		if (mesg.toLowerCase().startsWith(NudoCCUtil.ROLL_COMMAND)) {
    			return this.getRollNumber(mesg.toLowerCase().replace(NudoCCUtil.ROLL_COMMAND, StringUtils.EMPTY));
    		} else if (mesg.equalsIgnoreCase(NudoCCUtil.GET_USER_ID_COMMAND)) {
    			return new TextMessage(String.format("senderId=[%s], userId=[%s]", senderId, userId));
    		} else if (mesg.equals(NudoCCUtil.LEAVE_COMMAND)) {
    			leave(senderId);
    			return null; 
    		} else if (mesg.equals(NudoCCUtil.WHOAMI_COMMAND)) {
    			return getWoWNameById(userId);
    		} else if (pattern.matcher(mesg.toLowerCase()).matches()) {
    			String[] array = mesg.split("的");
    			return getCharacterWCLByUserId(array[0], array[1], userId);
    		} else {
    			// logger talking
				return processUserTalk(mesg, userId);
    		}
    	}
	}
	
	private Message processUserTalk(String mesg, String userId) {
		if (StringUtils.isBlank(userId)) {
			return null;
		}
		UserTalkLevelKey key = new UserTalkLevelKey(userId, mesg);
		UserTalkLevel userTalkLevel = userTalkLevelDao.findOne(key);
		if (userTalkLevel != null) {
			try {
				userTalkLevel.setTalkCount(userTalkLevel.getTalkCount()+1);
				userTalkLevelDao.save(userTalkLevel);
				String displayName = getDisplayName(userId);
				switch (userTalkLevel.getTalkCount()) {
					case 10:
						return  new TextMessage(String.format("%s, 你的\"%s\"發言累計次數已達10次, 從現在開始你就是 『真誠的%s』！", displayName, mesg, mesg));
					case 25: 
						return  new TextMessage(String.format("%s, 你的\"%s\"發言累計次數已達25次, 從現在開始你就是 『超級的%s』！", displayName, mesg, mesg));
					case 50: 
						return  new TextMessage(String.format("%s, 你的\"%s\"發言累計次數已達50次, 從現在開始你就是 『魅力的%s』！", displayName, mesg, mesg));
					case 99: 
						return  new TextMessage(String.format("%s, 你的\"%s\"發言累計次數已達99次, 從現在開始你就是 『永遠的%s』！", displayName, mesg, mesg));
					default:
						break;
				}
			} catch (Exception e) {
				LOG.error("processUserTalk error!", e);
			}
		} else {
			userTalkLevel = new UserTalkLevel(userId, mesg);
			userTalkLevel.setTalkCount(1);
			userTalkLevelDao.save(userTalkLevel);
		}
		return null;
	}
	
	/**
	 * get line display name
	 * 
	 * @param lineId
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private String getDisplayName(String lineId) throws InterruptedException, ExecutionException {
		UserProfileResponse userProfileResponse = lineMessagingClient.getProfile(lineId).get();
		return userProfileResponse.getDisplayName();
	}

	private Message getCharacterWCLByUserId(String mode, String metric, String userId) {
		if (StringUtils.isBlank(userId)) {
			return new TextMessage("請先+我好友哦～");
		}
		try {
			WoWCharacterMapping po = wowCharacterMappingDao.findOne(userId);
			if (po == null) {
				return new TextMessage("？你還沒告訴我你是誰");
			}
			return this.getCharacterWCL(po.getName(), po.getRealm(), po.getLocation(), metric, mode);
		} catch (Exception e) {
			return null;
		}
	}

	private Message getWoWNameById(String userId) {
		if (StringUtils.isBlank(userId)) {
			return new TextMessage("請先+我好友哦～");
		}
		try {
			WoWCharacterMapping po = wowCharacterMappingDao.findOne(userId);
			if (po != null) {
				return new TextMessage(String.format("我知道你是%s-%s", po.getName(), po.getRealm()));
			} else {
				return new TextMessage("？你是誰？");
			}
		} catch (Exception e) {
			return new TextMessage("？你到底是誰？");
		}
	}

	private DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

	private Message saveCharacter(String name, String realm, String location, String userId) {
		if (StringUtils.isBlank(userId)) {
			return new TextMessage("請先+我好友哦～");
		}
		try {
			// first char to upper
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
						
			WoWCharacterMapping po = wowCharacterMappingDao.findCharacterByName(name, realm);
			if (po != null && !userId.equals(po.getLineId())) {
				String lineId = po.getLineId();
				UserProfileResponse userProfileResponse = lineMessagingClient.getProfile(lineId).get();
				String lineName = userProfileResponse.getDisplayName();
				
				return new TextMessage(String.format("你少騙,%s-%s明明是%s", name, realm, lineName));
			}
			wowCommunityClient.getCharacterProfile(realm, name).get();
			
			po = wowCharacterMappingDao.findOne(userId);
			if (po != null) {
				LOG.info("delete WoWCharacterMapping begin...");
				wowCharacterMappingDao.delete(po);
			}
			WoWCharacterMapping bean = new WoWCharacterMapping();
			bean.setLineId(userId);
			
			bean.setName(name);
			bean.setRealm(realm);
			bean.setLocation(location);
			bean.setLastMdfyDttm(df.format(new Date()));
			
			wowCharacterMappingDao.save(bean);
		} catch (Exception e) {
			if (e.getCause() instanceof WoWCommunityException) {
				return new TextMessage("要馬你太久沒上, 不然就是你唬洨我！");
			}
			return new TextMessage("？儲存失敗了那？");
		}
		return new TextMessage("已經和角色資訊建立連結！");
	}

	/**
	 * 取得角色WCL資訊
	 * 
	 * @param name
	 * @param realm
	 * @param location
	 * @param metric
	 * @return
	 */
	private Message getCharacterWCL(String name, String realm, String location, String metric, String mode) {
		try {
			Map<String, List<String>> map = new HashMap<>();
			DateFormat df = new SimpleDateFormat("(MM/dd)");
			
			List<CharacterRankResponse> resps = warcraftLogsClient.getRankingsByCharacter(name, realm, location, metric).get();
			
			StringBuilder sb = new StringBuilder();
			
			for (CharacterRankResponse resp :resps) {
				if (mode != null && !mode.equalsIgnoreCase(getBossMode(resp.getDifficulty()))) {
					continue;
				}
				String specName = getSpecName(resp.getClz(), resp.getSpec());
				
				BigDecimal rank = new BigDecimal(resp.getRank().toString());
				BigDecimal outOf = new BigDecimal(resp.getOutOf().toString());
				String rankPercent = BigDecimal.ONE.subtract(rank.divide(outOf, 4, RoundingMode.HALF_EVEN)).multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();
				
				sb.append("	").append(this.getBossNameByEncounter(resp.getEncounter()));
				sb.append("-").append(getBossMode(resp.getDifficulty()));
				
				sb.append(" ：").append(resp.getTotal()).append("(").append(rankPercent).append("%) ");
				sb.append(" ( ").append(resp.getReportID()).append(" ").append(df.format(resp.getStartTime())).append(" ) ");
				
				if (map.containsKey(specName)) {
					map.get(specName).add(sb.toString());
				} else {
					List<String> list = new ArrayList<>();
					list.add(sb.toString());
					map.put(specName, list);
				}
				sb.delete(0, sb.length());
			}
			
			sb.append(String.format("%s-%s 的 %s 如下：\r\n", name, realm, metric));
			
			for (String specName : map.keySet()) {
				sb.append("　--").append(specName);
				sb.append("--------------------------------------------\r\n");
				
				for (String str :map.get(specName)) {
					sb.append(str).append("\r\n\r\n");
				}
			}
			
			return new TextMessage(sb.toString());
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	private String getBossMode(int difficulty) {
		switch (difficulty) {
			case 5: return "M";
			case 4: return "H";
			case 3: return "N";
			default: return "??";
		}
	}

	private String getBossNameByEncounter(Long encounter) {
		int boss = encounter.intValue();
		switch (boss) {
			case 2032: return "狗洛斯";
			case 2048: return "惡魔審判官";
			case 2036: return "哈亞談";
			case 2037: return "薩斯音女士";
			case 2050: return "月光議會";
			case 2054: return "荒瘠聚合體";
			case 2052: return "剩女";
			case 2038: return "墮落化身";
			case 2051: return "基爾加單";
			default: return "???";
		}
	}

	private void leave(String groupId) {
		LOG.info("leaveGroup BEGIN");
		lineMessagingClient.leaveGroup(groupId);
		LOG.info("leaveGroup END");
	}

	private TextMessage getRollNumber(String command) {
		if (StringUtils.isNotBlank(command) && command.indexOf(" ") == 0) {
			String[] scopes = command.trim().split("-");
			if (scopes.length != 2) {
				return new TextMessage("指定範圍有誤！");
			} else {
				int start, end = 0;
				try {
					start = Integer.parseInt(scopes[0]);
					end = Integer.parseInt(scopes[1]);
					if (start > end) {
						return new TextMessage("你的數學老師在哭！");
					}
					if (end > 99999) {
						return new TextMessage("骰子那麼大去拉斯維加斯阿！");
					}
				} catch (NumberFormatException e) {
					return new TextMessage("不要亂骰！");
				}
				int size = wowBossMaster.getBosses().size();
    			int point = this.probabilityControl(start, end);
    					
    			Random randBoss = new Random();
    			int index = randBoss.nextInt(size);
    			String name = wowBossMaster.getBosses().get(index).getName();
    			return new TextMessage(String.format("%s 擲出了%s (%s-%s)！", name, point, start, end));
			}
		} else {
			int size = wowBossMaster.getBosses().size();
			int point = this.probabilityControl(1, 100);
			Random randBoss = new Random();
			int index = randBoss.nextInt(size);
			String name = wowBossMaster.getBosses().get(index).getName();
			return new TextMessage(String.format("%s 擲出了%s (1-100)！", name, point));
		}
	}

	private int probabilityControl(int start, int end) {
		List<Integer> nums = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			nums.add(i);
		}
		int[] numsToGenerate = nums.stream().mapToInt(i->i).toArray();
		double[] discreteProbabilities = NudoCCUtil.zipfDistribution(end-start+1);
		int[] result = NudoCCUtil.getIntegerDistribution(numsToGenerate, discreteProbabilities, 1);
		return result[0];
	}
	
	private String getSpecName(int clz, int specId) {
		StringBuilder sb = new StringBuilder();
		loop :for (WarcraftLogsClass wclClass : wclClasses) {
			if (wclClass.getId() == clz) {
				for (Spec spec :wclClass.getSpecs()) {
					if (spec.getId() == specId) {
						sb.append(spec.getName());
						break loop;
					}
				}
			}
		}
		return sb.toString();
	}
}
