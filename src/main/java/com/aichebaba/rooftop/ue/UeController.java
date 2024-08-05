package com.aichebaba.rooftop.ue;

import com.aichebaba.rooftop.config.AppConfig;
import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.ext.PicUpload;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UeController extends BaseController {


    private Map<String, Object> configMap;

    @PostConstruct
    public void config() throws IOException {
        if (configMap == null) {
            StringBuilder builder = new StringBuilder();
            try {
                InputStreamReader reader = new InputStreamReader(
                        new FileInputStream(PathKit.getRootClassPath() + "/config.json"), "UTF-8");
                BufferedReader bfReader = new BufferedReader(reader);

                String tmpContent;
                while ((tmpContent = bfReader.readLine()) != null) {
                    builder.append(tmpContent);
                }
                bfReader.close();
            } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
            }
            String config = filter(builder.toString());
            configMap = (Map<String, Object>) com.alibaba.fastjson.JSONObject.parse(config);
        }
    }

    @Clear
    public void index() throws IOException {
        String action = getPara("action");
        Object result = null;
        switch (action) {
            case "config":
                result = configMap;
                break;
            case "uploadimage":
                result = uploadImage(action);
                break;
        }
        renderJson(result);
    }

    private UeditorState uploadImage(String action) throws IOException {
        UploadFile uploadFile = getFile();
        Map<String, Object> conf = getConfig(action);

//        String filePath = uploadFile.getSaveDirectory() + File.separator + uploadFile.getFileName();
        File file = uploadFile.getFile();

        String upFile = PicUpload.picUpload(file.getPath());
        if (StrKit.isBlank(upFile)) {
            return new UeditorState("图片上传失败,请重新上传！", null, null, null);
        }

        upFile = AppConfig.getImgPath() + upFile;

        return new UeditorState(UeditorState.SUCCESS, upFile, "图片", getName(upFile));
    }

    private String getPath(String fileName) {
        if (StrKit.isBlank(fileName)) {
            return "";
        }
        int pos = fileName.lastIndexOf("/");
        return fileName.substring(0, pos);
    }

    private String getName(String fileName) {
        if (StrKit.isBlank(fileName)) {
            return "";
        }
        int pos = fileName.lastIndexOf("/");
        return fileName.substring(pos + 1);
    }

    private Map<String, Object> getConfig(String action) {
        Map<String, Object> conf = new HashMap<>();
        String savePath = null;
        int type = ActionMap.getType(action);
        switch (type) {
            case 4:
                conf.put("isBase64", "false");
                conf.put("maxSize", configMap.get("fileMaxSize"));
                conf.put("allowFiles", configMap.get("fileAllowFiles"));
                conf.put("fieldName", configMap.get("fileFieldName"));
                savePath = configMap.get("filePathFormat").toString();
                break;
            case 1:
                conf.put("isBase64", "false");
                conf.put("maxSize", configMap.get("imageMaxSize"));
                conf.put("allowFiles", configMap.get("imageAllowFiles"));
                conf.put("fieldName", configMap.get("imageFieldName"));
                savePath = configMap.get("imagePathFormat").toString();
                break;
            case 3:
                conf.put("maxSize", configMap.get("videoMaxSize"));
                conf.put("allowFiles", configMap.get("videoAllowFiles"));
                conf.put("fieldName", configMap.get("videoFieldName"));
                savePath = configMap.get("videoPathFormat").toString();
                break;
            case 2:
                conf.put("filename", "scrawl");
                conf.put("maxSize", configMap.get("scrawlMaxSize"));
                conf.put("fieldName", configMap.get("scrawlFieldName"));
                conf.put("isBase64", "true");
                savePath = configMap.get("scrawlPathFormat").toString();
                break;
            case 5:
                conf.put("filename", "remote");
                conf.put("filter", configMap.get("catcherLocalDomain"));
                conf.put("maxSize", configMap.get("catcherMaxSize"));
                conf.put("allowFiles", configMap.get("catcherAllowFiles"));
                conf.put("fieldName", configMap.get("catcherFieldName") + "[]");
                savePath = configMap.get("catcherPathFormat").toString();
                break;
            case 7:
                conf.put("allowFiles", configMap.get("imageManagerAllowFiles"));
                conf.put("dir", configMap.get("imageManagerListPath"));
                conf.put("count", configMap.get("imageManagerListSize"));
                break;
            case 6:
                conf.put("allowFiles", configMap.get("fileManagerAllowFiles"));
                conf.put("dir", configMap.get("fileManagerListPath"));
                conf.put("count", configMap.get("fileManagerListSize"));
        }
        conf.put("savePath", savePath);
        conf.put("rootPath", "/");

        return conf;
    }

    private String filter(String input) {
        return input.replaceAll("/\\*[\\s\\S]*?\\*/", "");
    }
}
