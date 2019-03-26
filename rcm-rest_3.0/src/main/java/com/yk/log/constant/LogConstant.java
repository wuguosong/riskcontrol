package com.yk.log.constant;

/**
 * Created by LiPan on 2019/3/25.
 */
public interface LogConstant {
    /**
     * 操作常量
     */
    String CREATE = "CREATE";// 创建
    String UPDATE = "UPDATE";// 更新
    String QUERY = "QUERY";// 查询
    String DELETE = "DELETE";// 删除
    String IMPORT = "IMPORT";// 导入
    String EXPORT = "EXPORT";// 导出
    String WORKFLOW = "WORKFLOW";// 提交审批
    /**
     * 业务模块
     */
    String MODULE_SYS = "SYS";// 系统
    String MODULE_BULLETIN = "BULLETIN";// 其它评审
    String MODULE_FORMAL_ASSESSMENT = "FORMAL_ASSESSMENT";// 正式评审
    String MODULE_PRE_AUDIT = "PRE_AUDIT"; // 投标评审
    String MODULE_MEETING = "MEETING"; // 会议管理
    String MODULE_FILL_MATERIALS = "FILL_MATERIALS"; // 资料填写管理
}
