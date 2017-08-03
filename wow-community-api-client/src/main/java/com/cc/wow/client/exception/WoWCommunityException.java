package com.cc.wow.client.exception;

import lombok.Getter;

/**
 * @author Caleb Cheng
 *
 */
@Getter
public class WoWCommunityException extends Exception {

	private static final long serialVersionUID = 2173876289552191918L;

	public WoWCommunityException(final String message) {
        super(message, null);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}