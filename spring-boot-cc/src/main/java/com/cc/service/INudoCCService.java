/**
 * 
 */
package com.cc.service;

/**
 * @author Caleb.Cheng
 *
 */
public interface INudoCCService {

	public String findWowCharacterProfile(String name, String server);
	
	public String findWowCharacterProfileByName(String name);
	
	public String findWowCharacterImgPath(String name);
}
