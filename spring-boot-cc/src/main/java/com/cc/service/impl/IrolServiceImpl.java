package com.cc.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.bean.IrolCommandBean;
import com.cc.dao.irol.IFightingIrolStatusDao;
import com.cc.dao.irol.IFightingLogDao;
import com.cc.dao.irol.IFightingMonsterStatusDao;
import com.cc.dao.irol.IIrolDao;
import com.cc.dao.irol.IMasterCardsDao;
import com.cc.dao.irol.IMonsterDao;
import com.cc.entity.irol.FightingIrolStatus;
import com.cc.entity.irol.FightingLog;
import com.cc.entity.irol.FightingMonsterStatus;
import com.cc.entity.irol.Irol;
import com.cc.entity.irol.MasterCards;
import com.cc.entity.irol.Monster;
import com.cc.entity.key.MasterCardsKey;
import com.cc.enums.IrolEventEnum;
import com.cc.service.IIrolService;
import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.PostbackAction;
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
public class IrolServiceImpl implements IIrolService {

	@Autowired
	private IIrolDao irolDao;
	
	@Autowired
	private IMonsterDao monsterDao;
	
	@Autowired
	private IMasterCardsDao masterCardsDao;
	
	@Autowired
	private IFightingLogDao fightingLogDao;
	
	@Autowired
	private IFightingIrolStatusDao fightingIrolStatusDao;
	
	@Autowired
	private IFightingMonsterStatusDao fightingMonsterStatusDao;
	
	@Override
	public boolean isIrolCommand(String command) {
		boolean isIrol = false;
		
		isIrol = isIrol || command.toLowerCase().endsWith(NudoCCUtil.BATTLE_COMMAND);
		isIrol = isIrol || command.equalsIgnoreCase(NudoCCUtil.OPEN_COMMAND);
		isIrol = isIrol || command.toLowerCase().startsWith(NudoCCUtil.IROL_COMMAND);
		
		return isIrol;
	}

	@Override
	public Message getIrols(String userId) {
		StringBuilder sb = new StringBuilder();
		sb.append(NudoCCUtil.codeMessage("IRL001"));
		List<MasterCards> masterCards = masterCardsDao.findIrolsByLineId(userId);
		Set<String> names = new HashSet<>();
		for (MasterCards masterCard :masterCards) {
			names.add(masterCard.getIrol().getName());
		}
		sb.append("[");
		sb.append(String.join(",", names));
		sb.append("]");
		return new TextMessage(sb.toString());
	}

	@Override
	@Transactional
	public Message doBattle(String userId, String irolName) {
		irolName = irolName.trim();
		if (StringUtils.isBlank(irolName) || StringUtils.isBlank(userId)) {
			return null;
		}
		
		Irol irol = irolDao.findByName(irolName);
		if (irol == null) {
			return null;
		}
		
		MasterCardsKey masterCardsKey = new MasterCardsKey(userId, irol);
		MasterCards masterCards = masterCardsDao.findOne(masterCardsKey);
		if (masterCards == null) {
			return null;
		}
		
		Monster monster = monsterDao.findOne(2L);//FIXME hard code for id
		if (monster == null) {
			return null;
		}
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		FightingLog fightingLog = fightingLogDao.findLastLog(userId, irol.getId(), monster.getId());
		
		if (fightingLog == null) {
			//init
			fightingLog = new FightingLog();
			
			FightingIrolStatus fightingIrolStatus = new FightingIrolStatus();
			fightingIrolStatus.setHp(irol.getHp());
			fightingIrolStatus.setStatus(FightingIrolStatus.STATUS_NORMAL);
			fightingIrolStatus.setIrol(irol);
			fightingIrolStatusDao.save(fightingIrolStatus);
			
			FightingMonsterStatus fightingMonsterStatus = new FightingMonsterStatus();
			fightingMonsterStatus.setHp(monster.getHp());
			fightingMonsterStatus.setStatus(FightingMonsterStatus.STATUS_NORMAL);
			fightingMonsterStatus.setMonster(monster);
			fightingMonsterStatusDao.save(fightingMonsterStatus);
			
			fightingLog.setFightingMonsterStatus(fightingMonsterStatus);
			fightingLog.setLastDttm(df.format(new Date()));
			fightingLog.setLineId(userId);
			fightingLog.setStatus(FightingLog.STATUS_FIGHTING);
			fightingLog.setFightingIrolStatus(fightingIrolStatus);
			fightingLog.setFightingMonsterStatus(fightingMonsterStatus);
			
			fightingLogDao.save(fightingLog);
		} else {
			//continue
		}
		
		List<Action> actions = new ArrayList<>();
		
		String cmd = "-irol -fight " + irol.getId() + ";" + monster.getId();
		PostbackAction action = new PostbackAction(NudoCCUtil.codeMessage("IRL003"), cmd, NudoCCUtil.codeMessage("IRL004", irol.getName(), monster.getName()));
		actions.add(action);
		
		if (irol.getSkill1() != null) {
			String command = "-irol -skill " + irol.getId() + ";" + monster.getId() + ";" + irol.getSkill1().getId();
			PostbackAction postbackAction = new PostbackAction(irol.getSkill1().getName(), command, NudoCCUtil.codeMessage("IRL002", irol.getName(), irol.getSkill1().getName()));
			actions.add(postbackAction);
		}
		if (irol.getSkill2() != null) {
			String command = "-irol -skill " + irol.getId() + ";" + monster.getId() + ";" + irol.getSkill2().getId();
			PostbackAction postbackAction = new PostbackAction(irol.getSkill2().getName(), command, NudoCCUtil.codeMessage("IRL002", irol.getName(), irol.getSkill2().getName()));
			actions.add(postbackAction);
		}
		
		String alt = NudoCCUtil.codeMessage("IRL005", monster.getName());
		
		String title = NudoCCUtil.codeMessage("IRL005", monster.getName());
		
		String text = NudoCCUtil.codeMessage("IRL006", monster.getHp(), monster.getAtk(), monster.getDef(), monster.getSpeed());
		//FIXME hard code img
		String img = "https://tw.webimage.beanfun.com/uploadimg/LineageWEB/monster_image/lin20001130_47.jpg";
		
		ButtonsTemplate buttonsTemplate = new ButtonsTemplate(img, title, text, actions);
		TemplateMessage result = new TemplateMessage(alt, buttonsTemplate);
		
		return result;
	}

