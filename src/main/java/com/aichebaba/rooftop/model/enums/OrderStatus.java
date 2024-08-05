package com.aichebaba.rooftop.model.enums;

import com.google.common.collect.Sets;
import com.jfinal.plugin.activerecord.annotation.EnumValue;

import java.util.Set;

public enum OrderStatus implements EnumValue {
    CREATED("待付款", 10),
    CLOSED("交易关闭", 20),
    WAIT_PICKING("待拣货", 30),

    WAIT_AGREE_CANCEL("取消订单审核中", 31),
    WAIT_REFUND_BY_CANCEL("取消订单待退款", 32),
    CLOSED_BY_CANCEL("取消订单已退款", 33),

    PICKING("拣货中", 35),
    PICK_FINISHED("拣货完成", 38),          //*废弃
    WAIT_DELIVERY("待发货", 40),           //*废弃
    WAIT_RECEIVE("待收", 50),

    APPLY_REFUND_GOODS("已申请退货", 70),
    REFUND_GOODS_REFUSE("拒绝退货", 75),

    REFUND_GOODS_DELIVERY("退货发回", 80),
    REFUND_GOODS_DELIVERY_ING("退货发回中", 85),

    FULL_REFUND_GOODS("全额待退款", 90),
    REFUND_GOODS_DISCUSS("协商待退款", 100),

    CLOSED_BY_REFUND_GOODS("退货订单已退款", 101),

    APPLY_REFUND("已申请退款", 105),
    REFUND_REFUSE("拒绝退款", 110),
    REFUND_CONFIRM("退款订单待退款", 115),
    CLOSED_BY_REFUND("退款订单已退款", 116),

    REFUND_GOODS_CANCEL("退货申请已取消", 120),
    REFUND_CANCEL("退款申请已取消", 122),

    FINISHED("完成", 200);

    private OrderStatus(String name, int val) {
        this.name = name;
        this.val = val;
    }

    private String name;
    private int val;

    public String getName() {
        return name;
    }

    public int getValue(){
        return val;
    }

    @Override
    public Object getVal() {
        return val;
    }

    @Override
    public Enum valOf(Object o) {
        int i = (int) o;
        OrderStatus e = null;
        switch (i) {
            case 10:
                e = CREATED;
                break;
            case 20:
                e = CLOSED;
                break;
            case 30:
                e = WAIT_PICKING;
                break;
            case 31:
                e = WAIT_AGREE_CANCEL;
                break;
            case 32:
                e = WAIT_REFUND_BY_CANCEL;
                break;
            case 33:
                e = CLOSED_BY_CANCEL;
                break;
            case 35:
                e = PICKING;
                break;
            case 38:
                e = PICK_FINISHED;
                break;
            case 40:
                e = WAIT_DELIVERY;
                break;
            case 50:
                e = WAIT_RECEIVE;
                break;
            case 70:
                e = APPLY_REFUND_GOODS;
                break;
            case 75:
                e = REFUND_GOODS_REFUSE;
                break;
            case 80:
                e = REFUND_GOODS_DELIVERY;
                break;
            case 85:
                e = REFUND_GOODS_DELIVERY_ING;
                break;
            case 90:
                e = FULL_REFUND_GOODS;
                break;
            case 100:
                e = REFUND_GOODS_DISCUSS;
                break;
            case 101:
                e = CLOSED_BY_REFUND_GOODS;
                break;
            case 105:
                e = APPLY_REFUND;
                break;
            case 110:
                e = REFUND_REFUSE;
                break;
            case 115:
                e = REFUND_CONFIRM;
                break;
            case 116:
                e = CLOSED_BY_REFUND;
                break;
            case 120:
                e = REFUND_CANCEL;
                break;
            case 122:
                e = REFUND_GOODS_CANCEL;
                break;
            case 200:
                e = FINISHED;
                break;
            default:
                e = null;
                break;
        }
        return e;
    }

    public static OrderStatus valueOf(int val) {
        return (OrderStatus) OrderStatus.PICKING.valOf(val);
    }

    public static Set<OrderStatus> DEALINGS = Sets.newHashSet(
            CREATED,
            WAIT_PICKING,

            WAIT_AGREE_CANCEL,
            WAIT_REFUND_BY_CANCEL,

            PICKING,
            PICK_FINISHED,
            WAIT_DELIVERY,
            WAIT_RECEIVE,

            APPLY_REFUND_GOODS,
            REFUND_GOODS_REFUSE,

            REFUND_GOODS_DELIVERY,
            REFUND_GOODS_DELIVERY_ING,

            FULL_REFUND_GOODS,
            REFUND_GOODS_DISCUSS,

            APPLY_REFUND,
            REFUND_REFUSE,
            REFUND_CONFIRM
    );

    public static Set<OrderStatus> REFUNDS = Sets.newHashSet(CLOSED_BY_CANCEL, CLOSED_BY_REFUND_GOODS, CLOSED_BY_REFUND);
    public static Set<OrderStatus> REFUNDS2 = Sets.newHashSet(WAIT_REFUND_BY_CANCEL, FULL_REFUND_GOODS, REFUND_GOODS_DISCUSS, REFUND_CONFIRM, CLOSED_BY_CANCEL, CLOSED_BY_REFUND_GOODS, CLOSED_BY_REFUND);

    public static boolean isDealing(OrderStatus status) {
        return DEALINGS.contains(status);
    }

    public static boolean isFinished(OrderStatus status) {
        return status == FINISHED;
    }

    public static boolean isRefund(OrderStatus status) {
        return REFUNDS.contains(status);
    }

    public static boolean isRefund2(OrderStatus status) {
        return REFUNDS2.contains(status);
    }

    public static boolean isClosed(OrderStatus status) {
        return status == CLOSED;
    }

    public static boolean isEnabled(OrderStatus status) {
        return (!(status.equals(CLOSED) || status.equals(CREATED)));
    }

    public static OrderStatus getVariable(Integer val) {
        if (val != null) {
            for (OrderStatus orderStatus : OrderStatus.values()) {
                if (val == orderStatus.val) {
                    return orderStatus;
                }
            }
        }
        return null;
    }

}
