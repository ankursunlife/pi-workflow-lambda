package com.aarete.pi.claimprocess.helper;

public class BadRequestExceptionHandler extends RuntimeException {


	private static final long serialVersionUID = 1L;
	
	public BadRequestExceptionHandler() {
		super();
	}
	
	public BadRequestExceptionHandler(String message) {
		super(message);
	}
	
}