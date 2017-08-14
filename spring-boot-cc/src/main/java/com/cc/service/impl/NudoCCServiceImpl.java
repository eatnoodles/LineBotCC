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
import com.cc.bean.CommandBean;
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
import com.linecorp.bot.model.message.StickerMessage;
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
	
	private DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	
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
	 * find line sticker message
	 * 
	 * @param packageId
	 * @param stickerId
	 * @return
	 */
	@Override
	public Message findStickerMessage(String packageId, String stickerId) {
		return new StickerMessage(packageId, stickerId);
	}
	
	/**
	 * generator command bean
	 * 
	 * @param event
	 * @return
	 */
	@Override
	public CommandBean genCommandBean(String command, String senderId, String userId) {
		
		if (StringUtils.isBlank(command)) {
			return null;
		}
		
		CommandBean bean = new CommandBean();
		bean.setSenderId(senderId);
		bean.setUserId(userId);
		bean.setCommand(command);
		
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
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR016"));
				return bean;
			}
			name = array[0];
			String realm = array[1];
			if (Arrays.binarySearch(NudoCCUtil.ALL_REALMS, realm) < 0) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR016"));
				return bean;
			}
			bean.setRealm(realm);
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_CHECK_ENCHANTS)) {
			bean.setEventEnum(WowEventEnum.CHECK_ENCHANTS);
			String[] array = command.replaceAll(NudoCCUtil.WOW_COMMAND_CHECK_ENCHANTS, StringUtils.EMPTY).trim().split(";");
			if (array.length != 2) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR017"));
				return bean;
			}
			name = array[0];
			String realm = array[1];
			if (Arrays.binarySearch(NudoCCUtil.ALL_REALMS, realm) < 0) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR016"));
				return bean;
			}
			bean.setRealm(realm);
		} else if (command.startsWith(NudoCCUtil.WOW_COMMAND_WCL)) {
			bean.setEventEnum(WowEventEnum.WCL);
			String[] array = command.replaceAll(NudoCCUtil.WOW_COMMAND_WCL, StringUtils.EMPTY).trim().split(";");
			if (array.length != 4 && array.length != 5) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR001"));
				return bean;
			}
			name = array[0];
			
			String realm = array[1];
			if (Arrays.binarySearch(NudoCCUtil.ALL_REALMS, realm) < 0) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR002"));
				return bean;
			}
			bean.setRealm(realm);
			
			String location = array[2];
			if (Arrays.binarySearch(NudoCCUtil.LOCATIONS, location) < 0) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR003"));
				return bean;
			}
			bean.setLocation(location);
			
			String metric = array[3];
			if (!metric.equalsIgnoreCase("dps")
				&& !metric.equalsIgnoreCase("hps")
				&& !metric.equalsIgnoreCase("bossdps")
				&& !metric.equalsIgnoreCase("tankhps")
				&& !metric.equalsIgnoreCase("playerspeed")) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR004"));
				return bean;
			}
			bean.setMetric(metric);
			if (array.length == 5) {
				String mode = array[4];
				if (!mode.equalsIgnoreCase("N")
					&& !mode.equalsIgnoreCase("H")
					&& !mode.equalsIgnoreCase("M")) {
					bean.setErrorMsg(NudoCCUtil.codeMessage("ERR005"));
					return bean;
				}
				bean.setMode(mode);
			}
			
		}  else if (command.startsWith(NudoCCUtil.WOW_COMMAND_SAVE)) {
			bean.setEventEnum(WowEventEnum.MAPPING_A);
			String[] array = command.replaceAll(NudoCCUtil.WOW_COMMAND_SAVE, StringUtils.EMPTY).trim().split(";");
			if (array.length != 3) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR001"));
				return bean;
			}
			name = array[0];
			
			String realm = array[1];
			if (Arrays.binarySearch(NudoCCUtil.ALL_REALMS, realm) < 0) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR002"));
				return bean;
			}
			bean.setRealm(realm);
			
			String location = array[2];
			if (Arrays.binarySearch(NudoCCUtil.LOCATIONS, location) < 0) {
				bean.setErrorMsg(NudoCCUtil.codeMessage("ERR003"));
				return bean;
			}
			bean.setLocation(location);
			
		} else {
			bean.setEventEnum(WowEventEnum.PROFILE);
			name = command;
		}
		if (!checkWowName(name)) {
			bean.setErrorMsg(NudoCCUtil.codeMessage("ERR015"));
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
				
				String alt = NudoCCUtil.codeMessage("WOW001", resp.getBattlegroup(), resp.getLevel(), resp.getName(), race, clz,
						resp.getTotalHonorableKills(), resp.getAchievementPoints());
				
				String title = NudoCCUtil.codeMessage("WOW002", resp.getBattlegroup(), resp.getName(),
						resp.getRealm(), resp.getLevel(), race, clz);
				
				String text = NudoCCUtil.codeMessage("WOW003", resp.getTotalHonorableKills(), resp.getAchievementPoints());
				
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
			
			String msgCode = this.getMsgCodeByItemLevel(resp.getItems().getAverageItemLevel());
			
			sb.append(NudoCCUtil.codeMessage(msgCode, resp.getItems().getAverageItemLevel(),
					resp.getItems().getAverageItemLevelEquipped(), name, realm));
			
			CharacterItemsResponse items = resp.getItems();
			
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
			
			return new TextMessage(sb.toString());
			
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * get message code by item level
	 * 
	 * @param itemLevel
	 * @return
	 */
	private String getMsgCodeByItemLevel(Integer itemLevel) {
		if (itemLevel >= 900) {
			return "WOW004";
		} else if (itemLevel <= 860){
			return "WOW005";
		} else {
			return "WOW006";
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
				sb.append("\r\n");
				sb.append(NudoCCUtil.codeMessage("WOW007", name, realm));
				return new TextMessage(sb.toString());
			} else {
				return new TextMessage(NudoCCUtil.codeMessage("WOW008", name, realm));
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
		return new PostbackAction(WowEventEnum.CHARACTER_ITEM.getContext(), command, NudoCCUtil.codeMessage("WOW009", name, realm));
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
		return new PostbackAction(WowEventEnum.CHECK_ENCHANTS.getContext(), command, NudoCCUtil.codeMessage("WOW010", name, realm));
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
		sb.append(NudoCCUtil.codeMessage("HLP001")).append(NudoCCUtil.NEW_LINE);
		sb.append(NudoCCUtil.codeMessage("HLP002")).append(NudoCCUtil.NEW_LINE);
		sb.append(NudoCCUtil.codeMessage("HLP003")).append(NudoCCUtil.NEW_LINE);
		sb.append(NudoCCUtil.codeMessage("HLP004")).append(NudoCCUtil.NEW_LINE);
		sb.append(NudoCCUtil.codeMessage("HLP005")).append(NudoCCUtil.NEW_LINE);
		sb.append(NudoCCUtil.codeMessage("HLP006")).append(NudoCCUtil.NEW_LINE);
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
		
		String command = event.getMessage().getText();
        String senderId = event.getSource().getSenderId();
        String userId = event.getSource().getUserId();
        
		CommandBean commandBean = this.genCommandBean(command, senderId, userId);
		
		if (commandBean == null) {
			return null;
		}
		
		return commandBean.isWowCommand() ? processWoWCommand(commandBean) : processOtherCommand(commandBean);
	}

	/**
	 * 
	 * @param commandBean
	 * @return
	 */
	private Message processOtherCommand(CommandBean commandBean) {
		
		String command = commandBean.getCommand();
		String senderId = commandBean.getSenderId();
		String userId = commandBean.getUserId();
		
		Pattern pattern = Pattern.compile(NudoCCUtil.WCL_USER_COMMANDS);
        
		//other command
		if (command.toLowerCase().startsWith(NudoCCUtil.ROLL_COMMAND)) {
			return this.getRollMessage(command.toLowerCase().replace(NudoCCUtil.ROLL_COMMAND, StringUtils.EMPTY));
		} else if (command.equalsIgnoreCase(NudoCCUtil.GET_USER_ID_COMMAND)) {
			return new TextMessage(NudoCCUtil.codeMessage("OTR001", senderId, userId));
		} else if (command.equals(NudoCCUtil.LEAVE_COMMAND)) {
			leave(senderId);
			return null; 
		} else if (command.equals(NudoCCUtil.WHOAMI_COMMAND)) {
			return getWoWNameById(userId);
		} else if (pattern.matcher(command.toLowerCase()).matches()) {
			String[] array = command.split(NudoCCUtil.codeMessage("OTR002"));
			return getCharacterWCLByUserId(array[0], array[1], userId);
		}  else if (command.indexOf(NudoCCUtil.IMG1_COMMAND) != -1) {
			return findStickerMessage("3", "181");
		} else {
			// logger talking
			return processUserTalk(command, userId);
		}
	}

	/**
	 * process wow command
	 * 
	 * @param commandBean
	 * @return
	 */
	private Message processWoWCommand(CommandBean commandBean) {
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
					return this.saveCharacter(commandBean.getName(), commandBean.getRealm(), commandBean.getLocation(), commandBean.getUserId());
				case TEST:
					//TODO ...
				default:
					return null;
			}
    	}
	}

	/**
	 * return message by talking count
	 * 
	 * @param mesg
	 * @param userId
	 * @return
	 */
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
						return  new TextMessage(NudoCCUtil.codeMessage("OTR003", displayName, mesg, mesg));
					case 25: 
						return  new TextMessage(NudoCCUtil.codeMessage("OTR004", displayName, mesg, mesg));
					case 50: 
						return  new TextMessage(NudoCCUtil.codeMessage("OTR005", displayName, mesg, mesg));
					case 99: 
						return  new TextMessage(NudoCCUtil.codeMessage("OTR006", displayName, mesg, mesg));
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

	/**
	 * 
	 * @param mode
	 * @param metric
	 * @param userId
	 * @return
	 */
	private Message getCharacterWCLByUserId(String mode, String metric, String userId) {
		if (StringUtils.isBlank(userId)) {
			return new TextMessage(NudoCCUtil.codeMessage("COM001"));
		}
		try {
			WoWCharacterMapping po = wowCharacterMappingDao.findOne(userId);
			if (po == null) {
				return new TextMessage(NudoCCUtil.codeMessage("COM002"));
			}
			return this.getCharacterWCL(po.getName(), po.getRealm(), po.getLocation(), metric, mode);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * get mapping wow name by line id
	 * 
	 * @param userId
	 * @return
	 */
	private Message getWoWNameById(String userId) {
		if (StringUtils.isBlank(userId)) {
			return new TextMessage(NudoCCUtil.codeMessage("COM001"));
		}
		try {
			WoWCharacterMapping po = wowCharacterMappingDao.findOne(userId);
			if (po != null) {
				return new TextMessage(NudoCCUtil.codeMessage("WOW011", po.getName(), po.getRealm()));
			} else {
				return new TextMessage(NudoCCUtil.codeMessage("ERR006"));
			}
		} catch (Exception e) {
			return new TextMessage(NudoCCUtil.codeMessage("ERR007"));
		}
	}

	/**
	 * save character by line id
	 * 
	 * @param name
	 * @param realm
	 * @param location
	 * @param userId
	 * @return
	 */
	private Message saveCharacter(String name, String realm, String location, String userId) {
		
		if (StringUtils.isBlank(userId)) {
			return new TextMessage(NudoCCUtil.codeMessage("COM001"));
		}
		try {
			// first char to upper
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
						
			WoWCharacterMapping po = wowCharacterMappingDao.findCharacterByName(name, realm);
			
			if (po != null && !userId.equals(po.getLineId())) {
				String lineName = getDisplayName(po.getLineId());
				
				return new TextMessage(NudoCCUtil.codeMessage("ERR008", name, realm, lineName));
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
				return new TextMessage(NudoCCUtil.codeMessage("ERR009"));
			}
			
			return new TextMessage(NudoCCUtil.codeMessage("ERR010"));
		}
		return new TextMessage(NudoCCUtil.codeMessage("COM003"));
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
			
			sb.append(NudoCCUtil.codeMessage("WCL001", name, realm, metric));
			sb.append(NudoCCUtil.NEW_LINE);
			
			for (String specName : map.keySet()) {
				sb.append("　--").append(specName);
				sb.append("--------------------------------------------");
				sb.append(NudoCCUtil.NEW_LINE);
				
				for (String str :map.get(specName)) {
					sb.append(str);
					sb.append(NudoCCUtil.NEW_LINE);
					sb.append(NudoCCUtil.NEW_LINE);
				}
			}
			
			return new TextMessage(sb.toString());
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * @param difficulty
	 * @return
	 */
	private String getBossMode(int difficulty) {
		switch (difficulty) {
			case 5: return "M";
			case 4: return "H";
			case 3: return "N";
			default: return "??";
		}
	}

	/**
	 * 
	 * @param encounter
	 * @return
	 */
	private String getBossNameByEncounter(Long encounter) {
		int boss = encounter.intValue();
		switch (boss) {
			case 2032: return NudoCCUtil.codeMessage("WCL002");
			case 2048: return NudoCCUtil.codeMessage("WCL003");
			case 2036: return NudoCCUtil.codeMessage("WCL004");
			case 2037: return NudoCCUtil.codeMessage("WCL005");
			case 2050: return NudoCCUtil.codeMessage("WCL006");
			case 2054: return NudoCCUtil.codeMessage("WCL007");
			case 2052: return NudoCCUtil.codeMessage("WCL008");
			case 2038: return NudoCCUtil.codeMessage("WCL009");
			case 2051: return NudoCCUtil.codeMessage("WCL010");
			default: return "???";
		}
	}

	/**
	 * leave group
	 * 
	 * @param groupId
	 */
	private void leave(String groupId) {
		LOG.info("leaveGroup BEGIN");
		lineMessagingClient.leaveGroup(groupId);
		LOG.info("leaveGroup END");
	}

	/**
	 * Roll
	 * 
	 * @param command
	 * @return
	 */
	private TextMessage getRollMessage(String command) {
		if (StringUtils.isNotBlank(command) && command.indexOf(" ") == 0) {
			String[] scopes = command.trim().split("-");
			if (scopes.length != 2) {
				return new TextMessage(NudoCCUtil.codeMessage("ERR011"));
			} else {
				// validate start & end
				int start, end = 0;
				try {
					start = Integer.parseInt(scopes[0]);
					end = Integer.parseInt(scopes[1]);
					if (start > end) {
						return new TextMessage(NudoCCUtil.codeMessage("ERR012"));
					}
					if (end > 99999) {
						return new TextMessage(NudoCCUtil.codeMessage("ERR013"));
					}
				} catch (NumberFormatException e) {
					return new TextMessage(NudoCCUtil.codeMessage("ERR014"));
				}
				return this.getRollMessage(start, end);
			}
		} else {
			return this.getRollMessage(1, 100);
		}
	}

	/**
	 * get roll number message
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private TextMessage getRollMessage(int start, int end) {
		int size = wowBossMaster.getBosses().size();
		int point = this.probabilityControl(start, end);
				
		Random randBoss = new Random();
		int index = randBoss.nextInt(size);
		String name = wowBossMaster.getBosses().get(index).getName();
		
		return new TextMessage(NudoCCUtil.codeMessage("COM004", name, point, start, end));
	}

	/**
	 * probability point by start,end
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
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
	
	/**
	 * get wow spec name by class, spec id
	 * 
	 * @param clz
	 * @param specId
	 * @return
	 */
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
