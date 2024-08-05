package com.aichebaba.rooftop.beetl.functions;

import com.aichebaba.rooftop.model.GoodsClass;
import com.aichebaba.rooftop.vo.AttrSearch;
import com.aichebaba.rooftop.vo.SearchParams;
import com.jfinal.kit.StrKit;

public class UrlFunctions {
    public String searchUrl(String url, String where, SearchParams params, String... exKeys) {
        StringBuilder sb = new StringBuilder();
        if (StrKit.notBlank(url)) {
            sb.append(url);
            if (StrKit.notBlank(where) || (params != null && params.notEmpty())) {
                sb.append("?");
            }
        }
        if (StrKit.notBlank(where)) {
            sb.append(where);
            if (params != null && params.notEmpty()) {
                sb.append("&");
            }

        }
        if (params != null && params.notEmpty()) {
            sb.append(params.getUrlParams(exKeys));
        }
        return sb.toString();
    }

    public String searchUrl2(String url, String where, SearchParams params, String... exKeys) {
        StringBuilder sb = new StringBuilder();
        String sorts;
        if (StrKit.notBlank(url)) {
            sb.append(url);
            if (StrKit.notBlank(where) || (params != null && params.notEmpty())) {
                sb.append("?");
            }
        }

        if (StrKit.notBlank(where)) {
            if(!where.endsWith("sorts")) {
                sb.append(where);
                if (params != null && params.notEmpty()) {
                    sb.append("&");
                }
            }
        }
        if (params != null && params.notEmpty()) {
            sb.append(params.getUrlParams2(exKeys));
        }
        if (StrKit.notBlank(where)) {
            if(!(sb.toString().endsWith("?") || sb.toString().endsWith("&"))){
                sb.append("&");
            }
            if (where.endsWith("sorts")) {
                sb.append(where);
            }
        }
        if(sb.toString().endsWith("&")){
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 分类查询页用
     * @param url
     * @param where
     * @param params
     * @param exKeys
     * @return
     */
    public String searchUrl3(String url, String where, SearchParams params, String... exKeys) {
        StringBuilder sb = new StringBuilder();
        if (StrKit.notBlank(url)) {
            sb.append(url);
            if (StrKit.notBlank(where) || (params != null && params.notEmpty())) {
                sb.append("?");
            }
        }
        if (StrKit.notBlank(where)) {
            if (where.startsWith("attr")) {
                //属性值处理(例:attr=1_2)
                String value = where.replace("attr=", "");
                String[] pp = value.split("_");
                if ("0".equals(pp[1])) {
                    sb.append("attr=");
                } else {
                    sb.append(where);
                }
                if (!params.getAttrs().isEmpty()) {
                    for (AttrSearch attrSearch : params.getAttrs().values()) {
                        if (!pp[0].equals(attrSearch.getPid())) {
                            if (!sb.toString().endsWith("attr=")) {
                                sb.append("-");
                            }
                            sb.append(attrSearch.getPid()).append("_").append(attrSearch.getValue());
                        }
                    }
                }
                if (params != null && (params.notEmpty())) {
                    sb.append("&");
                }

            } else {
                sb.append(where);
                if (params != null && (params.notEmpty() || !params.getAttrs().isEmpty())) {
                    sb.append("&");
                }
            }
        }
        if (params != null && params.notEmpty()) {
            sb.append(params.getUrlParams3(exKeys));
        }
        return sb.toString();
    }
}
