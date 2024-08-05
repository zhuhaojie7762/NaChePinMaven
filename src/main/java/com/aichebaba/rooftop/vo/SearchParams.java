package com.aichebaba.rooftop.vo;

import com.aichebaba.rooftop.utils.StringUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;

public class SearchParams implements Serializable {

    private Map<String, Where> params = new LinkedHashMap<>();
    private Map<String, String> orderBy = new HashMap<>();
    private Map<String, AttrSearch> attrs = new LinkedHashMap<>();

    private PageParam pageParam;

    public void addParam(String key, String value, String field, String name) {
        params.put(key, Where.builder(key, value, value, field, name));
    }

    public void addParam(String key, String paramValue, String value, String field, String name) {
        params.put(key, Where.builder(key, paramValue, value, field, name));
    }

    public void addParam(String key, String value, String name) {
        params.put(key, new Where(key, value, name));
    }

    public void addAttr(String value, String pid, String name){
        attrs.put(value, new AttrSearch(value, pid, name));
    }

    public void addOrderBy(String sorts) {
        if (StrKit.notBlank(sorts)) {
            String[] sortArr = sorts.split(" ");
            for (String sort : sortArr) {
                if (sort.contains("_")) {
                    String[] kv = sort.split("_");
                    if (StrKit.notBlank(kv[1]) && (kv[1].equalsIgnoreCase("desc") || kv[1].equalsIgnoreCase("asc")))
                        orderBy.put(kv[0], kv[1]);
                }
            }
        }
    }

