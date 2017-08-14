package com.cc.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.cc.enums.WowEventEnum;
import com.cc.service.IIrolService;
import com.cc.service.INudoCCService;
import com.cc.service.IWoWService;
import com.cc.wow.boss.BossMaster;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientImpl;
import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.utils.NudoCCUtil;

/**
 * @author Caleb.Cheng
 *
 */
@Component
public class NudoCCServiceImpl implements INudoCCService {
	
	private static BossMaster wowBossMaster;
	
	private static final Logger LOG = LoggerFactory.getLogger(NudoCCServiceImpl.class);
	
	private LineMessagingClient lineMessagingClient;
	{
		LineMessagingService lineMessagingService = LineMessagingServiceBuilder.create(System.getenv("LINE_BOT_CHANNEL_TOKEN")).build();
		lineMessagingClient = new LineMessagingClientImpl(lineMessagingService);
	}
	
	static {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			wowBossMaster = mapper.readValue(Application.class.getResourceAsStream("/wowBoss.json"), new TypeReference<BossMaster>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Autowired
	private IWoWService wowService;
	
	@Autowired
	private WoWCharacterMappingDao wowCharacterMappingDao;
	
	@Autowired
	private IIrolService irolService;
	
	@Autowired
	private UserTalkLevelDao userTalkLevelDao;
		
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
		if (!checkWoWName(name)) {
			bean.setErrorMsg(NudoCCUtil.codeMessage("ERR015"));
		} else {
			bean.setName(name);
		}
		return bean;
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
	 * get line display name
	 * 
	 * @param lineId
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Override
	public String getDisplayName(String lineId) throws InterruptedException, ExecutionException {
		UserProfileResponse userProfileResponse = lineMessagingClient.getProfile(lineId).get();
		return userProfileResponse.getDisplayName();
	}
	
	/**
	 * check wow name
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	private boolean checkWoWName(String name) {
		Pattern patternCh = Pattern.compile(NudoCCUtil.PATTERN_CH);
		Pattern patternEn = Pattern.compile(NudoCCUtil.PATTERN_EN);
	    Matcher matcherCh = patternCh.matcher(name);
	    Matcher matcherEn = patternEn.matcher(name);
	    return (matcherCh.matches() && name.length() <= 6) || (matcherEn.matches() && name.length() <= 12);
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
		} else if (command.indexOf(NudoCCUtil.IMG1_COMMAND) != -1) {
			return findStickerMessage("3", "181");
		} else if (command.equals(NudoCCUtil.OPEN_COMMAND)) {
			return irolService.getIrols(userId);
		} else if (command.toLowerCase().endsWith(NudoCCUtil.BATTLE_COMMAND)) {
			return irolService.doBattle(userId, command.toLowerCase().replace(NudoCCUtil.BATTLE_COMMAND, StringUtils.EMPTY));
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
    				return wowService.getHelp();
				case PROFILE:
					return wowService.buildCharacterTemplate(commandBean.getName());
				case IMG:
					return wowService.getWoWCharacterImgPath(commandBean.getName());
				case CHARACTER_ITEM:
					return wowService.getWoWCharacterItems(commandBean.getName(), commandBean.getRealm());
				case CHECK_ENCHANTS:
					return wowService.checkCharacterEnchants(commandBean.getName(), commandBean.getRealm());
				case WCL:
					return wowService.getCharacterWCL(commandBean.getName(), commandBean.getRealm(), commandBean.getLocation(), commandBean.getMetric(), commandBean.getMode());
				case MAPPING_A:
					return wowService.saveCharacter(commandBean.getName(), commandBean.getRealm(), commandBean.getLocation(), commandBean.getUserId());
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
			return wowService.getCharacterWCL(po.getName(), po.getRealm(), po.getLocation(), metric, mode);
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
	
}
