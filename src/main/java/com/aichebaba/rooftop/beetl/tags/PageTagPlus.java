package com.aichebaba.rooftop.beetl.tags;

import com.jfinal.plugin.activerecord.dao.Page;
import org.beetl.core.GeneralVarTagBinding;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PageTagPlus extends GeneralVarTagBinding {
    public static final String small = "small";
    public static final String middle = "middle";
    public static final String large = "large";
    private long total = 0;
    private int perSize = 0;
    private int curNo = 0;
    private int totalPage = 0;
    private Map<String, String[]> params;

    //pagination-small pagination-large
    private String style = "";

    private void init() {
        Page page = (Page) getAttributeValue("val");
        //small, middle, large
        String size = (String) getAttributeValue("size");
        Object o = getAttributeValue("params");
        params = o == null ? null : (Map<String, String[]>) o;
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
    }

    @Override
    public void render() {
        init();
        StringBuilder html = new StringBuilder();
        html.append("<form class='pagination-form'><div class='sui-pagination ").append(style).append("'>")
                .append("<ul>")
                .append("<li class='first ").append(curNo == 1 ? "disabled" : "")
                .append("'><a style='padding: 4px 5px'><i class=\"sui-icon icon-pc-prev\"></i></a></li>")
                .append("<li class='prev ").append(curNo == 1 ? "disabled" : "")
                .append("'><a style='padding: 4px 5px'><i class=\"sui-icon icon-pc-chevron-left\"></i></a></li>");
        int endNo = Math.min((curNo + 2), totalPage);
        int startNo = curNo <= 2 ? 1 : curNo - 2;
        endNo += curNo <= 3 ? (3 - curNo) : 0;
        endNo = Math.min(endNo, totalPage);
        startNo -= (curNo + 2) > totalPage ? (curNo + 2 - totalPage) : 0;
        startNo = Math.max(startNo, 1);
        for (int i = startNo; i <= endNo; i++) {
            html.append("<li ").append(i == curNo ? "class='active'>" : ">").append("<a>").append(i).append("</a ></li>");
        }
        html.append("<li class='next ").append(curNo == totalPage ? "disabled" : "")
                .append("'><a style='padding: 4px 5px'><i class=\"sui-icon icon-pc-chevron-right\"></i></a ></li>")
                .append("<li class='last ").append(curNo == totalPage ? "disabled" : "")
                .append("'><a style='padding: 4px 5px'><i class=\"sui-icon icon-pc-next\"></i></a ></li>")
                .append("</ul>")
                .append("<div><span>共").append(totalPage).append("页").append(total).append("条</span>&nbsp;")
                .append("<span>到<input type=\"text\" name='curNo' value=\"")
                .append(curNo)
                .append("\" class=\"page-num\"><input type='button' class=\"page-confirm\" value='确定' />")
                .append("页</span>")
                .append("&nbsp;&nbsp;<span>每页条数<input type=\"text\" name='perSize' value=\"")
                .append(perSize)
                .append("\" class=\"page-num\"><input type='button' class=\"page-confirm\" value='确定' />")
                .append("</span></div>")
        .append("</div>");
        if (params != null) {
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
                if (!keys.contains(entry.getKey())) {
                    String[] values = entry.getValue();
                    for(String value : values) {
                        if (!"perSize".equalsIgnoreCase(entry.getKey())) {
                            html.append("<input type=\"hidden\" name=\"").append(entry.getKey()).append("\" value=\"").append(value).append("\" />");
                        }
                    }
                }
            }
        }
        html.append("<input type=\"hidden\" name=\"curNo\" value=\"").append(curNo).append("\" />")
                .append("<input type=\"hidden\" name=\"totalPage\" value=\"").append(totalPage).append("\" />")
                .append("<input type='hidden' name='tabParam' value=\"${tabParam!'total'}\" />")
                .append("</form>");
        try {
            ctx.byteWriter.writeString(html.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<String> keys = new HashSet<>();
}
