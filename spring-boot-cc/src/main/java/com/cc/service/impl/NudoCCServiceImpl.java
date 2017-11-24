package com.cc.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.Application;
import com.cc.bean.CommandBean;
import com.cc.bean.IrolCommandBean;
import com.cc.bean.OtherCommandBean;
import com.cc.bean.WoWCommandBean;
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
import com.utils.GaussUtil;
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
	private IIrolService irolService;
		
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
		
		//other command
		if (command.equals(NudoCCUtil.ROLL_COMMAND)) {
			bean.setEventEnum(OtherEventEnum.ROLL);
		} else if (command.toLowerCase().startsWith(NudoCCUtil.GAUSS_COMMAND)) {
			command = command.replaceAll(NudoCCUtil.GAUSS_COMMAND, StringUtils.EMPTY).trim();
			bean.setEventEnum(OtherEventEnum.GAUSS);
			bean.setCommand(command);
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
    		LOG.info(String.format("command={%s}", commandBean.getCommand()));
    		switch (commandBean.getEventEnum()) {
    			case ROLL:
    				return this.getRollMessage(commandBean.getCommand().toLowerCase().replace(NudoCCUtil.ROLL_COMMAND, StringUtils.EMPTY), commandBean.getSenderId());
    			case GAUSS:
    				return this.getGaussResult(commandBean.getCommand().toLowerCase().replace(NudoCCUtil.GAUSS_COMMAND, StringUtils.EMPTY));
				default:
					return null;
			}
    	}
	}

	/**
	 * 
	 * @param command
	 * @return
	 */
	private Message getGaussResult(String command) {
		
		StringBuilder sb = new StringBuilder();
		
		String[] args = command.split("\r\n");
		GaussUtil gaussUtil = new GaussUtil(args.length);
		
		List<Double> list = new ArrayList<>();
		for (String arg :args) {
			String[] nums = arg.split(" ");
			for (String num :nums) {
				list.add(Double.parseDouble(num));
			}
		}
		gaussUtil.setA(list);
		double[] results = gaussUtil.getResult();
		
		for (int i = 1; i <= results.length; i++) {
			sb.append(String.format("C%s={%s}", i, results[i-1]));
		}
		return new TextMessage(sb.toString());
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
		
		int point = this.probabilityControl(start, end);
		int size = wowBossMaster.getBosses().size();
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
