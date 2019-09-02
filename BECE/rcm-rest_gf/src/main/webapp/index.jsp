<%@page import="com.yk.common.SpringUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Map" %> 
<%@page import="javax.servlet.http.Cookie" %>
<%@page import="fnd.SysUser" %>
<%@page import="com.yk.power.service.IUserService" %>
<%@page import="java.net.URLEncoder" %>
<%@page import="com.google.gson.Gson" %>
<%@page import="util.PropertiesUtil,com.sso.client.servlet.SsoUserUtil" %>
<!-- <a href="http://sso.bewg-dev.net/wd_sso_logout.axd">Logout</a> -->
<%
	String userId = (String)request.getAttribute("loginid");
	String requestUrl = (String)request.getParameter("requestUrl");
	String allowDomain = PropertiesUtil.getProperty("domain.allow");
	if(userId == null || "".equals(userId)){
// 		out.print("登录失效，请先登录");
		response.sendRedirect(allowDomain+"/html/index.html");
	}else{
		IUserService user = (IUserService) SpringUtil.getBean("userService");
// 		SysUser user = new SysUser();
		Map userMap = user.getAUser("{userID:'"+userId+"'}");
		Gson gson = new Gson();
		String credentials = gson.toJson(userMap);
		credentials = URLEncoder.encode(credentials, "UTF-8");
		Cookie c1 = new Cookie("credentials", credentials);
		c1.setPath("/");
		Cookie c2 = new Cookie("loged", "true");
		c2.setPath("/");
		response.addCookie(c1);
		response.addCookie(c2);
		if(requestUrl!=null && requestUrl!=""){
			response.sendRedirect(allowDomain+"/html/index.html#/"+requestUrl);
		}else{
			response.sendRedirect(allowDomain+"/html/index.html");
		}
	}
%>