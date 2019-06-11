package com.yk.initfile.service;

import com.alibaba.fastjson.JSONObject;
import com.yk.initfile.entity.InitFile;
import com.yk.initfile.entity.MeetingFile;

import java.util.List;

/**
 * @author LiPan
 * @description 期初文件处理业务接口
 * @Date 2019-05-30
 */
public interface IInitFileService {
    /**
     * 从Mongo中查询
     *
     * @param jsonObjectList
     * @return
     */
    List<InitFile> queryMongo(List<JSONObject> jsonObjectList, JSONObject jsonCondition);

    /**
     * 查询数据同步情况
     *
     * @param jsonObjectList
     * @return
     */
    List<InitFile> querySynchronize(List<JSONObject> jsonObjectList, JSONObject jsonCondition);

    /**
     * 执行同步
     *
     * @param initFile
     * @return
     */
    InitFile executeSynchronize(InitFile initFile);

    /**
     * 执行模块同步
     *
     * @param jsonObjectList
     * @return
     */
    List<InitFile> executeSynchronizeModule(List<JSONObject> jsonObjectList, JSONObject jsonCondition);

    /**
     * 查询上会附件
     *
     * @param meeting
     * @param condition
     * @return
     */
    List<MeetingFile> queryMeetingSynchronize(JSONObject meeting, JSONObject condition);

    /**
     * 同步上会附件
     *
     * @param meeting
     * @param condition
     * @return
     */
    List<MeetingFile> executeMeetingSynchronize(JSONObject meeting, JSONObject condition);

    /**
     * 同步上会文件，一个
     *
     * @param meetingFile
     * @return
     */
    MeetingFile executeMeetingSynchronize(MeetingFile meetingFile);

    /**
     * 查询文件名不一致的文件列表
     *
     * @param list
     * @param condition
     * @return
     */
    List<InitFile> queryDifferentFiles(List<JSONObject> list, JSONObject condition);

    /**
     * 更新文件名不一致的文件列表
     *
     * @param list
     * @param condition
     * @return
     */
    List<InitFile> updateDifferentFiles(List<JSONObject> list, JSONObject condition);

    /**
     * 更新文件名不一致的文件
     *
     * @param initFile
     * @return
     */
    InitFile updateDifferentFile(InitFile initFile);
}
