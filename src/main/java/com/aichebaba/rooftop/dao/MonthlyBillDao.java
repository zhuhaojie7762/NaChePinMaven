package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.MonthlyBill;
import com.aichebaba.rooftop.model.Order;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.Dao;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.aichebaba.rooftop.model.enums.OrderStatus.*;

@Repository
public class MonthlyBillDao extends Dao<MonthlyBill, MonthlyBill> {

    @Autowired
    private OrderDao orderDao;

    public MonthlyBillDao() {
        super("monthly_bill", MonthlyBill.class);
    }

    public PageList<MonthlyBill> findBills(Date month, String code, String name, String phone, Boolean paid, PageParam pageParam) {
        StringBuilder sql = new StringBuilder("select b.*, c.`code` sellerCode, c.supplierCompany from monthly_bill b")
                .append(" join customer c on b.sellerId=c.id where 1=1");
        List<Object> params = new ArrayList<>();

        if (month != null) {
            sql.append(" and b.month = ?");
            params.add(month);
        }
        if (StrKit.notBlank(code)) {
            sql.append(" and c.code = ?");
            params.add(code);
        }
        if (StrKit.notBlank(name)) {
            sql.append(" and c.name = ?");
            params.add(name);
        }
        if (StrKit.notBlank(phone)) {
            sql.append(" and c.phone = ?");
            params.add(phone);
        }
        if (paid != null) {
            sql.append(" and b.paid = ? ");
            params.add(paid);
        }
        return findPaging(sql, pageParam, params);
    }

    public void paid(MonthlyBill bill, long money, String remark) {
        bill.setPaidPayment(money);
        bill.setRemark(remark);
        bill.setPaid(true);
        bill.setPaidDay(new Date());
        update(bill);
    }

    public MonthlyBill calMonthBill(int sellerId, Date month, int financeEndDay) {
        LocalDate financeDay = new LocalDate(month).withDayOfMonth(financeEndDay);
        Date from = financeDay.minusMonths(1).plusDays(1).toDate();
        Date end = financeDay.plusDays(1).toDate();
        List<Order> orders = orderDao.findMonthOrders(sellerId, from, end,
                Arrays.asList(FINISHED, CLOSED_BY_CANCEL, CLOSED_BY_REFUND_GOODS, CLOSED_BY_REFUND));
        int totalNum = 0;
        long totalPayment = 0;
        int finishNum = 0;
        long finishPayment = 0;
        int cancelNum = 0;
        long cancelPayment = 0;
        int refundNum = 0;
        long refundPayment = 0;
        int refundGoodsNum = 0;
        long refundGoodsPayment = 0;

        for (Order order : orders) {
            totalNum++;
            switch (order.getStatus()) {
                case FINISHED:
                    finishNum++;
                    finishPayment += order.getPayment();
                    totalPayment += order.getPayment();
                    break;
                case CLOSED_BY_CANCEL:
                    cancelNum++;
                    cancelPayment += order.getPayment();
                     totalPayment += order.getPayment();
                    break;
                case CLOSED_BY_REFUND_GOODS:
                    refundGoodsNum++;
                    refundGoodsPayment += order.getPayment();
                    totalPayment += order.getPayment();
                    break;
                case CLOSED_BY_REFUND:
                    refundNum++;
                    refundPayment += order.getPayment();
                    totalPayment += order.getPayment();
                    break;
            }
        }

        MonthlyBill bill = new MonthlyBill();
        bill.setMonth(financeDay.withDayOfMonth(1).toDate());
        bill.setSellerId(sellerId);
        bill.setStartDay(from);
        bill.setEndDay(end);
        bill.setTotalNum(totalNum);
        bill.setTotalPayment(totalPayment);
        bill.setFinishNum(finishNum);
        bill.setFinishPayment(finishPayment);
        bill.setCancelNum(cancelNum);
        bill.setCancelPayment(cancelPayment);
        bill.setRefundNum(refundNum);
        bill.setRefundPayment(refundPayment);
        bill.setRefundGoodsNum(refundGoodsNum);
        bill.setRefundGoodsPayment(refundGoodsPayment);
        bill.setPaid(false);
        return bill;
    }
}
