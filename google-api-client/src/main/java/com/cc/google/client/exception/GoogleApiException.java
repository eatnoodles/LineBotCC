package com.cc.google.client.exception;

import lombok.Getter;

/**
 * @author Caleb Cheng
 *
 */
@Getter
public class GoogleApiException extends Exception {

	private static final long serialVersionUID = 1750451883345577499L;

	public GoogleApiException(final String message) {
        super(message, null);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}