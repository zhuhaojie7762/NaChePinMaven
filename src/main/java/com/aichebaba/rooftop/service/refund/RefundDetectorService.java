package com.aichebaba.rooftop.service.refund;

import com.aichebaba.rooftop.dao.OrderDao;
import com.aichebaba.rooftop.model.Order;
import com.aichebaba.rooftop.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.aichebaba.rooftop.model.enums.OrderStatus.*;

/**
 * Created He Daoyuan on 2016/12/9.
 */
@Service
public class RefundDetectorService {
    public List<Order> detect(String pickCode) {
        List<Order> orders = orderDao.findPickOrderCode(pickCode);
        for (Iterator<Order> it = orders.iterator(); it.hasNext();) {
            Order order = it.next();
            if (doInclude(order)) {
                it.remove();
            }
        }
        return orders;
    }

    public static boolean doInclude(Order order) {
        List<OrderStatus> list = Arrays.asList(APPLY_REFUND_GOODS, REFUND_GOODS_REFUSE, REFUND_GOODS_DELIVERY, REFUND_GOODS_DELIVERY_ING, FULL_REFUND_GOODS, REFUND_GOODS_DISCUSS, CLOSED_BY_REFUND_GOODS, APPLY_REFUND, REFUND_REFUSE, REFUND_CONFIRM, CLOSED_BY_REFUND);
        boolean result = false;
        for (OrderStatus status : list) {
            if (order.getStatus().equals(status)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Autowired
    private OrderDao orderDao;
}
