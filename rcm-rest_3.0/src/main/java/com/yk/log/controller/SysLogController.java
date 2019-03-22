package com.yk.log.controller;

import com.yk.log.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/sys/log")
public class SysLogController {
	@Autowired
	private ISysLogService sysLogService;
	
}
