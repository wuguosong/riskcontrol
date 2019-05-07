package common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.JSONPResponseBodyAdvice;
import com.yk.common.service.ICommonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2019/5/7 0007.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class CommonTest {
    @Resource
    private ICommonService commonService;

    @Test
    public void testAll() {
        JSONObject jsonObject = commonService.validateProject("bulletin", "1ad195e8ef074346bac3598e727f333e");
        System.out.println(jsonObject);
        jsonObject = commonService.validateProject("formalReview", "5a1e2f7222ddf2508278de51");
        System.out.println(jsonObject);
        jsonObject = commonService.validateProject("preReview", "592f7b4e22ddf21cc13a31a1");
        System.out.println(jsonObject);
    }

    @Test
    public void testApproval() {
        JSONObject jsonObject = commonService.validateApprovalProject("bulletin", "1ad195e8ef074346bac3598e727f333e");
        System.out.println(jsonObject);
        jsonObject = commonService.validateApprovalProject("formalReview", "5a1e2f7222ddf2508278de51");
        System.out.println(jsonObject);
        jsonObject = commonService.validateApprovalProject("preReview", "592f7b4e22ddf21cc13a31a1");
        System.out.println(jsonObject);
    }

    @Test
    public void testReview() {
        JSONObject jsonObject = commonService.validateReviewProject("bulletin", "1ad195e8ef074346bac3598e727f333e");
        System.out.println(jsonObject);
        jsonObject = commonService.validateReviewProject("formalReview", "5a1e2f7222ddf2508278de51");
        System.out.println(jsonObject);
        jsonObject = commonService.validateReviewProject("preReview", "592f7b4e22ddf21cc13a31a1");
        System.out.println(jsonObject);
    }
}
