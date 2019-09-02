/**
 * 
 */
package com.yk.exception;

/**
 * 系统级异常的父类
 * @author 80845530
 *
 */
public class SystemException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String prefix = "SYS:";
	
	
	public SystemException() {
		super();
	}
	
	public SystemException(String message) {
		super(prefix+message);
	}

	public SystemException(String message, Throwable cause) {
		super(prefix+message, cause);
	}
	
	public SystemException(Throwable cause) {
        super(cause);
    }
	
}
