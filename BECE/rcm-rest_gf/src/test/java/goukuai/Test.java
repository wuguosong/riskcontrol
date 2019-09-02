package goukuai;

import com.goukuai.constant.YunkuConf;
import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import com.goukuai.srv.YunkuFileSrv;
import com.yk.rcm.file.service.IFileService;
import com.yunkuent.sdk.ScopeType;
import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/config/applicationContext.xml")
@ActiveProfiles("local")
public class Test {
	@Resource
	private IFileService fileService;
	@org.junit.Test
	public void testUpload() {
		try {
			String fullPath = YunkuConf.UPLOAD_ROOT + "/" + "LiPan" + "/" + "10086" + "/";
			fullPath = fullPath.replaceFirst(YunkuConf.UPLOAD_ROOT, "");
			FileDto fileDto = fileService.fileUpload(fullPath + "Love.jpg", "C:\\Users\\mnipa\\Desktop\\Love.jpg","LiPan", "10086", "#1", "lipan", 10000);
			System.out.println(fileDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void testList() {
		List<FileDto> files = null;
		try {
			files = YunkuFileSrv.getInstance().getFileList("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("==========================================");
		for(FileDto dto : files) {
			System.out.println(dto.getFullpath());
		}
	}

	@org.junit.Test
	public void testGet() {
		FileDto fileDto = null;
		try {
			fileDto = YunkuFileSrv.getInstance().getFileInfo("/text/xinling.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("==========================================");
		System.out.println(fileDto.getFilename());
	}

	@org.junit.Test
	public void testDel() {
		try {
			FileDto fileDto = YunkuFileSrv.getInstance().deleteFile("/pics/Love.jpg", "我就是喜欢吃辣椒");
			System.out.println("==========================================");
			System.out.println(fileDto.getFilename());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void testPreviewLink() {
		LinkDto result3 = null;
		try {
			result3 = YunkuFileSrv.getInstance().getPreviewWindow("/pics/Love.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result3);
	}

	@org.junit.Test
	public void testDownloadLink() {
		LinkDto result3 = null;
		try {
			result3 = YunkuFileSrv.getInstance().getDownloadWindow("/pics/Love.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result3);
	}

	@org.junit.Test
	public void testPreviewUrl() {
		String result3 = null;
		try {
			result3 = YunkuFileSrv.getInstance().getPreviewUrl("/pics/Love.jpg", "我就是喜欢吃辣椒");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result3);
	}

	@org.junit.Test
	public void testDownloadUrlByFullPath() {
		List<String> list = null;
		try {
			list = YunkuFileSrv.getInstance().getDownloadUrlByFullPath("/pics/Love.jpg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(String url : list) {
			System.out.println(url);
		}
	}

	@org.junit.Test
	public void testDownloadUrlByHash() {
		List<String> list = null;
		try {
			list = YunkuFileSrv.getInstance().getDownloadUrlByHash("02209dc19c7950413ce9946ee606ef6331f6b58a");
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(String url : list) {
			System.out.println(url);
		}
	}
	
	@org.junit.Test
	public void testUploadServer() {
		List<String> list = null;
		try {
			list = YunkuFileSrv.getInstance().getUploadServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(String form : list) {
			System.out.println(form);
		}
	}
	
	@org.junit.Test
	public void testSearch() {
		List<FileDto> list = null;
		try {
			list = YunkuFileSrv.getInstance().searchFile("x", "", 0, 100, ScopeType.FILENAME,ScopeType.TAG,ScopeType.CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(FileDto form : list) {
			System.out.println(form);
		}
	}
	@org.junit.Test
	public void test() {
		System.out.println(StringUtils.isAnyBlank(null, "n", "c"));
	}

	public void test2(){
	}
}
