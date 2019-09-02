/**
 * 
 */
package com.yk.adapter;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import util.Util;

import com.yk.common.SpringUtil;
import com.yk.exception.common.BeanCreateException;

import common.BusinessException;
import common.Constants;
import common.Result;

/**
 * @author 80845530
 *
 */
@Service("preServiceAdapter")
@Transactional
public class PreServiceAdapter {
	/**
	 * 无参方法调用
	 * @param className
	 * @param methodName
	 * @return
	 */
	public Object execute(String className, String methodName){
		Object bean = SpringUtil.getBean(className);
		try {
			Method method = bean.getClass().getMethod(methodName);
			return method.invoke(bean);
		} catch (Exception e) {
			if(e.getCause() instanceof BusinessException){
				return new Result(Constants.E, e.getMessage());
			}
			throw new BeanCreateException(e);
		}
	}
	/**
	 * 有参方法调用
	 * @param className
	 * @param methodName
	 * @param param
	 * @return
	 */
	public Object execute(String className, String methodName, String param){
		Object bean = SpringUtil.getBean(className);
		try {
			Method method = bean.getClass().getMethod(methodName, String.class);
			return method.invoke(bean, param);
		} catch (Exception e) {
			if(e.getCause() instanceof BusinessException){
				return new Result(Constants.E, e.getCause().getMessage());
			}
			throw new BeanCreateException(e);
		}
	}
	/**
	 * 文件上传方法调用
	 * @param className
	 * @param methodName
	 * @param param
	 * @param files
	 * @return
	 */
	public Object execute(String className, String methodName, String param, List<FileItem> files){
		Object bean = SpringUtil.getBean(className);
		try {
//			if (files.size() == 1) {
//				//一个文件上传
//				Method method = bean.getClass().getMethod(methodName, String.class, FileItem.class);
//				return method.invoke(bean, param, files.get(0));
//			}else{
				//多个文件上传
				Method method = bean.getClass().getMethod(methodName, String.class, List.class);
				return method.invoke(bean, param, files);
//			}
		} catch (Exception e) {
			if(e.getCause() instanceof BusinessException){
				return new Result(Constants.E, e.getMessage());
			}
			throw new BeanCreateException(e);
		}
			
	}

}
