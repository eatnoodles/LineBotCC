package com.cc.service.impl;

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.service.INudoCCService;

@Component
public class WowNewsTask extends TimerTask {
	
	@Autowired
	private INudoCCService nudoCCService;
	
//	private LineMessagingService retrofitImpl;
	
	public void run() {
//		retrofitImpl = LineMessagingServiceBuilder.create(System.getenv("LINE_BOT_CHANNEL_TOKEN")).build();
//		LineMessagingClientImpl client = new LineMessagingClientImpl(retrofitImpl);
//		
//		List<Message> messages = new ArrayList<>();
//		messages.add(new TextMessage("test"));
//		PushMessage pushMessage = new PushMessage("U220c4d64ae3d59601364677943517c91", messages);
//		client.pushMessage(pushMessage);
		nudoCCService.processGuildNew();
	}
}
