package com.aichebaba.rooftop.utils;

import com.aichebaba.rooftop.model.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Sql 工具类
 * Created by He DaoYuan on 2016/11/30.
 */
public class SqlHelper {
    private static final Logger logger = LoggerFactory.getLogger(SqlHelper.class);

    // S SQL查询语句 IN 关键字对field进行组装
    public static List<Object> fieldInGroup(List<OrderStatus> statuses) {
        List<Object> statusList = new ArrayList<>();
        for (OrderStatus os : statuses) {
            statusList.add(os.getVal());
        }
        return statusList;
    }
    // E SQL查询语句 IN 关键字对field进行组装
}
