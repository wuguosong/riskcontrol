package com.yk.log.aspect;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.yk.log.annotation.SysLog;
import com.yk.log.entity.SysLogDto;
import com.yk.log.service.ISysLogService;
import com.yk.log.utils.HttpContextUtils;
import com.yk.log.utils.IPUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import util.DateUtil;
import util.UserUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;


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
        try {
            object = point.proceed();
        } catch (Exception e) {
            success = "N";
        }
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        //保存日志
        saveSysLog(point, time, success);
        return object;
    }

    @JsonBackReference
    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, String success) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogDto sysLogDto = new SysLogDto();
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog != null) {
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
        String params = "";
        try {
            System.out.println(args);
            // String params = new Gson().toJson(args);// 遇到request时,堆栈溢出,对象中有对象的互相引用
            params = JSON.toJSONString(args);
            sysLogDto.setParams(params);
        } catch (Exception e) {
        }
        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if(StringUtils.isBlank(params)){
            try {
                Map<String, Object> map = request.getParameterMap();
                sysLogDto.setParams(JSON.toJSONString(map));
            }catch (Exception e){

            }
        }
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
