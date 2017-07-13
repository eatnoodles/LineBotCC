package com.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;

import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;

public class CCTask extends TimerTask {
	
	private int i = 0;
	
	@Autowired
	private LineMessagingService retrofitImpl;
	
	public void run() {
		List<Message> messages = new ArrayList<>();
		messages.add(new TextMessage("測試" + i+1));
		PushMessage pushMessage = new PushMessage("U220c4d64ae3d59601364677943517c91", messages);
		retrofitImpl.pushMessage(pushMessage);
	}
}