    public String getUrlParams(String... exKey) {
        StringBuilder sb = new StringBuilder();
        Set set = Sets.newHashSet(exKey);
        if (!params.isEmpty()) {
            for (Where where : params.values()) {
                if (!set.contains(where.getKey())) {
                    sb.append(where.getKey()).append("=").append(where.getParamValue()).append("&");
                }
            }
        }
        if (!orderBy.isEmpty()) {
            if (!params.isEmpty()) {
                sb.append("&");
            }
            StringBuilder oSb = new StringBuilder();
            for (Map.Entry<String, String> entry : orderBy.entrySet()) {
                if (!set.contains(entry.getKey())) {
                    oSb.append(entry.getKey()).append("_").append(entry.getValue()).append(" ");
                }
            }
            if (oSb.length() > 0) {
                sb.append("sorts=").append(oSb);
            }
        }
        if (sb.length() > 1) {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getUrlParams2(String... exKey) {
        StringBuilder sb = new StringBuilder();
        Set set = Sets.newHashSet(exKey);
        if (!params.isEmpty()) {
            for (Where where : params.values()) {
                if (!set.contains(where.getKey())) {
                    sb.append(where.getKey()).append("=").append(where.getParamValue()).append("&");
                }
            }
        }

        if (!orderBy.isEmpty() && !set.contains("sorts")) {
            if (!params.isEmpty()) {
                sb.append("&");
            }
            StringBuilder oSb = new StringBuilder();
            for (Map.Entry<String, String> entry : orderBy.entrySet()) {
                if (!set.contains(entry.getKey())) {
                    oSb.append(entry.getKey()).append("_").append(entry.getValue()).append(" ");
                }
            }
            if (oSb.length() > 0) {
                sb.append("sorts=").append(oSb);
            }
        }
        if (sb.length() > 1) {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getUrlParams3(String... exKey) {
        StringBuilder sb = new StringBuilder();
        Set set = Sets.newHashSet(exKey);
        //普通条件
        if (!params.isEmpty()) {
            for (Where where : params.values()) {
                if (!set.contains(where.getKey())) {
                    sb.append(where.getKey()).append("=").append(where.getParamValue()).append("&");
                }
            }
        }
        //属性条件
        if (!attrs.isEmpty()) {
            if (!set.contains("attr")) {
                sb.append("attr=");
                for (AttrSearch attr : attrs.values()) {
                    sb.append(attr.getPid()).append("_").append(attr.getValue()).append("-");
                }
                sb.deleteCharAt(sb.length() - 1).append("&");
            }
        }
        //排序
        if (!orderBy.isEmpty() && !set.contains("sorts")) {
//            if (!params.isEmpty()) {
//                sb.append("&");
//            }
            StringBuilder oSb = new StringBuilder();
            for (Map.Entry<String, String> entry : orderBy.entrySet()) {
                if (!set.contains(entry.getKey())) {
                    oSb.append(entry.getKey()).append("_").append(entry.getValue()).append(" ");
                }
            }
            if (oSb.length() > 0) {
                sb.append("sorts=").append(oSb);
            }
        }
        if (sb.length() > 1) {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public Pair<String, List<Object>> getSqlParams() {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        for (Where where : params.values()) {
            if (where.getOperator() == null)
                continue;
            sql.append(" and ");
            if (Operator.SET != where.getOperator()) {
                sql.append(where.getField());
            }
            sql.append(where.getOperator().getOp());
            if (Operator.BETWEEN == where.getOperator()) {
                sql.append(" >= ? and ").append(where.getField()).append(" < ? ");
            } else if (Operator.IN == where.getOperator() || Operator.NIN == where.getOperator()) {
                sql.append(StringUtils.inSql(where.getValues().length));
            } else if (Operator.SET == where.getOperator()) {
                sql.append("(?,").append(where.getField()).append(")");
            } else {
                sql.append("?");
            }
            sql.append(" ");
            paras.addAll(Arrays.asList(where.getValues()));
        }
        return Pair.of(sql.toString(), paras);
    }

    public Map<String, Where> getParams() {
        return params;
    }

    public Map<String, Object> getParameters() {
        Map<String, Object> map = new HashMap<>();
        for (Where w : params.values()) {
            map.put(w.getKey(), w.getParamValue());
        }
        map.put("sorts", getSorts());
        return map;
    }

    public Map<String, String> getOrders() {
        return orderBy;
    }

    public String getSorts() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : orderBy.entrySet()) {
            sb.append(entry.getKey()).append("_").append(entry.getValue()).append(" ");
        }
        return sb.toString();
    }

    public boolean notEmpty() {
        return !params.isEmpty() || !orderBy.isEmpty();
    }

    public PageParam getPageParam() {
        return pageParam;
    }

    public void setPageParam(PageParam pageParam) {
        this.pageParam = pageParam;
    }

    public enum Sort {
        DESC, ASC
    }

    public Map<String, AttrSearch> getAttrs() {
        return attrs;
    }

    public static class Where {
        private String key;
        /**
         * 原始值
         */
        private String paramValue;
        private String field;
        private Operator operator;
        private String[] values;
        private String value;
        private String name;

        private Where() {
        }

        private Where(String key, String value, String name) {
            this.key = key;
            this.value = value;
            this.paramValue = value;
            this.name = name;
        }

        /**
         * @param key   参数名
         * @param value 参数值 op_v[1_2]: key参数等于2
         * @param field 字段
         * @return Where
         */
        public static Where builder(String key, String value, String field) {
            Where w = new Where();
            w.setKey(key);
            w.setField(field);
            w.setValue(value);
            Iterable<String> ss = Splitter.on('_').split(value);
            Iterator<String> it = ss.iterator();
            String op = it.next();
            Operator operator = Operator.valueOf(op.toUpperCase());
            if (operator != null) {
                w.setOperator(operator);
                if (Operator.BETWEEN == operator) {
                    String[] v = {it.next(), it.next()};
                    w.setValues(v);
                } else if (Operator.IN == operator || Operator.NIN == operator) {
                    int size = Iterables.size(ss) - 1;
                    String[] v = new String[size];
                    int i = 0;
                    while (it.hasNext()) {
                        v[i++] = it.next();
                    }
                    w.setValues(v);
                } else if (Operator.LIKE == operator) {
                    String[] v = {"%" + it.next().replaceAll("%", "") + "%"};
                    w.setValues(v);
                } else if (Operator.LIKES == operator) {
                    String[] v = {it.next().replaceAll("%", "") + "%"};
                    w.setValues(v);
                } else {
                    String[] v = {it.next()};
                    w.setValues(v);
                }
            }
            return w;
        }

        /**
         * @param key   参数名
         * @param value 参数值 op_v[1_2]: key参数等于2
         * @param field 字段
         * @return Where
         */
        public static Where builder(String key, String paramValue, String value, String field, String name) {
            Where w = Where.builder(key, value, field);
            w.setName(name);
            w.setParamValue(paramValue);
            return w;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String[] getValues() {
            return values;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Operator getOperator() {
            return operator;
        }

        public void setOperator(Operator operator) {
            this.operator = operator;
        }

        public void setValues(String[] values) {
            this.values = values;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParamValue() {
            return paramValue;
        }

        public void setParamValue(String paramValue) {
            this.paramValue = paramValue;
        }
    }

}
