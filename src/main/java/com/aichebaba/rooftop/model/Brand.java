package com.aichebaba.rooftop.model;

import java.io.Serializable;
import java.util.Date;

public class Brand implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int TOP_STATE_TOP = 3; // 置顶
	public static final int TOP_STATE_WAIT_CANCEL_TOP = 2; // 待取消置顶 默认
	public static final int TOP_STATE_WAIT_TOP = 1; // 待置顶
	public static final int TOP_STATE_NO_TOP = 0; // 未置顶 默认

	private int id;
    private String name;
    private String logo;
    private String comment;
    private Date created;
    private String pinyin;
	private int top;
	private Date toptime;

	private Customer customer;

	// 供货商
	private String supplierCompany;
	// 新增 三级分类id
	private Integer thirdclassid;
	private String thirdclassname;
	// 新增 二级分类id
	private Integer secondclassid;
	private String secondclassname;
	// 新增 一级分类id
	private Integer firstclassid;
	private String firstclassname;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
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

	public String getSupplierCompany() {
		return supplierCompany;
	}

	public void setSupplierCompany(String supplierCompany) {
		this.supplierCompany = supplierCompany;
	}

	public Integer getThirdclassid() {
		return thirdclassid;
	}

	public void setThirdclassid(Integer thirdclassid) {
		this.thirdclassid = thirdclassid;
	}

	public String getThirdclassname() {
		return thirdclassname;
	}

	public void setThirdclassname(String thirdclassname) {
		this.thirdclassname = thirdclassname;
	}

	public Integer getSecondclassid() {
		return secondclassid;
	}

	public void setSecondclassid(Integer secondclassid) {
		this.secondclassid = secondclassid;
	}

	public String getSecondclassname() {
		return secondclassname;
	}

	public void setSecondclassname(String secondclassname) {
		this.secondclassname = secondclassname;
	}

	public Integer getFirstclassid() {
		return firstclassid;
	}

	public void setFirstclassid(Integer firstclassid) {
		this.firstclassid = firstclassid;
	}

	public String getFirstclassname() {
		return firstclassname;
	}

	public void setFirstclassname(String firstclassname) {
		this.firstclassname = firstclassname;
	}
}
