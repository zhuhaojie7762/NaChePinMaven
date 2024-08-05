package com.aichebaba.rooftop.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.annotation.Transient;

/**
 * 商品分类属性表
 */
public class GoodsClass implements Serializable{

	/**
	 * 默认第二分类ID
	 */
	public static  final int defaultSecondClassId = 19;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;

    private String name;

	private int pid;

	private int level;

//	private Boolean packFlag;
//
//	private int packNum;

    private Date created;

	@Transient
	private List<GoodsClass> children;

	@Transient
	private GoodsClass value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

//	public int getPackNum() {
//		return packNum;
//	}
//
//	public void setPackNum(int packNum) {
//		this.packNum = packNum;
//	}
//
//	public Boolean getPackFlag() {
//		return packFlag;
//	}
//
//	public void setPackFlag(Boolean packFlag) {
//		this.packFlag = packFlag;
//	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public List<GoodsClass> getChildren() {
		return children;
	}

	public void setChildren(List<GoodsClass> children) {
		this.children = children;
	}

	public GoodsClass getValue() {
		return value;
	}

	public void setValue(GoodsClass value) {
		this.value = value;
	}
}
