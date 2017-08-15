package com.cc.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.dao.irol.IIrolDao;
import com.cc.dao.irol.IMasterCardsDao;
import com.cc.dao.irol.IMonsterDao;
import com.cc.dao.irol.ISkillDao;
import com.cc.entity.irol.Irol;
import com.cc.entity.irol.MasterCards;
import com.cc.entity.irol.Monster;
import com.cc.entity.key.MasterCardsKey;
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
	private ISkillDao skillDao;
	
	@Autowired
	private IIrolDao irolDao;
	
	@Autowired
	private IMonsterDao monsterDao;
	
	@Autowired
	private IMasterCardsDao masterCardsDao;

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
		monsterDao.save(monster);
		List<Action> actions = new ArrayList<>();
		
		String cmd = "-irol -battle " + irol.getId() + ";" + monster.getId();
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

	
}
