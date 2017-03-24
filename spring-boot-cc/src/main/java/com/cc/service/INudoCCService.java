/**
 * 
 */
package com.cc.service;

import com.cc.bean.WowCommandBean;
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
	 * 以name、server搜尋角色基本資料
	 * 
	 * @param name :角色名稱
	 * @param server :伺服器名稱
	 * @return
	 */
	public TextMessage findWowCharacterProfile(String name, String server);
	
	/**
	 * 以name搜尋角色基本資料
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	public TextMessage findWowCharacterProfileByName(String name);
	
	/**
	 * 以name搜尋角色大頭照
	 * 
	 * @param name :角色名稱
	 * @return
	 */
	public ImageMessage findWowCharacterImgPath(String name);
	
	/**
	 * 處理前端傳來的wow命令列成bean
	 * 
	 * @param command :命令列
	 * @return
	 */
	public WowCommandBean processCommand(String command);

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
	public TextMessage findWowCharacterItem(String name, String realm);

	/**
	 * 檢核裝備有無附魔
	 * 
	 * @param name :角色名稱
	 * @param realm :伺服器名稱
	 * @return
	 */
	public TextMessage checkCharacterEnchants(String name, String realm);
}
