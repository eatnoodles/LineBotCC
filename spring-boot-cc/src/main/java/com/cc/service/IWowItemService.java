package com.cc.service;

import com.cc.bean.WowItemParamBean;
import com.cc.bean.WowItemResponse;

public interface IWowItemService {

	public WowItemResponse doSend(WowItemParamBean paramBean) throws Exception;
}
