package com.aichebaba.rooftop.ext;

import com.aichebaba.rooftop.config.Constant;

import java.io.File;
import java.io.IOException;
import java.util.Map;


/**
 * 文件 上 传
 */
public class FileUpload {

    // 运行前先设置好以下三个参数
    private static final String BUCKET_NAME = "nachepin-storage";
    private static final String OPERATOR_NAME = "developer666";
    private static final String OPERATOR_PWD = "developer2788";

    private static UpYun upyun = null;

    private static void init(){
        // 初始化空间
        upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);

        // 切换 API 接口的域名接入点，默认为自动识别接入点
        upyun.setApiDomain(UpYun.ED_AUTO);

        //设置连接超时时间，默认为30秒
        upyun.setTimeout(180);
    }


//    public static void main(String[] args) throws Exception {
//
//        // 初始化空间
//        upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);
//
//        // ****** 可选设置 begin ******
//
//        // 切换 API 接口的域名接入点，默认为自动识别接入点
//        // upyun.setApiDomain(UpYun.ED_AUTO);
//
//        // 设置连接超时时间，默认为30秒
//        // upyun.setTimeout(60);
//
//        // 设置是否开启debug模式，默认不开启
//        upyun.setDebug(true);
//
//        // ****** 可选设置 end ******
//
//        // 1.创建目录，有两种形式
//        testMkDir();
//
//        // 2.上传文件，图片空间的文件上传请参考 PicBucketDemo.java
//        testWriteFile();
//        // 3.获取文件信息
//        testGetFileInfo();
//
//        // 4.读取目录
//        testReadDir();
//
//        // 5.获取空间占用大小
//        testGetBucketUsage();
//
//        // 6.获取某个目录的空间占用大小
//        testGetFolderUsage();
//
//        // 7.读取文件/下载文件
//        testReadFile();
//
//        TestPicBucket testpic = new TestPicBucket();
//        testpic.testWritePic();
//
//        // 8.删除文件
//        //	testDeleteFile();
//
//        // 9.删除目录
//        //	testRmDir();
//    }

    /**
     * 获取空间占用大小
     */
    public static void testGetBucketUsage() {

        long usage = upyun.getBucketUsage();

        System.out.println("空间总使用量：" + usage + "B");
        System.out.println();
    }

    /**
     * 获取某个目录的空间占用大小
     */
    public static void testGetFolderUsage() {

        // 带查询的目录，如 "/" 或 "/tmp"
        String dirPath = Constant.FILE_UPLOAD_ROOT;

        long usage = upyun.getFolderUsage(dirPath);

        System.out.println("'" + dirPath + "'目录占用量： " + usage + "B");
        System.out.println();
    }

    /**
     * 上传文件
     *
     * @throws IOException
     */
    public static String fileUpload(String path) throws IOException {

        init();

		/*
		 * 上传方法：采用数据流模式上传文件（节省内存），可自动创建父级目录（最多10级）
		 */
        File file = new File(path);
        String fileName = file.getName();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        fileName = System.currentTimeMillis() + prefix;

        // 要传到upyun后的文件路径
        String filePath = Constant.FILE_UPLOAD_ROOT + fileName;

        boolean result = upyun.writeFile(filePath, file, true);

        if(result){
            file.delete();
        }else{
            filePath="";
        }

        return filePath;
    }

    /**
     * 获取文件信息
     */
    public static Map<String, String> getFileInfo(String filePath) {

        init();
        return upyun.getFileInfo(filePath);
    }

    /**
     * 读取文件/下载文件
     *
     * @throws IOException
     */
    public static File readFile(String filePath) throws IOException {

        init();
		/*
		 * 方法2：下载文件，采用数据流模式下载文件（节省内存）
		 */
        // 要写入的本地临时文件
        File file = File.createTempFile("upyunTempFile_", "");

        // 把upyun空间下的文件下载到本地的临时文件
        boolean result = upyun.readFile(filePath, file);
        return file;
    }

    /**
     * 删除文件
     */
    public static Boolean deleteFile(String filePath) {

        init();
        // 删除文件
        boolean result = upyun.deleteFile(filePath);

        return result;
    }

    /**
     * 创建目录
     */
    public static Boolean makeDir(String dir) {
        init();
        boolean result = upyun.mkDir(dir, true);
        return result;
    }

//    /**
//     * 读取目录下的文件列表
//     */
//    public static void testReadDir() {
//
//        // 参数可以换成其他目录路径
//        String dirPath = DIR_ROOT;
//
//        // 读取目录列表，将返回 List 或 NULL
//        List<UpYun.FolderItem> items = upyun.readDir(dirPath);
//
//        if (null == items) {
//            System.out.println("'" + dirPath + "'目录下没有文件。");
//
//        } else {
//
//            for (int i = 0; i < items.size(); i++) {
//                System.out.println(items.get(i));
//            }
//
//            System.out.println("'" + dirPath + "'目录总共有 " + items.size()
//                    + " 个文件。");
//        }
//
//        System.out.println();
//    }

    /**
     * 删除目录
     */
    public static Boolean removeDir(String dirPath) {

        init();
        boolean result = upyun.rmDir(dirPath);

        return result;
    }

}
