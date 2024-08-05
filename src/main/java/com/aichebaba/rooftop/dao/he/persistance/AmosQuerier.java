package com.aichebaba.rooftop.dao.he.persistance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created Querier;
 * Created by He DaoYuan on 2016/12/25.
 */
public class AmosQuerier {
    public static final String ORDER_DESC = "desc";
    public static final String ORDER_ASC = "asc";

	private Map<String, String> ordersMap = new LinkedHashMap<>();
	private Map<String, Object> equalsMap = new LinkedHashMap<>();
	private Map<String, Object> orMap = new LinkedHashMap<>();
	private Map<String, Object[]> betweenMap = new LinkedHashMap<>();
	private Map<String, Object[]> inMap = new LinkedHashMap<>();
	private Map<String, Object> likeMap = new LinkedHashMap<>();
    private Map<String, Object> notLikeMap = new LinkedHashMap<>();
	private List<Integer> limits = new ArrayList<>();

    public AmosQuerier() {

    }

    public AmosQuerier orders(String field, String descOrAsc) {
        ordersMap.put(field, descOrAsc);
        return this;
    }

    public AmosQuerier equals(String field, Object value) {
        equalsMap.put(field, value);
        return this;
    }
    public AmosQuerier or(String field, Object value) {
        orMap.put(field, value);
        return this;
    }

    public AmosQuerier between(String field, Object leftValue, Object rightValue) {
        betweenMap.put(field, new Object[]{leftValue, rightValue});
        return this;
    }

    public AmosQuerier greater(String field, Object leftValue) {
        betweenMap.put(field, new Object[]{leftValue, null});
        return this;
    }

    public AmosQuerier less(String field, Object rightValue) {
        betweenMap.put(field, new Object[]{null, rightValue});
        return this;
    }

    public AmosQuerier in(String field, Object... params) {
        if (params.length == 0) {
            throw new RuntimeException("No in parameters found!");
        }

        inMap.put(field, params);
        return this;
    }

    public AmosQuerier like(String field, Object params) {
        likeMap.put(field, params);
        return this;
    }

    public AmosQuerier notLike(String field, Object params) {
        notLikeMap.put(field, params);
        return this;
    }

    public AmosQuerier limit(int counts) {
        limits.clear();
        limits.add(0);
        limits.add(counts);
        return this;
    }

    public AmosQuerier limit(int startIndex, int counts) {
        limits.clear();
        limits.add(startIndex);
        limits.add(counts);
        return this;
    }

    /**
     * @return the ordersMap
     */
    public Map<String, String> getOrdersMap() {
        return ordersMap;
    }

    /**
     * @return the equalsMap
     */
    public Map<String, Object> getEqualsMap() {
        return equalsMap;
    }

    /**
     * @return the limits
     */
    public List<Integer> getLimits() {
        return limits;
    }

    /**
     * @return the inMap
     */
    public Map<String, Object[]> getInMap() {
        return inMap;
    }

    /**
     * @return the betweenMap
     */
    public Map<String, Object[]> getBetweenMap() {
        return betweenMap;
    }

    /**
     * @return the orMap
     */
    public Map<String, Object> getOrMap() {
        return orMap;
    }

    /**
     * @return the likeMap
     */
    public Map<String, Object> getLikeMap() {
        return likeMap;
    }

    /**
     * @return the notLikeMap
     */
    public Map<String, Object> getNotLikeMap() {
        return notLikeMap;
    }
}
