/**
 * 
 */
package com.cc.service.impl;

import org.springframework.stereotype.Component;

import com.cc.bean.WowCharacterProfileItemResponse;
import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;
import com.cc.service.IWowCharacterProfileService;
import com.cc.service.WowCommunityService;

/**
 * @author Caleb.Cheng
 *
 */
@Component
public class WowCharacterProfileServiceImpl extends WowCommunityService<WowCharacterProfileResponse, WowCharacterProfileParamBean> implements IWowCharacterProfileService{

	private Class<? extends WowCharacterProfileResponse> responseType;
	
	@Override
	public WowCharacterProfileResponse doSendProfile(WowCharacterProfileParamBean paramBean) throws Exception {
		this.responseType = WowCharacterProfileResponse.class;
		return send(paramBean);
	}

	@Override
	public WowCharacterProfileItemResponse doSendItem(WowCharacterProfileParamBean paramBean) throws Exception {
		this.responseType = WowCharacterProfileItemResponse.class;
		return (WowCharacterProfileItemResponse) send(paramBean);
	}

	@Override
	public Class<? extends WowCharacterProfileResponse> getResponseType() {
		return responseType;
	}
}
