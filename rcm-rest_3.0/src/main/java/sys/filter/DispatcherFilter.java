package sys.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.log4j.Logger;

import util.ThreadLocalUtil;
import util.Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yk.adapter.PreServiceAdapter;
import com.yk.common.SpringUtil;
import com.yk.exception.SystemException;
import com.yk.power.service.IUserService;
import com.yk.rcm.project.service.IJournalService;

import common.BusinessException;
import common.Constants;
import common.Result;
import fnd.SysUser;

/**
 * 分发处理
 * 
 * @author zhouyoucheng
 *
 */
public class DispatcherFilter implements Filter {
	private static Logger logger = Logger.getLogger(DispatcherFilter.class);
	private static Pattern excludePattern = null;

	private IJournalService journalService;
	
	public void init(FilterConfig config) throws ServletException {
		String excluded = config.getInitParameter("excluded");
		excludePattern = Pattern.compile(excluded);
		this.journalService = (IJournalService) SpringUtil.getBean("journalService");
	}
	
	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		/*Cookie[] cookies = request.getCookies();
		for(int i = 0; cookies != null && i < cookies.length; i++){
			Cookie cookie = cookies[i];
			System.out.println(cookie.getDomain()+"-:-"+cookie.getName()+"-:-"+cookie.getValue());
		}*/
		
		String userId = request.getHeader("authorization");
		ThreadLocalUtil.setUserId(userId);
		
		String uri = request.getRequestURI();
		
		if(userId != null && !"".equals(userId)){
			
			HttpSession session = request.getSession();
			String userSId = (String) session.getAttribute("userId");
			boolean isAdmin = false;
			Map<String, Object> userInfo = null; 
			if(userSId != null){
				isAdmin = (Boolean) session.getAttribute("isAdmin");
				userInfo = (Map<String, Object>)session.getAttribute("userInfo");
			}else{
				IUserService user = (IUserService) SpringUtil.getBean("userService");
				isAdmin = user.isAdmin(userId);
				userInfo = user.queryById(userId);
				session.setAttribute("isAdmin", isAdmin);
				session.setAttribute("userInfo", userInfo);
			}
			

			ThreadLocalUtil.setIsAdmin(isAdmin);
			ThreadLocalUtil.setUser(userInfo);
		}
		
