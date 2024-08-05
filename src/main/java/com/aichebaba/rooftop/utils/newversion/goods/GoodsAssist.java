package com.aichebaba.rooftop.utils.newversion.goods;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created He Dy on 2016/12/16.
 */
public class GoodsAssist {
    private static Logger logger = LoggerFactory.getLogger(GoodsAssist.class);

    public static List getShowSize(int sell, List object) {
        List list = new ArrayList();
        if (sell < 0) {
            logger.error("展示个数应大于0");
            return list;
        }
        for (int i = 0; i < sell; i++) {
            if (object.size() > i) {
                list.add(object.get(i));
            } else {
                break;
            }
        }
        return list;
    }

    public static String getEncryptAccount(String userName) {
        int size = userName.length();
        if (size > 5) {
            userName = userName.substring(0, 3) + "****" + userName.substring(userName.length() - 2);
        } else if (size < 5 && size > 2) {
            userName = userName.charAt(0) + "*******" + userName.substring(userName.length() - 1);
        } else if (size == 2) {
            userName = userName.charAt(0) + "********";
        } else {
            userName = "*********";
        }
        return userName;
    }
}
