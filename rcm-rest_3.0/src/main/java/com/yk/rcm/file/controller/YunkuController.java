package com.yk.rcm.file.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import common.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yk.common.FileController;
import com.yk.rcm.file.service.IFileService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YunkuController extends FileController{
	@Resource
	private IFileService iFileService;

	@RequestMapping("/upload")
	@ResponseBody
	public Result upload(String folder, HttpServletRequest request, String docType, String docCode){
		Result result = super.uploadFile(folder, request);
		if(result != null && result.getResult_data() != null){
			ArrayList<Map<String, String>> mapsLis = (ArrayList<Map<String, String>>) result.getResult_data();
			for (Map map : mapsLis){
				String filePath = (String)map.get("filePath");
				File file = new File(filePath);
				if(file.exists() && file.isFile()){
					String fileName = file.getName();
					// TODO 构造上传路径
				}
			}
		}
		return result;
	}
}
