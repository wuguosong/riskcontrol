/**
 * 
 */
package com.yk.exception.rcm;

import com.yk.exception.BusinessException;

/**
 * 分页异常
 * @author 80845530
 *
 */
public class PageException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public static final String code = "30001";
	
	private String info;
	
	public PageException() {
		super();
	}
	
	public PageException(String message) {
		super(message);
		this.info = message;
	}

	public PageException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public PageException(Throwable cause) {
        super(cause);
    }

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
