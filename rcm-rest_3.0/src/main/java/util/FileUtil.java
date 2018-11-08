/**
 * 
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * @author 80845530
 *
 */
public class FileUtil {
	
	 public static String downloadFromTz2(String downfile, String savepath){
		 	downfile = downfile.replaceAll("\\\\", File.separator);
		 	String inputfile = PropertiesUtil.getProperty("tzshare")+downfile;
			String extName =  downfile.substring(downfile.lastIndexOf("."));
	        //文件保存位置  
	        File saveDir = new File(savepath);  
	        if(!saveDir.exists()){  
	            saveDir.mkdirs();  
	        }  
	        //文件绝对路径
	        String filePath= saveDir+File.separator+util.Util.getUUID()+extName;
	        File file = new File(filePath);      
			try {
				FileInputStream in = new FileInputStream(inputfile);
				FileOutputStream os = new FileOutputStream(file); 
				byte[] temp = new byte[4*1024];
				int len = 0;
				while((len = in.read(temp))>0){
					os.write(temp, 0, len);
				}
				in.close();
				os.close();
			} catch (IOException e) {
				throw new RuntimeException("从文件服务器下载文件时出错", e);
			}
			return filePath;
		}
	/**
	 * 复制本地文件
	 * @param from 将要复制的文件
	 * @param to 复制后的文件
  	 */
	public static void copyFile(File from, File to) {
        //如果文件父目录不存在，则创建 
        if(!to.getParentFile().exists()){  
            to.getParentFile().mkdirs();  
        }
        try {
			FileUtils.copyFile(from, to);
		} catch (IOException e) {
			throw new RuntimeException("复制文件时出错", e);
		}
	}
	/**
	 * 复制本地文件
	 * @param from 将要复制的文件
	 * @param to 复制后的文件
  	 */
	public static void copyFile(String fromFilePath, String toFilePath) {
		File from = new File(fromFilePath);
		File to = new File(toFilePath);
		copyFile(from, to);
	}
	 /**
	  * 删除指定的文件
	  * @param filepath
	  */
	 public static void removeFile(String filepath){
		 File file = new File(filepath);
		 if(file.exists()){
			 file.delete();
		 }
	 }
	 
//	 public static void main(String[] args) {
//		 String url = "10.10.20.4\ipm\\ProjectFiles\CTSW20161209236\4ff2c18e-b532-4b97-b14d-0d0ccfd2f01dSetting.xml";
//		downloadFromTz2("ProjectFiles\\CTSW20161209236\\4ff2c18e-b532-4b97-b14d-0d0ccfd2f01dSetting.xml", "/mw/aa");
//	}
	 /**
	  * 获取给定文件的扩展名，如果给定的为null，或者给的是文件夹，抛出异常
	  * @param file
	  * @return
	  */
	 public static String getFileSuffix(File file){
		 if(file == null || file.isDirectory()){
			 throw new RuntimeException("给定的文件为空或为文件夹，无法获取扩展名["+file.getPath()+"]");
		 }
		 String fileName = file.getName();
		 int index = fileName.lastIndexOf(".");
		 if(index == -1){
			 return "";
		 }
	     String suffix = fileName.substring(index+1);
	     return suffix;
	 }
	 
}
