/**
 * 
 */
package com.cc.service;

import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;

/**
 * @author Caleb.Cheng
 *
 */
public interface IWowCharacterProfileService {
	
	public WowCharacterProfileResponse doSend(WowCharacterProfileParamBean paramBean) throws Exception;

}
