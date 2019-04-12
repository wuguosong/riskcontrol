package newInterface.appointment.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import common.Result;
import newInterface.appointment.service.IAppointmentService;
import util.EncryptUtil;
import util.ThreadLocalUtil;

/**
 * 
 * @author Sunny Qi
 * 
 * 20190411
 *
 */
@RequestMapping("/meetingManage/appointmentAPI")
@Controller
public class appointmentController {
	
	@Resource
	private IAppointmentService appointmentService;
	
	/**
	 * 接收约会系统传送约会相关数据
	 * */
	@RequestMapping(value="/getMeetingInfo/{par}", method = RequestMethod.GET)
	@ResponseBody
	public Result getMeetingInfo(@PathVariable("par")String Json){
		Result result = new Result();
		this.appointmentService.saveMeeting(Json);
		
		return result;
	}
	
	
	/**
	 * 调用约会系统接口，同步数据过去
	 * */
	@RequestMapping("/sendData")
	public String sendData(){
	    HttpClient client = new HttpClient() ;
	    PostMethod method = null;//post 方式   get 方式 GetMethod gMethod
	    String result = "" ;
	    try {
	        Map<String, Object> params = new HashMap<String, Object>();
	        params.put("rnd", "tgbj");
	        params.put("key", EncryptUtil.MD5("tgbj" + "fkxt2019)$)%"));
	        params.put("OperatorId", ThreadLocalUtil.getUserId());
	        params.put("SystemCode", "");
	        JSONObject Json = (JSONObject)JSON.toJSON(params);
	    	method = new PostMethod("http://bkapitest.hengtaiboyuan.com/AppointmentMeeTingManage/WebAPI/GetMeeTingTypeListsByPartyCSystemId") ;
	        method.setParameter("param",Json.toString());//设置参数
	        client.executeMethod(method);
	        if(method.getStatusCode() == HttpStatus.SC_OK){//响应成功
	            result = method.getResponseBodyAsString();//调用返回结果
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
	    return result ;
	}
}
