package com.yk.initfile.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goukuai.constant.YunkuConf;
import com.goukuai.dto.FileDto;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.yk.common.IBaseMongo;
import com.yk.exception.BusinessException;
import com.yk.initfile.entity.InitFile;
import com.yk.initfile.entity.JsonField;
import com.yk.initfile.entity.MeetingFile;
import com.yk.initfile.entity.MyJson;
import com.yk.initfile.service.IInitFileService;
import com.yk.initfile.service.MyFilter;
import com.yk.rcm.bulletin.service.IBulletinInfoService;
import com.yk.rcm.file.dao.IFileMapper;
import com.yk.rcm.file.service.IFileService;
import com.yk.rcm.newFormalAssessment.service.IFormalAssessmentInfoCreateService;
import com.yk.rcm.newPre.service.IPreInfoCreateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.DbUtil;
import util.UserUtil;
import ws.todo.utils.JaXmlBeanUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * @author LiPan
 * @description 期初文件处理业务接口实现
 * @Date 2019-05-30
 */
@Service
@Transactional
public class InitFileService implements IInitFileService {
    private static final Logger logger = LoggerFactory.getLogger(InitFileService.class);
    @Resource
    private IBaseMongo baseMongo;
    @Resource
    private IFileMapper fileMapper;
    @Resource
    private IFileService fileService;
    @Resource
    private IPreInfoCreateService preInfoCreateService;
    @Resource
    private IFormalAssessmentInfoCreateService formalAssessmentInfoCreateService;
    @Resource
    private IBulletinInfoService bulletinInfoService;

