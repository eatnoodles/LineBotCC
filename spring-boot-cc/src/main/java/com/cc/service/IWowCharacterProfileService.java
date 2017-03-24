/**
 * 
 */
package com.cc.service;

import com.cc.bean.WowCharacterProfileItemResponse;
import com.cc.bean.WowCharacterProfileParamBean;
import com.cc.bean.WowCharacterProfileResponse;

/**
 * @author Caleb.Cheng
 *
 */
public interface IWowCharacterProfileService {
	
	/**
	 * <p>The Character Profile API is the primary way to access character information.</p>
	 * <p>This Character Profile API can be used to fetch a single character at a time through an 
	 *  HTTP GET request to a URL describing the character profile resource. By default, a basic dataset 
	 *  will be returned and with each request and zero or more additional fields can be retrieved.</p>
	 * <p>To access this API, craft a resource URL pointing to the character who's information is to be retrieved.</p>
	 * 
	 * @param paramBean
	 * @return
	 * @throws Exception
	 */
	public WowCharacterProfileResponse doSendProfile(WowCharacterProfileParamBean paramBean) throws Exception;

	
	public WowCharacterProfileItemResponse doSendItem(WowCharacterProfileParamBean paramBean) throws Exception;
}
