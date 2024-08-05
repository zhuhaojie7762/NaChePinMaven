package com.aichebaba.rooftop.dao.page;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageParam;

public class PageParamV1 extends PageParam {
	private Map<String, String> linkedOrderMap = new LinkedHashMap<String, String>();

	public PageParamV1(int perSize, int curNo) {
		super(perSize, curNo);
		linkedOrderMap.clear();
	}

	public PageParamV1(int perSize, int curNo, String key, Object val) {
		super(perSize, curNo, key, val);
		linkedOrderMap.clear();
	}

	public PageParamV1(int perSize, int curNo, String key, Object val, String alias) {
		super(perSize, curNo, key, val, alias);
		linkedOrderMap.clear();
	}

	@Override
	public void putSort(String field, String sort) {
		this.linkedOrderMap.put(field, sort);
	}

	@Override
	public void setSort(String sorts) {
		if (StrKit.notBlank(sorts)) {
			String[] sortArr = sorts.split(" ");
			for (String sort : sortArr)
				if (sort.contains("_")) {
					String[] kv = sort.split("_");
					if (StrKit.notBlank(kv[1]))
						this.linkedOrderMap.put(kv[0], kv[1]);
				}
		}
	}

	@Override
	public String getSortValue() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : this.linkedOrderMap.entrySet()) {
			if (StrKit.notBlank(entry.getValue())) {
				sb.append(entry.getKey()).append("_").append(entry.getValue()).append(" ");
			}
		}
		if (this.linkedOrderMap.size() > 0) {
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	@Override
	public void replaceSortField(String key, String field) {
		String sort = (String) this.linkedOrderMap.get(key);
		if (sort != null) {
			this.linkedOrderMap.put(field, sort);
			this.linkedOrderMap.remove(key);
		}
	}

	@Override
	public String buildSQL() {
		StringBuilder sql = new StringBuilder(" ");
		if (this.linkedOrderMap.size() > 0) {
			sql.append(" order by ");
			String prefix = (getAlias() != null) ? new StringBuilder().append(getAlias()).append(".").toString() : "";
			for (Map.Entry<String, String> entry : this.linkedOrderMap.entrySet()) {
				sql.append(prefix).append(entry.getKey()).append(" ").append(entry.getValue()).append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
		}
		sql.append(" limit ").append(getPerSize() * (getCurNo() - 1)).append(", ").append(getPerSize());
		return sql.toString();
	}

	@Override
	public Map<String, String> getSortMap() {
		return this.linkedOrderMap;
	}
}
