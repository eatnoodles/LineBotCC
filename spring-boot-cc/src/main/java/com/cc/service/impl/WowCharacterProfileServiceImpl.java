/**
 * 
 */
package com.cc.service.impl;

import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;
import com.cc.service.IWowCharacterProfileService;
import com.cc.service.WowCommunityService;

/**
 * @author Caleb.Cheng
 *
 */
public class WowCharacterProfileServiceImpl extends WowCommunityService<WowCharacterProfileResponse, WowCharacterProfileParamBean> implements IWowCharacterProfileService{

	@Override
	public WowCharacterProfileResponse doSend(WowCharacterProfileParamBean paramBean) throws Exception {
		return send(paramBean, WowCharacterProfileResponse.class);
	}

}
