package com.utils;

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.PushMessage;

public class CCTask extends TimerTask {
	
	@Autowired
	private LineMessagingService retrofitImpl;
	
	public void run() {
//		PushMessage pushMessage = new PushMessage(to, messages)
//		retrofitImpl.pushMessage(pushMessage);
	}
}
