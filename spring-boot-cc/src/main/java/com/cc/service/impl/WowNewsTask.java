package com.cc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.bean.WowGuildParamBean;
import com.cc.bean.WowGuildResponse;
import com.cc.bean.WowGuildResponse.New;
import com.cc.bean.WowItemParamBean;
import com.cc.bean.WowItemResponse;
import com.cc.service.IWowGuildService;
import com.cc.service.IWowItemService;
import com.linecorp.bot.client.LineMessagingClientImpl;
import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.utils.NudoCCUtil;

@Component
public class WowNewsTask extends TimerTask {
	
	@Autowired
	private IWowItemService wowItemService;
	
	@Autowired
	private IWowGuildService wowGuildService;
	

	private static Map<String, WowItemResponse> legendMap = new ConcurrentHashMap<>();
	
	private LineMessagingService retrofitImpl;
	
	public void run() {
//		retrofitImpl = LineMessagingServiceBuilder.create(System.getenv("LINE_BOT_CHANNEL_TOKEN")).build();
//		LineMessagingClientImpl client = new LineMessagingClientImpl(retrofitImpl);
//		
//		List<Message> messages = new ArrayList<>();
//		messages.add(new TextMessage("test"));
//		PushMessage pushMessage = new PushMessage("U220c4d64ae3d59601364677943517c91", messages);
//		client.pushMessage(pushMessage);
		processGuildNew();
	}
	
	public void processGuildNew() {
		List<New> news = getNews();
		Date now = new Date();
		if (news != null && news.isEmpty()) {
			for (New guildNew :news) {
				//15hr
				if ((now.getTime() - guildNew.getTimestamp()) > 57600000 || !"itemLoot".equalsIgnoreCase(guildNew.getType())) {
					continue;
				}
				WowItemResponse item = getItemById(guildNew.getItemId());
				
//				if ("970".equals(item.getItemLevel())) {
					String character = guildNew.getCharacter();
					String itemName = item.getName();
					String key = character + guildNew.getTimestamp();
					if (!legendMap.containsKey(key)) {
						sendMessageToUser(new TextMessage(String.format("[%s]取得一件[%s]-[%s]", character, item.getItemLevel(), itemName)));
						legendMap.put(key, item);
					}
//				}
			}
		}
	}
	
	private void sendMessageToUser(TextMessage textMessage) {
//		"Cb5dbe73a17f36fda9b3bb23f4eea8fa5"
		retrofitImpl = LineMessagingServiceBuilder.create(System.getenv("LINE_BOT_CHANNEL_TOKEN")).build();
		LineMessagingClientImpl client = new LineMessagingClientImpl(retrofitImpl);
		
		List<Message> messages = new ArrayList<>();
		messages.add(textMessage);
		PushMessage pushMessage = new PushMessage("U220c4d64ae3d59601364677943517c91", messages);
		client.pushMessage(pushMessage);
	}

	private WowItemResponse getItemById(String itemId) {
		WowItemParamBean paramBean = new WowItemParamBean();
		paramBean.setItemId(itemId);
		try {
			WowItemResponse resp = wowItemService.doSend(paramBean);
			return resp;
		} catch (Exception e) {
			return null;
		}
	}

	private List<New> getNews() {
		WowGuildParamBean paramBean = new WowGuildParamBean();
		paramBean.setGuild("Who is Ur Daddy");
		paramBean.setRealm(NudoCCUtil.DEFAULT_SERVER);
		try {
			WowGuildResponse resp = wowGuildService.doSendNews(paramBean);
			if (resp.getNews() != null && !resp.getNews().isEmpty()) {
				return resp.getNews();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
