package com.aichebaba.rooftop.job;

import com.aichebaba.rooftop.dao.MonthlyBillDao;
import com.aichebaba.rooftop.dao.UserDao;
import com.aichebaba.rooftop.dao.UserRoleDao;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.Job;
import com.aichebaba.rooftop.model.MonthlyBill;
import com.aichebaba.rooftop.model.enums.JobStatus;
import com.aichebaba.rooftop.model.enums.JobType;
import com.aichebaba.rooftop.model.enums.MessageObjectType;
import com.aichebaba.rooftop.service.CustomerService;
import com.aichebaba.rooftop.service.MessageService;
import com.aichebaba.rooftop.utils.SMSUtil;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.google.common.collect.ImmutableMap;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.DbPro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProviderCheckOutJob extends JobExecutor {

    @Value("${sms.settlementMsg}")
    private String settlementMsg;

    @Value("${finance.end.day}")
    private int financeEndDay;

    @Value("${finance.sms.time}")
    private int sendTime;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MonthlyBillDao billDao;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private UserDao userDao;

    @Override
    public void internalExecute(Job job) {
        logger.info("start execute job {}", job.getId());
        job.setStatus(JobStatus.PROCESSING);
        jobDao.update(job);
        MonthlyBill bill = billDao.calMonthBill(job.getCustomerId(), job.getBusinessDay(), financeEndDay);

        billDao.add(bill);

        Customer customer = customerService.getById(job.getCustomerId());
//        List<Integer> userIds = userRoleDao.getUsersByRole(4);
//        for (Integer id : userIds) {
//            User user = userDao.getById(id);
//            if (user != null && user.getState().equals(UserState.normal)) {
//                String content = TemplateUtils.parseText(settlementMsg, ImmutableMap.<String, Object>of("name", customer.getName(), "bill", bill));
//                messageService.addUserMessage(id, user.getPhone(), content, );
//            }
//        }
        String content = TemplateUtils.parseText(settlementMsg,
                ImmutableMap.<String, Object>of("name", customer.getName(), "bill", bill));
        if (bill.getTotalNum() > 0) {
            messageService.addCustomerMessage(customer.getId(), customer.getPhone(), content, bill,
                    MessageObjectType.CHECK_OUT, sendTime);
        }
        logger.info("execute job {} end", job.getId());
    }

    @Override
    public JobType getType() {
        return JobType.PROVIDER_CHECK_OUT;
    }
}
