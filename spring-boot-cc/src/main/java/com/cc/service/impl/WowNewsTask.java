package com.cc.service.impl;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cc.service.INudoCCService;

@Component
public class WowNewsTask extends TimerTask {
	
	@Autowired
	private INudoCCService nudoCCService;
	
	private static final Logger LOG = LoggerFactory.getLogger(WowNewsTask.class);
	
	public void run() {
		LOG.info("WowNewsTask BEGIN");
		//TODO 
		LOG.info("WowNewsTask END");
	}
}