	@Override
	public IrolCommandBean genIrolCommandBean(String command, String senderId, String userId) {
		IrolCommandBean bean = new IrolCommandBean();
		bean.setSenderId(senderId);
		bean.setUserId(userId);
		bean.setCommand(command);
		
		if (command.toLowerCase().startsWith(NudoCCUtil.IROL_COMMAND)) {
			// start with -irol command
			command = command.toLowerCase().replace(NudoCCUtil.IROL_COMMAND, StringUtils.EMPTY).trim();
			
			if (command.toLowerCase().startsWith(NudoCCUtil.IROL_COMMAND_FIGHT)) {
				bean.setEventEnum(IrolEventEnum.FIGHT);
				
				command = command.toLowerCase().replace(NudoCCUtil.IROL_COMMAND_FIGHT, StringUtils.EMPTY).trim();
				
				String[] args = command.split(";");
				if (args.length != 2) {
					bean.setErrorMsg(NudoCCUtil.codeMessage("ERR099"));
					return bean;
				}
				bean.setIrolId(Long.parseLong(args[0]));
				bean.setMonsterId(Long.parseLong(args[1]));
				
			} else if (command.toLowerCase().startsWith(NudoCCUtil.IROL_COMMAND_SKILL)) {
				bean.setEventEnum(IrolEventEnum.SKILL);
				
				command = command.toLowerCase().replace(NudoCCUtil.IROL_COMMAND_SKILL, StringUtils.EMPTY).trim();
				
				String[] args = command.split(";");
				if (args.length != 3) {
					bean.setErrorMsg(NudoCCUtil.codeMessage("ERR099"));
					return bean;
				}
				bean.setIrolId(Long.parseLong(args[0]));
				bean.setMonsterId(Long.parseLong(args[1]));
				bean.setSkillId(Long.parseLong(args[2]));
			}
		} else {
			//not start with -irol command
			if (command.equalsIgnoreCase(NudoCCUtil.OPEN_COMMAND)) {
				bean.setEventEnum(IrolEventEnum.OPEN);
				return bean;
			} else if (command.toLowerCase().endsWith(NudoCCUtil.BATTLE_COMMAND)) {
				bean.setEventEnum(IrolEventEnum.BATTLE);
				String name = command.replaceAll(NudoCCUtil.BATTLE_COMMAND, StringUtils.EMPTY).trim();
				bean.setIrolName(name);
			} 
		}
		
		return bean;
	}

	@Override
	public Message doFight(String userId, Long irolId, Long monsterId) {
		
		if (irolId == null || monsterId == null || StringUtils.isBlank(userId)) {
			return null;
		}
		
		// check irol exists
		Irol irol = irolDao.findOne(irolId);
		if (irol == null) {
			return null;
		}
		
		MasterCardsKey key = new MasterCardsKey(userId, irol); 
		MasterCards masterCards = masterCardsDao.findOne(key);
		if (masterCards == null) {
			return null;
		}
		
		Monster monster = monsterDao.findOne(monsterId);
		if (monster == null) {
			return null;
		}
		return null;
	}
	
}
