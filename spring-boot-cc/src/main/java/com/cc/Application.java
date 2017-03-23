/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.cc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.cc.service.INudoCCService;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapAction;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.utils.NudoCCUtil;

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
        String mesg = event.getMessage().getText();
        if (StringUtils.isNotBlank(mesg)) {
        	if (mesg.startsWith("-wow ")) {
        		//wow api
        		mesg = mesg.replaceAll("-wow ", StringUtils.EMPTY).trim();
        		if (mesg.startsWith("-img ")) {
        			String name = mesg.replaceAll("-img ", StringUtils.EMPTY).trim();
            		Pattern patternCh = Pattern.compile(NudoCCUtil.PATTERN_CH);
            		Pattern patternEn = Pattern.compile(NudoCCUtil.PATTERN_EN);
            	    Matcher matcherCh = patternCh.matcher(name);
            	    Matcher matcherEn = patternEn.matcher(name);
            	    if ( (matcherCh.matches() && name.length() <= 6) ||
            	    	 (matcherEn.matches() && name.length() <= 12) ) {
            	    	String imgPath = nudoCCService.findWowCharacterImgPath(name);
            	    	return StringUtils.isBlank(imgPath) ? null : new ImageMessage(imgPath, imgPath);
            	    }else {
            	    	return new TextMessage("角色名稱的格式有誤哦~");
            	    }
        		} else {
        			String name = mesg;
            		Pattern patternCh = Pattern.compile(NudoCCUtil.PATTERN_CH);
            		Pattern patternEn = Pattern.compile(NudoCCUtil.PATTERN_EN);
            	    Matcher matcherCh = patternCh.matcher(name);
            	    Matcher matcherEn = patternEn.matcher(name);
            	    if ( (matcherCh.matches() && name.length() <= 6) ||
            	    	 (matcherEn.matches() && name.length() <= 12) ) {
            	    	String result = nudoCCService.findWowCharacterProfileByName(name);
                    	return StringUtils.isBlank(result) ? null : new TextMessage(result);
            	    }else {
            	    	return new TextMessage("角色名稱的格式有誤哦~");
            	    }
        		}
        	} else {
        		//TODO
        		return null;
        	}
        } else {
            return null;
        }

    }

    /**
     * 
     * @param event
     */
    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}
