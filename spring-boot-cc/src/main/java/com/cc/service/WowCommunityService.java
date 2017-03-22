/**
 * 
 */
package com.cc.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.cc.bean.BaseWOWParamBean;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Caleb.Cheng
 *
 */
public abstract class WowCommunityService < TResponseDataBean, TParamBean extends BaseWOWParamBean > {

	@Autowired
	private IRemoteService remoteService;
	
	protected TResponseDataBean send (TParamBean paramBean, Class<TResponseDataBean> responseClz) throws Exception {
		String response = remoteService.call(paramBean.getUrl());
		ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TResponseDataBean result = mapper.readValue(response, responseClz);
		return result;
	}
}
