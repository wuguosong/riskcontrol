package bpm;

import org.activiti.engine.ProcessEngine;

import com.yk.common.SpringUtil;

public class BpmFactory {

	private BpmFactory() {
	}

	private static  ProcessEngine processEngine = null;
	// 工厂方法
	public static ProcessEngine getInstance() {
		if (processEngine == null) {
			/*ProcessEngineConfiguration configuration = ProcessEngineConfiguration
					.createStandaloneProcessEngineConfiguration();
			configuration.setJdbcDriver(PropertiesUtil.getProperty("jdbc.driverClassName"));
			configuration.setJdbcUrl(PropertiesUtil.getProperty("jdbc.url"));
			configuration.setJdbcUsername(PropertiesUtil.getProperty("jdbc.username"));
			configuration.setJdbcPassword(PropertiesUtil.getProperty("jdbc.password"));
			// 建表策略
			configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
			// 创建核心心对象
			processEngine = configuration.buildProcessEngine();*/
			
			processEngine = (ProcessEngine) SpringUtil.getBean("processEngine");
		}
		return processEngine;

	}

}
