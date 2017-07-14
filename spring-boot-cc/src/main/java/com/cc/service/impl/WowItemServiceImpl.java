package com.cc.service.impl;

import org.springframework.stereotype.Component;

import com.cc.bean.WowItemParamBean;
import com.cc.bean.WowItemResponse;
import com.cc.service.IWowItemService;
import com.cc.service.WowCommunityService;

@Component
public class WowItemServiceImpl extends WowCommunityService<WowItemResponse, WowItemParamBean> implements IWowItemService {

	@Override
	public Class<? extends WowItemResponse> getResponseType() {
		return WowItemResponse.class;
	}
	
	@Override
	public WowItemResponse doSend(WowItemParamBean paramBean) throws Exception {
		return send(paramBean);
	}

}
