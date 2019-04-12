package Util;

import org.junit.Test;

import util.EncryptUtil;

/**
 * 
 * @author Sunny Qi
 * 
 * 20190412
 *
 */
public class EncryptTest {
    @Test
    public void getTest(){
        String str = "tgbj" + "fkxt2019)$)%";
        System.out.println(EncryptUtil.MD5(str));
    }
}
