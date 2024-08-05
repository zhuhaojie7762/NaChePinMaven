package com.aichebaba.rooftop.job;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.aichebaba.rooftop.dao.JobDao;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.Job;
import com.aichebaba.rooftop.model.enums.JobType;
import com.aichebaba.rooftop.service.CouponService;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.MemberService;
import com.aichebaba.rooftop.service.MessageService;
import com.aichebaba.rooftop.service.OrderService;
import com.aichebaba.rooftop.service.SalesVolumeService;

@Configuration
@ComponentScan(basePackageClasses = JobConfig.class)
@EnableScheduling
public class JobConfig implements SchedulingConfigurer {

    private Logger logger = LoggerFactory.getLogger(JobConfig.class);

    @Value("${job.open:0}")
    private int jobOpen;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ThreadPoolTaskExecutor poolTaskExecutor;

    @Autowired
    private SalesVolumeService salesVolumeService;

	private Map<JobType, JobExecutor> jobExecutorMap = new HashMap<>();

    @Autowired
    public void setJobExecutorMap(Collection<JobExecutor> executors) {
        for (JobExecutor e : executors) {
            jobExecutorMap.put(e.getType(), e);
        }
    }

    @Scheduled(cron = "${job.general_check_out_cron}")
    public void generalProviderJob() throws SQLException {
        if (jobOpen == 0) return;
        logger.info("general provider job start");
        List<Customer> customers = customerService.findProviders();
        List<Job> jobs = new ArrayList<>(customers.size());
        Date businessDay = LocalDate.now().plusDays(1).toDate();
        for (Customer c : customers) {
            jobs.add(new Job(c.getId(), businessDay, JobType.PROVIDER_CHECK_OUT));
        }
        jobDao.adds(jobs);
        logger.info("general provider job end: {}", jobs.size());
    }

    @Scheduled(cron = "${job.execute.cron}")
    public void executeJobs() {
        if (jobOpen == 0) return;
        List<Job> jobs = jobDao.getCreatedJobs();
        for (final Job job : jobs) {
            final JobExecutor executor = jobExecutorMap.get(job.getType());
            poolTaskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    if (executor != null) {
                        executor.execute(job);
                    } else {
                        logger.error("{} not executor", job.getType());
                    }
                }
            });
        }
    }

    @Scheduled(cron = "${finished.trade.cron}")
    public void finishedTrades() {
        if (jobOpen == 1) {
            orderService.finishedTrades();
            orderService.finishedOrders();
        }
    }

    @Scheduled(cron = "${auto.send.sms.cron}")
    public void sendSMS() {
        if (jobOpen == 1) {
            messageService.sendMessages();
        }
    }

    @Scheduled(cron = "${coupon.activity.finished.cron}")
    public void finishedActivity() {
        couponService.timesFinishActivity();
        logger.info("times");
    }

    @Scheduled(cron = "${sales.volume.week.cron}")
    public void weekSales(){
        salesVolumeService.cleanWeekSales();
    }

    @Scheduled(cron = "${sales.volume.month.cron}")
    public void MonthSales(){
        salesVolumeService.cleanMonthSales();
    }

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

	@Bean
	public ThreadPoolTaskScheduler taskExecutor() {
		ThreadPoolTaskScheduler pool = new ThreadPoolTaskScheduler();
		pool.setPoolSize(10);
		return pool;
	}
}