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
 * Created by mnipa on 2019/1/21.
 */
public class UserUtil {
    private final static Logger logger = LoggerFactory.getLogger(UserUtil.class);

    public static UserDto getCurrentUser(HttpServletRequest request) {
        UserDto userDto = (UserDto) request.getSession().getAttribute("userInfo");
        Map<String, Object> map = null;
        if (userDto == null) {
            map = ThreadLocalUtil.getUser();
        }
        userDto = JSON.parseObject(JSON.toJSONString(map), UserDto.class);
        return userDto;
    }

    public static UserDto getCurrentUser() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        return JSON.parseObject(JSON.toJSONString(map), UserDto.class);
    }

    public static String getCurrentUserId() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        if (map == null){
            return null;
        }else{
            return JSON.parseObject(JSON.toJSONString(map), UserDto.class).getId();
        }
    }

    public static String getCurrentUserUuid() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        if (map == null){
            return null;
        }else{
            return JSON.parseObject(JSON.toJSONString(map), UserDto.class).getUuid();
        }
    }

    public static String getCurrentUserName() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        if (map == null){
            return null;
        }else{
            return JSON.parseObject(JSON.toJSONString(map), UserDto.class).getName();
        }
    }

    public static Boolean getCurrentUserAdmin() {
        Map<String, Object> map = ThreadLocalUtil.getUser();
        if (map == null){
            return null;
        }else{
            return JSON.parseObject(JSON.toJSONString(map), UserDto.class).getAdmin();
        }
    }
}
