package com.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.linecorp.bot.client.LineMessagingClientImpl;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;

public class CCTask extends TimerTask {
	
	private int i = 0;
	
	private static CCTask instance = null;

	
	private CCTask() {
		
	}
	
	public static CCTask getInstance(LineMessagingClientImpl client) {
		if (instance == null) {
			synchronized (CCTask.class) {
				if (instance == null) {
					instance = new CCTask(client);
				}
			}
		}
		return instance;
	}
	
	private CCTask(LineMessagingClientImpl client){
		this.client = client;
	}
	
	private LineMessagingClientImpl client;
	
	public void run() {
		List<Message> messages = new ArrayList<>();
		messages.add(new TextMessage("測試" + i+1));
		PushMessage pushMessage = new PushMessage("U220c4d64ae3d59601364677943517c91", messages);
		client.pushMessage(pushMessage);
	}
}
