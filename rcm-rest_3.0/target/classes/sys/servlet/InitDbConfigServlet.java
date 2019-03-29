package sys.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import bpm.BpmFactory;
import sys.jobs.JobExecutor;
import util.DbUtil;

/**
 * 初始化数据库连接
 * 
 * @author zhouyoucheng
 *
 */
public class InitDbConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 初始化
	public void init(ServletConfig config) throws ServletException {
		// mybatis配置
		/*String mybatisConfig = config.getInitParameter("mybatisConfig");
		// mongodb配置
		String mongoDbConfig = config.getInitParameter("mongoDbConfig");
		// 初始化数据库
		DbUtil.init(mybatisConfig, mongoDbConfig);
		// 鉴权信息初始化
		//BasicAuthFilter.init();
		//初始化流程引擎
		BpmFactory.getInstance();*/
		//系统任务调度初始化
		JobExecutor.init();
		//发布webservice
		//WebserviceContext.publish();
	}
}
