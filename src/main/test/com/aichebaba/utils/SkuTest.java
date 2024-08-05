package com.aichebaba.utils;

import com.aichebaba.rooftop.model.Sku;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.joda.time.LocalDate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 2016/3/29.
 */
public class SkuTest extends TestCase {

    public void testSku() {
        Sku sku = new Sku();
        sku.setStock(10);
        sku.setLockStock(2);
        System.out.println(JSON.toJSONString(sku));
    }

    public void testMonth() {
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.minusMonths(seasonMap.get(now.getMonthOfYear())).withDayOfMonth(1);
        System.out.println(getPreSeasonStart(now));
    }


    private LocalDate getPreSeasonStart(LocalDate now) {

        return now.minusMonths(seasonMap.get(now.getMonthOfYear()) +3).withDayOfMonth(1);
    }

    private Map<Integer, Integer> seasonMap = new HashMap<Integer, Integer>() {
        {
            //春
            put(3, 0);
            put(4, 1);
            put(5, 2);

            //夏
            put(6, 0);
            put(7, 1);
            put(8, 2);

            //秋
            put(9, 0);
            put(10, 1);
            put(11, 2);

            //冬
            put(12, 0);
            put(1, 1);
            put(2, 2);
        }
    };

    public void testStr() {
        String[] ss = new String[]{};

        System.out.println(ss.toString());
    }

    public void testDate() {
        LocalDate l = LocalDate.now().withDayOfMonth(13);
        System.out.println(l.toDate());
    }


    public void testToJson() {
        String jsonStr = JSON.toJSONString(1.2d);
        System.out.println(jsonStr);
        Object o = JSON.parse(jsonStr);
        System.out.println(o.getClass());
    }
}
