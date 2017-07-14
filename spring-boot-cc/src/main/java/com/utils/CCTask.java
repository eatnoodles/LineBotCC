package com.utils;

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.service.INudoCCService;
@Component
public class CCTask extends TimerTask {
	
	@Autowired
	private INudoCCService nudoCCService;
	
	private static CCTask instance = null;

	
	private CCTask() {
		
	}
	
	public static CCTask getInstance() {
		if (instance == null) {
			synchronized (CCTask.class) {
				if (instance == null) {
					instance = new CCTask();
				}
			}
		}
		return instance;
	}
	
	public void run() {
		nudoCCService.processGuildNew();
	}
}
