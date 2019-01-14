package goukuai;

import java.util.List;

import com.goukuai.srv.YunkuFileSrv;
import com.goukuai.dto.FileDto;
import com.goukuai.dto.LinkDto;
import com.yunkuent.sdk.ScopeType;

public class Test {
	@org.junit.Test
	public void testUpload() {
		FileDto fileDto = null;
		try {
			fileDto = YunkuFileSrv.getInstance().uploadByBlock("/pics/xinling.txt", "C:\\Users\\mnipa\\Desktop\\xinling.txt",
					"xxx", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(fileDto);
	}

	@org.junit.Test
	public void testList() {
		List<FileDto> files = null;
		try {
			files = YunkuFileSrv.getInstance().getFileList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(FileDto dto : files) {
			System.out.println(dto);
		}
	}

	@org.junit.Test
	public void testGet() {
		FileDto fileDto = null;
		try {
			fileDto = YunkuFileSrv.getInstance().getFileInfo("/pics/Love3.jpg");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(fileDto);
	}

	@org.junit.Test
	public void testDel() {
		try {
			FileDto fileDto = YunkuFileSrv.getInstance().deleteFile("/pics/Love3.jpg", "xxx");
			System.out.print(fileDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@org.junit.Test
	public void testPreviewLink() {
		LinkDto result3 = null;
		try {
			result3 = YunkuFileSrv.getInstance().getPreviewWindow("pics/Love3.jpg");
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
			result3 = YunkuFileSrv.getInstance().getDownloadWindow("/pics/Love3.jpg");
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
			result3 = YunkuFileSrv.getInstance().getPreviewUrl("pics/Love3.jpg", "xxxx");
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
			list = YunkuFileSrv.getInstance().getDownloadUrlByFullPath("pics/Love3.jpg");
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
			list = YunkuFileSrv.getInstance().getDownloadUrlByHash("980bc6db56fea28cf903ae1fce1c6ebb03dee8d0");
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
			list = YunkuFileSrv.getInstance().searchFile("北京", "/pics/", 1, 100, ScopeType.FILENAME,ScopeType.TAG,ScopeType.CONTENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(FileDto form : list) {
			System.out.println(form);
		}
	}
}
