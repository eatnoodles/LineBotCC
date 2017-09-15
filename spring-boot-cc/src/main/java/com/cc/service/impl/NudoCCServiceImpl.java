package com.cc.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import com.cc.dao.UserImgFuncDao;
import com.cc.dao.UserTalkLevelDao;
import com.cc.dao.WoWCharacterMappingDao;
import com.cc.entity.UserImgFunc;
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
import com.linecorp.bot.model.message.ImageMessage;
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
	
	@Autowired
	private UserImgFuncDao userImgFuncDao;
		
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
		} else if (command.equals(NudoCCUtil.USER_ROLL_START_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.USER_ROLL_START);
		} else if (command.equals(NudoCCUtil.USER_ROLL_END_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.USER_ROLL_END);
		} else if (command.equals(NudoCCUtil.EMOJI_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.EMOJI);
		} else if (command.startsWith(NudoCCUtil.PARROT_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.PARROT);
		} else {
			bean.setEventEnum(OtherEventEnum.TALKING);
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
    				return this.getRollMessage(commandBean.getCommand().toLowerCase().replace(NudoCCUtil.ROLL_COMMAND, StringUtils.EMPTY), commandBean.getSenderId());
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
				case TALKING:
//					return processUserTalk(commandBean.getCommand(), commandBean.getUserId());
					return null;
				case USER_ROLL_START:
					return updateUserRoll(commandBean.getSenderId(), true);
				case USER_ROLL_END:
					return updateUserRoll(commandBean.getSenderId(), false);
				case EMOJI:
					return getEmojiMessage();
				case PARROT:
					return getParrotImage(commandBean.getCommand().replace(NudoCCUtil.PARROT_COMMAND, StringUtils.EMPTY));
				default:
					return null;
			}
    	}
	}

	/**
	 * 
	 * @param msg
	 * @return
	 */
	private Message getParrotImage(String msg) {
		LOG.info("getParrotImage msg=" + msg);
		
		try {
			if (msg.length() > 22) {
				msg = NudoCCUtil.codeMessage("OTR008");
			}
			msg = msg.replaceAll("+", "{plus}");
			msg = msg.replaceAll("/", "{slash}");
			
			msg = URLEncoder.encode(msg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error(e.getMessage());
			return null;
		}
		Random random = new Random();
		int index = random.nextInt(20) + 1;
		
		String path = String.format("%s/API/parrot/%s/%s", System.getenv("ROOT_PATH"), index, msg) ;
		
		LOG.info("getParrotImage path=" + path);
		return new ImageMessage(path, path);
	}

	/**
	 * get emoji message
	 * 
	 * @return
	 */
	private TextMessage getEmojiMessage() {
		return new TextMessage(NudoCCUtil.codeMessage("OTR007"));
	}

	/**
	 * 
	 * @param senderId
	 * @param status
	 * @return
	 */
	private Message updateUserRoll(String senderId, boolean status) {
		UserImgFunc userImgFunc = userImgFuncDao.findOne(senderId);
		if (userImgFunc == null) {
			userImgFunc = new UserImgFunc();
			userImgFunc.setLineId(senderId);
		}
		userImgFunc.setStatus(status);
		userImgFuncDao.save(userImgFunc);
		return new TextMessage("hs..hs..");
	}

	/**
	 * process wow command
	 * 
	 * @param commandBean
	 * @return
	 */
	@Override
	public Message processWoWCommand(WoWCommandBean commandBean) {
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
	@Override
	public Message processIrolCommand(IrolCommandBean commandBean) {
		//irol command
		if (StringUtils.isNotBlank(commandBean.getErrorMsg())) {
    		return new TextMessage(commandBean.getErrorMsg());
    	} else {
    		switch (commandBean.getEventEnum()) {
    			case OPEN:
    				return irolService.getIrols(commandBean.getUserId());
    			case BATTLE:
    				return irolService.doBattle(commandBean.getUserId(), commandBean.getIrolName());
    			case FIGHT:
    				return irolService.doFight(commandBean.getUserId(), commandBean.getIrolId(), commandBean.getMonsterId());
    			case SKILL:
    				return irolService.doSkill(commandBean.getUserId(), commandBean.getIrolId(), commandBean.getMonsterId(), commandBean.getSkillId());
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
	private Message getRollMessage(String command, String senderId) {
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
				return this.getRollMessage(start, end, senderId);
			}
		} else {
			return this.getRollMessage(1, 100, senderId);
		}
	}

	/**
	 * get roll number message
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	private Message getRollMessage(int start, int end, String senderId) {
		boolean isUserRoll = isUserRoll(senderId);
		
		int point = this.probabilityControl(start, end);
		
		if (isUserRoll) {
			String img = System.getenv("ROOT_PATH") + "/API/img/" + point;
			return new ImageMessage(img, img);
		} else {
			int size = wowBossMaster.getBosses().size();
			Random randBoss = new Random();
			int index = randBoss.nextInt(size);
			String name = wowBossMaster.getBosses().get(index).getName();
			
			return new TextMessage(NudoCCUtil.codeMessage("COM004", name, point, start, end));
		}
	}

	/**
	 * 
	 * @param senderId
	 * @return
	 */
	private boolean isUserRoll(String senderId) {
		UserImgFunc userImgFunc = userImgFuncDao.findOne(senderId);
		return userImgFunc == null ? false : userImgFunc.isStatus();
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
