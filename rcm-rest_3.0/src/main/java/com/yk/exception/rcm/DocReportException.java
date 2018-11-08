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
public class DocReportException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public static final String code = "40001";
	
	private String info;
	
	public DocReportException() {
		super();
	}
	
	public DocReportException(String message) {
		super(message);
		this.info = message;
	}

	public DocReportException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DocReportException(Throwable cause) {
        super(cause);
    }

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
