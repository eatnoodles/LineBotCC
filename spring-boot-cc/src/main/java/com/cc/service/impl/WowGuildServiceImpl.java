package com.cc.service.impl;

import org.springframework.stereotype.Component;

import com.cc.bean.WowGuildParamBean;
import com.cc.bean.WowGuildResponse;
import com.cc.service.IWowGuildService;
import com.cc.service.WowCommunityService;

@Component
public class WowGuildServiceImpl extends WowCommunityService<WowGuildResponse, WowGuildParamBean> implements IWowGuildService {

	@Override
	public Class<? extends WowGuildResponse> getResponseType() {
		return WowGuildResponse.class;
	}
	
	@Override
	public WowGuildResponse doSendNews(WowGuildParamBean paramBean) throws Exception {
		return send(paramBean);
	}

}