		//免除该过滤器处理的url可以直接通过
		String suffix = uri.contains(".")?uri.substring(uri.lastIndexOf(".")):"";
		boolean isSpring = uri.length() > 1 && (".do".equals(suffix) || ".jsp".equals(suffix)|| ".js".equals(suffix)|| ".css".equals(suffix)|| ".jpg".equals(suffix)||".png".equals(suffix)||".axd".equals(suffix));
		if(!(".js".equals(suffix)|| ".css".equals(suffix)|| ".jpg".equals(suffix))){
			System.out.println(Util.getTime()+"***************URI:"+uri);
		}
		if(isSpring || excludePattern.matcher(uri).find()){
			try{
				chain.doFilter(req, resp);
			}catch (Exception e) {
				logger.error(Util.parseException(e));
				
				Enumeration<String> names = request.getParameterNames();
				Map<String, Object> params = new HashMap<String, Object>();
				while(names.hasMoreElements()){
					String key = names.nextElement();
					Object value = request.getParameter(key);
					params.put(key, value);
				}
				ObjectMapper mapper = new ObjectMapper();
				String str = mapper.writeValueAsString(params);
				generateErrorLog(request, str, e);
				Result resultt = new Result();
				try {
					if(resultt != null){
						resultt.setSuccess(false).setResult_name("系统异常，请联系管理员！");
						writeToResponse(response, resultt);
					}
				} catch (Exception ee) {
					logger.error(Util.parseException(ee));
				}
				response.getOutputStream().flush();
				response.getOutputStream().close();
			}
			
			return;
		}
		Result result = null;
		Object retObj = null;
		// 调用方法
		try {
			retObj = invoke(request, response);
			if (retObj instanceof InputStream) {
				//下载文件
				String fileName = request.getParameter("fileName");
				response.setContentType("application/octet-stream");
				if (Util.isNotEmpty(fileName)) {
					String userAgent = request.getHeader("User-Agent");
					if(userAgent.contains("Firefox")){
						fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
						response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
					}else{
						fileName = URLEncoder.encode(fileName,"UTF-8");
						response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
					}
				}
				InputStream in = (InputStream) retObj;
				OutputStream out = response.getOutputStream();
				// Copy the contents of the file to the output stream
				byte[] buf = new byte[1024];
				int count = 0;
				while ((count = in.read(buf)) >= 0) {
					out.write(buf, 0, count);
				}
				if(in != null) in.close();
			} else {
				if(retObj instanceof Result){
					result = (Result) retObj;
				}else{
					//输出json
					result = new Result(Constants.S, "执行成功!", retObj);
				}
			}
		} catch (Exception e) {
			logger.error(Util.parseException(e));
			String msg = "";
			if(e instanceof BusinessException || e instanceof com.yk.exception.BusinessException){
				//如果是业务异常
				msg = e.getMessage();
			}else if(e instanceof SystemException){
				//如果是系统异常
				msg = "系统异常，请联系管理员！";
			}else{
				//其它未知异常
				msg = "系统异常，请联系管理员！";
			}
			result = new Result(Constants.E, msg);
		} finally {
			try {
				if(result != null){
					writeToResponse(response, result);
				}
			} catch (Exception e) {
				logger.error(Util.parseException(e));
			}
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	private void writeToResponse(HttpServletResponse response, Result result) throws Exception {
		response.getOutputStream().write(result.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	// 调用方法，返回结果
	public Object invoke(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取url相关参数
		String[] urls = getUrls(request);
		// 调用
		return invoke(urls, request);
	}

	// 调用
	@SuppressWarnings("unchecked")
	public Object invoke(String[] urls, HttpServletRequest request) throws Exception {
		// 请求类型
		String requestType = request.getMethod();
		// 请求参数
		StringBuilder params = new StringBuilder();
		// 上传文件
		List<FileItem> fileList = new ArrayList<FileItem>();
		// get请求
		if (Constants.GET.equals(requestType)) {
			Enumeration<String> parameEnum = request.getParameterNames();
			while (parameEnum.hasMoreElements()) {
				String key = parameEnum.nextElement();
				String val = request.getParameter(key);
				params.append("\"" + key + "\"").append(":").append("\"" + val + "\",");
			}
			// 移除最后一个,
			dealComma(params);
		}
		// post请求
		else {
			// 是否有文件上传
			if (ServletFileUpload.isMultipartContent(request)) {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(new ServletRequestContext(request));
				Iterator<FileItem> it = items.iterator();
				while (it.hasNext()) {
					FileItem item = it.next();
					// 是否表单域(非文件上传元素)
					if (item.isFormField()) {
						String key = item.getFieldName();
						String val = item.getString();
						params.append("\"" + key + "\"").append(":").append("\"" + val + "\",");
					} else {
						fileList.add(item);
					}
				}
				// 移除最后一个,
				dealComma(params);
			} else {
				// 读取body中的json
				InputStreamReader isr = new InputStreamReader(request.getInputStream(), Constants.UTF8);
				BufferedReader br = new BufferedReader(isr);
				String temp;
				while ((temp = br.readLine()) != null) {
					params.append(temp);
				}
				br.close();
				// 如果从body中读取到的json串为key=val格式，则尝试解析该格式，转化为json串
				if (params.length() > 0) {
					if (params.indexOf(":") < 0 && params.indexOf("=") > 0) {
						String[] ary = params.toString().split("&");
						// 清空
						params.delete(0, params.length());
						for (String param : ary) {
							String[] kv = param.split("=");
							String key = URLDecoder.decode(kv[0], Constants.UTF8);
							String val = URLDecoder.decode(kv[1], Constants.UTF8);
							params.append("\"" + key + "\"").append(":").append("\"" + val + "\",");
						}
						// 移除最后一个,
						dealComma(params);
					}
				} else {
					params.append("{}");
				}
			}
		}
		// 寻找可用方法并调用
		return findAndInvoke(request, urls, params.toString(), fileList);
	}

	// 寻找可用方法并调用
	public Object findAndInvoke(HttpServletRequest request, String[] urls, String json, List<FileItem> fileList) throws Exception {
		logger.info("the request parameter is " + json);
		PreServiceAdapter preServiceAdapter = (com.yk.adapter.PreServiceAdapter) SpringUtil.getBean("preServiceAdapter");
		String pkg = urls[0];// 包名
		String className = urls[1];// 类名
		String methodName = urls[2];// 方法名
		className = pkg+"."+className;
		try{
			if (Util.isNotEmpty(fileList)) {
				return preServiceAdapter.execute(className, methodName, json, fileList);
			}else if(json == null || "".equals(json.trim()) || "{}".equals(json.replaceAll(" ", ""))){
				return preServiceAdapter.execute(className, methodName);
			}else if(json != null && !"".equals(json)){
				return preServiceAdapter.execute(className, methodName, json);
			}else{
				throw new NoSuchMethodException(
						"failed to find available method, the method is " + pkg + "." + className + "." + methodName);
			}
		}catch (Exception e) {
			logger.error(Util.parseException(e));
			this.generateErrorLog(request, json, e);
			Result result=new Result(Constants.E, "系统异常，请联系管理员！");
			return result;
		}
	}

	// 获取url相关参数
	public String[] getUrls(HttpServletRequest request) throws Exception {
		String contextPath = request.getContextPath();
		// 去除应用上下文
		String url = request.getRequestURI().replace(contextPath + "/", "");
		String[] urls = null;
		// url校验
		if (url.indexOf("/") < 0 || (urls = url.split("/")).length != 3) {
			throw new Exception("the url is illegal, the url is " + request.getRequestURL());
		}
		return urls;
	}

	// 拼接json串处理
	public void dealComma(StringBuilder params) {
		// 移除最后一个,
		if (params.lastIndexOf(",") > 0) {
			params.deleteCharAt(params.lastIndexOf(","));
		}
		params.insert(0, "{");
		params.append("}");
	}

	// 获取一个实例
	public Object getBean(String className) throws Exception {
		try {
			return Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new Exception("failed to find class, the class name is " + className);
		}
	}
	/**
	 * 封装异常日志
	 * @param request
	 * @param journal
	 */
	private void generateErrorLog(HttpServletRequest request,String params, Exception e){
		Map<String, Object> journal = new HashMap<String, Object>();
		String requestUrl = request.getRequestURL().toString();
		journal.put("requestUrl", requestUrl);
		journal.put("accesstime", Util.getTime());
		journal.put("content", Util.parseException(e));
		journal.put("type", e.getClass().getName());
		journal.put("requestParams", params);
		journal.put("requestUserId",  (String)request.getAttribute("loginid"));
		String ipAddress = Util.getIpAddress(request);
		journal.put("ip", ipAddress);
		try {
			this.journalService.save(journal);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void destroy() {
	}

}
