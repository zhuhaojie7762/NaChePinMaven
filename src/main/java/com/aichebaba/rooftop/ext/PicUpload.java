package com.aichebaba.rooftop.ext;


import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.ext.UpYun.PARAMS;
import com.aichebaba.rooftop.ue.PathFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PicUpload {
    // 运行前先设置好以下三个参数
    private static final String BUCKET_NAME = "nachepin-storage";
    private static final String OPERATOR_NAME = "developer666";
    private static final String OPERATOR_PWD = "developer2788";

    // 图片文件上传
    public static String picUpload(String path) throws IOException {

        UpYun upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);

        // 本地待上传的图片文件
        File file = new File(path);
        String prefix = file.getName().substring(file.getName().lastIndexOf("."));
        String fileName = System.currentTimeMillis() + prefix;

        // 要传到upyun后的文件路径

        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH)+1;
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//        int hour = cal.get(Calendar.HOUR_OF_DAY);
//        int minute = cal.get(Calendar.MINUTE);

        String filePath = Constant.IMAGE_UPLOAD_ROOT + PathFormat.parse("{yyyy}/{mm}/{dd}/{hh}/{ii}/{rand:16}", cal.getTime()) + prefix;

        upyun.setTimeout(300);
        // 设置待上传文件的 Content-MD5 值
        // 如果又拍云服务端收到的文件MD5值与用户设置的不一致，将回报 406 NotAcceptable 错误
        upyun.setContentMD5(UpYun.md5(file));

        // 设置待上传文件的"访问密钥"
        // 注意：
        // 仅支持图片空！，设置密钥后，无法根据原文件URL直接访问，需带URL后面加上（缩略图间隔标志符+密钥）进行访问
        // 举例：
        // 如果缩略图间隔标志符为"!"，密钥为"bac"，上传文件路径为"/folder/test.jpg"，
        // 那么该图片的对外访问地址为：http://空间域名 /folder/test.jpg!bac
        //upyun.setFileSecret("bac");

        // 上传文件，并自动创建父级目录（最多10级）
        boolean result = upyun.writeFile(filePath, file, true);

//        // 图片宽度
//        String width = upyun.getPicWidth();
//        // 图片高度
//        String height = upyun.getPicHeight();
//        // 图片帧数
//        String frames = upyun.getPicFrames();
//        // 图片类型
//        String type = upyun.getPicType();
        if (result) {
            file.delete();
        } else {
            filePath = "";
        }
        return filePath;
    }


    // 制作图片缩略图
    public static Boolean picGmkerl(String path, String fileName, String size) throws IOException {
        UpYun upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);

        File sFile = new File(path);
        File tFile = new File(sFile.getParent() + File.separator + fileName);
        fileCopy(sFile, tFile);

        // 本地待上传的图片文件
//        File file = new File(path);
//        String prefix = file.getName().substring(file.getName().lastIndexOf("."));
//        String fileName = System.currentTimeMillis() + prefix;

        // 要传到upyun后的文件路径
        String filePath = Constant.IMAGE_UPLOAD_ROOT + fileName;

        // 设置缩略图的参数
        Map<String, String> params = new HashMap<>();

        // 设置缩略图类型，必须搭配缩略图参数值（KEY_VALUE）使用，否则无效
        params.put(PARAMS.KEY_X_GMKERL_TYPE.getValue(),
                PARAMS.VALUE_FIX_MAX.getValue());

        // 设置缩略图参数值，必须搭配缩略图类型（KEY_TYPE）使用，否则无效
        params.put(PARAMS.KEY_X_GMKERL_VALUE.getValue(), size);

        // 设置缩略图的质量，默认 95
        params.put(PARAMS.KEY_X_GMKERL_QUALITY.getValue(), "95");

        // 设置缩略图的锐化，默认锐化（true）
        params.put(PARAMS.KEY_X_GMKERL_UNSHARP.getValue(), "true");

//        // 若在 upyun 后台配置过缩略图版本号，则可以设置缩略图的版本名称
//        // 注意：只有存在缩略图版本名称，才会按照配置参数制作缩略图，否则无效
//        params.put(PARAMS.KEY_X_GMKERL_THUMBNAIL.getValue(), "small");

        // 上传文件，并自动创建父级目录（最多10级）
        boolean result = upyun.writeFile(filePath, tFile, true, params);

        tFile.delete();

        return result;
    }

    // 图片旋转
    public static void picRotate(String path) throws IOException {
        UpYun upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);

        // 本地待上传的图片文件
        File file = new File(path);
        String prefix = file.getName().substring(file.getName().lastIndexOf("."));
        String fileName = System.currentTimeMillis() + prefix;

        // 要传到upyun后的文件路径
        String filePath = Constant.IMAGE_UPLOAD_ROOT + fileName;

        // 图片旋转功能具体可参考：http://wiki.upyun.com/index.php?title=图片旋转
        Map<String, String> params = new HashMap<String, String>();

        // 设置图片旋转：只接受"auto"，"90"，"180"，"270"四种参数
        params.put(PARAMS.KEY_X_GMKERL_ROTATE.getValue(),
                PARAMS.VALUE_ROTATE_90.getValue());

        // 上传文件，并自动创建父级目录（最多10级）
        boolean result = upyun.writeFile(filePath, file, true, params);

    }

    // 图片裁剪
    public static String picGrop(String path, int x, int y, int width, int height) throws IOException {
        UpYun upyun = new UpYun(BUCKET_NAME, OPERATOR_NAME, OPERATOR_PWD);

        // 本地待上传的图片文件
        File file = new File(path);
        String prefix = file.getName().substring(file.getName().lastIndexOf("."));
        String fileName = System.currentTimeMillis() + prefix;

        // 要传到upyun后的文件路径
        String filePath = Constant.IMAGE_UPLOAD_ROOT + fileName;

        // 图片裁剪功能具体可参考：http://wiki.upyun.com/index.php?title=图片裁剪
        Map<String, String> params = new HashMap<String, String>();

        // 设置图片裁剪，参数格式：x,y,width,height
        params.put(PARAMS.KEY_X_GMKERL_CROP.getValue(), x + "," + y + "," + width + "," + height);

        // 上传文件，并自动创建父级目录（最多10级）
        boolean result = upyun.writeFile(filePath, file, true, params);

        if (result) {
            file.delete();
        } else {
            filePath = "";
        }
        return filePath;
    }

    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */

    private static void fileCopy(File s, File t) {

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;

        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                fi.close();
                in.close();
                fo.close();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
