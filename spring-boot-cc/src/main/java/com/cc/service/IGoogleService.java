package com.cc.service;

import com.linecorp.bot.model.message.TextMessage;

public interface IGoogleService {

	/**
	 * 取得縮網址
	 * 
	 * @return
	 */
	public TextMessage getShortenURL(String url);
}
