/**
 * 
 */
package com.cc.service;

import com.cc.bean.CommandBean;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;

/**
 * @author Caleb.Cheng
 *
 */
public interface INudoCCService {
	
	/**
	 * 以name搜尋角色大頭照
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	public ImageMessage getWoWCharacterImgPath(String name);

	/**
	 * 產生角色的template訊息
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	public TemplateMessage buildCharacterTemplate(String name);

	/**
	 * 取得角色裝備資訊
	 * 
	 * @param name :角色名稱
	 * @param realm :伺服器名稱
	 * @return
	 */
	public TextMessage getWoWCharacterItems(String name, String realm);

	/**
	 * 檢核裝備有無附魔
	 * 
	 * @param name :角色名稱
	 * @param realm :伺服器名稱
	 * @return
	 */
	public TextMessage checkCharacterEnchants(String name, String realm);

	/**
	 * 取得協助
	 * 
	 * @return
	 */
	public TextMessage getHelp();

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

	public CommandBean genCommandBean(String command, String senderId, String userId);
}
