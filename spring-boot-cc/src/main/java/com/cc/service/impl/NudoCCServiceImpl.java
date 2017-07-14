/**
 * 
 */
package com.cc.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.Application;
import com.cc.bean.WowBossMaster;
import com.cc.bean.WowCharacterProfileItemResponse;
import com.cc.bean.WowCharacterProfileItemResponse.ItemParts;
import com.cc.bean.WowCharacterProfileItemResponse.ItemParts.Appearance;
import com.cc.bean.WowCharacterProfileItemResponse.Items;
import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;
import com.cc.bean.WowCommandBean;
import com.cc.enums.WowClassEnum;
import com.cc.enums.WowEventEnum;
import com.cc.enums.WowItemPartsEnum;
import com.cc.enums.WowProfileFieldEnum;
import com.cc.enums.WowRaceEnum;
import com.cc.service.INudoCCService;
import com.cc.service.IRemoteService;
import com.cc.service.IWowCharacterProfileService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.utils.NudoCCUtil;

/**
 * @author Caleb.Cheng
 *
 */
@Component
public class NudoCCServiceImpl implements INudoCCService {

	@Autowired
	private IWowCharacterProfileService wowCharacterProfileService;
	
	@Autowired
	private IRemoteService remoteService;
	
	@Autowired
	private WowNewsTask wowNewsTask;
	
	private static WowBossMaster wowBossMaster;
	
	static {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			wowBossMaster = mapper.readValue(Application.class.getResourceAsStream("/wowBoss.json"), new TypeReference<WowBossMaster>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
			StringBuilder sb = new StringBuilder();
			if (resp.getItems().getAverageItemLevel() >= 900) {
				sb.append("豪可怕!! 背包裝等%s, 穿在身上的裝等居然%s!!");
			} else if (resp.getItems().getAverageItemLevel() <= 860){
				sb.append("好費... 背包裝等%s, 穿在身上的裝等....%s...額");
			} else {
				sb.append("背包裝等%s, 穿在身上的裝等%s");
			}
			Items items = resp.getItems();
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
		WowCharacterProfileParamBean paramBean = new WowCharacterProfileParamBean();
		paramBean.setCharacterName(name);
		paramBean.setRealm(realm);
		paramBean.setFields(WowProfileFieldEnum.ITEMS);
		try {
			WowCharacterProfileItemResponse resp = wowCharacterProfileService.doSendItem(paramBean);
			if (StringUtils.isBlank(resp.getName())) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			Items items = resp.getItems();
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
		return new TextMessage(sb.toString());
	}
	
	/**
	 * 根據request傳來的command回傳message
	 * 
	 * @param command
	 * @return
	 */
	@Override
	public Message processCommand(String mesg, String userId) {
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
						return this.findWowCharacterImgPath(commandBean.getName());
					case CHARACTER_ITEM:
						return this.findWowCharacterItem(commandBean.getName(), commandBean.getRealm());
					case CHECK_ENCHANTS:
						return this.checkCharacterEnchants(commandBean.getName(), commandBean.getRealm());
					case TEST:
//						return this.getNews();
					default:
						return null;
				}
        	}
    	} else {
    		//other command
    		if (mesg.toLowerCase().startsWith(NudoCCUtil.ROLL_COMMAND)) {
    			return this.getRollNumber(mesg.toLowerCase().replace(NudoCCUtil.ROLL_COMMAND, StringUtils.EMPTY));
    		} else if (mesg.toLowerCase().startsWith(NudoCCUtil.NS_COMMAND)) {
    			return this.getNintendoStoreResult();
    		} else if (mesg.toLowerCase().startsWith(NudoCCUtil.GET_USER_ID_COMMAND)) {
    			return new TextMessage(String.format("你的userId=[%s]", userId));
    		} else if (mesg.toLowerCase().startsWith(NudoCCUtil.RUN_TIMER_COMMAND)) {
    			return runTimer(userId);
    		}
    		return null;
    	}
	}

	private Message runTimer(String userId) {
		Timer timer = new Timer();
		timer.schedule(wowNewsTask, 5000, 60000);
		return new TextMessage(String.format("開始timer!"));
	}

	private Message getNintendoStoreResult() {
		String response = remoteService.call("https://store.nintendo.co.jp/customize.html");
		if (response.indexOf("SOLD OUT") != -1) return new TextMessage("switch官網現在沒貨!");
		else return new TextMessage("switch官網現在有貨!");
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
				} catch (NumberFormatException e) {
					return new TextMessage("指定範圍有誤！");
				}
				int size = wowBossMaster.getBosses().size();
//    			Random rand = new Random();
//    			int point = rand.nextInt(end-start+1) + start;
    			int point = this.probabilityControl(start, end);
    					
    			Random randBoss = new Random();
    			int index = randBoss.nextInt(size);
    			String name = wowBossMaster.getBosses().get(index).getName();
    			return new TextMessage(String.format("%s 擲出了%s (%s-%s)！", name, point, start, end));
			}
		} else {
			int size = wowBossMaster.getBosses().size();
//			Random rand = new Random();
//			int point = rand.nextInt(100) + 1;
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
	
}
