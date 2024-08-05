package com.aichebaba.rooftop.ext;

import com.aichebaba.rooftop.utils.ExcelUtils;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ExcelRender extends Render {

    private static Logger logger = LoggerFactory.getLogger(ExcelRender.class);

    private String fileName;

    private String template;

    private final static String CONTENT_TYPE = "application/msexcel;charset=" + getEncoding();

    public ExcelRender(String template, String fileName) {
        this.template = template;
        this.fileName = fileName;
    }

    @Override
    public void render() {
        response.reset();
        try {
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            logger.error("encode in export ", e);
        }
        response.setContentType(CONTENT_TYPE);
        OutputStream os = null;
        try {
            Enumeration<String> attrs = request.getAttributeNames();
            Map<String, Object> root = new HashMap<>();
            while (attrs.hasMoreElements()) {
                String attrName = attrs.nextElement();
                root.put(attrName, request.getAttribute(attrName));
            }
            os = response.getOutputStream();
            Workbook wb = ExcelUtils.export(template, root);
            wb.write(os);
        } catch (Exception e) {
            logger.error("error in encode fileName", e);
            throw new RenderException(e);
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
