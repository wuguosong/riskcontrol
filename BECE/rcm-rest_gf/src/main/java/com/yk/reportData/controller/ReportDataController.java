package com.yk.reportData.controller;

import com.yk.power.service.IRoleService;
import com.yk.reportData.service.IReportDataService;
import common.PageAssistant;
import common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 同步报表数据
 *
 * @author Sunny Qi
 */
@Controller
@RequestMapping("/reportData")
public class ReportDataController {

    @Resource
    private IReportDataService reportDataService;
    @Resource
    private IRoleService roleService;

    @RequestMapping("/saveOrUpdate")
    @ResponseBody
    public Result saveOrUpdate(HttpServletRequest request) {
        Result result = new Result();
        String json = request.getParameter("json");
        try {
            this.reportDataService.saveOrUpdateReportData(json);
            result.setResult_code("S");
        } catch (Exception e) {
            result.setResult_code("R");
            result.setResult_name("同步数据报表失败，请联系管理员！" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 查询项目列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/getProjectList")
    @ResponseBody
    public Result getProjectList(HttpServletRequest request) {
        Result result = new Result();
        PageAssistant page = new PageAssistant(request.getParameter("page"));
        String json = request.getParameter("json");
        this.reportDataService.getProjectList(page, json);
        page.setParamMap(null);
        result.setResult_data(page);
        return result;
    }


    /**
     * 根据角色编码查询用户
     *
     * @param code 角色编码
     * @return
     */
    @RequestMapping("queryRoleUserByCode")
    @ResponseBody
    public Result queryRoleUserByCode(String code) {
        Result result = new Result();
        List<Map<String, Object>> mapList = roleService.queryRoleuserByCode(code);
        // 对结果进行过滤，根据当前登录用户
        // mapList = reportDataService.filterRoleUser(mapList);
        result.setResult_data(mapList);
        return result;
    }
}
