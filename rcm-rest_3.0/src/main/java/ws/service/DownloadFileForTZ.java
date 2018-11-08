package ws.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import util.DbUtil;
import util.FileUtil;

import com.mongodb.BasicDBObject;
import common.Constants;

public class DownloadFileForTZ {
	private final String DocumentNamePre=Constants.PREASSESSMENT;
	private final String DocumentNamePfr=Constants.RCM_PROJECTFORMALREVIEW_INFO;
	private final String DocumentNamePreFeedBack=Constants.RCM_FEEDBACK_INFO;
	
	public void downloadFile(String pams,String OId,String filepan) throws IOException{
		BasicDBObject queryAndWhere=new BasicDBObject();
		queryAndWhere.put("_id",new ObjectId(OId));
		if(pams.equals("pre")){
			Document oldDoc =DbUtil.getColl(DocumentNamePre).find(queryAndWhere).first();
			oldDoc.put("_id",new ObjectId(OId));
			List<Document> attachmentList =(List<Document>) oldDoc.get("attachment");
			for (Document OldDocum : attachmentList) {
				//获取原始所有文件
				List<Object> oldFile=(List<Object>) OldDocum.get("files");
				if(null!=oldFile && !oldFile.isEmpty() && !"".equals(oldFile)){
					for(int j=0;j<oldFile.size();j++){
						if(!"".equals(oldFile.get(j))){
							Document file=(Document) oldFile.get(j);
							if(null!=file){
								if(null!=file.get("filePath") && !"".equals(file.get("filePath"))){
									String  str = file.get("filePath").toString();
									String newStr = str.replaceAll("\\\\","/");
									String newfilePath=FileUtil.downloadFromTz2(newStr,filepan);
									Document d=new Document();
									d.put("fileName", file.get("fileName"));
									d.put("filePath", newfilePath);
									d.put("version", file.get("version"));
									d.put("upload_date", file.get("upload_date"));
									if(null!=file.get("programmed") && !"".equals(file.get("programmed"))){
										d.put("programmed", file.get("programmed"));
									}
									if(null!=file.get("approved") && !"".equals(file.get("approved"))){
										d.put("approved", file.get("approved"));
									}
									oldFile.set(j, d); 
							    }
							}
						}
					}
				}
			}
			BasicDBObject updateSetValue=new BasicDBObject("$set",oldDoc);
			DbUtil.getColl(DocumentNamePre).updateOne(queryAndWhere,updateSetValue);
		}else if(pams.equals("pfr")){
			Document oldDoc = DbUtil.getColl(DocumentNamePfr).find(queryAndWhere).first();
			oldDoc.put("_id",new ObjectId(OId));
			List<Document> attachmentList =(List<Document>) oldDoc.get("attachment");
			for (Document OldDocum : attachmentList) {
				//获取原始所有文件
				List<Object> oldFile=(List<Object>) OldDocum.get("files");
				if(null!=oldFile && !oldFile.isEmpty() && !"".equals(oldFile)){
					for(int j=0;j<oldFile.size();j++){
						if(!"".equals(oldFile.get(j))){
							Document file=(Document) oldFile.get(j);
							if(null!=file){
								if(null!=file.get("filePath") && !"".equals(file.get("filePath"))){
									String  str = file.get("filePath").toString();
									String newStr = str.replaceAll("\\\\","/");
									String newfilePath=FileUtil.downloadFromTz2(newStr,filepan);
									Document d=new Document();
									d.put("fileName", file.get("fileName"));
									d.put("filePath", newfilePath);
									d.put("version", file.get("version"));
									d.put("upload_date", file.get("upload_date"));
									if(null!=file.get("programmed") && !"".equals(file.get("programmed"))){
										d.put("programmed", file.get("programmed"));
									}
									if(null!=file.get("approved") && !"".equals(file.get("approved"))){
										d.put("approved", file.get("approved"));
									}
									oldFile.set(j, d); 
							    }
							}
						}
					}
				}
			}
			BasicDBObject updateSetValue=new BasicDBObject("$set",oldDoc);
			DbUtil.getColl(DocumentNamePfr).updateOne(queryAndWhere,updateSetValue);
		}else if(pams.equals("preFeedBack")){
			Document oldDoc = DbUtil.getColl(DocumentNamePreFeedBack).find(queryAndWhere).first();
			oldDoc.put("_id",new ObjectId(oldDoc.get("_id").toString()));
			Document file=(Document) oldDoc.get("projectNoticefile");
			if(null!=file){
				if(null!=file.get("filePath") && !"".equals(file.get("filePath"))){
					String  str = file.get("filePath").toString();
					String newStr = str.replaceAll("\\\\","/");
					String newfilePath=FileUtil.downloadFromTz2(newStr,filepan);
					file.put("filePath", newfilePath);
				}
			}
			BasicDBObject updateSetValue=new BasicDBObject("$set",oldDoc);
			DbUtil.getColl(DocumentNamePreFeedBack).updateOne(queryAndWhere,updateSetValue);
    	}
    	
	}
	
