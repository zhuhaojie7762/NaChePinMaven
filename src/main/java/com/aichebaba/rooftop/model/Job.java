package com.aichebaba.rooftop.model;

import com.aichebaba.rooftop.model.enums.JobStatus;
import com.aichebaba.rooftop.model.enums.JobType;
import com.jfinal.plugin.activerecord.annotation.EnumVal;
import com.jfinal.plugin.activerecord.annotation.EnumValType;
import com.jfinal.plugin.activerecord.annotation.Sql;

import java.io.Serializable;
import java.util.Date;

@Sql(insertSQL = "INSERT IGNORE INTO `jobs` (`customerId`, `businessDay`, `startTime`, `endTime`, `status`, `type`, `created`) VALUES (?, ?, ?, ?, ?, ?, ?);")
public class Job implements Serializable {
    private int id;
    private int customerId;
    private Date businessDay;
    private Date startTime;
    private Date endTime;
    @EnumVal(EnumValType.Name)
    private JobStatus status;
    @EnumVal(EnumValType.Name)
    private JobType type;
    private Date created;

    public Job() {

    }

    public Job(int customerId, Date businessDay, JobType type) {
        this.customerId = customerId;
        this.businessDay = businessDay;
        this.type = type;
        this.status = JobStatus.CREATED;
        this.created = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getBusinessDay() {
        return businessDay;
    }

    public void setBusinessDay(Date businessDay) {
        this.businessDay = businessDay;
    }

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

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
