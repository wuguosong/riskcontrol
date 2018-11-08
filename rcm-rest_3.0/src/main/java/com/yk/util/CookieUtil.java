/**
 * 
 */
package com.yk.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;


/**
 * @author wufucan
 *
 */
public class CookieUtil {
	
	public static Cookie addCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String value,
			Integer expiry, String domain,String path) {
		Cookie cookie = new Cookie(name, value);
		if (expiry != null) {
			cookie.setMaxAge(expiry);
		}
		if (StringUtils.isNotBlank(domain)) {
			cookie.setDomain(domain);
		}
		cookie.setPath(path);
		response.addCookie(cookie);
		return cookie;
	}

	
	public static void cancleCookie(HttpServletRequest request,
			HttpServletResponse response, String name, String domain) {
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0);
		String ctx = request.getContextPath();
		if (name.equals("AuthUser_LoginId") || name.equals("AuthUser_AuthNum")
				|| name.equals("AuthUser_AuthToken")
				|| name.equals("AuthUser_AuthMAC")){
			cookie.setPath("/");
		} else {
			cookie.setPath(StringUtils.isBlank(ctx) ? "/" : ctx);
		}
		if (StringUtils.isNotBlank(domain)) {
			cookie.setDomain(domain);
		}
		response.addCookie(cookie);
	}

	
}
