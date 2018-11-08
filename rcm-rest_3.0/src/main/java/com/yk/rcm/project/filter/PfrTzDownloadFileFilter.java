package com.yk.rcm.project.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import report.Path;
import util.FileUtil;
import util.PropertiesUtil;
import util.Util;

import com.yk.ext.filter.IProjectTzFilter;
import com.yk.ext.filter.ProjectTzFilterChain;
import com.yk.flow.util.JsonUtil;

import common.Result;

/**
 * 对投资推送的正式评审的附件进行下载
 * @author wufucan
 *
 */
public class PfrTzDownloadFileFilter implements IProjectTzFilter {

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(Map<String, Object> data, Result result,
			ProjectTzFilterChain chain) {
		Document doc = (Document)data;
		Document apply=(Document) doc.get("apply");
		List<Document> attachmentList =(List<Document>) doc.get("attachment");
		
		boolean otherFlag = true;
		for (Document attachment : attachmentList) {
			String item_name = (String) attachment.get("ITEM_NAME");
			if(item_name.indexOf("其他")>=0 || item_name.indexOf("其它")>=0){
				otherFlag = false;
			}
		}
		
		if(otherFlag){
			Document newFile = new Document();
			newFile.put("ITEM_NAME", "其他文件");
			newFile.put("UUID", Util.getUUID());
			newFile.put("files", new ArrayList<Document>());
			attachmentList.add(newFile);
		}
		
		//将原始附件信息备份
		String attachStr = JsonUtil.toJson(attachmentList);
		List<Document> oldAttachment = JsonUtil.fromJson(attachStr, ArrayList.class);
		doc.put("oldAttachment", oldAttachment);
		
		//生成文件路径地址,附件将要存放的文件夹
		String newFileFolder = Path.formalAttachmentPath()+apply.get("projectNo")+"/";
		
		//循环所有附件，进行处理
		for (Document attachment : attachmentList) {
			//生成uuid
			if(attachment.containsKey("UUID")){
				attachment.put("UUID", Util.getUUID());
			}
			List<Document> files = (List<Document>) attachment.get("files");
			for(int m = 0; m < files.size(); m++){
				Document file = files.get(m);
				String filePath = file.getString("filePath");
				//替换文件路径中的斜杠
				String preStr = filePath.replace("\\", File.separator).replace("\\\\", File.separator);
				String tzshare = PropertiesUtil.getProperty("tzshare");
				preStr = tzshare.concat(File.separator).concat(preStr);
				//原文件名
			    File preFile = new File(preStr);
			    String extSuffix = FileUtil.getFileSuffix(preFile);
				//新文件名
				String newFilePath = newFileFolder.concat(Util.getUUID()).concat(".").concat(extSuffix);
				File newFile = new File(newFilePath);
				/*//设置文件的路径为最新的，风控提供的附件路径
				file.put("filePath", preStr);*/
				FileUtil.copyFile(preFile, newFile);
				Document d=new Document();
				d.put("fileName", file.get("fileName"));
				d.put("filePath", newFilePath);
				d.put("version", file.get("version"));
				d.put("upload_date", file.get("upload_date"));
				if(Util.isNotEmpty(file.get("programmed"))){
					d.put("programmed", file.get("programmed"));
				}
				if(Util.isNotEmpty(file.get("approved"))){
					d.put("approved", file.get("approved"));
				}
				//附件添加UUId
				d.put("UUID", new ObjectId().toHexString());
				//替换文件信息
				files.set(m, d);
			}
		}
		chain.doFilter(data, result, chain);
	}
}
