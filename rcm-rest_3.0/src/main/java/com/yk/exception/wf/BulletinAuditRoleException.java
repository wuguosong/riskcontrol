/**
 * 
 */
package com.yk.exception.wf;

import com.yk.exception.BusinessException;

/**
 * 与第三方系统通过webservice交互时，文件上传下载过程中发生错误的异常定义
 * @author 80845530
 *
 */
public class BulletinAuditRoleException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String code = "21001";
	
	private String info;
	
	public BulletinAuditRoleException() {
		super();
	}
	
	public BulletinAuditRoleException(String message) {
		super(message);
		this.info = message;
	}

	public BulletinAuditRoleException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BulletinAuditRoleException(Throwable cause) {
        super(cause);
    }

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}
