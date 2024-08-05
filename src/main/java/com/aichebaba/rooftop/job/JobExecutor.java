package com.aichebaba.rooftop.job;

import com.aichebaba.rooftop.dao.JobDao;
import com.aichebaba.rooftop.model.Job;
import com.aichebaba.rooftop.model.enums.JobStatus;
import com.aichebaba.rooftop.model.enums.JobType;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.Date;

public abstract class JobExecutor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected JobDao jobDao;

    public void execute(final Job job) {
        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    job.setStatus(JobStatus.INIT);
                    job.setStartTime(new Date());
                    jobDao.update(job);
                    internalExecute(job);
                    job.setStatus(JobStatus.FINISHED);
                    job.setEndTime(new Date());
                    jobDao.update(job);
                    return true;
                } catch (Exception e) {
                    job.setStatus(JobStatus.ERROR);
                    jobDao.update(job);
                    logger.error("execute job error", e);
                    return false;
                }
            }
        });
    }

    public abstract void internalExecute(Job job);

    public abstract JobType getType();
}
