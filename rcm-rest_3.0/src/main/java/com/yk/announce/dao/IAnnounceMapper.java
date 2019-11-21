package com.yk.announce.dao;

import com.yk.announce.entity.Announce;
import com.yk.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author lipan92
 * @version 1.0
 * @calssName IAnnounceMapper
 * @description 公告
 * @date 2019/11/21 0021 14:26
 **/
@Repository
public interface IAnnounceMapper extends BaseMapper {
    /**
     * @return List<Announce>
     * @author lipan92
     * @description 获取列表
     * @date 2019/11/21 0021 14:47
     **/
    List<Announce> findList();

    /**
     * @param id 主键Id
     * @return Announce
     * @author lipan92
     * @description 通过Id获取
     * @date 2019/11/21 0021 14:48
     **/
    Announce findOne(@Param("id") Long id);

    /**
     * @param id 主键id
     * @author lipan92
     * @description 通过主键删除
     * @date 2019/11/21 0021 14:50
     **/
    int delete(@Param("id") Long id);

    /**
     * @param announce
     * @return Announce
     * @author lipan92
     * @description 保存
     * @date 2019/11/21 0021 14:51
     **/
    int save(Announce announce);

    /**
     * @param announce
     * @return Announce
     * @author lipan92
     * @description 更新
     * @date 2019/11/21 0021 14:53
     **/
    int update(Announce announce);
}
