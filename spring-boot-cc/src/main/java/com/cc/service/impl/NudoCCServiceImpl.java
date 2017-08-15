package com.cc.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.Application;
import com.cc.bean.CommandBean;
import com.cc.bean.IrolCommandBean;
import com.cc.bean.OtherCommandBean;
import com.cc.bean.WoWCommandBean;
import com.cc.dao.UserTalkLevelDao;
import com.cc.dao.WoWCharacterMappingDao;
import com.cc.entity.UserTalkLevel;
import com.cc.entity.WoWCharacterMapping;
import com.cc.entity.key.UserTalkLevelKey;
import com.cc.enums.OtherEventEnum;
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
		
		if (command.startsWith(NudoCCUtil.WOW_COMMAND)) {
			return wowService.genWoWCommandBean(command, senderId, userId);
		}
		
		if (irolService.isIrolCommand(command)) {
			return irolService.genIrolCommandBean(command, senderId, userId);
		}
		
		return this.genOtherCommandBean(command, senderId, userId);
	}

	/**
	 * 
	 * @param command
	 * @param senderId
	 * @param userId
	 * @return
	 */
	private CommandBean genOtherCommandBean(String command, String senderId, String userId) {
		
		OtherCommandBean bean = new OtherCommandBean(command, senderId, userId);
		
		Pattern pattern = Pattern.compile(NudoCCUtil.WCL_USER_COMMANDS);
        
		//other command
		if (command.toLowerCase().startsWith(NudoCCUtil.ROLL_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.ROLL);
		} else if (command.equalsIgnoreCase(NudoCCUtil.GET_USER_ID_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.GET_USER_ID);
		} else if (command.equals(NudoCCUtil.LEAVE_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.LEAVE);
		} else if (command.equals(NudoCCUtil.WHOAMI_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.WHOAMI);
		} else if (pattern.matcher(command.toLowerCase()).matches()) {
			bean.setEventEnum(OtherEventEnum.WCL_USER);
		} else if (command.indexOf(NudoCCUtil.IMG1_COMMAND) != -1) {
			bean.setEventEnum(OtherEventEnum.IMG1);
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
		
		if (commandBean instanceof WoWCommandBean) {
			return processWoWCommand((WoWCommandBean)commandBean);
		}
		
		if (commandBean instanceof IrolCommandBean) {
			return processIrolCommand((IrolCommandBean)commandBean);
		}
		
		if (commandBean instanceof OtherCommandBean) {
			return processOtherCommand((OtherCommandBean)commandBean);
		}
		
		return null;
	}

	/**
	 * 
	 * @param commandBean
	 * @return
	 */
	private Message processOtherCommand(OtherCommandBean commandBean) {
		//other command
		if (StringUtils.isNotBlank(commandBean.getErrorMsg())) {
    		return new TextMessage(commandBean.getErrorMsg());
    	} else {
    		switch (commandBean.getEventEnum()) {
    			case ROLL:
    				return this.getRollMessage(commandBean.getCommand().toLowerCase().replace(NudoCCUtil.ROLL_COMMAND, StringUtils.EMPTY));
				case GET_USER_ID:
					return new TextMessage(NudoCCUtil.codeMessage("OTR001", commandBean.getSenderId(), commandBean.getUserId()));
				case LEAVE:
					leave(commandBean.getSenderId());
					return null; 
				case WHOAMI:
					return getWoWNameById(commandBean.getUserId());
				case WCL_USER:
					String[] array = commandBean.getCommand().split(NudoCCUtil.codeMessage("OTR002"));
					return getCharacterWCLByUserId(array[0], array[1], commandBean.getUserId());
				case IMG1:
					return findStickerMessage("3", "181");
				default:
					return processUserTalk(commandBean.getCommand(), commandBean.getUserId());
			}
    	}
	}

	/**
	 * process wow command
	 * 
	 * @param commandBean
	 * @return
	 */
	private Message processWoWCommand(WoWCommandBean commandBean) {
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
	 * process irol command
	 * 
	 * @param commandBean
	 * @return
	 */
	private Message processIrolCommand(IrolCommandBean commandBean) {
		//irol command
		if (StringUtils.isNotBlank(commandBean.getErrorMsg())) {
    		return new TextMessage(commandBean.getErrorMsg());
    	} else {
    		switch (commandBean.getEventEnum()) {
    			case OPEN:
    				return irolService.getIrols(commandBean.getUserId());
    			case BATTLE:
    				return irolService.doBattle(commandBean.getUserId(), commandBean.getIrolName());
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
