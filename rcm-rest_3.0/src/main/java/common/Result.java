package common;

import com.google.gson.Gson;

/**
 * 返回信息
 * 
 * @author zhouyoucheng
 *
 */
public class Result {

	// 返回编码，S成功 E异常 R错误
	private String result_code;
	// 返回消息
	private String result_name = "执行成功!";
	// 返回结果
	private Object result_data;
	/**
	 * 对接约会系统返回消息设置
	 * */
	// 返回编码， 200接口正常， 500接口内部错误
	private String error_code;
	// 返回信息
	private String error_msg;
	/**
	 * 是否执行成功
	 */
	private boolean success = true;

	public Result() {
	}

	public Result(String result_code, String result_name) {
		this.result_code = result_code;
		this.result_name = result_name;
	}

	public Result(String result_code, String result_name, Object result_data) {
		this.result_code = result_code;
		this.result_name = result_name;
		this.result_data = result_data;
	}

	public String getResult_code() {
		return result_code;
	}

	public Result setResult_code(String result_code) {
		this.result_code = result_code;
		return this;
	}

	public String getResult_name() {
		return result_name;
	}

	public Result setResult_name(String result_name) {
		this.result_name = result_name;
		return this;
	}

	public Object getResult_data() {
		return result_data;
	}

	public Result setResult_data(Object result_data) {
		this.result_data = result_data;
		return this;
	}
	
	public String getError_code() {
		return error_code;
	}

	public Result setError_code(String error_code) {
		this.error_code = error_code;
		return this;
	}
	
	public String getError_msg() {
		return error_msg;
	}

	public Result setError_msg(String error_msg) {
		this.error_msg = error_msg;
		return this;
	}

	public String toString() {
		/*Document doc = new Document();
		doc.put("result_code", this.result_code);
		doc.put("result_data", this.result_data);
		doc.put("result_name", this.result_name);*/
		Gson gs = new Gson();
		return gs.toJson(this);
	}

	public boolean isSuccess() {
		return success;
	}

	public Result setSuccess(boolean success) {
		this.success = success;
		return this;
	}
}
