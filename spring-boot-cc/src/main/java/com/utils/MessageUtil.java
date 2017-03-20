/**
 * 
 */
package com.utils;

import com.linecorp.bot.model.message.TextMessage;

/**
 * @author Caleb.Cheng
 *
 */
public class MessageUtil {

	/**
	 * 
	 * @param message
	 * @return
	 */
	public static TextMessage getCaculResult(String message) {
		try {
			return new TextMessage(FormulaParser.parse(message).toPlainString());
		} catch(Exception e) {
			return null;
		}
	}
	
}
