package com.cc.service;

import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;

/**
 * @author Caleb Cheng
 *
 */
public interface IWoWService {
	
	/**
	 * 取得協助
	 * 
	 * @return
	 */
	public TextMessage getHelp();

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
	 * 取得角色WCL資訊
	 * 
	 * @param name
	 * @param realm
	 * @param location
	 * @param metric
	 * @return
	 */
	public Message getCharacterWCL(String name, String realm, String location, String metric, String mode);

	/**
	 * save character by line id
	 * 
	 * @param name
	 * @param realm
	 * @param location
	 * @param userId
	 * @return
	 */
	public Message saveCharacter(String name, String realm, String location, String userId);
}
