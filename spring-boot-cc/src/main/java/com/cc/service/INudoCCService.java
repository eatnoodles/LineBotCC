package com.cc.service;

import java.util.concurrent.ExecutionException;

import com.cc.bean.CommandBean;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;

/**
 * @author Caleb.Cheng
 *
 */
public interface INudoCCService {

	/**
	 * 根據request傳來的command回傳message
	 * 
	 * @param event
	 * @return
	 */
	public Message processCommand(MessageEvent<TextMessageContent> event);

	/**
	 * @param packageId
	 * @param stickerId
	 * @return
	 */
	public Message findStickerMessage(String packageId, String stickerId);

	/**
	 * 
	 * @param command
	 * @param senderId
	 * @param userId
	 * @return
	 */
	public CommandBean genCommandBean(String command, String senderId, String userId);

	/**
	 * get line display name
	 * 
	 * @param lineId
	 * @return
	 */
	public String getDisplayName(String lineId) throws InterruptedException, ExecutionException;
}
