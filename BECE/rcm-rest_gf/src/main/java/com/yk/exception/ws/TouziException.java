/**
 * 
 */
package com.yk.exception.ws;

import com.yk.exception.BusinessException;

/**
 * 与第三方系统通过webservice交互时，文件上传下载过程中发生错误的异常定义
 * @author 80845530
 *
 */
public class TouziException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String code = "10002";
	
	public TouziException() {
		super();
	}
	
	public TouziException(String message) {
		super(message);
	}

	public TouziException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TouziException(Throwable cause) {
        super(cause);
    }
	
}
