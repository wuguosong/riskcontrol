package com.yk.log.aspect;

import com.google.gson.Gson;
import com.yk.log.annotation.SysLog;
import com.yk.log.entity.SysLogDto;
import com.yk.log.service.ISysLogService;
import com.yk.log.utils.HttpContextUtils;
import com.yk.log.utils.IPUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.DateUtil;
import util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


/**
 * 系统日志，切面处理类
 */
@Aspect
@Component
public class SysLogAspect {
	@Resource
	private ISysLogService sysLogService;
	
	@Pointcut("@annotation(com.yk.log.annotation.SysLog)")
	public void logPointCut() {
		
	}

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		Object object = null;
		String success = "Y";
		long beginTime = System.currentTimeMillis();
		//执行方法
		try{
			object = point.proceed();
		}catch (Exception e){
			success = "N";
		}
		//执行时长(毫秒)
		long time = System.currentTimeMillis() - beginTime;
		//保存日志
		saveSysLog(point, time, success);
		return object;
	}

	private void saveSysLog(ProceedingJoinPoint joinPoint, long time, String success) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		SysLogDto sysLogDto = new SysLogDto();
		SysLog syslog = method.getAnnotation(SysLog.class);
		if(syslog != null){
			//注解上的描述
			sysLogDto.setOperation(syslog.operation());
			sysLogDto.setCode(syslog.code());
			sysLogDto.setModule(syslog.module());
			sysLogDto.setDescription(syslog.description());
		}
		//请求的方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();
		sysLogDto.setMethod(className + "." + methodName + "()");
		//请求的参数
		Object[] args = joinPoint.getArgs();
		try{
			String params = new Gson().toJson(args);
			sysLogDto.setParams(params);
		}catch (Exception e){

		}
		//获取request
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		//设置IP地址
		sysLogDto.setIp(IPUtils.getIpAddr(request));
		//用户名
		sysLogDto.setUserName(UserUtil.getCurrentUser().getName());
		sysLogDto.setUser(UserUtil.getCurrentUserUuid());
		sysLogDto.setTime(time);
		sysLogDto.setSuccess(success);
		sysLogDto.setCreateDate(DateUtil.getCurrentDate());
		//保存系统日志
		sysLogService.save(sysLogDto);
	}
}
