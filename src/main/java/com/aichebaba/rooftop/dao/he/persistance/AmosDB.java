package com.aichebaba.rooftop.dao.he.persistance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map.Entry;

/**
 * Created by He DaoYuan on 2016/12/25.
 */
public class AmosDB {
    private static Logger logger = LoggerFactory.getLogger(AmosDB.class);

    public static AmosQuerier newQuerier() {
        return new AmosQuerier();
    }

    public static void list(StringBuilder sql, List<Object> paramList, AmosQuerier querier) {
        if (!querier.getEqualsMap().isEmpty()) {
            for (Entry<String, Object> entry : querier.getEqualsMap().entrySet()) {
                sql.append(" and " + entry.getKey() + " = ?");
                paramList.add(entry.getValue());
            }
        }

        if (!querier.getLikeMap().isEmpty()) {
            for (Entry<String, Object> entry : querier.getLikeMap().entrySet()) {
                sql.append(" and " + entry.getKey() + " like ?");
                paramList.add(entry.getValue());
            }
        }

        if (!querier.getNotLikeMap().isEmpty()) {
            for (Entry<String, Object> entry : querier.getNotLikeMap().entrySet()) {
                sql.append(" and " + entry.getKey() + " not like ?");
                paramList.add(entry.getValue());
            }
        }

        if (!querier.getOrMap().isEmpty()) {
            for (Entry<String, Object> entry : querier.getOrMap().entrySet()) {
                sql.append(" or " + entry.getKey() + " = ?");
                paramList.add(entry.getValue());
            }
        }

        if (!querier.getInMap().isEmpty()) {
            for (Entry<String, Object[]> entry : querier.getInMap().entrySet()) {
                Object[] inParams = entry.getValue();
                sql.append(" and " + entry.getKey() + " in ( ? ");
                paramList.add(inParams[0]);
                for (int i = 1; i < inParams.length; i++) {
                    sql.append(", ? ");
                    paramList.add(inParams[i]);
                }

                sql.append(")");
            }
        }

        if (!querier.getBetweenMap().isEmpty()) {
            for (Entry<String, Object[]> entry : querier.getBetweenMap().entrySet()) {
                String field = entry.getKey();
                Object[] params = entry.getValue();
                if (params[0] != null) {
                    sql.append(" and " + field + " > ? ");
                    paramList.add(params[0]);
                }
                if (params[1] != null) {
                    sql.append("and " + field + " < ?");
                    paramList.add(params[1]);
                }
            }
        }

        if (!querier.getOrdersMap().isEmpty()) {
            sql.append(" order by ");
            for (Entry<String, String> entry : querier.getOrdersMap().entrySet()) {
                sql.append(entry.getKey() + " " + entry.getValue() + ", ");
            }
            sql.delete(sql.length() - 2, sql.length());
        }

        if (!querier.getLimits().isEmpty()) {
            sql.append(" limit " + querier.getLimits().get(0) + ", " + querier.getLimits().get(1));
        }

        //logger.info("==>  Preparing: [" + sql.toString() + ";]");
    }
}
