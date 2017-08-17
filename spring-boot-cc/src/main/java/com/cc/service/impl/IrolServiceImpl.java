package com.cc.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.bean.IrolCommandBean;
import com.cc.dao.irol.IFightingIrolBuffStatusDao;
import com.cc.dao.irol.IFightingIrolDebuffStatusDao;
import com.cc.dao.irol.IFightingIrolStatusDao;
import com.cc.dao.irol.IFightingLogDao;
import com.cc.dao.irol.IFightingMonsterBuffStatusDao;
import com.cc.dao.irol.IFightingMonsterDebuffStatusDao;
import com.cc.dao.irol.IFightingMonsterStatusDao;
import com.cc.dao.irol.IIrolDao;
import com.cc.dao.irol.IMasterCardsDao;
import com.cc.dao.irol.IMonsterDao;
import com.cc.entity.irol.Buff;
import com.cc.entity.irol.Debuff;
import com.cc.entity.irol.FightingIrolBuffStatus;
import com.cc.entity.irol.FightingIrolDebuffStatus;
import com.cc.entity.irol.FightingIrolStatus;
import com.cc.entity.irol.FightingLog;
import com.cc.entity.irol.FightingMonsterBuffStatus;
import com.cc.entity.irol.FightingMonsterDebuffStatus;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(IrolServiceImpl.class);
	
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
	
	@Autowired
	private IFightingIrolBuffStatusDao fightingIrolBuffStatusDao;
	
	@Autowired
	private IFightingIrolDebuffStatusDao fightingIrolDebuffStatusDao;
	
	@Autowired
	private IFightingMonsterBuffStatusDao fightingMonsterBuffStatusDao;
	
	@Autowired
	private IFightingMonsterDebuffStatusDao fightingMonsterDebuffStatusDao;
	
	
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
			this.initIrolStatus(fightingIrolStatus, irol);
			
			fightingIrolStatusDao.save(fightingIrolStatus);
			
			FightingMonsterStatus fightingMonsterStatus = new FightingMonsterStatus();
			fightingMonsterStatus.setHp(monster.getHp());
			fightingMonsterStatus.setStatus(FightingMonsterStatus.STATUS_NORMAL);
			fightingMonsterStatus.setMonster(monster);
			this.initMonsterStatus(fightingMonsterStatus, monster);
			
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

	private void initMonsterStatus(FightingMonsterStatus fightingMonsterStatus, Monster monster) {
		fightingMonsterStatus.setAtk(monster.getAtk());
		fightingMonsterStatus.setDef(monster.getDef());
		fightingMonsterStatus.setSpeed(monster.getSpeed());
		fightingMonsterStatus.setMaxhp(monster.getHp());
	}

	/**
	 * init irol status
	 * 
	 * @param fightingIrolStatus
	 * @param irol
	 */
	private void initIrolStatus(FightingIrolStatus fightingIrolStatus, Irol irol) {
		fightingIrolStatus.setAtk(irol.getAtk());
		fightingIrolStatus.setDef(irol.getDef());
		fightingIrolStatus.setSpeed(irol.getSpeed());
		fightingIrolStatus.setMaxhp(irol.getHp());
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
		try {
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
			
			FightingLog fightingLog = fightingLogDao.findLastLog(userId, irolId, monsterId);
			if (fightingLog == null) {
				return null;
			}
			
			LOG.info("Fighting begin...");
			StringBuilder sb = new StringBuilder();
			sb.append(NudoCCUtil.codeMessage("IRL007"));
			
			FightingIrolStatus irolStatus = fightingLog.getFightingIrolStatus();
			FightingMonsterStatus monsterStatus = fightingLog.getFightingMonsterStatus();
			
			List<FightingIrolBuffStatus> irolBuffs = irolStatus.getFightingIrolBuffStatusList();
			List<FightingIrolDebuffStatus> irolDebuffs = irolStatus.getFightingIrolDebuffStatusList();
			
			List<FightingMonsterBuffStatus> monsterBuffs = monsterStatus.getFightingMonsterBuffStatusList();
			List<FightingMonsterDebuffStatus> monsterDebuffs = monsterStatus.getFightingMonsterDebuffStatusList();
			
			if (irolStatus.getSpeed() >= monsterStatus.getSpeed()) {
				sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL008", irol.getName()));

				// irol attk!
				boolean isMonsterDead = this.doIrolFighting(irol, monster, irolStatus, monsterStatus, sb);
				if (isMonsterDead) {
					return getMonsterDeadMessage(irol, monsterStatus, fightingLog, sb);
				}
				
				// monster attk!
				boolean isIrolDead = this.doMonsterFighting(irol, monster, irolStatus, monsterStatus, sb);
				if (isIrolDead) {
					return getIrolDeadMessage(monster, irolStatus, fightingLog, sb);
				}

				// irol bonus attk!
				isMonsterDead = this.doIrolSpeedBonus(irol, monster, irolStatus, monsterStatus, sb); 
				if (isMonsterDead) {
					return getMonsterDeadMessage(irol, monsterStatus, fightingLog, sb);
				}
				
				// irol buff process
				if (irolBuffs != null && !irolBuffs.isEmpty()) {
					sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL014", irol.getName()));
					this.processIrolBuffs(irolBuffs, irolStatus, sb);
				}
				// irol debuff process
				if (irolDebuffs != null && !irolDebuffs.isEmpty()) {
					sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL017", irol.getName()));
					
					isIrolDead = this.processIrolDebuffs(irolDebuffs, irolStatus, sb);
					if (isIrolDead) {
						return getIrolDeadMessage(monster, irolStatus, fightingLog, sb);
					}
				}
				
				// monster buff process
				if (monsterBuffs != null && !monsterBuffs.isEmpty()) {
					sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL014", monster.getName()));
					this.processMonsterBuffs(monsterBuffs, monsterStatus);
				}
				// monster debuff process
				if (monsterDebuffs != null && !monsterDebuffs.isEmpty()) {
					sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL017", monster.getName()));
					
					isMonsterDead = this.processMonsterDebuffs(monsterDebuffs, monsterStatus);
					if (isMonsterDead) {
						return getMonsterDeadMessage(irol, monsterStatus, fightingLog, sb);
					}
				}
				sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL012"));
			} else {
				sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL008", monster.getName()));

				// monster attk!
				boolean isIrolDead = this.doMonsterFighting(irol, monster, irolStatus, monsterStatus, sb);
				if (isIrolDead) {
					return getIrolDeadMessage(monster, irolStatus, fightingLog, sb);
				}
				
				// irol attk!
				boolean isMonsterDead = this.doIrolFighting(irol, monster, irolStatus, monsterStatus, sb);
				if (isMonsterDead) {
					return getMonsterDeadMessage(irol, monsterStatus, fightingLog, sb);
				}

				// monster bonus attk!
				isIrolDead = this.doMonsterSpeedBonus(irol, monster, irolStatus, monsterStatus, sb); 
				if (isIrolDead) {
					return getIrolDeadMessage(monster, irolStatus, fightingLog, sb);
				}
				
				// irol buff process
				if (irolBuffs != null && !irolBuffs.isEmpty()) {
					sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL014", irol.getName()));
					this.processIrolBuffs(irolBuffs, irolStatus, sb);
				}
				// irol debuff process
				if (irolDebuffs != null && !irolDebuffs.isEmpty()) {
					sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL017", irol.getName()));
					
					isIrolDead = this.processIrolDebuffs(irolDebuffs, irolStatus, sb);
					if (isIrolDead) {
						return getIrolDeadMessage(monster, irolStatus, fightingLog, sb);
					}
				}
				
				// monster buff process
				if (monsterBuffs != null && !monsterBuffs.isEmpty()) {
					sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL014", monster.getName()));
					this.processMonsterBuffs(monsterBuffs, monsterStatus);
				}
				// monster debuff process
				if (monsterDebuffs != null && !monsterDebuffs.isEmpty()) {
					sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL017", monster.getName()));
					
					isMonsterDead = this.processMonsterDebuffs(monsterDebuffs, monsterStatus);
					if (isMonsterDead) {
						return getMonsterDeadMessage(irol, monsterStatus, fightingLog, sb);
					}
				}
				sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL012"));
			}
			
			return new TextMessage(sb.toString());
		} catch (Exception e) {
			LOG.error("doFight error!", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param irol
	 * @param monster
	 * @param irolStatus
	 * @param monsterStatus
	 * @param sb
	 * @return
	 */
	private boolean doMonsterSpeedBonus(Irol irol, Monster monster, FightingIrolStatus irolStatus,
			FightingMonsterStatus monsterStatus, StringBuilder sb) {
		if (isSpeedBonus(monsterStatus.getSpeed(), irolStatus.getSpeed())) {
			int monsterDamage = this.getMonsterDamage(irol, monster, irolStatus, monsterStatus);
			irolStatus.setHp(this.getHpByDamage(irolStatus.getHp(), monsterDamage));
			
			sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL013", monster.getName(), irol.getName(), monsterStatus));
			sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL010", irol.getName(), irolStatus.getHp()));
			
			return irolStatus.getHp() == 0;
		}
		return false;
	}

	/**
	 * 
	 * @param monster
	 * @param irolStatus
	 * @param fightingLog
	 * @param sb
	 * @return
	 */
	private Message getIrolDeadMessage(Monster monster, FightingIrolStatus irolStatus, FightingLog fightingLog,
			StringBuilder sb) {
		irolStatus.setStatus(FightingIrolStatus.STATUS_DEAD);
		fightingIrolStatusDao.save(irolStatus);
		
		fightingLog.setStatus(FightingLog.STATUS_COMPLETE);
		fightingLogDao.save(fightingLog);
		
		sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL011", monster.getName()));
		sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL012"));
		
		return new TextMessage(sb.toString());
	}

	/**
	 * 
	 * @param irol
	 * @param monsterStatus
	 * @param fightingLog
	 * @param sb
	 * @return
	 */
	private Message getMonsterDeadMessage(Irol irol, FightingMonsterStatus monsterStatus, FightingLog fightingLog,
			StringBuilder sb) {
		monsterStatus.setStatus(FightingMonsterStatus.STATUS_DEAD);
		fightingMonsterStatusDao.save(monsterStatus);
		
		fightingLog.setStatus(FightingLog.STATUS_COMPLETE);
		fightingLogDao.save(fightingLog);
		
		sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL011", irol.getName()));
		sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL012"));
		
		return new TextMessage(sb.toString());
	}

	/**
	 * 
	 * @param irol
	 * @param monster
	 * @param irolStatus
	 * @param monsterStatus
	 * @param sb
	 * @return
	 */
	private boolean doIrolSpeedBonus(Irol irol, Monster monster, FightingIrolStatus irolStatus,
			FightingMonsterStatus monsterStatus, StringBuilder sb) {
		
		if (isSpeedBonus(irolStatus.getSpeed(), monsterStatus.getSpeed())) {
			int irolDamage = this.getIrolDamage(irol, monster, irolStatus, monsterStatus);
			monsterStatus.setHp(this.getHpByDamage(monsterStatus.getHp(), irolDamage));
			
			sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL013", irol.getName(), monster.getName(), irolDamage));
			sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL010", monster.getName(), monsterStatus.getHp()));
			
			return monsterStatus.getHp() == 0;
		}
		return false;
	}

	/**
	 * 
	 * @param irol
	 * @param monster
	 * @param irolStatus
	 * @param monsterStatus
	 * @param sb
	 * @return
	 */
	private boolean doMonsterFighting(Irol irol, Monster monster, FightingIrolStatus irolStatus,
			FightingMonsterStatus monsterStatus, StringBuilder sb) {
		
		int monsterDamage = this.getMonsterDamage(irol, monster, irolStatus, monsterStatus);
		irolStatus.setHp(this.getHpByDamage(irolStatus.getHp(), monsterDamage));
		
		sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL009", monster.getName(), irol.getName(), monsterDamage));
		sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL010", irol.getName(), irolStatus.getHp()));
		return irolStatus.getHp() == 0;
	}

	/**
	 * 
	 * @param irol
	 * @param monster
	 * @param irolStatus
	 * @param monsterStatus
	 * @param sb
	 * @return
	 */
	private boolean doIrolFighting(Irol irol, Monster monster, FightingIrolStatus irolStatus,
			FightingMonsterStatus monsterStatus, StringBuilder sb) {
		
		int irolDamage = this.getIrolDamage(irol, monster, irolStatus, monsterStatus);
		monsterStatus.setHp(this.getHpByDamage(monsterStatus.getHp(), irolDamage));
		
		sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL009", irol.getName(), monster.getName(), irolDamage));
		sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL010", monster.getName(), monsterStatus.getHp()));
		
		return monsterStatus.getHp() == 0;
	}

	/**
	 * 
	 * @param irol
	 * @param monster
	 * @param irolStatus
	 * @param monsterStatus
	 * @return
	 */
	private int getIrolDamage(Irol irol, Monster monster, FightingIrolStatus irolStatus, FightingMonsterStatus monsterStatus) {
		
		int irolDamage = new BigDecimal(Integer.toString(irolStatus.getAtk()))
				.multiply(this.getAttributeBonus(irol.getAttribute(), monster.getAttribute()))
				.setScale(BigDecimal.ROUND_DOWN).intValue();
		
		irolDamage = irolDamage - monsterStatus.getDef();
		return irolDamage;
	}
	
	/**
	 * 
	 * @param irol
	 * @param monster
	 * @param irolStatus
	 * @param monsterStatus
	 * @return
	 */
	private int getMonsterDamage(Irol irol, Monster monster, FightingIrolStatus irolStatus, FightingMonsterStatus monsterStatus) {
		
		int monsterDamage = new BigDecimal(Integer.toString(monsterStatus.getAtk()))
				.multiply(this.getAttributeBonus(monster.getAttribute(), irol.getAttribute()))
				.setScale(BigDecimal.ROUND_DOWN).intValue();
		
		monsterDamage = monsterDamage - irolStatus.getDef();
		return monsterDamage;
	}

	/**
	 * 
	 * @param monsterDebuffs
	 * @param monsterStatus
	 * @return
	 */
	private boolean processMonsterDebuffs(List<FightingMonsterDebuffStatus> monsterDebuffs, FightingMonsterStatus monsterStatus) {
		boolean isDead = false;
		// process debuff
		for (FightingMonsterDebuffStatus debuffStatus :monsterDebuffs) {
			debuffStatus.setOverCount(debuffStatus.getOverCount() - 1);
			Debuff debuff = debuffStatus.getDebuff();
			//damage
			monsterStatus.setHp(this.getHpByDamage(monsterStatus.getHp(), debuff.getDamage()));
			
			if (monsterStatus.getHp() == 0) {
				return true;
			}
			
			if (debuffStatus.getOverCount() == 0) {
				//reset att
				monsterStatus.setAtk(monsterStatus.getAtk() + debuff.getAtk());
				monsterStatus.setDef(monsterStatus.getDef() + debuff.getDef());
				monsterStatus.setSpeed(monsterStatus.getSpeed() + debuff.getSpeed());
				monsterStatus.setMaxhp(monsterStatus.getMaxhp() + debuff.getMaxHp());
				fightingMonsterDebuffStatusDao.delete(debuffStatus);
			} else {
				fightingMonsterDebuffStatusDao.save(debuffStatus);
			}
		}
		return isDead;
	}

	/**
	 * 
	 * @param monsterBuffs
	 * @param monsterStatus
	 */
	private void processMonsterBuffs(List<FightingMonsterBuffStatus> monsterBuffs, FightingMonsterStatus monsterStatus) {
		// process buff
		for (FightingMonsterBuffStatus buffStatus :monsterBuffs) {
			buffStatus.setOverCount(buffStatus.getOverCount() - 1);
			Buff buff = buffStatus.getBuff();
			//hot
			monsterStatus.setHp(monsterStatus.getHp() + buff.getHeal());
			
			if (buffStatus.getOverCount() == 0) {
				//reset att
				monsterStatus.setAtk(monsterStatus.getAtk() - buff.getAtk());
				monsterStatus.setDef(monsterStatus.getDef() - buff.getDef());
				monsterStatus.setSpeed(monsterStatus.getSpeed() - buff.getSpeed());
				monsterStatus.setMaxhp(monsterStatus.getMaxhp() - buff.getMaxHp() <= 0 ? 1 :  monsterStatus.getMaxhp() - buff.getMaxHp());
				fightingMonsterBuffStatusDao.delete(buffStatus);
			} else {
				fightingMonsterBuffStatusDao.save(buffStatus);
			}
		}
	}

	/**
	 * 
	 * @param irolDebuffs
	 * @param irolStatus
	 * @return
	 */
	private boolean processIrolDebuffs(List<FightingIrolDebuffStatus> irolDebuffs, FightingIrolStatus irolStatus, StringBuilder sb) {
		boolean isDead = false;
		// process debuff
		for (FightingIrolDebuffStatus debuffStatus :irolDebuffs) {
			debuffStatus.setOverCount(debuffStatus.getOverCount() - 1);
			Debuff debuff = debuffStatus.getDebuff();
			//damage
			irolStatus.setHp(this.getHpByDamage(irolStatus.getHp(), debuff.getDamage()));
			
			sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL018", debuff.getName(), debuff.getDamage(), irolStatus.getHp()));
			
			if (irolStatus.getHp() == 0) {
				return true;
			}
			
			if (debuffStatus.getOverCount() == 0) {
				//reset att
				sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL016", debuff.getName()));
				
				irolStatus.setAtk(irolStatus.getAtk() + debuff.getAtk());
				irolStatus.setDef(irolStatus.getDef() + debuff.getDef());
				irolStatus.setSpeed(irolStatus.getSpeed() + debuff.getSpeed());
				irolStatus.setMaxhp(irolStatus.getMaxhp() + debuff.getMaxHp());
				fightingIrolDebuffStatusDao.delete(debuffStatus);
			} else {
				fightingIrolDebuffStatusDao.save(debuffStatus);
			}
		}
		return isDead;
	}

	/**
	 * 
	 * @param irolBuffs
	 * @param irolStatus
	 */
	private void processIrolBuffs(List<FightingIrolBuffStatus> irolBuffs, FightingIrolStatus irolStatus, StringBuilder sb) {
		// process buff
		for (FightingIrolBuffStatus buffStatus :irolBuffs) {
			buffStatus.setOverCount(buffStatus.getOverCount() - 1);
			Buff buff = buffStatus.getBuff();
			//hot
			if (buff.getHeal() > 0) {
				int healing = this.healing(irolStatus.getHp(), buff.getHeal(), irolStatus.getMaxhp());
				irolStatus.setHp(irolStatus.getHp() + healing);
				sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL015", buff.getName(), healing));
			}
			
			if (buffStatus.getOverCount() == 0) {
				//reset att
				sb.append(NudoCCUtil.NEW_LINE).append(NudoCCUtil.codeMessage("IRL016", buff.getName()));
				
				irolStatus.setAtk(irolStatus.getAtk() - buff.getAtk());
				irolStatus.setDef(irolStatus.getDef() - buff.getDef());
				irolStatus.setSpeed(irolStatus.getSpeed() - buff.getSpeed());
				irolStatus.setMaxhp(irolStatus.getMaxhp() - buff.getMaxHp() <= 0 ? 1 :  irolStatus.getMaxhp() - buff.getMaxHp());
				fightingIrolBuffStatusDao.delete(buffStatus);
			} else {
				fightingIrolBuffStatusDao.save(buffStatus);
			}
		}
	}

	/**
	 * 
	 * @param hp
	 * @param heal
	 * @param maxhp
	 * @return
	 */
	private int healing(int hp, int heal, int maxhp) {
		int afterHp = (hp+heal) >= maxhp ? maxhp : (hp+heal);
		return afterHp - hp;
	}

	/**
	 * 
	 * @param hp
	 * @param damage
	 * @return
	 */
	private int getHpByDamage(int hp, int damage) {
		return hp - damage <= 0 ? 0 : hp-damage;
	}

	/**
	 * 
	 * @param attribute1
	 * @param attribute2
	 * @return
	 */
	private BigDecimal getAttributeBonus(int attribute1, int attribute2) {
		switch (attribute1 - attribute2) {
			case   -1:
			case    2:
				return new BigDecimal("0.5");
			case    1:
			case   -2:
			case  198:
			case -198:
				return new BigDecimal("1.5");
			default:
				return BigDecimal.ONE;
		}
	}

	/**
	 * 追擊
	 * 
	 * @param damage
	 * @param speed1
	 * @param speed2
	 * @return
	 */
	private boolean isSpeedBonus(int speed1, int speed2) {
		return speed1 - speed2 >= 5;
	}
	
}
