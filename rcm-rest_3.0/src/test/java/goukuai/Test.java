package goukuai;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.goukuai.constant.YunkuConf;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.goukuai.srv.YunkuFileSrv;
import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import com.yunkuent.sdk.ScopeType;

public class Test {
	@org.junit.Test
	public void testUpload() {
		FileDto fileDto = null;
		try {
			fileDto = YunkuFileSrv.getInstance().uploadByBlock("/pics/Love.jpg", "C:\\Users\\mnipa\\Desktop\\Love.jpg",
					"我就是喜欢吃辣椒", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(fileDto);
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
}
