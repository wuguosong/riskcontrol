/**
 * 
 */
package rcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.apache.ibatis.session.SqlSession;

import util.DbUtil;
import util.Util;

/**
 * @Description: 流程预警时限及form_key设置
 * @Author zhangkewei
 * @Date 2016年9月28日 下午3:05:39  
 */
public class TaskWarningConf {
	
	public enum TaskWarningConfCol{
		procDefId,procDefTaskId,procDefTaskName,displayOrder,dueDays,formKey
	}
	
	
	int order = 0;
	//部署流程时调用该方法封装待添加数据
	public void addTaskWarningInfo(List<Map<String, Object>> retList, ActivityImpl act, String processDefId) {
		if(!"userTask".equals(act.getProperty("type"))) return;
		UserTaskActivityBehavior ut = null;
		if(act.getActivityBehavior() instanceof UserTaskActivityBehavior){
			ut = (UserTaskActivityBehavior)act.getActivityBehavior();
		}else if(act.getActivityBehavior() instanceof ParallelMultiInstanceBehavior){
			ParallelMultiInstanceBehavior pm = (ParallelMultiInstanceBehavior)act.getActivityBehavior();
			ut = (UserTaskActivityBehavior)pm.getInnerActivityBehavior();
		}
		TaskDefinition td = ut != null ? ut.getTaskDefinition() : null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(TaskWarningConfCol.procDefId.toString(), processDefId);
		map.put(TaskWarningConfCol.procDefTaskId.toString(), act.getId());
		map.put(TaskWarningConfCol.procDefTaskName.toString(), act.getProperty("name"));
		map.put(TaskWarningConfCol.displayOrder.toString(), order++);
		map.put(TaskWarningConfCol.dueDays.toString(), "");
		map.put(TaskWarningConfCol.formKey.toString(), td!=null ? td.getFormKeyExpression().getExpressionText():"");
		retList.add(map);
	}
	
	public void insert(List<Map<String, Object>> list){
		SqlSession session = DbUtil.openSession();
		TaskWarningConfCol[] cols = TaskWarningConfCol.values();
		for(Map<String, Object> map : list){
			for(TaskWarningConfCol key : cols){
				if(map.get(key.toString()) == null){
					map.put(key.toString(), "");
				}
			}
			map.put("id", Util.getUUID());
			map.put("state", "1");
			session.insert("taskWarningConf.insertTaskWarningConf", map);
		}
		DbUtil.close();
	}

	public void update(Map<String, Object> map) {
		SqlSession session = DbUtil.openSession();
		session.update("taskWarningConf.updateTaskWarningConf", map);
		DbUtil.close();
	}
	
}
