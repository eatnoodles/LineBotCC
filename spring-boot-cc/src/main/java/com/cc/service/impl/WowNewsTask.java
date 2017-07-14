package com.cc.service.impl;

import java.util.TimerTask;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.service.INudoCCService;

@Component
public class WowNewsTask extends TimerTask {
	
	@Autowired
	private INudoCCService nudoCCService;
	
	public void run() {
		nudoCCService.processGuildNew();
	}
}
