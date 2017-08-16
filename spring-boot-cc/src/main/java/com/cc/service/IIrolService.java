package com.cc.service;

import com.cc.bean.IrolCommandBean;
import com.linecorp.bot.model.message.Message;

/**
 * @author Caleb Cheng
 *
 */
public interface IIrolService {

	/**
	 * 取得該使用者底下的irol
	 * 
	 * @param userId
	 * @return
	 */
	public Message getIrols(String userId);

	/**
	 * battle
	 * 
	 * @param userId
	 * @param irolName
	 * @return
	 */
	public Message doBattle(String userId, String irolName);

	/**
	 * 是否為 irol 命令
	 * 
	 * @param command
	 * @return
	 */
	public boolean isIrolCommand(String command);

	/**
	 * generator irol command bean
	 * 
	 * @param command
	 * @param senderId
	 * @param userId
	 * @return
	 */
	public IrolCommandBean genIrolCommandBean(String command, String senderId, String userId);

	/**
	 * 
	 * @param userId
	 * @param irolId
	 * @param monsterId
	 * @return
	 */
	public Message doFight(String userId, Long irolId, Long monsterId);

}
