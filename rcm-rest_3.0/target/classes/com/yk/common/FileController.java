/**
 * 
 */
package com.yk.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import util.FileUtil;
import util.PropertiesUtil;
import util.Util;

import com.google.gson.Gson;

import common.Constants;
import common.Result;

/**
 * @author 80845530
 *
 */
@RequestMapping("/file")
@Controller
public class FileController {

	/**
	 * 上传文件
	 * 
	 * @param folder
	 * @param request
	 * @return
	 */
	@RequestMapping("/uploadFile")
	@ResponseBody
	public Result uploadFile(String folder, HttpServletRequest request) {
		Result result = null;
		try {

			// 将当前上下文初始化给 CommonsMutipartResolver （多部分解析器）
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
					request.getSession().getServletContext());
			// 检查form中是否有enctype="multipart/form-data"
			if (multipartResolver.isMultipart(request)) {
				// 将request变成多部分request
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				// 获取multiRequest 中所有的文件名
				Iterator<?> iter = multiRequest.getFileNames();

				while (iter.hasNext()) {
					// 一次遍历所有文件
					MultipartFile multipartFile = multiRequest.getFile(iter.next().toString());
					if (multipartFile != null) {
						String originalFilename = multipartFile.getOriginalFilename();
						String extName = originalFilename.substring(originalFilename.lastIndexOf("."));
						String path = Constants.UPLOAD_DIR + folder + "/";
						String nowFileName = System.currentTimeMillis() + extName;

						File file = new File(path);
						if (!file.exists()) {
							file.mkdirs();
						}

						// 上传
						multipartFile.transferTo(new File(path + nowFileName));

						// 返回结果
						result = new Result();
						List<Map<String, String>> listsMap = new ArrayList<Map<String, String>>();
						Map<String, String> map = new HashMap<String, String>();
						map.put("fileName", originalFilename);
						File file2 = new File(path + nowFileName);
						map.put("filePath", file2.getAbsolutePath().replaceAll("\\\\", "/"));
						map.put("upload_date", Util.format(Util.now()));
						listsMap.add(map);
						result.setResult_data(listsMap);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 下载文件
	 * 
	 * @param filenames
	 * @param filepaths
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/downloadFile")
	@ResponseBody
	public ResponseEntity<byte[]> downloadFile(String filenames, String filepaths, HttpServletRequest request,
			HttpServletResponse response) {
//		byte[] b = null;
//		HttpHeaders headers = null;
//		try {
//			filenames = URLDecoder.decode(filenames, "UTF-8");
//			filepaths = URLDecoder.decode(filepaths, "UTF-8");
//			File file = new File(filepaths);
//			b = FileUtils.readFileToByteArray(file);
//			headers = new HttpHeaders();
//
//			headers.setContentDispositionFormData("attachment", filenames);
//			//headers.setContentDispositionFormData("attachment", new String(filenames.getBytes("UTF-8"), "iso-8859-1"));
//			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//		} catch (Exception e) {
//			throw new RuntimeException("下载失败！", e);
//		}
//
//		return new ResponseEntity<byte[]>(b, headers, HttpStatus.CREATED);
		try {
			filenames = URLDecoder.decode(filenames, "UTF-8");
			filepaths = URLDecoder.decode(filepaths, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("下载失败！", e);
		}
		response.setContentType("application/octet-stream");
		String userAgent = request.getHeader("User-Agent");
		try {
			if (userAgent.contains("Firefox")) {
				filenames = new String(filenames.getBytes("UTF-8"), "ISO-8859-1");
				response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filenames));
			} else {
				filenames = URLEncoder.encode(filenames, "UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=" + filenames);
			}
		} catch (UnsupportedEncodingException e2) {
			response.setHeader("Content-Disposition", "attachment; filename=" + filenames);
		}
		try {
			File file = new File(filepaths);
			InputStream inputStream = new FileInputStream(file);
			OutputStream os  = response.getOutputStream();
			byte[] b = new byte[2048];
			int length;
			while ((length = inputStream.read(b)) > 0) {
				os.write(b, 0, length);
			}
			inputStream.close();
			os.flush();
			os.close();
		} catch (IOException e) {
			throw new RuntimeException("下载失败！", e);
		}
		return null;
	}

	/**
	 * 批量打包下载
	 * 
	 * @param filenames
	 * @param filepaths
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/downloadBatch")
	public ModelAndView downloadBatch(String filenames, String filepaths, HttpServletRequest request,
			HttpServletResponse response) {
		response.setCharacterEncoding("utf-8");
		try {
			filenames = filenames.replaceAll("%", "%25");  
			filenames = filenames.replaceAll("\\+", "%2B");
			filenames = URLDecoder.decode(filenames, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new RuntimeException("中文转码失败！", e1);
		}
		if (filenames == null || filepaths == null || "".equals(filenames) || "".equals(filepaths)) {
			response.setContentType("text/json; charset=utf-8");

			Result result = new Result();
			result.setSuccess(false);
			result.setResult_name("下载文件不存在！");
			Gson gs = new Gson();
			String print = gs.toJson(result);
			PrintWriter out;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				return null;
			}
			out.print(print);
			out.flush();
			out.close();
			return null;
		}
		String[] filenameArr = filenames.split(",");
		String[] filepathArr = filepaths.split(",");

		String briefName = "";
		if (filenameArr[0].length() < 10) {
			briefName = filenameArr[0].substring(0, filenameArr[0].lastIndexOf("."));
		} else {
			briefName = filenameArr[0].substring(0, 10);
		}

		String fileName = briefName + "...附件" + System.currentTimeMillis() + ".zip";
		response.setContentType("application/octet-stream");
		String userAgent = request.getHeader("User-Agent");
		try {
			if (userAgent.contains("Firefox")) {
				fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
				response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
			} else {
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			}
		} catch (UnsupportedEncodingException e2) {
			fileName = filenameArr[0];
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		}
		String tempFolder = PropertiesUtil.getProperty("tempfileFolder");
		File temp = new File(tempFolder);
		if (!temp.exists()) {
			temp.mkdirs();
		}
		String zipfile = tempFolder + fileName;
		this.makeZip(filenameArr, filepathArr, zipfile);
		try {
			File file = new File(zipfile);
			InputStream inputStream = new FileInputStream(file);
			OutputStream os = response.getOutputStream();
			byte[] b = new byte[2048];
			int length;
			while ((length = inputStream.read(b)) > 0) {
				os.write(b, 0, length);
			}
			inputStream.close();
			file.delete();
			os.flush();
			os.close();
		} catch (IOException e3) {
			throw new RuntimeException("批量下载文件失败！", e3);
		}
		return null;
	}

	/**
	 * 验证指定的文件是否存在
	 * 
	 * @param filepaths：用逗号隔开的文件路径
	 * @return
	 */
	@RequestMapping("/validFileExists")
	@ResponseBody
	public Result validFileExists(String filepaths) {
		Result result = new Result();
		if (filepaths == null || "".equals(filepaths)) {
			result.setSuccess(false);
			result.setResult_name("下载文件不存在！");
			return result;
		}
		String[] filepathArr = filepaths.split(",");
		boolean fileNotExists = false;
		for (int i = 0; i < filepathArr.length; i++) {
			File f = new File(filepathArr[i]);
			if (!f.exists()) {
				fileNotExists = true;
			}
		}
		if (fileNotExists) {
			result.setSuccess(false);
			result.setResult_name("下载文件不存在！");
		}
		return result;
	}

	/**
	 * 批量下载时，生成压缩文件
	 * 
	 * @param filenames
	 * @param filepaths
	 * @param zipfile
	 */
	private void makeZip(String[] filenames, String[] filepaths, String zipfile) {
		try {
			File file = new File(zipfile);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(zipfile);
			ZipOutputStream ops = new ZipOutputStream(fos);

			ops.setEncoding("gbk");
			byte[] buffer = new byte[2048];
			for (int i = 0; i < filenames.length; i++) {
				File f = new File(filepaths[i]);
				FileInputStream fis = new FileInputStream(f);
				ops.putNextEntry(new ZipEntry(filenames[i]));

				int len = -1;
				while ((len = fis.read(buffer)) != -1) {
					ops.write(buffer, 0, len);
				}
				ops.flush();
				ops.closeEntry();
				fis.close();
			}
			ops.close();
		} catch (IOException e) {
			throw new RuntimeException("批量下载文件失败，创建批量临时文件时出错！", e);
		}

	}
	@RequestMapping("/queryFile")
	@ResponseBody
	public Result queryFile(HttpServletRequest request) {
		Result result = new Result();
		String url = request.getParameter("url");
		url = url.replaceAll("\\\\", "/");
		File file = new File(url);
		boolean isDirectory = file.isDirectory();
		boolean exists = file.exists();
		boolean file2 = file.isFile();
		if(!exists){
			result.setSuccess(false).setResult_name("您输入的路径不存在！");
		}else if(file2){
			result.setSuccess(false).setResult_name("您输入的路径为文件路径，请输入文件夹路径！");
		}else if(isDirectory){
			File[] listFiles = file.listFiles();
			List<Map<String, Object>> FileList = new ArrayList<Map<String,Object>>();
			for (File f : listFiles) {
				Map<String, Object> map = new HashMap<String,Object>();
				System.out.println(f.getName()+"   "+f.getAbsolutePath());
				map.put("fileName", f.getName());
				map.put("filePath", f.getAbsolutePath());
				map.put("isFile", f.isFile());
				FileList.add(map);
			}
			result.setResult_data(FileList);
		}
		return result;
	}

}
