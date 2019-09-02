package com.yk.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import report.Path;
import util.Util;

import com.yk.exception.rcm.DocReportException;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 生成docx文件
 * @author hubiao
 *
 */
public class DocxReportUtil {
	private static Configuration cfg = null;
	static {
		cfg = new Configuration(Configuration.VERSION_2_3_23);
		try {
			// 注册tmlplate的load路径
			cfg.setTemplateLoader(new FileTemplateLoader(new File(Path.TEMPLATE_FILE)));
		} catch (Exception e) {

		}
	}

	/**
	 * 根据docx.xml模版生成docx文件
	 * @param param 参数(其中\r\n|\n  是可识别换行)
	 * @param templateXml  docx模版文件里的word/document.xml文件
	 * @param templateDoc  docx模版文件
	 * @param outpath		输出路径
	 */
	public static void generateWord(Map<String, Object> param, String templateXml,String templateDoc,
			String outpath){
		if(Util.isEmpty(outpath)){
			throw new DocReportException("生成word文档失败，输出路径不能为空！");
		}
		if(Util.isEmpty(templateXml)|| Util.isEmpty(templateDoc) || !new File(Path.TEMPLATE_FILE,templateXml).exists()|| !new File(Path.TEMPLATE_FILE,templateDoc).exists()){
			throw new DocReportException("生成word文档失败，给定的模版不存在！");
		}
		if(!new File(outpath).getParentFile().exists()){
			new File(outpath).getParentFile().mkdirs();
		}
		//将为null的值赋一个空字符串,不后模版引擎合成遇到null值会报错
		Set<String> keySet = param.keySet();
		for (String key : keySet) {
			Object value = param.get(key);
			if(Util.isEmpty(value)){
				value = "";
			}else{
				value = value.toString().replaceAll("\n", "<w:br/>");
			}
			param.put(key, value);
		}
		ByteArrayOutputStream baos = null;
		Writer out = null;
		try {
			// 获取模板
			Template template = cfg.getTemplate(templateXml);
			template.setOutputEncoding("UTF-8");
			
			// 合并数据
			baos = new ByteArrayOutputStream(2048);
			out = new OutputStreamWriter(baos);
			template.process(param, out);
			
			//将xml输出到docx
			outDocx(baos.toByteArray(),new File(Path.TEMPLATE_FILE,templateDoc),outpath);
		} catch (Exception e) {
			throw new DocReportException("生成word文档失败，详情请查看日志！", e);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void outDocx(byte[] templateArray,File docxTemplateFile,String toFilePath) throws ZipException, IOException {
		ZipFile zipFile = new ZipFile(docxTemplateFile);			
		Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
		ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(toFilePath));
		try {
			int len=-1;
			byte[] buffer=new byte[1024];
			while(zipEntrys.hasMoreElements()) {
				ZipEntry next = zipEntrys.nextElement();
				InputStream is = zipFile.getInputStream(next);
				//把输入流的文件传到输出流中 如果是word/document.xml由我们输入
				zipout.putNextEntry(new ZipEntry(next.toString()));
				if("word/document.xml".equals(next.toString())){
					zipout.write(templateArray,0,templateArray.length);
				}else {
					while((len = is.read(buffer))!=-1){
						zipout.write(buffer,0,len);
					}
				}		
				is.close();
			}	
		} catch (Exception e) {
			throw new DocReportException("生成word文档失败，详情请查看日志！", e);
		}finally{
			zipFile.close();
			zipout.close();
		}
	}
}
