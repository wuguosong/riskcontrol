package Util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import util.EncryptUtil;
import util.ThreadLocalUtil;
import util.Util;

/**
 * 
 * @author Sunny Qi
 * 
 * 20190412
 *
 */
public class EncryptTest {
    /*@Test
    public void sendData(){
	    HttpClient client = new HttpClient() ;
	    PostMethod method = null;//post 方式   get 方式 GetMethod gMethod
	    String result = "" ;
	    try {
	        Map<String, Object> params = new HashMap<String, Object>();
	        String str = Util.getUUID().substring(0, 8);
	        params.put("Rnd", "tgbj");
	        params.put("Key", EncryptUtil.MD5("tgbj" + "fkxt2019)$)%"));
	        params.put("OperatorId", "0001N6100000000M9HN7");
	        params.put("SystemCode", "001");
	        JSONObject Json = (JSONObject)JSON.toJSON(params);
	    	method = new PostMethod("http://bksitenew.hengtaiboyuan.com/AppointmentMeeTingManage/WebAPI/GetMeeTingTypeListsByPartyCSystemId") ;
	        method.setParameter("par",Json.toString());//设置参数
	        client.executeMethod(method);
	        System.out.println(method.getStatusCode());
	        if(method.getStatusCode() == HttpStatus.SC_OK){//响应成功
	            result = method.getResponseBodyAsString();//调用返回结果
	            System.out.println(result);
	        }else{//不成功组装结果
	            Map<String , Object >map = new HashMap<String , Object>();
	            Gson gson = new Gson() ;
	            map.put("success", false);
	            result = gson.toJson(map);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }finally{
	      method.releaseConnection();
	    }
	}*/
	
	@Test
	public void test (){
		String str = "\u9884\u8BC4\u5BA1\u76F8\u5173\u9644\u4EF6\u5B58\u653E\u8DEF\u5F84，"
				+ "\u6B63\u5F0F\u8BC4\u5BA1\u6A21\u677F\u5B58\u653E\u4F4D\u7F6E，"
				+ "\u6B63\u5F0F\u8BC4\u5BA1\u76F8\u5173\u9644\u4EF6\u5B58\u653E\u8DEF\u5F84，"
				+ "\u9884\u8BC4\u5BA1\u62A5\u544A\u5B58\u50A8\u8DEF\u5F84，"
				+ "\u6B63\u5F0F\u8BC4\u5BA1\u62A5\u544A\u5B58\u50A8\u8DEF\u5F84，"
				+ "\u51B3\u7B56\u901A\u77E5\u4E66\u7533\u8BF7\u5355\u751F\u6210word\u6587\u6863\u5B58\u50A8\u8DEF\u5F84，"
				+ "\u51B3\u7B56\u901A\u77E5\u4E66\u6700\u7EC8\u9886\u5BFC\u7B7E\u5B57\u540E\u7684\u6587\u4EF6，"
				+ "\u5176\u4ED6\u9700\u51B3\u7B56\u4E8B\u9879\u4F1A\u8BAE\u7EAA\u8981，"
				+ "\u5176\u4ED6\u9700\u51B3\u7B56\u4E8B\u9879\u6CD5\u5F8B\u9644\u4EF6\u5B58\u50A8\u8DEF\u5F84，"
				+ "\u5176\u4ED6\u9700\u51B3\u7B56\u4E8B\u9879\u4E1A\u52A1\u9644\u4EF6\u5B58\u50A8\u8DEF\u5F84，"
				+ "\u4E0A\u4F1A\u4FE1\u606F\u9644\u4EF6，"
				+ "\u6863\u6848\u7CFB\u7EDF\u6587\u4EF6\u8DEF\u5F84，"
				+ "\u5B58\u653E\u4E34\u65F6\u6587\u4EF6\u7684\u6587\u4EF6\u5939，"
				+ "\u5B58\u653E\u6587\u4EF6\u7684\u6839\u8DEF\u5F84";
		System.out.println(str);
	}
}
