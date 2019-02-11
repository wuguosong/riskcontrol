package com.yk.restful.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Restful风格请求方式示例
 * Created by LiPan on 2019/1/30.
 */
@Controller
@RequestMapping("articles")
public class ArticleController {
    /**
     * 新增-POST
     *
     * @param method
     * @param article
     * @return
     */
    @RequestMapping(value = "article", method = RequestMethod.POST)
    @ResponseBody
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
    public String get(String method, @PathVariable String id) {
        System.out.println(method);
        System.out.println(id);
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
