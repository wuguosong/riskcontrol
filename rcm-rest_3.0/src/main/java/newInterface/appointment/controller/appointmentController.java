package newInterface.appointment.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;

import common.Result;
import newInterface.appointment.service.IAppointmentService;
import util.EncryptUtil;
import util.ThreadLocalUtil;
import util.Util;

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
	 * 接收约会系统传送会议相关数据
	 * 有parameterName，为：par
	 * */
	@RequestMapping(value="/getMeetingTypeInfo", method = RequestMethod.POST)
	@ResponseBody
	public Result getMeetingTypeInfo(String par){
		Result result = new Result();
		System.out.println("查看约会系统传送过来的会议类型相关数据，par = " + par);
		/*try {*/
			//this.appointmentService.saveMeeting(par);
			result.setError_code("200");
			result.setError_msg("成功");
		/*} catch (Exception e) {
			result.setError_code("500");
			result.setError_msg("服务器内部错误");
		}*/
		return result;
	}
	
	/**
	 * 接收约会系统传送约会相关数据
	 * 有parameterName，为：par
	 * */
	@RequestMapping(value="/getMeetingInfo", method = RequestMethod.POST)
	@ResponseBody
	public Result getMeetingInfo(String par){
		Result result = new Result();
		try {
			this.appointmentService.saveMeeting(par);
			result.setError_code("200");
			result.setError_msg("成功");
		} catch (Exception e) {
			result.setError_code("500");
			result.setError_msg("服务器内部错误");
		}
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
	        String str = Util.getUUID().substring(0, 8);
	        params.put("Rnd", str);
	        params.put("Key", EncryptUtil.MD5(str + "fkxt2019)$)%"));
	        params.put("OperatorId", ThreadLocalUtil.getUserId());
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
	    return result ;
	}
}
