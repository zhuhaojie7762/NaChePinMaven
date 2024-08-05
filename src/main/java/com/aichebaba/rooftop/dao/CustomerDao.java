package com.aichebaba.rooftop.dao;

import com.aichebaba.rooftop.model.Customer;
import com.aichebaba.rooftop.model.SettlementOrder;
import com.aichebaba.rooftop.model.enums.CustomerState;
import com.aichebaba.rooftop.model.enums.CustomerType;
import com.aichebaba.rooftop.model.enums.SettlementMethodType;
import com.aichebaba.rooftop.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CustomerDao extends GeneralCodeDao<Customer, Integer> {
    public CustomerDao() {
        super("customer", Customer.class, "KH", "seq_customer");
    }

    public PageList<Customer> findCustomers(CustomerState state, String phone, String name, CustomerType type, Date startTime, Date endTime, SettlementMethodType methodType, PageParam pageParam) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select * from customer where 1=1");
        if (state != null) {
            sql.append(" and state = ?");
            paras.add(state.getVal());
        }
        if (StrKit.notBlank(phone)) {
            sql.append(" and phone like ?");
            paras.add(StringUtils.likeValue(phone));
        }
        if (StrKit.notBlank(name)) {
            sql.append(" and name like ?");
            paras.add(StringUtils.likeValue(name));
        }
        if (type != null) {
            sql.append(" and type = ?");
            paras.add(type.ordinal());
        }
        if (startTime != null) {
            sql.append(" and created >= ?");
            paras.add(startTime);
        }
        if (endTime != null) {
            sql.append(" and created <= ?");
            paras.add(endTime);
        }
        if (methodType != null) {
            sql.append(" and settlementMethod = ? ");
            paras.add(methodType.name());
        }
        pageParam.putSort("id", "desc");
        return findPaging(sql, pageParam, paras);
    }

    public void stop(int customerId) {
        updateByWhere("state=?", "id=?", CustomerState.stop.getVal(), customerId);
    }

    public void enable(int customerId) {
        updateByWhere("state=?", "id=?", CustomerState.normal.getVal(), customerId);
    }

    public List<Customer> findProviders() {
        return findByWhere("type=? and state=?", CustomerType.seller.getVal(), CustomerState.normal.getVal());
    }

    public Customer getCustomer(String username, String phone, int customerId, CustomerState state) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select c.*, p.status purchaseState, s.status supplierState from customer c ")
                .append(" left join customer_check p on c.id = p.customerId and p.type = 'PURCHASE' ")
                .append(" left join customer_check s on c.id = s.customerId and s.type = 'SUPPLIER' ")
                .append(" where 1 = 1 ");
        if (StrKit.notBlank(username)) {
            sql.append(" and c.username = ? ");
            paras.add(username);
        }
        if (StrKit.notBlank(phone)) {
            sql.append(" and c.phone = ? ");
            paras.add(phone);
        }
        if (customerId > 0) {
            sql.append(" and c.id = ? ");
            paras.add(customerId);
        }
        if (state != null) {
            sql.append(" and c.state = ? ");
            paras.add(state.getVal());
        }
        sql.append(" limit 1 ");
        return getBySQL(sql.toString(), paras.toArray());
    }

    public Customer getStateByCode(String username, String phone, int customerId) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select c.* from customer c ").append(" where 1 = 1 ");
        if (StrKit.notBlank(username)) {
            sql.append(" and c.username = ? ");
            paras.add(username);
        }
        if (StrKit.notBlank(phone)) {
            sql.append(" and c.phone = ? ");
            paras.add(phone);
        }
        if (customerId > 0) {
            sql.append(" and c.id = ? ");
            paras.add(customerId);
        }
        return getBySQL(sql.toString(), paras.toArray());
    }

    public List<Integer> findBuyerIdsByAddress(String address) {
        return findSingleList("select id from customer where address=?", address);
    }

    public List<Customer> getCustomerByCodes(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        return findByWhere(inSql("code", codes.size()), codes.toArray());
    }

    public List<Customer> findCustomerByUsernames(Collection<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return Collections.emptyList();
        }
        return findByWhere(inSql("username", usernames.size()), usernames.toArray());
    }

    public PageList<Customer> getMembers(String userName, int level, String province, CustomerState state, CustomerType type, String signs, Long totalStart, Long totalEnd,
                                         Long countStart, Long countEnd, Date lastStart, Date lastEnd, PageParam pageParam) {
        StringBuilder sql = new StringBuilder("select c.*, ml.name levelName from customer c left join member_level ml on ml.level = c.level where 1=1 ");
        List<Object> param = new ArrayList<>();
        if (StrKit.notBlank(userName)) {
            sql.append(" and c.username like ?");
            param.add(StringUtils.likeValue(userName));
        }
        if (level > 0) {
            sql.append(" and c.level = ?");
            param.add(level);
        }
        if (StrKit.notBlank(province)) {
            sql.append(" and c.province like ?");
            param.add(StringUtils.likeValue(province));
        }
        if (state != null) {
            sql.append(" and c.state = ?");
            param.add(state.getVal());
        }
        if (type != null) {
            sql.append(" and c.type = ?");
            param.add(type.getVal());
        }
        if (StrKit.notBlank(signs)) {
            sql.append(" and c.sign like ?");
            param.add("%" + signs + "%");
        }
        if (totalStart != null) {
            sql.append(" and c.totalPurchaseMoney >= ?");
            param.add(totalStart);
        }
        if (totalEnd != null) {
            sql.append(" and c.totalPurchaseMoney <= ?");
            param.add(totalEnd);
        }
        if (countStart != null) {
            sql.append(" and c.totalPurchaseCount >= ?");
            param.add(countStart);
        }
        if (countEnd != null) {
            sql.append(" and c.totalPurchaseCount <= ?");
            param.add(countEnd);
        }
        if (lastStart != null) {
            sql.append(" and c.lastPurchaseTime >= ?");
            param.add(lastStart);
        }
        if (lastEnd != null) {
            sql.append(" and c.lastPurchaseTime <= ?");
            param.add(lastEnd);
        }
        sql.append(" and c.level != 0");
        return findPaging(sql, pageParam, param);
    }

    public List<Customer> getByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return findByWhere(inSql("id", ids.size()), ids.toArray());
    }

    public Long getCustomerCount(CustomerType type, CustomerState state, Integer level) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<>();
        sql.append("select count(*) from customer where 1 = 1 ");
        if (type != null) {
            sql.append(" and type = ? ");
            param.add(type.getVal());
        }
        if (state != null) {
            sql.append(" and state = ? ");
            param.add(state.getVal());
        }
        if (level != null) {
            sql.append(" and level = ? ");
            param.add(level);
        }
        return getSingle(sql.toString(), param.toArray());
    }

    public List<Customer> getCustomerByLevel(CustomerType type, CustomerState state, Collection<Integer> levels) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<>();
        sql.append("select * from customer where 1 = 1 ");
        if (type != null) {
            sql.append(" and type = ? ");
            param.add(type.getVal());
        }
        if (state != null) {
            sql.append(" and state = ? ");
            param.add(state.getVal());
        }
        if (!levels.isEmpty()) {
            sql.append(" and ").append(inSql(" level ", levels.size()));
            param.addAll(levels);
        }
        return findSQL(sql.toString(), param.toArray());
    }

    /**
     * 获取所有进货商及供货商
     *
     * @return
     */
    public List<Customer> findAllMember() {
        return findWhere("type >= ? and state = ?", CustomerType.buyer.getVal(), CustomerState.normal);
    }

    /**
     * 获取客户列表
     * @param companyName
     * @return
     */
    public List<Customer> findSuppliers(CustomerType type, String companyName, SettlementMethodType method) {
        StringBuilder sql = new StringBuilder();
        List<Object> paras = new ArrayList<>();
        sql.append("select * from customer ")
                .append(" where state = ? ");
        paras.add(CustomerState.normal.getVal());
        if (type != null) {
            sql.append(" and type = ? ");
            paras.add(CustomerType.seller.getVal());
        }
        if (StrKit.notBlank(companyName)) {
            sql.append(" and supplierCompany like ? ");
            paras.add(StringUtils.likeAllValue(companyName));
        }
        if (method != null) {
            sql.append(" and settlementMethod = ? ");
            paras.add(method.name());
        }
        return findSQL(sql.toString(), paras.toArray());
    }

    public List<Customer> getSuppliersList(SettlementOrder settlementOrder) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * from customer c ");

        //sql.append("select c.* from customer c join account_payee a on c.id = a.customerId where 1 = 1 and state = ?");
        List<Object> params = new ArrayList<>();

        if (StrKit.notBlank(settlementOrder.getAlipayCode()) || StrKit.notBlank(settlementOrder.getAlipayName())) {
            sql.append("join (select distinct customerId from account_payee where 1 = 1");
            if (StrKit.notBlank(settlementOrder.getAlipayCode())) {
                sql.append(" and payeeAccount = ?");
                params.add(settlementOrder.getAlipayCode());
            }
            if (StrKit.notBlank(settlementOrder.getAlipayName())) {
                sql.append(" and payeeName = ?");
                params.add(settlementOrder.getAlipayName());
            }
            sql.append(") a on c.id = a.customerId ");
        }
        params.add(CustomerState.normal.getVal());
        sql.append("where 1 = 1 and c.state = ?");

        if (StrKit.notBlank(settlementOrder.getSupplierCompany())) {
            sql.append(" and c.supplierCompany like ?");
            params.add(StringUtils.likeAllValue(settlementOrder.getSupplierCompany()));
        }
        if (settlementOrder.getSettlementMethod() != null) {
            sql.append(" and c.settlementMethod = ?");
            params.add(settlementOrder.getSettlementMethod().name());
        }
        return findSQL(sql.toString(), params.toArray());
    }
}
