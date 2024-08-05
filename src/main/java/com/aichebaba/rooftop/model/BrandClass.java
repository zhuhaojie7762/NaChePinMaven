package com.aichebaba.rooftop.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2016-12-22.
 */
public class BrandClass implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
    private int brandId;
    private int classId;
	private int top;
	private Date toptime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public Date getToptime() {
		return toptime;
	}

	public void setToptime(Date toptime) {
		this.toptime = toptime;
	}
}
