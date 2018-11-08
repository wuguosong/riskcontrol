package sys.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Constants;
import util.PropertiesUtil;

/**
 * 跨域处理
 * 
 * @author zhouyoucheng
 *
 */
public class CrossDomainFilter implements Filter {
	private static String allowDomain = PropertiesUtil.getProperty("domain.allow");
	// 初始化
	public void init(FilterConfig config) throws ServletException {
		
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpServletRequest request = (HttpServletRequest) req;
		//请求方法，针对跨域请求OPTIONS
		String method = request.getMethod();
		crossDomain(request, response);
		if (method.equals(Constants.OPTIONS)) {
			response.getWriter().flush();
			response.getWriter().close();
			return;
		}
		chain.doFilter(req, resp);
	}


	// 跨域
	public void crossDomain(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", allowDomain);
		//缓存此次请求的秒数。在这个时间范围内，所有同类型的请求都将不再发送预检请求而是直接使用此次返回的头作为判断依据
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		response.addHeader("Access-Control-Max-Age", "3600");
		response.addHeader("Access-Control-Allow-Headers", "Content-Type,authorization");
		response.addHeader("Access-Control-Allow-Credentials", "true");
	}

	public void destroy() {
	}
}
