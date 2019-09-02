package util;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Clob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.xmlbeans.impl.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import common.BusinessException;

/**
 * 工具类
 * 
 * @author zhouyoucheng
 *
 */
public class Util {

	// 是否不为空
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

	// 判断对象是否为空
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		// 字符串
		if (obj instanceof String) {
			String s = (String) obj;
			return trim(s).equals("");
		}
		// Collection集合
		if (obj instanceof Collection<?>) {
			Collection<?> c = (Collection<?>) obj;
			return c.size() == 0;
		}
		// Map集合
		if (obj instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) obj;
			return map.size() == 0;
		}
		// 数组
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}
		return false;
	}

	// 去除字符串两端非字符符号，包括空格，\n，\r等
	public static String trim(String s) {
		return s == null ? "" : s.trim();
	}

	// 获取当前时间
	public static String getTime() {
		return format(new Date(), "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 获取当前时间
	 * @return
	 */
	public static Date now(){
		return Calendar.getInstance().getTime();
	}
	// 获取当前系统毫秒值
	public static long getMs() {
		return new Date().getTime();
	}

	// 将日期按 指定格式 转换成时间
	public static String format(Date date, String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(date);
	}
	//将日期按默认格式转换成时间
	public static String format(Date date) {
		if(date == null) return "";
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(date);
	}
	/**
	 * 将时间按 指定格式 转换成日期,如果转换失败，返回null
	 * @param time
	 * @param format
	 * @return
	 */
	public static Date parse(String time, String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		Date r;
		try {
			r = sf.parse(time);
		} catch (ParseException e) {
			return null;
		}
		return r;
	}

	// 空值校验
	public static Object notNull(String name, Object value) {
		if (isEmpty(value)) {
			throw new BusinessException(new StringBuilder().append(name).append(" can not be null").toString());
		} else {
			return value;
		}
	}
	//将json格式字符串转为Map<String, Object>
	public static Map<String, Object> parseJson2Map(String json){
		Gson gs = new GsonBuilder().create();
		Map<String, Object> map = gs.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
		return map;
	}
	
	/**
	 * 将Map中的key由大写改为小写
	 * @param list
	 * @return 
	 */
	public static List<Map> toLowerList(List<Map> list) {
		List<Map> lowerList = new ArrayList<Map>();
		if(Util.isNotEmpty(list)){
			for(Map<String, Object> map : list){
				Map<String, Object> mapLower = new HashMap<String, Object>();
				for(String key : map.keySet()){
					mapLower.put(key.toLowerCase(), map.get(key));
				}
				lowerList.add(mapLower);
			}
		}
		return lowerList;
	}
	
	//获得当前时间距离传入时间的秒数
	public static Long getDelaySeconds(String executeTime) {
		if(isEmpty(executeTime) || !executeTime.contains(":")) return null;
		String[] array = executeTime.split(":");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(array[0]));
		c.set(Calendar.MINUTE, Integer.valueOf(array[1]));
		c.set(Calendar.SECOND, 0);
		long timeNow = System.currentTimeMillis();
		if(c.getTimeInMillis()<timeNow){
			c.add(Calendar.DATE,1);
		}
		//目标时间减去当前时间
		long delays = c.getTimeInMillis()-timeNow;
		return delays/1000;
	}
	
	// Oracle.sql.Clob类型转换成String类型
	public static String clobToString(Clob clob){
		String reString = "";
		StringBuffer sb;
		try {
			Reader is = clob.getCharacterStream();// 得到流
			BufferedReader br = new BufferedReader(is);
			String s = br.readLine();
			sb = new StringBuffer();
			while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
				sb.append(s);
				s = br.readLine();
			}
			reString = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reString;
	}
	/**
	 * 生成32位的uuid
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String parseException(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		StringBuffer error = new StringBuffer();
		error.append(e.getMessage())
			.append(sw.toString());
		return error.toString();
	}
	/**
	 * url进行编码处理
	 * @param oldurl
	 * @return
	 */
	public static String  encodeUrl(String oldurl){
		try {
			ScriptEngineManager sem = new ScriptEngineManager();
			ScriptEngine engine = sem.getEngineByExtension("js");
			String str = engine.eval("encodeURIComponent(escape('"+oldurl+"'))")+"";
			String out = new String(Base64.encode(str.getBytes("UTF-8")));
			return out;
		} catch (Exception e) {
			throw new RuntimeException("url编码时出错,url:["+oldurl+"]", e);
		}
	}
	
	/**
	   * @Name getDate2
	   * @Description 获取不带时分秒的日期 00:00:00
	   * @Author hubiao
	   * @Version V1.00
	   * @CreateDate 2017年2月11日 上午8:37:46
	   * @param date
	   * @return
	   */
	  public static Date getDateNotHHMMSS(Date date) {
	    Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    // 设置当前时刻的时钟为0
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    // 设置当前时刻的分钟为0
	    c.set(Calendar.MINUTE, 0);
	    // 设置当前时刻的秒钟为0
	    c.set(Calendar.SECOND, 0);
	    // 设置当前的毫秒钟为0
	    c.set(Calendar.MILLISECOND, 0);
	    // 获取当前时刻的时间戳
	    return c.getTime();
	  }
	  
	public static String getIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");

		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknow".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();

			if (ipAddress.equals("127.0.0.1")
					|| ipAddress.equals("0:0:0:0:0:0:0:1")) {
				// 根据网卡获取本机配置的IP地址
				InetAddress inetAddress = null;
				try {
					inetAddress = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inetAddress.getHostAddress();
			}
		}

		// 对于通过多个代理的情况，第一个IP为客户端真实的IP地址，多个IP按照','分割
		if (null != ipAddress && ipAddress.length() > 15) {
			// "***.***.***.***".length() = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}

		return ipAddress;
	}
	public static XMLGregorianCalendar dateToXmlDate(Date date){  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        DatatypeFactory dtf = null;  
         try {  
            dtf = DatatypeFactory.newInstance();  
        } catch (DatatypeConfigurationException e) {  
        }  
        XMLGregorianCalendar dateType = dtf.newXMLGregorianCalendar();  
        dateType.setYear(cal.get(Calendar.YEAR));  
        //由于Calendar.MONTH取值范围为0~11,需要加1  
        dateType.setMonth(cal.get(Calendar.MONTH)+1);  
        dateType.setDay(cal.get(Calendar.DAY_OF_MONTH));  
        dateType.setHour(cal.get(Calendar.HOUR_OF_DAY));  
        dateType.setMinute(cal.get(Calendar.MINUTE));  
        dateType.setSecond(cal.get(Calendar.SECOND));  
        return dateType;  
    } 
}
