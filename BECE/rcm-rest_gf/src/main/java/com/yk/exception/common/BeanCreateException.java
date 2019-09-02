package com.yk.exception.common;

import com.yk.exception.BusinessException;

public class BeanCreateException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
public static final String code = "00001";
	
	private String info;
	
	public BeanCreateException() {
		super();
	}
	
	public BeanCreateException(String message) {
		super(message);
		this.info = message;
	}

	public BeanCreateException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BeanCreateException(Throwable cause) {
        super(cause);
    }

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
