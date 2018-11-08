/**
 * 
 */
package com.yk.exception.ws;

import com.yk.exception.BusinessException;

/**
 * 与第三方系统通过webservice交互时，向合同系统推送数据失败异常
 * @author 80845530
 *
 */
public class HetongException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String code = "10003";
	
	public HetongException() {
		super();
	}
	
	public HetongException(String message) {
		super(message);
	}

	public HetongException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public HetongException(Throwable cause) {
        super(cause);
    }
	
}
