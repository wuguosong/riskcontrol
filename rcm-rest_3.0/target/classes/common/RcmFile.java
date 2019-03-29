/**
 * 
 */
package common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.bson.Document;

import report.Path;
import util.Util;

/**
 * @Description: 上传及下载文件
 * @Author zhangkewei
 * @Date 2016年8月22日 下午5:13:09  
 */
public class RcmFile {
	
	
	
	/**
	 * 上传文件
	 * @param json
	 * @param fileList
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, String>> upload(String json, List<FileItem> fileList) throws IOException{
		Document doc = Document.parse(json);
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		if(Util.isNotEmpty(fileList)){
			String folder = doc.getString("folder");
			String typeKey = doc.getString("typeKey");
			boolean notEmpty = Util.isNotEmpty(typeKey);
			if(notEmpty){
				folder = Path.getPropertyPath(typeKey);
			}
			for(FileItem fi : fileList){
				Map<String, String> map = new HashMap<String, String>();
				String fileName = fi.getName();
				String extName =  fileName.substring(fileName.lastIndexOf("."));
				
				String filePath = null;
				if(notEmpty){
					filePath = folder + System.currentTimeMillis()+extName;
				}else{
					filePath = Constants.UPLOAD_DIR+folder+"/"+System.currentTimeMillis()+extName;
				}
				File saveFile = new File(filePath);
				FileUtils.copyInputStreamToFile(fi.getInputStream(), saveFile);
				map.put("fileName", fileName);
				map.put("filePath", saveFile.getAbsolutePath().replaceAll("\\\\", "/"));
				listMap.add(map);
			}
		}
		return listMap;
	}
	public List<Map<String, String>> change(String json, List<FileItem> fileList) throws IOException{
		Document doc = Document.parse(json);
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		if(Util.isNotEmpty(fileList)){
			String folder = doc.getString("folder");
			String typeKey = doc.getString("typeKey");
			String toPath = doc.getString("toPath");
			boolean notEmpty = Util.isNotEmpty(typeKey);
			if(notEmpty){
				folder = Path.changeFrontFilePathRoot();
			}
			for(FileItem fi : fileList){
				Map<String, String> map = new HashMap<String, String>();
				String fileName = fi.getName();
				String filePath = null;
				if(notEmpty){
					filePath = folder +toPath+ fileName;
				}else{
					filePath = Constants.UPLOAD_DIR+folder+"/"+fileName;
				}
				File saveFile = new File(filePath);
				FileUtils.copyInputStreamToFile(fi.getInputStream(), saveFile);
				map.put("fileName", fileName);
				map.put("filePath", saveFile.getAbsolutePath().replaceAll("\\\\", "/"));
				listMap.add(map);
			}
		}
		return listMap;
	}
	
	/**
	 * 下载文件
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public InputStream downLoad(String json) throws IOException{
		Document doc = Document.parse(json);
		String filePath = doc.getString("filePath");
		File file = new File(filePath);
		InputStream inputStream = new FileInputStream(file);
		return inputStream;
	}

}
