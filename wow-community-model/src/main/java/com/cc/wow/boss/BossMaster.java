/**
 * 
 */
package com.cc.wow.boss;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class BossMaster {

	private final List<Boss> bosses;
	
	public BossMaster(@JsonProperty("bosses") List<Boss> bosses){
		this.bosses = bosses != null ? bosses : Collections.emptyList();
	}
	
}
