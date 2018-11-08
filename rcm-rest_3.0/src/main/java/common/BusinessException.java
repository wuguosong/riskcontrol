package common;

/**
 * 业务异常类，如果需要业务校验，抛出异常须使用此类，否则无法正常返回错误码
 * 
 * @author zhouyoucheng
 *
 */
@Deprecated
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BusinessException() {
		super();
	}

	public BusinessException(String s) {
		super(s);
	}
}
