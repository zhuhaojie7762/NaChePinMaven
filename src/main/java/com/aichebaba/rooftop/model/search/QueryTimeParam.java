package com.aichebaba.rooftop.model.search;

import java.util.Date;

/**
 * 用于查询时间段set和get值
 * Created by He DaoYuan on 2016/12/1.
 */
public class QueryTimeParam {
    private Date startTime;
    private Date endTime;
    private Date startCreated;
    private Date endCreated;
    private Date startRefund;
    private Date endRefund;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartCreated() {
        return startCreated;
    }

    public void setStartCreated(Date startCreated) {
        this.startCreated = startCreated;
    }

    public Date getEndCreated() {
        return endCreated;
    }

    public void setEndCreated(Date endCreated) {
        this.endCreated = endCreated;
    }

    public Date getStartRefund() {
        return startRefund;
    }

    public void setStartRefund(Date startRefund) {
        this.startRefund = startRefund;
    }

    public Date getEndRefund() {
        return endRefund;
    }

    public void setEndRefund(Date endRefund) {
        this.endRefund = endRefund;
    }
}
