package util;

import com.alibaba.fastjson.JSON;
import com.yk.common.SpringUtil;
import com.yk.power.service.IUserService;
import fnd.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户工具类
 */
public class UserUtil {
    private final static Logger logger = LoggerFactory.getLogger(UserUtil.class);

    /**
     * 获取当前登录用户
     * @param request 请求
     * @return UserDto 用户实体
     */
    public static UserDto getCurrentUser(HttpServletRequest request) {
        UserDto userDto = (UserDto) request.getSession().getAttribute("userInfo");
        Map<String, Object> map = null;
        if (userDto == null) {
            map = ThreadLocalUtil.getUser();
        }
        userDto = JSON.parseObject(JSON.toJSONString(map), UserDto.class);
        return userDto;
    }

    /**
     * 获取当前登录用户
     * @return UserDto 用户实体
     */
    public static UserDto getCurrentUser() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        return JSON.parseObject(JSON.toJSONString(map), UserDto.class);
    }

    /**
     * 获取当前登录用户ID
     * @return String 用户Id
     */
    public static String getCurrentUserId() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        if (map == null){
            return null;
        }else{
            return JSON.parseObject(JSON.toJSONString(map), UserDto.class).getId();
        }
    }

    /**
     * 获取当前登录用户
     * @return String UUID
     */
    public static String getCurrentUserUuid() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        if (map == null){
            return null;
        }else{
            return JSON.parseObject(JSON.toJSONString(map), UserDto.class).getUuid();
        }
    }

    /**
     * 获取当前登录用户名
     * @return String 用户名
     */
    public static String getCurrentUserName() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        if (map == null){
            return null;
        }else{
            return JSON.parseObject(JSON.toJSONString(map), UserDto.class).getName();
        }
    }

    /**
     * 当前登录用户是否管理员
     * @return Boolean
     */
    public static Boolean getCurrentUserAdmin() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        if (map == null){
            return null;
        }else{
            return JSON.parseObject(JSON.toJSONString(map), UserDto.class).getAdmin();
        }
    }
}
