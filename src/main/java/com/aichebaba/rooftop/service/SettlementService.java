package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.dao.*;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.SettlementDetailType;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.model.enums.SettlementOrderStatus;
import com.aichebaba.rooftop.utils.DateUtil;
import com.aichebaba.rooftop.vo.SettlementExpress;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 结算用
 */
@Service
public class SettlementService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SettlementTotalOrderDao settlementTotalOrderDao;

    @Autowired
    private SettlementOrderDao settlementOrderDao;

    @Autowired
    private SettlementDetailDao settlementDetailDao;

    @Autowired
    private AccountPayeeService accountPayeeService;

    @Autowired
    private SendOrderDao sendOrderDao;

    @Autowired
    private SettlementCheckLogService settlementCheckLogService;

    /**
     * 从原始订单中获取生成供货商结算单的信息
     * @param companyName       供货商公司名称
     * @param type              结算类型
     * @param settlementMoney   结算差额最小值
     * @return
     */
    public List<SettlementOrder> getTempSettlementByOrder(String companyName, SettlementMethodType type, Double settlementMoney){
        List<SettlementOrder> settlementOrderList = new ArrayList<>();
        List<Customer> suppliers = customerService.findSuppliers(companyName, type);
        Date startTime;
        Date endTime;
        for (Customer supplier : suppliers) {
            startTime = supplier.getLastSettlementTime();
            if (supplier.getSettlementMethod().equals(SettlementMethodType.monthly)) {
                endTime = DateUtil.getMonthStart(new Date());
            } else if (supplier.getSettlementMethod().equals(SettlementMethodType.weekly)) {
                endTime = DateUtil.getWeekStart(new Date());
            } else {
                endTime = DateUtil.getDayStart(new Date());
            }
            //已结算的供应商不再显示
            if(startTime.getTime() >= endTime.getTime()){
                continue;
            }
            //结算中的供应商不再显示
            List<SettlementOrderStatus> statuses = new ArrayList<>();
            statuses.add(SettlementOrderStatus.wait_check_first);
            statuses.add(SettlementOrderStatus.wait_check_second);
            statuses.add(SettlementOrderStatus.wait_check_thirdly);
            statuses.add(SettlementOrderStatus.return_check_first);
            statuses.add(SettlementOrderStatus.return_check_second);
            statuses.add(SettlementOrderStatus.return_check_thirdly);
            statuses.add(SettlementOrderStatus.wait_pay);
            List<SettlementOrder> settlementOrders = findSettlementOrder(supplier.getId(), statuses);
            if (settlementOrders.size() > 0) {
                continue;
            }

            SettlementOrder settlementOrder = getTempSettlement(supplier.getId(), startTime, endTime);
            if (settlementMoney == null || ((settlementMoney * 100) <= (settlementOrder.getSaleMoney() - settlementOrder.getBackMoney()))) {
                settlementOrderList.add(settlementOrder);
            }

        }
        return settlementOrderList;
    }

    /**
     * 从原始订单中获取单个供应商的结算信息
     * @param supplierId
     * @param startTime
     * @param endTime
     * @return
     */
    public SettlementOrder getTempSettlement(int supplierId, Date startTime, Date endTime){
        Customer supplier = customerService.getById(supplierId);
        Map<String, Object> info = orderDao.getSettlementInfo(supplierId, startTime, endTime);
        long lngSaleMoney = 0l;
        long lngBackMoney = 0l;
        if (info.get("saleMoney") != null) {
            BigDecimal saleMoney = (BigDecimal) info.get("saleMoney");
            lngSaleMoney =saleMoney.longValue();
        }
        if (info.get("backMoney") != null) {
            BigDecimal backMoney = (BigDecimal) info.get("backMoney");
            lngBackMoney = backMoney.longValue();
        }

        AccountPayee accountPayee = accountPayeeService.getDefaultAccountPayee(supplierId);

        SettlementOrder settlementOrder = new SettlementOrder();
        settlementOrder.setStartTime(startTime);
        settlementOrder.setEndTime(endTime);
        settlementOrder.setSupplierId(supplierId);
        settlementOrder.setSupplierName(supplier.getName());
        settlementOrder.setSupplierCompany(supplier.getSupplierCompany());
        settlementOrder.setSaleMoney(lngSaleMoney);
        settlementOrder.setBackMoney(lngBackMoney);
        settlementOrder.setSettlementMethod(supplier.getSettlementMethod());
        if(accountPayee != null){
            settlementOrder.setAlipayCode(accountPayee.getPayeeAccount());
            settlementOrder.setAlipayName(accountPayee.getPayeeName());
        }
        return settlementOrder;
    }

    /**
     * 获取上次的结算单
     * @param supplierId
     * @return
     */
    public SettlementOrder getLastSettlementOrder(int supplierId){
        return settlementOrderDao.getLastSettlementOrder(supplierId);
    }

    /**
     * 获取指定供应商指定时间段内的交易订单信息
     * @return
     */
    public List<Order> findSaleSettlementDetail(int supplierId, Date startTime, Date endTime){
        return orderDao.findOrders(supplierId, startTime, endTime, null);
    }

    /**
     * 获取指定供应商指定时间段内的退款订单信息
     * @return
     */
    public List<Order> findBackSettlementDetail(int supplierId, Date startTime, Date endTime){
        return orderDao.findRefundOrders(supplierId, startTime, endTime);
    }

    /**
     * 根据结算单号，获取结算单明细
     * @param code
     * @param type
     * @return
     */
    public List<SettlementDetail> findSettlementDetail(String code, SettlementDetailType type){
        return settlementDetailDao.findByWhere("settlementCode = ? and type = ?", code, type.getVal());
    }

    /**
     * 根据结算单号，获取结算单
     * @param code
     * @return
     */
    public SettlementOrder getSettlementOrderByCode(String code){
        return settlementOrderDao.getByPK(code);
    }

    /**
     * 保存供应商结算单
     * @param settlementOrder
     * @return
     */
    public SettlementOrder saveSettlementOrder(SettlementOrder settlementOrder){
        return settlementOrderDao.save(settlementOrder);
    }

    /**
     * 添加供应商结算单详细
     * @param settlementDetail
     */
    public void addSettlementDetail(SettlementDetail settlementDetail){
        settlementDetailDao.add(settlementDetail);
    }

    public List<SettlementOrder> findSettlementOrder(int supplierId, Collection<SettlementOrderStatus> statuses){
        return settlementOrderDao.findSettlementOrder(supplierId, statuses, null, null, null, null);
    }

    /**
     * 批量获取结算单信息
     * @param codes
     * @return
     */
    public List<SettlementOrder> findSettlementOrderByCodes(Collection<String> codes){
        return settlementOrderDao.findSettlementOrderByCodes(codes);
    }

    /**
     * 添加供应商结算总单
     * @param settlementTotalOrder
     * @return
     */
    public SettlementTotalOrder addSettlementTotalOrder(SettlementTotalOrder settlementTotalOrder){
        settlementTotalOrder.setCode(settlementTotalOrderDao.getNextCode());
        settlementTotalOrder.setStatus(SettlementOrderStatus.wait_check_first);
        settlementTotalOrder.setCreated(new Date());
        settlementTotalOrderDao.add(settlementTotalOrder);
        return settlementTotalOrder;
    }

    /**
     * 获取供应商结算总订单
     * @param status
     * @return
     */
    public List<SettlementTotalOrder> findSettlementTotalOrder(SettlementOrderStatus status){
        return settlementTotalOrderDao.findByWhere("status = ? ", status.getVal());
    }

    /**
     * 获取供应商结算总订单
     * @param statuses
     * @return
     */
    public List<SettlementTotalOrder> findSettlementTotalOrder(String code, Collection<SettlementOrderStatus> statuses, Date startTime, Date endTime){
        return settlementTotalOrderDao.findSettlementTotalOrder(code, statuses, startTime, endTime);
    }

    /**
     * 根据结算总订单号获取结算订单信息
     * @param totalCode
     * @return
     */
    public List<SettlementOrder> findSettlementOrder(String totalCode, String supplierName, SettlementMethodType type, Double settlementMoney){
        Long minSettlementMoney = null;
        if(settlementMoney != null){
            minSettlementMoney = Math.round(settlementMoney * 100);
        }
        return settlementOrderDao.findSettlementOrder(0, null, totalCode, supplierName, type, minSettlementMoney);
    }

    // he daoyuan
    public Map<String, Object> getSettlementOrder(SettlementOrder setOrd){
        return orderDao.getSettlementOrder(setOrd);
    }
    /**
     * 获取结算总订单信息
     * @param totalCode 结算总订单编号
     * @return
     */
    public SettlementTotalOrder getSettlementTotalOrderByCode(String totalCode){
        return settlementTotalOrderDao.getByPK(totalCode);
    }

    /**
     * 修改结算总订单
     * @param settlementTotalOrder
     * @return
     */
    public Boolean update(SettlementTotalOrder settlementTotalOrder) {
        return settlementTotalOrderDao.update(settlementTotalOrder);
    }

    /**
     * 修改结算单状态
     * @param settlementCode
     * @param status
     */
    public void changeStatus(String settlementCode, SettlementOrderStatus status){
        SettlementOrder settlementOrder = getSettlementOrderByCode(settlementCode);
        settlementOrder.setStatus(status);
        saveSettlementOrder(settlementOrder);

        if(status.equals(SettlementOrderStatus.finish)){
            //如果所有结算单结束，则结算总订单也结束
            List<SettlementOrder> settlementOrders = findSettlementOrder(settlementOrder.getTotalCode(), null, null, null);
            for(SettlementOrder item : settlementOrders){
                if(!item.getStatus().equals(SettlementOrderStatus.finish)){
                    return;
                }
            }
            SettlementTotalOrder settlementTotalOrder = getSettlementTotalOrderByCode(settlementOrder.getTotalCode());
            settlementTotalOrder.setStatus(SettlementOrderStatus.finish);
            settlementTotalOrderDao.update(settlementTotalOrder);

            SettlementCheckLog scl = new SettlementCheckLog();
            scl.setTotalCode(settlementTotalOrder.getCode());
            scl.setRemark("");
            scl.setStatus(SettlementOrderStatus.finish);
            scl.setCreatedTime(new Date());
            settlementCheckLogService.save(scl);
        }
    }

    /**
     * 获取支付宝批量支付的批次号
     * @return
     */
    public String getBatchCode() {
        Integer nextVal = settlementTotalOrderDao.getSingle("select nextval(?)", "seq_batch_pay");
        DecimalFormat numberFormat = new DecimalFormat("0000000000");
        return numberFormat.format(nextVal);
    }

    /**
     * He daoyuan
     * 供货商账单
     */
    public PageList<SettlementOrder> queryToBill(SettlementOrder settlementOrder, List<SettlementOrderStatus> statuses, PageParam pageParam) {
        return settlementOrderDao.queryToBill(settlementOrder, statuses, pageParam);
    }

    /**
     * hdy
     * 结算总汇
     */
    public PageList<SettlementOrder> getSettlementCleanAccount(SettlementOrder settlementOrder, List<SettlementOrderStatus> statuses, PageParam pageParam) {
        return settlementOrderDao.getSettlementCleanAccount(settlementOrder, statuses, pageParam);
    }

    /**
     * 从发货单获取快递结算信息
     * @param startTime
     * @param endTime
     * @param expressName
     * @return
     */
    public List<SettlementExpress> findSettlementExpress(Date startTime, Date endTime, String expressName){
        return sendOrderDao.findTempSettlementExpress(startTime, endTime, expressName);
    }
}
