package com.yk.common.dao;

import com.yk.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2019/5/7 0007.
 */
@Repository
public interface ICommonMapper extends BaseMapper {
    /**
     * 查询审批中的项目
     * @param type 项目类型
     * @param id 项目ID
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> selectApprovalProjects(@Param("type") String type, @Param("id")String id);
    /**
     * 查询评审中的项目
     * @param type 项目类型
     * @param id 项目ID
     * @return List<HashMap<String, Object>>
     */
    List<HashMap<String, Object>> selectReviewProjects(@Param("type")String type, @Param("id")String id);
}
