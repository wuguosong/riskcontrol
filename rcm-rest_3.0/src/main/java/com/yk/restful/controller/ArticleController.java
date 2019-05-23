package com.yk.restful.controller;

import com.yk.log.annotation.SysLog;
import com.yk.log.constant.LogConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Restful风格请求方式示例
 * Created by LiPan on 2019/1/30.
 */
@Controller
@RequestMapping("articles")
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    /**
     * 新增-POST
     *
     * @param method
     * @param article
     * @return
     */
    @RequestMapping(value = "article", method = RequestMethod.POST)
    @ResponseBody
    @SysLog(module = "SYS", operation = LogConstant.CREATE, description = "新增文章")
    public String add(String method, String article) {
        System.out.println(method);
        System.out.println(article);
        return "add[POST]";
    }

    /**
     * 更新-PUT
     *
     * @param method
     * @param article
     * @return
     */
    @RequestMapping(value = "article", method = RequestMethod.PUT)
    @ResponseBody
    @SysLog(module = "SYS", operation = LogConstant.UPDATE, description = "更新文章")
    public String update(String method, String article) {
        System.out.println(method);
        System.out.println(article);
        return "update[PUT]";
    }

    /**
     * 获取-GET
     *
     * @param method
     * @param id
     * @return
     */
    @RequestMapping(value = "article/{id}", method = RequestMethod.GET)
    @ResponseBody
    @SysLog(module = "SYS", operation = LogConstant.QUERY, description = "查询文章")
    public String get(String method, @PathVariable String id) {
        System.out.println(method);
        System.out.println(id);
        logger.info("获取文章：" + id + "\t" + method);
        return "get[GET]";
    }

    /**
     * 删除-DELETE
     *
     * @param method
     * @param id
     * @return
     */
    @RequestMapping(value = "article/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @SysLog(module = "SYS", operation = LogConstant.DELETE, description = "删除文章")
    public String delete(String method, @PathVariable String id) {
        System.out.println(method);
        System.out.println(id);
        return "delete[DELETE]";
    }

    /**
     * 注意:
     * 在web.xml中进行配置,否则PUT方式获取不到实体参数
     * 配置在字符转码过滤器之前
     * 提供PUT表单传参支持
     <filter>
     <filter-name>httpPutFormContentFilter</filter-name>
     <filter-class>org.springframework.web.filter.HttpPutFormContentFilter</filter-class>
     </filter>
     <filter-mapping>
     <filter-name>httpPutFormContentFilter</filter-name>
     <url-pattern>/*</url-pattern>
     </filter-mapping>
     */
}