    public static String downLoadFromUrl(String urlStr,String savePath) throws IOException{ 
    	FTPUtils f = FTPUtils.getInstance();
       /* URL url = new URL(urlStr);    
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
        //设置超时间为3秒  
        conn.setConnectTimeout(3*1000);  
        //防止屏蔽程序抓取而返回403错误  
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");*/
    	 //得到输入流  
        InputStream inputStream =f.retrieveFile(urlStr);
        //InputStream inputStream =conn.getInputStream();
        //获取自己数组  
        byte[] getData = readInputStream(inputStream);      
        //获取文件后缀
        String extName =  urlStr.substring(urlStr.lastIndexOf("."));
        //文件保存位置  
        File saveDir = new File(savePath);  
        if(!saveDir.exists()){  
            saveDir.mkdir();  
        }  
        //文件绝对路径
        String filePath= saveDir+File.separator+System.currentTimeMillis()+extName;
        File file = new File(filePath);      
        FileOutputStream fos = new FileOutputStream(file);       
        fos.write(getData);   
        if(fos!=null){  
            fos.close();    
        }  
        if(inputStream!=null){  
            inputStream.close();  
        }  
        System.out.println("info: download success");   
        return filePath;
    }  
  
    /** 
     * 从输入流中获取字节数组 
     * @param inputStream 
     * @return 
     * @throws IOException 
     */  
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }
   /* @SuppressWarnings("unused")
	public static String smbGet(String remoteUrl,String savePathDir) { 
    	InputStream in = null; 
    	OutputStream out = null; 
    	String filePath=null;
    	try { 
    		remoteUrl="smb://administrator:Abcd1234@/10.10.20.4/ipm/"+remoteUrl;
	    	SmbFile remoteFile = new SmbFile(remoteUrl); 
	    	if(remoteFile==null){ 
	    		System.err.println("共享文件不存在"); 
	    		return filePath; 
	    	} 
    		String fileName = remoteFile.getName(); 
		    String extName =  remoteUrl.substring(remoteUrl.lastIndexOf("."));
	        //文件保存位置  
	        File saveDir = new File(savePathDir);  
	        if(!saveDir.exists()){  
	            saveDir.mkdir();  
	        }  
	        //文件绝对路径
	        filePath= saveDir+File.separator+System.currentTimeMillis()+extName;
    		File localFile = new File(filePath); 
    		in = new BufferedInputStream(new FileInputStream(remoteFile.getUncPath())); 
    		out = new BufferedOutputStream(new FileOutputStream(localFile)); 
    		byte[] buffer = new byte[1024]; 
    		int len = 0;
    		while((len = in.read(buffer)) > 0){ 
    			out.write(buffer, 0, len);
    			buffer = new byte[1024]; 
    		} 
    	} catch (Exception e) { 
    		e.printStackTrace(); 
    	} finally { 
	    	try { 
		    	out.close(); 
		    	in.close(); 
	    	} catch (IOException e) { 
	    		e.printStackTrace(); 
	    	} 
    	}
    	return filePath;
    }*/
    
   /* public static void main(String[] args){
    	String url="ProjectFiles/CTSW2016111784/2d2ca747-a0fb-4cfb-8ba2-29c2901b78d7业务系统集成SSO接口说明.docx";
    	smbGet(url,"d:\\dovc\\");
    }*/
}
