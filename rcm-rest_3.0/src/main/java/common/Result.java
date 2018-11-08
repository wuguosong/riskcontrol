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
