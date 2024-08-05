package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Job;
import com.aichebaba.rooftop.model.enums.JobStatus;
import com.jfinal.plugin.activerecord.dao.Dao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobDao extends Dao<Job, Integer> {

    public JobDao() {
        super("jobs", Job.class);
    }

    public List<Job> getCreatedJobs() {
        return findByWhere("status=?", JobStatus.CREATED.name());
    }
}
