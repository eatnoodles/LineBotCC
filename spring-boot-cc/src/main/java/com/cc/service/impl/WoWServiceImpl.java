package com.cc.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.Application;
import com.cc.dao.WoWCharacterMappingDao;
import com.cc.entity.WoWCharacterMapping;
import com.cc.enums.WowClassEnum;
import com.cc.enums.WowEventEnum;
import com.cc.enums.WowItemPartsEnum;
import com.cc.enums.WowRaceEnum;
import com.cc.service.INudoCCService;
import com.cc.service.IWoWService;
import com.cc.wcl.client.WarcraftLogsClient;
import com.cc.wcl.client.WarcraftLogsClientImpl;
import com.cc.wcl.client.WarcraftLogsService;
import com.cc.wcl.client.WarcraftLogsServiceBuilder;
import com.cc.wcl.rank.CharacterRankResponse;
import com.cc.wcl.rank.Spec;
import com.cc.wcl.rank.WarcraftLogsClass;
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
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.utils.NudoCCUtil;

/**
 * @author Caleb Cheng
 *
 */
@Component
public class WoWServiceImpl implements IWoWService {
	
	private static final Logger LOG = LoggerFactory.getLogger(WoWServiceImpl.class);
	
	private static List<WarcraftLogsClass> wclClasses;
	
	static {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			wclClasses = mapper.readValue(Application.class.getResourceAsStream("/spec.json"), new TypeReference<List<WarcraftLogsClass>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	@Autowired
	private INudoCCService nudoCCService;
	
	@Autowired
	private WoWCharacterMappingDao wowCharacterMappingDao;
	
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
	 * 取得角色WCL資訊
	 * 
	 * @param name
	 * @param realm
	 * @param location
	 * @param metric
	 * @return
	 */
	@Override
	public Message getCharacterWCL(String name, String realm, String location, String metric, String mode) {
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
	 * save character by line id
	 * 
	 * @param name
	 * @param realm
	 * @param location
	 * @param userId
	 * @return
	 */
	@Override
	public Message saveCharacter(String name, String realm, String location, String userId) {
		
		if (StringUtils.isBlank(userId)) {
			return new TextMessage(NudoCCUtil.codeMessage("COM001"));
		}
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			
			// first char to upper
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
						
			WoWCharacterMapping po = wowCharacterMappingDao.findCharacterByName(name, realm);
			
			if (po != null && !userId.equals(po.getLineId())) {
				String lineName = nudoCCService.getDisplayName(po.getLineId());
				
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
