/**
 * 
 */
package rcm;

import java.util.Map;

import org.junit.Test;

/**
 * @Description: TODO
 * @Author zhangkewei
 * @Date 2016年11月3日 上午11:56:27  
 */
public class PTeamTest {
	@Test
	public void testSelectTeamLeaderByMemberId(){
		Pteam team = new Pteam();
		Map<String, String> map = team.selectTeamLeaderByMemberId("a9d578bb52af42d9b56da5963a8b3248", "1");
		System.out.println(map.toString());
	}
	@Test
	public void testGetTeamLeaderByMemberId(){
		Pteam team = new Pteam();
		String json = "{teamMemberId:'a9d578bb52af42d9b56da5963a8b3248',type:'1'}";
		Map<String, String> map = team.getTeamLeaderByMemberId(json);
		System.out.println(map.toString());
	}
}
