/**
 * 
 */
package util;

import java.util.Map;

/**
 * 缓存工具类
 * @author 80845530
 *
 */
public class ThreadLocalUtil {
	private static ThreadLocal<String> userId = new ThreadLocal<String>();
	private static ThreadLocal<Boolean> isAdmin = new ThreadLocal<Boolean>();
	private static ThreadLocal<Map<String, Object>> user = new ThreadLocal<Map<String, Object>>();
	
	public static String getUserId(){
		return userId.get();
	}
	
	public static void setUserId(String uid){
		userId.set(uid);
	}

	public static boolean getIsAdmin() {
		return isAdmin.get();
	}

	public static void setIsAdmin(boolean isadmin) {
		isAdmin.set(isadmin);
	}
	
	public static void setUser(Map<String, Object> data){
		user.set(data);
	}
	
	public static Map<String, Object> getUser(){
		Map<String, Object> userInfo = user.get();
		if(userInfo == null){
			throw new RuntimeException("获取用户信息失败！");
		}
		return userInfo;
	}
}
