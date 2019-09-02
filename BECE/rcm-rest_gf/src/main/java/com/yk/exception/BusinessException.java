/**
 * 
 */
package com.yk.exception;

/**
 * 系统级异常的父类
 * @author 80845530
 *
 */
public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public final static String prefix = "RCM:";
	
	public BusinessException() {
		super();
	}
	
	public BusinessException(String message) {
		super(prefix+message);
	}

	public BusinessException(String message, Throwable cause) {
		super(prefix+message, cause);
	}
	
	public BusinessException(Throwable cause) {
        super(cause);
    }
	
}
