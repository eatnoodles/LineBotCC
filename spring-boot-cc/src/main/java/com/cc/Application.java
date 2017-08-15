package com.cc;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.cc.bean.CommandBean;
import com.cc.bean.WoWCommandBean;
import com.cc.service.INudoCCService;
import com.cc.service.IWoWService;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

/**
 * 
 * @author Caleb.Cheng
 *
 */
@SpringBootApplication
@ComponentScan
@LineMessageHandler
public class Application {
	
	@Autowired
	private INudoCCService nudoCCService;
	
	@Autowired
	private IWoWService wowService;
	
	/**
	 * 
	 * @param args
	 */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 
     * @param event
     * @return
     */
    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
    	return nudoCCService.processCommand(event);
    }
    
    /**
     * 
     * @param event
     * @return
     */
    @EventMapping
    public Message handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
    	return nudoCCService.findStickerMessage(event.getMessage().getPackageId(), event.getMessage().getStickerId());
    }

	/**
     * 
     * @param event
     */
    @EventMapping
    public Message handleDefaultMessageEvent(PostbackEvent event) {
    	String command = event.getPostbackContent().getData();
        String senderId = event.getSource().getSenderId();
        String userId = event.getSource().getUserId();
    	
    	if (StringUtils.isNotBlank(command)) {
        	CommandBean commandBean = nudoCCService.genCommandBean(command, senderId, userId);
        	if (StringUtils.isNotBlank(commandBean.getErrorMsg())) {
        		return new TextMessage(commandBean.getErrorMsg());
        	} else {
        		if (commandBean instanceof WoWCommandBean) {
        			
        		}
        		WoWCommandBean wowCommandBean = (WoWCommandBean)commandBean;
        		switch (wowCommandBean.getEventEnum()) {
					case CHARACTER_ITEM:
						return wowService.getWoWCharacterItems(wowCommandBean.getName(), wowCommandBean.getRealm());
					case CHECK_ENCHANTS:
						return wowService.checkCharacterEnchants(wowCommandBean.getName(), wowCommandBean.getRealm());
					default:
						return null;
				}
        	}
        } else {
            return null;
        }
    }
}
