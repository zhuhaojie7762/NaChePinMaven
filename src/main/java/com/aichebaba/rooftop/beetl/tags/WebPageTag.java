package com.aichebaba.rooftop.beetl.tags;

import com.jfinal.plugin.activerecord.dao.Page;
import org.beetl.core.GeneralVarTagBinding;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebPageTag extends GeneralVarTagBinding {
    public static final String small = "small";
    public static final String middle = "middle";
    public static final String large = "large";
    private long total = 0;
    private int perSize = 0;
    private int curNo = 0;
    private int totalPage = 0;
    private Map<String, Object> params;
    private boolean showNum =false;

    //pagination-small pagination-large
    private String style = "";

    private void init() {
        Page page = (Page) getAttributeValue("val");
        //small, middle, large
        String size = (String) getAttributeValue("size");
        Object o = getAttributeValue("params");
        String show = (String) getAttributeValue("showNum");
        params = o == null ? null : (Map<String, Object>) o;
        if (size == null) {
            size = middle;
        }
        if (middle.equals(size)) {
            style = "";
        } else if (small.equals(size)) {
            style = "pagination-small";
        } else if (large.equals(size)) {
            style = "pagination-large";
        }
        total = page.getTotalNum();
        perSize = page.getPerSize();
        curNo = page.getCurNo();
        totalPage = (int) (total / perSize);
        if (totalPage * perSize < total) {
            totalPage++;
        }
        keys.add("perSize");
        keys.add("curNo");
        keys.add("totalPage");
        if("show".equals(show)){
            showNum = true;
        }else{
            showNum = false;
        }
    }

    @Override
    public void render() {
        init();
        StringBuilder html = new StringBuilder();
        html.append("<form class='pagination-form'><div class='sui-pagination ").append(style).append("'>")
                .append("<ul>")
                .append("<li class='prev ").append(curNo == 1 ? "disabled" : "")
                .append("'><a>«上一页</a></li>");
        int endNo = Math.min((curNo + 2), totalPage);
        int startNo = curNo <= 2 ? 1 : curNo - 2;
        endNo += curNo <= 3 ? (3 - curNo) : 0;
        endNo = Math.min(endNo, totalPage);
        startNo -= (curNo + 2) > totalPage ? (curNo + 2 - totalPage) : 0;
        startNo = Math.max(startNo, 1);

        if (startNo > 1) {
            html.append("<li class=\"dotted\"><span>...</span></li>");
        }
        for (int i = startNo; i <= endNo; i++) {
            html.append("<li ").append(i == curNo ? "class='active'>" : ">").append("<a>").append(i).append("</a ></li>");
        }
        if (totalPage > endNo) {
            html.append("<li class=\"dotted\"><span>...</span></li>");
        }
        html.append("<li class='next ").append(curNo >= totalPage ? "disabled" : "")
                .append("'><a>下一页»</a></li>")
                .append("</ul>");

        if(showNum) {
            html.append("<div><span>共").append(totalPage).append("页&nbsp;&nbsp;")
                    .append("<span>到<input type=\"text\" value=\"").append(curNo).append("\" class=\"page-num\"><input type='button' class=\"page-confirm\" value='确定' />")
                    .append("页</span></div>");
        }
        html.append("</div>");

        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!keys.contains(entry.getKey())) {
                    html.append("<input type=\"hidden\" name=\"").append(entry.getKey()).append("\" value=\"").append(entry.getValue()).append("\" />");
                }
            }
        }
        html.append("<input type=\"hidden\" name=\"perSize\" value=\"").append(perSize).append("\" />")
                .append("<input type=\"hidden\" name=\"curNo\" value=\"").append(curNo).append("\" />")
                .append("<input type=\"hidden\" name=\"totalPage\" value=\"").append(totalPage).append("\" />")
                .append("</form>");
        try {
            ctx.byteWriter.writeString(html.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<String> keys = new HashSet<>();
}
