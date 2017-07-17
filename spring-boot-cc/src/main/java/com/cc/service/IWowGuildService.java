package com.cc.service;

import com.cc.bean.WowGuildParamBean;
import com.cc.bean.WowGuildResponse;

public interface IWowGuildService {

	public WowGuildResponse doSendNews(WowGuildParamBean paramBean) throws Exception;
}
