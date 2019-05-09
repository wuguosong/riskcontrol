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
    @Test
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
	}
}
