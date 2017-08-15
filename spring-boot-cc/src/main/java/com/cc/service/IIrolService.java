package com.cc.service;

import com.cc.bean.IrolCommandBean;
import com.linecorp.bot.model.message.Message;

/**
 * @author Caleb Cheng
 *
 */
public interface IIrolService {

	public Message getIrols(String userId);

	public Message doBattle(String userId, String irolName);

	public boolean isIrolCommand(String command);

	public IrolCommandBean genIrolCommandBean(String command, String senderId, String userId);

}
