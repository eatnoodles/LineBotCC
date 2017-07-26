/**
 * 
 */
package com.cc.wow.guild;

import com.cc.wow.character.CharacterProfileResponse;
import com.cc.wow.character.Spec;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * @author Caleb Cheng
 *
 */
@Value
public class Member {
	
	private final CharacterProfileResponse character;

	private final Spec spec;

	public Member(
			@JsonProperty("character") CharacterProfileResponse character,
			@JsonProperty("spec") Spec spec) {
		this.character = character;
		this.spec = spec;
	}
	
}
