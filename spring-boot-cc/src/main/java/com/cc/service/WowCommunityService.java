/**
 * 
 */
package com.cc.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.cc.bean.BaseWOWParamBean;
import com.cc.bean.BaseWOWResponse;
import com.cc.service.impl.RemoteServiceImpl;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Caleb.Cheng
 *
 */
public abstract class WowCommunityService < TResponseDataBean extends BaseWOWResponse, TParamBean extends BaseWOWParamBean > {

	@Autowired
	private IRemoteService remoteService = new RemoteServiceImpl();
	
	protected TResponseDataBean send (TParamBean paramBean) throws Exception {
		String response = remoteService.call(paramBean.getUrl());
		ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TResponseDataBean result = mapper.readValue(response, getResponseType());
		return result;
	}
	
	public abstract Class<? extends TResponseDataBean> getResponseType();
}
