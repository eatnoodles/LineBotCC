package com.cc.service;

import com.linecorp.bot.model.message.Message;

/**
 * @author Caleb Cheng
 *
 */
public interface IIrolService {

	public Message getIrols(String userId);

	public Message doBattle(String userId, String irolName);

}
