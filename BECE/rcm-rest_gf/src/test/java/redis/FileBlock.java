package redis;

import org.junit.Test;

import java.io.*;
import java.util.*;

/**
 * Created by LiPan on 2019/2/1.
 */
public class FileBlock {
    String diskPath = "E:/upload";
    String fileName = "linux.x64_11gR2_database_1of2.zip";
    String filePath = diskPath + "/" + fileName;
    String tempPath = diskPath + "/temp";
    String mergePath = diskPath + "/merge";
    int size = 1024 * 1024 * 10;


    /**
     * 文件分割
     */
    @Test
    public void split() {
        try {
            File file = new File(filePath);
            Long total = file.length();
            InputStream in = new FileInputStream(file);
            byte[] buf = new byte[size];
            try {
                int i = 0;
                File tempDir = new File(tempPath);
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                long current = 0;
                while (in.read(buf) != -1) {
                    i++;
                    OutputStream out = new FileOutputStream(new File(tempPath + "/" + i));
                    out.write(buf);
                    out.flush();
                    out.close();
                    current += size;
                    System.out.println("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n");
                    System.out.println(current + "/" + total);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 合并文件
     */
    @Test
    public void merge() {
        File tempDir = new File(tempPath);
        File mergeDir = new File(mergePath);
        if (!mergeDir.exists()) {
            mergeDir.mkdirs();
        }
        if (tempDir.exists() && tempDir.isDirectory()) {
            File[] array = tempDir.listFiles();
            List<File> list = Arrays.asList(array);
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
                }
            });
            OutputStream out = null;
            try {
                out = new FileOutputStream(new File(mergePath + "/" + fileName), true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int total = list.size();
            Iterator<File> iterator = list.listIterator();
            int current = 0;
            while (iterator.hasNext()) {
                current++;
                File file = iterator.next();
                try {
                    InputStream in = new FileInputStream(file);
                    byte[] buf = new byte[size];
                    while (in.read(buf) != -1) {
                        out.write(buf);
                        out.flush();
                    }
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n");
                System.out.println(current + "/" + total);
                file.delete();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