    @Override
    public List<InitFile> queryMongo(List<JSONObject> jsonObjectList, final JSONObject jsonCondition) {
        final List<InitFile> list = new ArrayList<InitFile>();
        if (CollectionUtils.isNotEmpty(jsonObjectList)) {
            for (JSONObject jsonObject : jsonObjectList) {
                final String name = jsonObject.getString(JsonField.name);
                final String table = jsonObject.getString(JsonField.table);
                MongoCollection<Document> mongoCollection = baseMongo.getCollection(table);
                final String filePathField = jsonObject.getString(JsonField.filePathField);
                final String fileNameField = jsonObject.getString(JsonField.fileNameField);
                final JSONObject cloudParams = jsonObject.getJSONObject(JsonField.cloudParams);
                final List<String> locations = JSON.parseArray(JSON.toJSONString(cloudParams.get(JsonField.locations)), String.class);
                final List<String> jsonLevel = JSON.parseArray(JSON.toJSONString(jsonObject.get(JsonField.jsonLevel)), String.class);
                final List<String> mongoId = JSON.parseArray(JSON.toJSONString(jsonObject.get(JsonField.mongoId)), String.class);
                logger.info("table：" + table + "，name：" + name);
                logger.info("filePathField：" + filePathField + "，fileNameField：" + fileNameField);
                logger.info("cloudParams:" + JSON.toJSONString(cloudParams));
                logger.info("locations:" + JSON.toJSONString(locations));
                logger.info("jsonLevel:" + JSON.toJSONString(jsonLevel));
                logger.info("mongoId:" + JSON.toJSONString(mongoId));
                logger.info("总数量：" + mongoCollection.count());
                if (mongoCollection != null) {
                    FindIterable<Document> findIterable = mongoCollection.find();
                    findIterable.forEach(new Block<Document>() {
                        public void apply(Document document) {
                            JSONObject jsDoc = JSON.parseObject(document.toJson(), JSONObject.class);
                            if (jsDoc != null) {
                                // 迭代主键信息,最多两级
                                String id = getId(jsDoc, mongoId);
                                List<JSONObject> fileArray = createFileList(locations, jsDoc, jsonLevel, filePathField, fileNameField, name);
                                if (CollectionUtils.isNotEmpty(fileArray)) {
                                    for (JSONObject file : fileArray) {
                                        // System.out.println("id===" + id + "\n文件====" + JSON.toJSONString(file));
                                        InitFile initFile = new InitFile();
                                        initFile.setFilePathField(filePathField);
                                        initFile.setFileNameField(fileNameField);
                                        if (StringUtils.isNotBlank(file.getString(JsonField.version))) {
                                            initFile.setVersion(file.getString(JsonField.version));
                                        } else {
                                            initFile.setVersion("1");
                                        }
                                        initFile.setName(name);
                                        initFile.setTable(table);
                                        initFile.setNameServer(file.getString(fileNameField));
                                        initFile.setPathServer(file.getString(filePathField));
                                        initFile.setNameCloud(file.getString(fileNameField));
                                        // 构造云库的Path
                                        String pathCloud = YunkuConf.UPLOAD_ROOT + "/" + cloudParams.getString(JsonField.type) + "/" + id + "/" + file.getString(fileNameField);
                                        initFile.setPathCloud(pathCloud);
                                        initFile.setLocation(file.getString(JsonField._location));
                                        initFile.setType(cloudParams.getString(JsonField.type));
                                        initFile.setCode(id);
                                        initFile.setDifferentFileName(differentFileName(initFile));
                                        list.add(initFile);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }
        for (InitFile initFile : list) {
            initFile.setSequence(new Integer(list.indexOf(initFile)));
            initFile.setTotal(list.size());
        }
        int limit = 0;
        int skip = 0;
        if (jsonCondition.getInteger("limit") != null) {
            limit = jsonCondition.getInteger("limit");
        }
        if (jsonCondition.getInteger("skip") != null) {
            skip = jsonCondition.getInteger("skip");
        }
        List<InitFile> filter = new MyFilter(limit, skip).doFilter(list);
        if (CollectionUtils.isNotEmpty(filter)) {
            for (InitFile initFile : filter) {
                initFile.setLimitTotal(filter.size());
            }
        }
        return filter;
    }


    @Override
    public InitFile executeSynchronize(InitFile initFile) {
        if (!initFile.isCloud() && initFile.isServer()) {
            String pathServer = initFile.getPathServer();
            String optName = UserUtil.getCurrentUserName();
            String optId = UserUtil.getCurrentUserId();
            if (StringUtils.isNotBlank(pathServer)) {
                File file = new File(pathServer);
                if (file.exists()) {
                    String pathCloud = initFile.getPathCloud();
                    String fileLocal = file.getAbsolutePath();
                    try {
                        fileService.fileUpload(pathCloud.replaceFirst(YunkuConf.UPLOAD_ROOT, ""), fileLocal, initFile.getType(), initFile.getCode(), initFile.getLocation(), optName, new Integer(optId));
                        FileDto fileDto = judgeFileDtoByPath(initFile);
                        if (fileDto != null) {
                            initFile.setCloud(true);
                            initFile.setSynchronize(true);
                            try {
                                this.insertToMongo(initFile, fileDto);
                            } catch (Exception e) {
                                logger.error("##########Mongo数据插入失败：" + initFile);
                            }
                        } else {
                            initFile.setCloud(false);
                            initFile.setSynchronize(false);
                        }
                    } catch (Exception e) {
                        // 该异常导致事务回滚，FileDto不会保存
                        initFile.setCloud(false);
                        initFile.setSynchronize(false);
                        try {
                            // 删除已经上传到云库的附件
                            fileService.fileDelete(pathCloud.replaceFirst(YunkuConf.UPLOAD_ROOT, ""), optName);
                        } catch (Exception e1) {
                            logger.error("删除云库附件出错：" + e1.getMessage());
                            // 如果文件不存在，可能抛出异常，不做处理
                        }
                        // throw new BusinessException(e);
                    }
                } else {
                    initFile.setCloud(false);
                    initFile.setSynchronize(false);
                    throw new BusinessException("序号为：" + (initFile.getSequence() + 1) + "的文件：" + initFile.getNameServer() + "不存在！");
                }
            } else {
                initFile.setCloud(false);
                initFile.setSynchronize(false);
                throw new BusinessException("序号为：" + (initFile.getSequence() + 1) + "的文件，服务器文件路径为空！");
            }
        }
        return initFile;
    }

    /**
     * 插入Mongo数据
     *
     * @param initFile
     * @param fileDto
     */
    private void insertToMongo(InitFile initFile, FileDto fileDto) {
        if (initFile.getName().contains("相关资源")) {
            String table = initFile.getTable();
            Map<String, Object> preInfo = baseMongo.queryById(initFile.getCode(), initFile.getTable());
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("functionType", this.getFunctionType(initFile.getTable()));
            List<Document> services = (List<Document>) preInfo.get("serviceType");
            if (CollectionUtils.isNotEmpty(services)) {
                Document service = services.get(0);
                paramMap.put("serviceCode", service.get("KEY"));
            }
            List<Document> projectModels = (List<Document>) preInfo.get("projectModel");
            if (CollectionUtils.isNotEmpty(projectModels)) {
                Document projectModel = projectModels.get(0);
                paramMap.put("projectModelName", projectModel.get("VALUE"));
            }
            List<Map> attachmentTypeList = DbUtil.openSession().selectList("common.getAttachmentList", paramMap);
            DbUtil.close();
            List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) (preInfo.get("attachment") == null ? preInfo.get("fileList") : preInfo.get("attachment"));
            if (CollectionUtils.isNotEmpty(attachmentList)) {
                for (Map<String, Object> attachment : attachmentList) {
                    List<Map<String, Object>> files = null;
                    try {
                        files = (List<Map<String, Object>>) attachment.get("files");
                    } catch (Exception e) {
                        Map<String, Object> map = (Map<String, Object>) attachment.get("files");
                        files = new ArrayList<Map<String, Object>>();
                        files.add(map);
                    }
                    if (CollectionUtils.isNotEmpty(files)) {
                        // 获取最高版本的附件
                        List<JSONObject> jsonObjects = JSON.parseArray(JSON.toJSONString(files), JSONObject.class);
                        List<JSONObject> sortVersion = this.sortVersion(jsonObjects);
                        if (CollectionUtils.isNotEmpty(sortVersion)) {
                            JSONObject sortFile = sortVersion.get(0);
                            // 针对其它评审相关资源单独处理
                            if ("rcm_bulletin_info".equalsIgnoreCase(table)) {
                                sortFile.put(JsonField.version, "1");
                            }
                            if (sortFile.getString(JsonField.version).equals(initFile.getVersion())
                                    && sortFile.getString(initFile.getFilePathField()).equals(initFile.getPathServer())) {
                                Map<String, Object> file = JSON.parseObject(JSON.toJSONString(sortFile), HashMap.class);
                                // 上传附件准备数据
                                String fileName = (String) file.get("fileName");
                                // 同步Mongo数据
                                Map<String, Object> jsonMap = new HashMap<String, Object>();
                                Map<String, Object> data = new HashMap<String, Object>();
                                jsonMap.put("businessId", initFile.getCode());
                                jsonMap.put("oldFileName", fileName);
                                data.put("fileName", fileName);
                                data.put("lastUpdateBy", file.get("approved") == null ? preInfo.get("applyUser") : file.get("approved"));
                                data.put("lastUpdateData", file.get("upload_date") == null ? preInfo.get("createTime") : file.get("upload_date"));
                                data.put("fileId", String.valueOf(fileDto.getFileid()));
                                if (attachmentTypeList != null) {
                                    int count = 0;
                                    for (int q = 0; q < attachmentTypeList.size(); q++) {
                                        if (attachmentTypeList.get(q).get("ITEM_NAME").equals((String) file.get("ITEM_NAME"))) {
                                            count = 1;
                                            data.put("type", attachmentTypeList.get(q));
                                            break;
                                        }
                                    }
                                    if (count == 0) {
                                        Map<String, Object> type = new HashMap<String, Object>();
                                        type.put("ITEM_NAME", attachment.get("ITEM_NAME"));
                                        data.put("type", type);
                                    }
                                }
                                jsonMap.put("item", data);
                                String json = JSON.toJSONString(jsonMap);
                                try {
                                    if ("rcm_pre_info".equalsIgnoreCase(table)) {
                                        preInfoCreateService.addNewAttachment(json);
                                    } else if ("rcm_formalAssessment_info".equalsIgnoreCase(table)) {
                                        formalAssessmentInfoCreateService.addNewAttachment(json);
                                    } else {
                                        bulletinInfoService.addNewAttachment(json);
                                    }
                                } catch (Exception e) {
                                    logger.error(e.getMessage());
                                }
                                logger.info(initFile.getName() + "Mongo数据被保存：" + json);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取functionType
     *
     * @param table
     * @return
     */

    private String getFunctionType(String table) {
        if ("rcm_bulletin_info".equalsIgnoreCase(table)) {
            return "其它评审";
        } else if ("rcm_pre_info".equalsIgnoreCase(table)) {
            return "预评审";
        } else {
            return "正式评审";
        }
    }

    @Override
    public List<InitFile> querySynchronize(List<JSONObject> jsonObjectList, JSONObject jsonCondition) {
        boolean unSynchronize = false;
        if (jsonCondition.get("unSynchronize") != null) {
            unSynchronize = jsonCondition.getBoolean("unSynchronize");
        }
        List<InitFile> list = this.queryMongo(jsonObjectList, jsonCondition);
        List<InitFile> filter = new ArrayList<>();// 未同步的文件列表
        if (CollectionUtils.isNotEmpty(list)) {
            for (InitFile file : list) {
                FileDto fileDto = this.judgeFileDtoByPath(file);
                if (fileDto != null) {
                    file.setCloud(true);
                    file.setSynchronize(true);
                    file.setUpdate(this.compareSuffix(file, fileDto));
                } else {
                    file.setCloud(false);
                    file.setSynchronize(false);
                    file.setUpdate(false);
                }
                String pathServer = file.getPathServer();
                if (StringUtils.isNotBlank(pathServer)) {
                    File tmp = new File(pathServer);
                    file.setServer(tmp.exists());
                } else {
                    file.setServer(false);
                }
                logger.info(file.getNameServer() + ",同步查询:是否存在于服务器上:" + file.isServer() + ",是否存在于云库:" + file.isCloud() + ",是否同步到云库:" + file.isSynchronize());
                // 过滤未同的附件
                if (unSynchronize) {
                    boolean isSynchronize = file.isSynchronize();
                    if (!isSynchronize) {
                        filter.add(file);
                    }
                }
            }
        }
        if (unSynchronize) {
            list = filter;
        }
        return list;
    }

    /**
     * 获取文件，存在则返回文件实体， 否则返回null
     *
     * @param initFile
     * @return
     */
    private FileDto judgeFileDtoByPath(InitFile initFile) {
        String type = initFile.getType();
        String code = initFile.getCode();
        String location = initFile.getLocation();
        String pathServer = initFile.getPathServer();
        if (StringUtils.isEmpty(pathServer)) {
            pathServer = "PATH SERVER IS NULL";
        }
        List<FileDto> files = fileMapper.listFile(type, code, location);
        if (CollectionUtils.isEmpty(files)) {
            return null;
        } else {
            for (FileDto fileDto : files) {
                String uploadServer = fileDto.getUploadserver();
                if (StringUtils.isBlank(uploadServer)) {
                    uploadServer = "UPLOAD SERVER IS NULL";
                }
                if (uploadServer.replace("\\", "/").contains(pathServer)) {
                    return fileDto;
                }
            }
        }
        return null;
    }

    @Override
    public List<InitFile> executeSynchronizeModule(List<JSONObject> jsonObjectList, JSONObject jsonCondition) {
        List<InitFile> list = this.querySynchronize(jsonObjectList, jsonCondition);
        if (CollectionUtils.isNotEmpty(list)) {
            for (InitFile file : list) {
                this.executeSynchronize(file);
            }
        }
        return list;
    }

    /**
     * 创建文件基础列表
     *
     * @param locations
     * @param jsDoc
     * @param jsonLevel
     * @param filePathField
     * @param fileNameField
     * @return
     */
    private List<JSONObject> createFileList(List<String> locations, JSONObject jsDoc, List<String> jsonLevel, String filePathField, String fileNameField, String name) {
        String location = "";
        if (locations.size() == 1) {
            location = locations.get(0);
        }
        // 迭代附件信息
        List<JSONObject> fileArray = new ArrayList<JSONObject>();
        if (CollectionUtils.isNotEmpty(jsonLevel)) {
            if (jsonLevel.size() == 1) {// 一层
                if (jsDoc.get(jsonLevel.get(0)) != null) {
                    Object obj0 = jsDoc.get(jsonLevel.get(0));
                    try {
                        // 数组类型解析失败，尝试对象解析
                        fileArray = JSON.parseArray(JSON.toJSONString(obj0), JSONObject.class);
                        // 设置页面位置
                        if (CollectionUtils.isNotEmpty(fileArray)) {
                            for (JSONObject js0 : fileArray) {
                                js0.put(JsonField._location, getLocation(locations, js0, jsDoc));
                            }
                        }
                    } catch (Exception e) {
                        logger.error("一级列表转换出错：" + e.getMessage() + "，开始尝试对象转换！");
                        JSONObject js0 = jsDoc.getJSONObject(jsonLevel.get(0));
                        js0.put(JsonField._location, location);// 设置页面位置
                        fileArray.add(js0);
                        logger.info("一级列表转换出错，开始尝试对象转换，对象转换成功！");
                    }

                }
            }
            if (jsonLevel.size() == 2) {// 两层
                if (jsDoc.get(jsonLevel.get(0)) != null) {
                    List<JSONObject> jsonArray0 = JSON.parseArray(JSON.toJSONString(jsDoc.get(jsonLevel.get(0))), JSONObject.class);
                    if (CollectionUtils.isNotEmpty(jsonArray0)) {
                        for (JSONObject jsObj : jsonArray0) {
                            String local = getLocation(locations, jsObj, jsDoc);
                            Object obj1 = jsObj.get(jsonLevel.get(1));
                            if (obj1 != null) {
                                try {
                                    List<JSONObject> jsonArray1 = JSON.parseArray(JSON.toJSONString(obj1), JSONObject.class);
                                    // 设置页面位置
                                    if (CollectionUtils.isNotEmpty(jsonArray1)) {
                                        // 取最大的版本号
                                        if (name.contains("相关资源")) {
                                            List<JSONObject> sort = sortVersion(jsonArray1);
                                            jsonArray1 = sort.subList(0, 1);
                                            logger.debug("相关资源，长度：" + jsonArray1.size() + "，最新版本：" + jsonArray1.get(0).getString(JsonField.version));
                                        }
                                        for (JSONObject jsIt : jsonArray1) {
                                            jsIt.put(JsonField._location, local);
                                        }
                                    }
                                    fileArray.addAll(jsonArray1);
                                } catch (Exception e) {
                                    logger.error("二级列表转换出错：" + e.getMessage() + "，开始尝试对象转换！");
                                    JSONObject js1 = jsObj.getJSONObject(jsonLevel.get(1));
                                    js1.put(JsonField._location, location);// 设置页面位置
                                    fileArray.add(js1);
                                    logger.info("二级列表转换出错，开始尝试对象转换，对象转换成功！");
                                }
                            }
                        }
                    }
                }
            }
        } else {// 没有层级
            fileArray = new ArrayList<JSONObject>();
            JSONObject js = new JSONObject();
            js.put(JsonField._location, location);// 设置页面位置
            js.put(filePathField, jsDoc.get(filePathField));
            js.put(fileNameField, jsDoc.get(fileNameField));
            fileArray.add(js);
        }
        return fileArray;
    }

    /**
     * 排序
     *
     * @param jsonArray
     * @return
     */
    private List<JSONObject> sortVersion(List<JSONObject> jsonArray) {
        List<MyJson> list = JSON.parseArray(JSON.toJSONString(jsonArray), MyJson.class);
        Collections.sort(list);
        List<JSONObject> sort = JSON.parseArray(JSON.toJSONString(list), JSONObject.class);
        return sort;
    }

    /**
     * 获取业务Id
     *
     * @param jsDoc
     * @param mongoId
     * @return
     */
    private String getId(JSONObject jsDoc, List<String> mongoId) {
        String id = "";
        if (mongoId.size() == 2) {// 两层
            JSONObject id0 = jsDoc.getJSONObject(mongoId.get(0));
            if (id0 != null) {
                id = id0.getString(mongoId.get(1));
            }
        } else if (mongoId.size() == 1) {// 一层
            id = jsDoc.getString(mongoId.get(0));
        }
        return id;
    }

    /**
     * 获取文件位置
     *
     * @param locations
     * @param jsObj
     * @param jsDoc
     * @return
     */
    private String getLocation(List<String> locations, JSONObject jsObj, JSONObject jsDoc) {
        String location = "";
        if (locations.size() > 1) {
            for (String l : locations) {
                if (l.contains("$") && l.contains(".")) {// 子级
                    String key = l.split("\\.")[1];
                    location += jsObj.getString(key);
                } else if (l.contains("$") && !l.contains(".")) {// 根级
                    String key = l.split("\\$")[1];
                    location += jsDoc.getString(key);
                } else {// 常量
                    location += l;
                }
            }
        } else {
            location = locations.get(0);
        }
        return location;
    }

    @Override
    public List<MeetingFile> queryMeetingSynchronize(JSONObject meeting, JSONObject condition) {
        List<MeetingFile> list = new ArrayList();
        if (meeting != null) {
            final String dataName = meeting.getString(JsonField.dataName);
            final String dataTable = meeting.getString(JsonField.dataTable);
            final String fileTable = meeting.getString(JsonField.fileTable);
            /*===统计上会附件的单据信息===*/
            list = this.createMeetingFiles(fileTable, dataTable, dataName);
            /*===装入附件列表主数据记录===*/
            Map<String, JSONObject> dataTableMap = this.createMainDataMap(dataTable);
            /*===设置序列和其它属性===*/
            if (CollectionUtils.isNotEmpty(list)) {
                for (MeetingFile meetingFile : list) {
                    meetingFile.setSequence(list.indexOf(meetingFile));// 设置序列
                    this.setMeetingFileOtherAttributes(dataTableMap, meetingFile);// 设置属性
                    this.resetMeetingFileOtherAttributes(meetingFile.getFiles());// 重置属性
                }
            }
            /*===分页===*/
            int limit = 0;
            int skip = 0;
            if (condition.getInteger("limit") != null) {
                limit = condition.getInteger("limit");
            }
            if (condition.getInteger("skip") != null) {
                skip = condition.getInteger("skip");
            }
            List<MeetingFile> filter = new MyFilter(limit, skip).doFilter(list);
            logger.info("分页+过滤:" + filter.size());
            return filter;
        }
        return list;
    }

    @Override
    public MeetingFile executeMeetingSynchronize(MeetingFile meetingFile) {
        List<MeetingFile> meetingFiles = new ArrayList();
        meetingFiles.add(meetingFile);
        return this.executeMeetingSynchronize(meetingFiles, meetingFile.getDataTable()).get(0);
    }

    /**
     * 执行上会附件更新
     *
     * @param meetingFiles
     * @param dataTable
     * @return
     */
    private List<MeetingFile> executeMeetingSynchronize(List<MeetingFile> meetingFiles, String dataTable) {
        if (CollectionUtils.isNotEmpty(meetingFiles)) {
            for (MeetingFile meetingFile : meetingFiles) {
                List<JSONObject> files = meetingFile.getFiles();// 上会附件
                if (CollectionUtils.isNotEmpty(files)) {
                    String id = meetingFile.getId();
                    Map<String, Object> dataMap = baseMongo.queryById(id, dataTable);
                    if (dataMap != null) {
                        Object attachmentListObj = dataMap.get(JsonField.attachmentList);// 附件列表
                        if (attachmentListObj != null) {
                            List<JSONObject> attachmentListJs = JSON.parseArray(JSON.toJSONString(attachmentListObj), JSONObject.class);
                            for (JSONObject attachment : attachmentListJs) {// 迭代附件列表
                                String item_name = "";
                                JSONObject typeJs = attachment.getJSONObject(JsonField.type);
                                if (typeJs != null) {
                                    item_name = typeJs.getString(JsonField.ITEM_NAME);
                                }
                                String file_name = attachment.getString(JsonField.fileName);
                                for (JSONObject file : files) {// 迭代上会附件
                                    String ITEM_NAME = file.getString(JsonField.ITEM_NAME);
                                    String FILE_NAME = file.getString(JsonField.fileName);
                                    if (ITEM_NAME.equals(item_name) && FILE_NAME.equals(file_name)) {
                                        attachment.put(JsonField.isMettingAttachment, "1");
                                    }
                                }
                            }
                            if (dataMap != null) {
                                List<HashMap> attachmentListMap = JSON.parseArray(JSON.toJSONString(attachmentListJs), HashMap.class);
                                dataMap.put(JsonField.attachmentList, attachmentListMap);
                                baseMongo.updateSetByObjectId(id, dataMap, dataTable);// 更新
                            }
                        }
                    }
                }
            }
        }
        return meetingFiles;
    }

    @Override
    public List<MeetingFile> executeMeetingSynchronize(JSONObject meeting, JSONObject condition) {
        List<MeetingFile> meetingFiles = this.queryMeetingSynchronize(meeting, condition);
        return this.executeMeetingSynchronize(meetingFiles, meeting.getString(JsonField.dataTable));
    }

    /**
     * 创建上会文件
     *
     * @param fileTable
     * @param dataTable
     * @param dataName
     * @return
     */
    List<MeetingFile> createMeetingFiles(String fileTable, String dataTable, String dataName) {
        final List<MeetingFile> list = new ArrayList();
        MongoCollection<Document> collection = baseMongo.getCollection(fileTable);
        if (collection != null) {
            logger.info(dataName + ":[" + fileTable + "]:" + collection.count());
            FindIterable<Document> findIterable = collection.find();
            findIterable.forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    JSONObject record = JSON.parseObject(document.toJson(), JSONObject.class);
                    if (record != null) {
                        JSONObject policyDecision = record.getJSONObject(JsonField.policyDecision);
                        if (policyDecision != null) {
                            Object decisionMakingCommitteeStaffFiles = policyDecision.get(JsonField.decisionMakingCommitteeStaffFiles);
                            if (decisionMakingCommitteeStaffFiles != null) {
                                List<JSONObject> decisionMakingCommitteeStaffFilesJs = JSON.parseArray(JSON.toJSONString(decisionMakingCommitteeStaffFiles), JSONObject.class);
                                if (CollectionUtils.isNotEmpty(decisionMakingCommitteeStaffFilesJs)) {
                                    String id = "";
                                    String url = "";
                                    if ("rcm_pre_info".equalsIgnoreCase(fileTable)) {
                                        JSONObject idJs = record.getJSONObject(JsonField._id);
                                        if (idJs != null) {
                                            id = idJs.getString(JsonField.$oid);
                                        }
                                        url += "#/projectPreInfoAllBoardView/";
                                    } else {
                                        id = record.getString(JsonField.projectFormalId);
                                        url += "#/projectInfoAllBoardView/";
                                    }
                                    url += id + "/" + JaXmlBeanUtil.encodeScriptUrl("index.html#/");// hello world
                                    if (StringUtils.isNotBlank(id)) {
                                        MeetingFile meetingFile = new MeetingFile();
                                        meetingFile.setUrl(url);
                                        meetingFile.setId(id);
                                        meetingFile.setName(dataName);
                                        meetingFile.setDataTable(dataTable);
                                        meetingFile.setFileTable(fileTable);
                                        meetingFile.setFiles(decisionMakingCommitteeStaffFilesJs);
                                        meetingFile.setStatus(1);
                                        // 构造url
                                        list.add(meetingFile);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        return list;
    }


    /**
     * 创建主数据Map
     *
     * @param mainDataTable
     * @return
     */
    private Map<String, JSONObject> createMainDataMap(String mainDataTable) {
        final Map<String, JSONObject> dataTableMap = new HashMap();
        if (StringUtils.isNotBlank(mainDataTable)) {
            MongoCollection mongoCollection = baseMongo.getCollection(mainDataTable);
            if (mongoCollection != null) {
                FindIterable<Document> findIterable = mongoCollection.find();
                findIterable.forEach(new Block<Document>() {
                    @Override
                    public void apply(Document document) {
                        JSONObject record = JSON.parseObject(document.toJson(), JSONObject.class);
                        if (record != null) {
                            JSONObject _id = record.getJSONObject(JsonField._id);
                            if (_id != null) {
                                String id = _id.getString(JsonField.$oid);
                                dataTableMap.put(id, record);
                            }
                        }
                    }
                });
            }
        }
        return dataTableMap;
    }

    /**
     * 设置上会附件的其它属性
     *
     * @param dataTableMap
     * @param meetingFile
     */
    private void setMeetingFileOtherAttributes(Map<String, JSONObject> dataTableMap, MeetingFile meetingFile) {
        String id = meetingFile.getId();
        JSONObject record = dataTableMap.get(id);
        if (record != null) {
            List<JSONObject> files = meetingFile.getFiles();// 上会附件列表
            Object attachmentList = record.get(JsonField.attachmentList);// 主表附件列表
            if (attachmentList != null) {
                if (CollectionUtils.isNotEmpty(files)) {
                    List<JSONObject> attachmentListJs = JSON.parseArray(JSON.toJSONString(attachmentList), JSONObject.class);
                    for (JSONObject attachment : attachmentListJs) {// 主表附件循环
                        String item_name = "";
                        JSONObject typeJs = attachment.getJSONObject(JsonField.type);
                        if (typeJs != null) {
                            item_name = typeJs.getString(JsonField.ITEM_NAME);
                        }
                        String file_name = attachment.getString(JsonField.fileName);
                        for (JSONObject file : files) {// 上会附件循环
                            // 判断条件:资源类型名称+文件名称
                            String ITEM_NAME = file.getString(JsonField.ITEM_NAME);
                            String FILE_NAME = file.getString(JsonField.fileName);
                            if (item_name.equals(ITEM_NAME)
                                    && file_name.equals(FILE_NAME)) {
                                file.put(JsonField.update, true);
                                file.put(JsonField.exist, true);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 属性重置
     *
     * @param files
     */
    private void resetMeetingFileOtherAttributes(List<JSONObject> files) {
        for (JSONObject file : files) {
            file.put(JsonField.update, file.get(JsonField.update) != null ? file.get(JsonField.update) : false);
            file.put(JsonField.exist, file.get(JsonField.exist) != null ? file.get(JsonField.exist) : false);
        }
    }

    @Override
    public List<InitFile> queryDifferentFiles(List<JSONObject> list, JSONObject condition) {
        List<InitFile> synchronizeList = this.querySynchronize(list, condition);
        boolean unDifferent = false;
        if (condition.get("unDifferent") != null) {
            unDifferent = condition.getBoolean("unDifferent");
        }
        List<InitFile> unDifferentList = new ArrayList();
        if (unDifferent) {
            for (InitFile initFile : synchronizeList) {
                if (initFile.isUpdate()) {
                    unDifferentList.add(initFile);
                }
            }
        } else {
            unDifferentList = synchronizeList;
        }
        return this.filterDifferentFiles(unDifferentList);
    }

    /**
     * 初始化不同的文件
     *
     * @param queryList
     * @return
     */
    private List<InitFile> filterDifferentFiles(List<InitFile> queryList) {
        List<InitFile> differentFiles = new ArrayList();
        // 对查询出的结果再进行过滤：判断不一致的文件名，必须满足该条件
        if (CollectionUtils.isNotEmpty(queryList)) {
            for (InitFile file : queryList) {
                if (this.differentFileName(file)) {
                    differentFiles.add(file);
                }
            }
        }
        return differentFiles;
    }

    @Override
    public List<InitFile> updateDifferentFiles(List<JSONObject> list, JSONObject condition) {
        List<InitFile> differentFiles = this.queryDifferentFiles(list, condition);
        this.updateDifferentFiles(differentFiles);
        return differentFiles;
    }

    /**
     * 更新文件
     */
    private void updateDifferentFiles(List<InitFile> differentFiles) {
        if (CollectionUtils.isNotEmpty(differentFiles)) {
            for (InitFile file : differentFiles) {
                try {
                    // 查询是否已经上传
                    FileDto fileDto = this.judgeFileDtoByPath(file);
                    if (fileDto != null) {
                        // 再次校验，对于后缀名相同的，不再进行同步了
                        if (this.compareSuffix(file, fileDto)) {
                            String type = file.getType();
                            String code = file.getCode();
                            String location = file.getLocation();
                            String localFile = file.getPathServer();
                            String optName = UserUtil.getCurrentUserName();
                            Integer opdId = new Integer(UserUtil.getCurrentUserId());
                            // 先删除云库上的文件
                            fileService.deleteFile(fileDto);
                            // 构造新的云库文件路径
                            String fileName = this.getFileNameWithSuffix(file);
                            String pathCloud = YunkuConf.UPLOAD_ROOT + "/" + type + "/" + code + "/" + fileName;
                            logger.info("更新文件：" + file.getPathCloud() + "===>" + pathCloud);
                            file.setPathCloud(pathCloud);
                            // 重新上传文件
                            fileService.fileUpload(pathCloud, localFile, type, code, location, optName, opdId);
                        }
                    }
                    // 因为第三方接口，所以事务的回滚要控制在最小单元。
                    // 第三方接口不可回滚。
                    // 一个失败，其它本地的记录不能全部回滚，否则附件记录会增加。
                } catch (Exception e) {
                    logger.error("更新失败：" + e.getMessage());
                }
            }
        }
    }

    /**
     * 校验文件后缀是否相同
     *
     * @param file
     * @param fileDto
     * @return
     */
    private boolean compareSuffix(InitFile file, FileDto fileDto) {
        String pathServer = file.getPathServer();
        String rcmFileName = fileDto.getRcmfilename();
        if (StringUtils.isAnyBlank(pathServer, rcmFileName)) {
            return true;
        }
        String pathServerSuffix = "";
        if (pathServer.lastIndexOf(".") > 0) {
            pathServer.substring(pathServer.lastIndexOf("."), pathServer.length());
        }
        String rcmFileNameSuffix = "";
        if (rcmFileName.lastIndexOf(".") > 0) {
            rcmFileNameSuffix = rcmFileName.substring(rcmFileName.lastIndexOf("."), rcmFileName.length());
        }
        if (StringUtils.isAnyBlank(pathServerSuffix, rcmFileNameSuffix)) {
            return true;
        }
        return !pathServerSuffix.equalsIgnoreCase(rcmFileNameSuffix);
    }


    /**
     * 判断服务器路径后缀名和文件后缀名是否相同
     *
     * @param file
     * @return
     */
    private boolean differentFileName(InitFile file) {
        String nameCloud = file.getNameCloud();
        String nameServer = file.getNameServer();
        String pathCloud = file.getPathCloud();
        String pathServer = file.getPathServer();
        // 严格校验，任何一个为空都不进行添加
        if (StringUtils.isAnyBlank(nameCloud, nameServer, pathCloud, pathServer)) {
            return false;
        }
        String pathServerSuffix = "";
        if (pathServer.lastIndexOf(".") > 0) {
            pathServerSuffix = pathServer.substring(pathServer.lastIndexOf("."), pathServer.length());
        }
        String nameServerSuffix = "";
        if (nameServer.lastIndexOf(".") > 0) {
            nameServerSuffix = nameServer.substring(nameServer.lastIndexOf("."), nameServer.length());
        }
        // 严格校验，任何一个为空都不进行添加
        if (StringUtils.isAnyBlank(pathServerSuffix, nameServerSuffix)) {
            return false;
        }
        return !pathServerSuffix.equalsIgnoreCase(nameServerSuffix);
    }

    /**
     * 获取和实际文后缀一致的文件名
     *
     * @param file
     * @return
     */
    private String getFileNameWithSuffix(InitFile file) {
        String nameServer = file.getNameServer();
        String pathServer = file.getPathServer();
        String newName = nameServer.substring(0, nameServer.lastIndexOf("."));
        String suffix = pathServer.substring(pathServer.lastIndexOf("."), pathServer.length());
        return newName + suffix;
    }

    @Override
    public InitFile updateDifferentFile(InitFile initFile) {
        List<InitFile> list = new ArrayList();
        list.add(initFile);
        this.updateDifferentFiles(list);
        return list.get(0);
    }
}
