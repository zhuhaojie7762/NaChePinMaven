package com.aichebaba.rooftop.controller.admin;

import com.aichebaba.rooftop.controller.BaseController;
import com.aichebaba.rooftop.dao.MonthlyBillDao;
import com.aichebaba.rooftop.dao.OrderDao;
import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.MonthlyBill;
import com.aichebaba.rooftop.model.Order;
import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import static com.aichebaba.rooftop.model.enums.OrderStatus.*;

@Controller
@Scope("prototype")
public class MonthBillHistoryController extends BaseController {

    @Value("${finance.end.day}")
    private int financeEndDay;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private MonthlyBillDao monthlyBillDao;

    public void index() throws ParseException {
        String fieldName = getPara("fieldName");
        String value = getPara("value");
        String phone = null;
        String code = null;
        String name = null;
        if (StrKit.notBlank(value, fieldName)) {
            if ("phone".equals(fieldName)) {
                phone = value;
            } else if ("code".equals(fieldName)) {
                code = value;
            } else if ("name".equals(fieldName)) {
                name = value;
            }
        }
        String monthValue = getPara("month");
        Date month = null;
        if (StrKit.notBlank(monthValue)) {
            month = DateUtils.parseDate(monthValue, "yyyy-MM", "yyyy-MM-dd");
        }
        PageList<MonthlyBill> pager = monthlyBillDao.findBills(month, code, name, phone, true, getPageParam());
        setAttr("pager", pager);
        render("list.html");
    }

    public void detail() throws ParseException {
        int sellerId = getParaToInt("sellerId", 0);
        String monthValue = getPara("month");
        Date month = null;
        if (StrKit.notBlank(monthValue)) {
            month = DateUtils.parseDate(monthValue, "yyyy-MM", "yyyy-MM-dd");
        }
        LocalDate financeDay = new LocalDate(month).withDayOfMonth(financeEndDay);
        Date from = financeDay.minusMonths(1).plusDays(1).toDate();
        Date end = financeDay.plusDays(1).toDate();
        Customer customer = customerService.getById(sellerId);
        setAttr("customer", customer);
        PageList<Order> pager = orderDao.findMonthOrderPager(sellerId, from, end,
                Arrays.asList(FINISHED, CLOSED_BY_CANCEL, CLOSED_BY_REFUND_GOODS, CLOSED_BY_REFUND), getPageParam());
        setAttr("pager", pager);
        setAttr("params", ImmutableMap.of("sellerId", sellerId, "month", monthValue));
        render("detail.html");
    }
}
