package com.aichebaba.rooftop.service;

import com.aichebaba.rooftop.config.Constant;
import com.aichebaba.rooftop.dao.CustomerCheckDao;
import com.aichebaba.rooftop.dao.CustomerDao;
import com.aichebaba.rooftop.model.*;
import com.aichebaba.rooftop.model.enums.*;
import com.aichebaba.rooftop.utils.MD5;
import com.aichebaba.rooftop.utils.SMSUtil;
import com.aichebaba.rooftop.utils.TemplateUtils;
import com.aichebaba.rooftop.vo.CustomerApplyAudit;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.dao.PageList;
import com.jfinal.plugin.activerecord.dao.PageParam;
import org.apache.poi.xslf.model.geom.CurveToCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.aichebaba.rooftop.model.enums.CustomerAuditState.NO_PASS;
import static com.aichebaba.rooftop.model.enums.CustomerAuditState.PASS;

@Service
public class CustomerService {

    private static Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Value("${sms.auditResultMsg}")
    private String auditResultMsg;

    @Value("${sms.auditNoPassMsg}")
    private String auditNoPassMsg;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerCheckDao customerCheckDao;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BrandService brandService;

    public Customer getCustomerByUsername(String username) {
        return customerDao.getByWhere("username=?", username);
    }

    public Customer getCustomerByCode(String code) {
        return customerDao.getByWhere("code = ?", code);
    }

    public Customer getCustomerByPhone(String phone) {
        return customerDao.getByWhere("phone=?", phone);
    }

    /**
     * 根据用户名查找客户
     *
     * @param username 用户名
     * @return
     */
    public Customer findCustomerByUsername(String username) {
        return customerDao.getCustomer(username, null, 0, CustomerState.normal);
    }

    /**
     * 根据手机号查找客户
     *
     * @param phone 手机号
     * @return
     */
    public Customer findCustomerByPhone(String phone) {
        return customerDao.getCustomer(null, phone, 0, CustomerState.normal);
    }

    /*
    姓名
     */
    public Customer getStateByName(String username) {
        return customerDao.getStateByCode(username, null, 0);
    }

    /*
    电话
     */
    public Customer getStateByPhone(String phone) {
        return customerDao.getStateByCode(null, phone, 0);
    }

    /**
     * 根据ID查找客户
     *
     * @param customerId 客户ID
     * @return
     */
    public Customer getById(int customerId) {
        return customerDao.getCustomer(null, null, customerId, null);
    }

    /**
     * 获取正常客户
     * @param customerId
     * @return
     */
    public Customer getNormalCustomerById(int customerId) {
        return customerDao.getCustomer(null, null, customerId, CustomerState.normal);
    }


    public PageList<Customer> findCustomers(CustomerState state, String phone, String name, CustomerType type, Date startTime, Date endTime, SettlementMethodType methodType, PageParam pageParam) {
        return customerDao.findCustomers(state, phone, name, type, startTime, endTime, methodType, pageParam);
    }

    /**
     * 获取待审核的进货商认证
     *
     * @return
     */
    public PageList<CustomerApplyAudit> findWaitAuditPurchase(String phone, String code, String name, CustomerAuditState status,
                                                              PageParam pageParam) {
        return customerCheckDao
                .findWaitCustomerChecks(phone, code, name, CustomerAuditType.PURCHASE, status,
                        pageParam);
    }

    /**
     * 获取待审核的供货商认证
     *
     * @param phone
     * @param code
     * @param name
     * @param pageParam
     * @return
     */
    public PageList<CustomerApplyAudit> findWaitAuditSupplier(String phone, String code, String name, CustomerAuditState status,
                                                              PageParam pageParam) {

        return customerCheckDao
                .findWaitCustomerChecks(phone, code, name, CustomerAuditType.SUPPLIER, status,
                        pageParam);
    }

    /**
     * 客户帐号停用
     *
     * @param customerId 客户ID
     * @param curUserId  当前操作员工帐号
     */
    public void stopCustomer(int customerId, int curUserId) {
        customerDao.stop(customerId);
        logger.info("stop customer {} by {}", customerId, curUserId);
    }

    /**
     * 客户帐号启用
     *
     * @param customerId 客户ID
     * @param curUserId  当前操作员工帐号
     */
    public void enableCustomer(int customerId, int curUserId) {
        customerDao.enable(customerId);
        logger.info("enable customer {} by {}", customerId, curUserId);
    }

    public void resetPassword(int customerId, int curUserId) throws Exception {
        Customer customer = customerDao.getById(customerId);
        if (customer != null) {
            customer.setPassword(MD5.md5("123456", "UTF-8"));
            customerDao.update(customer);
            logger.info("reset password {} by {}", customerId, curUserId);
        }
    }

    public void resetPickCode(int customerId, String pickCode, int curUserId) {
        Customer customer = customerDao.getById(customerId);
        if (customer != null) {
            String old = customer.getPickAreaCode();
            customer.setPickAreaCode(pickCode);
            customerDao.update(customer);
            logger.info("reset pickAreaCode {} by {}: old - {} new - {}", customerId, curUserId, old, pickCode);
        }
    }

    /**
     * 保存
     *
     * @param customer
     * @return
     */
    public Customer save(Customer customer) {
        if (customer.getId() > 0) {
            customerDao.update(customer);
        } else {
            customer.setCode(customerDao.getNextCode());
            customer.setCreated(new Date());
            Object o = customerDao.add(customer);
            customer.setId(Integer.parseInt(o.toString()));
        }
        return customer;
    }

    public CustomerCheck getCustomerApplyCheck(int id) {
        return customerCheckDao.getById(id);
    }

    public CustomerCheck getCustomerCheck(int customerId, CustomerAuditType type) {
        return customerCheckDao.getByCustomerId(customerId, type);
    }

    /**
     * 进货商审核
     *
     * @param id
     * @param auditResult
     * @param noPassReason
     * @param curUser
     */
    public void auditApplyPurchaseAudit(int id, boolean auditResult, String noPassReason, User curUser, String sign) {
        CustomerCheck apply = customerCheckDao.getByPK(id);
        apply.setCheckerId(curUser.getId());
        apply.setStatus(auditResult ? PASS : NO_PASS);
        apply.setComment(noPassReason);
        apply.setSign(sign);
        customerCheckDao.update(apply);
        String msg;
        Customer customer = getById(apply.getCustomerId());
        if (auditResult) {
            customer.setShopUrl(apply.getShopUrl());
            customer.setWangwang(apply.getWangwang());
            customer.setQq(apply.getQq());
            if (customer.getType().equals(CustomerType.normal)) {
                customer.setType(CustomerType.buyer);
            }
            customer.setAlipayCode(apply.getAlipayCode());
            customer.setTenpayCode(apply.getTenpayCode());
            customer.setAlipayName(apply.getAlipayName());
            customer.setInGoodsType(apply.getInGoodsType());
            customer.setProvinceId(apply.getProvinceId());
            customer.setProvince(apply.getProvince());
            customer.setCityId(apply.getCityId());
            customer.setCity(apply.getCity());
            customer.setCountyId(apply.getCountyId());
            customer.setArea(apply.getArea());
            customer.setAddress(apply.getAddress());
            customer.setPostCode(apply.getPostCode());
            customer.setSign(apply.getSign());
            customer.setWeiXin(apply.getWeiXin());
            customer.setPassPurchaseTime(new Date());
            apply.setPassPurchaseTime(new Date());
//            customer.setFullAddress(apply.getFullAddress());
            customerDao.update(customer);
            customerCheckDao.update(apply);
            msg = TemplateUtils.parseText(auditResultMsg,
                    ImmutableMap.<String, Object>of("name", customer.getName(), "type", "进货商"));
        } else {

            msg = TemplateUtils.parseText(auditNoPassMsg,
                    ImmutableMap.<String, Object>of("name", customer.getName(), "type", "进货商"));
        }
        SMSUtil.sendSmsMsg2(customer.getPhone(), msg);
    }

    /**
     * 供货商审核
     *
     * @param id
     * @param auditResult
     * @param noPassReason
     * @param curUser
     */
    public void auditApplySupplierAudit(int id, boolean auditResult, String noPassReason, User curUser, String sign, String remarks) {

        /*如果供货商审核通过，进货商审核自动通过*/
        if (auditResult) {
            CustomerCheck supplierCheck = getCustomerApplyCheck(id);
            CustomerCheck purchaseCheck = getCustomerCheck(supplierCheck.getCustomerId(), CustomerAuditType.PURCHASE);
            if (purchaseCheck != null) {
                auditApplyPurchaseAudit(purchaseCheck.getId(), true, "", curUser, null);
            }
        }

        CustomerCheck apply = customerCheckDao.getByPK(id);
        apply.setCheckerId(curUser.getId());
        apply.setStatus(auditResult ? PASS : NO_PASS);
        apply.setComment(noPassReason);
        apply.setSign(sign);
        apply.setRemarks(remarks);
        customerCheckDao.update(apply);
        String msg;
        Customer customer = getById(apply.getCustomerId());
        if (auditResult) {
            customer.setSupplierCompany(apply.getSupplierCompany());
            customer.setArtificialPerson(apply.getArtificialPerson());
            customer.setBusinessLicence(apply.getBusinessLicence());
            customer.setEmergencyContact(apply.getEmergencyContact());
            customer.setEmergencyPhone(apply.getEmergencyPhone());
            customer.setIsProducer(apply.getIsProducer());
//            customer.setBrand(apply.getBrand());
            customer.setType(CustomerType.seller);
            customer.setAddress(apply.getAddress());
            customer.setPickAddress(apply.getPickAddress());
            customer.setPickAreaCode(apply.getPickAreaCode());
            customer.setAlipayCode(apply.getAlipayCode());
            customer.setTenpayCode(apply.getTenpayCode());
            customer.setOutGoodsType(apply.getOutGoodsType());
            customer.setAlipayName(apply.getAlipayName());
            customer.setProvinceId(apply.getProvinceId());
            customer.setProvince(apply.getProvince());
            customer.setCityId(apply.getCityId());
            customer.setCity(apply.getCity());
            customer.setCountyId(apply.getCountyId());
            customer.setArea(apply.getArea());
            customer.setSign(apply.getSign());
            customer.setRemarks(apply.getRemarks());
            customer.setQq(apply.getQq());
            customer.setWeiXin(apply.getWeiXin());
            customer.setPostCode(apply.getPostCode());
            customer.setPassSupplierTime(new Date());
            apply.setPassPurchaseTime(new Date());
            customerDao.update(customer);
            customerCheckDao.update(apply);
            String[] brands = apply.getBrand().split(Constant.SPLIT_MARK);

            //新添品牌及保存品牌供应商关系
            for(String brandName : brands){
                Brand brand = new Brand();
                brand.setName(brandName);
                brand = brandService.save(brand);

                brandService.saveBrandCustomer(brand.getId(), apply.getCustomerId());
            }

            msg = TemplateUtils.parseText(auditResultMsg,
                    ImmutableMap.<String, Object>of("name", customer.getName(), "type", "供货商"));
        } else {
            msg = TemplateUtils.parseText(auditNoPassMsg,
                    ImmutableMap.<String, Object>of("name", customer.getName(), "type", "供货商"));
        }
        SMSUtil.sendSmsMsg2(customer.getPhone(), msg);
    }

    public List<Customer> findProviders() {
        return customerDao.findProviders();
    }

    public String getCustomerNameById(int id) {
        Customer c = getById(id);
        return c == null ? "" : c.getName();
    }

    public String getCustomerPhoneById(int id) {
        Customer c = getById(id);
        return c == null ? "" : c.getPhone();
    }

    public void updateCustomer(Customer customer) {
        customerDao.update(customer);
    }

    public int getWeight(int customerId) {
        Customer customer = getById(customerId);
        return customer == null ? 0 : customer.getWeighting();
    }

    public Customer getCustomerByName(String name) {
        return customerDao.getByWhere("name = ?", name);
    }

    public List<Customer> getCustomerByCodes(Collection<String> codes) {
        return customerDao.getCustomerByCodes(codes);
    }


    public List<Customer> findCustomerByUsernames(Collection<String> usernames) {
        return customerDao.findCustomerByUsernames(usernames);
    }

    /**
     * 判断公司名是否有重复
     *
     * @param customerId  客户ID
     * @param companyName 公司名
     * @return true:有重复 false:无重复
     */
    public Boolean checkRepeatCompanyName(int customerId, String companyName) {
        List<Customer> list = customerDao.findWhere("id != ? and supplierCompany = ?", customerId, companyName);
        return list.size() > 0;
    }

    public PageList<Customer> getMembers(String userName, int level, String province, CustomerState state, CustomerType type, String signs, Long totalStart, Long totalEnd,
                                         Long countStart, Long countEnd,Date lastStart, Date lastEnd, PageParam pageParam){
        return customerDao.getMembers(userName, level, province, state, type, signs, totalStart, totalEnd, countStart, countEnd, lastStart, lastEnd, pageParam);
    }

    public Map<Integer, Customer> getCustomersByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new HashMap<>();
        List<Customer> customers = customerDao.getByIds(ids);
        return Maps.uniqueIndex(customers, Customer.idValue);
    }

    public Long getCustomerCount(CustomerType type, CustomerState state, Integer level) {
        return customerDao.getCustomerCount(type, state, level);
    }

    public List<Customer> getCustomersByLevel(CustomerType type, CustomerState state, Integer level) {
        return customerDao.getCustomerByLevel(type, state, Arrays.asList(level));
    }

    public List<Customer> getCustomersByLevel(CustomerType type, CustomerState state, Collection<Integer> levels) {
        return customerDao.getCustomerByLevel(type, state, levels);
    }

    /**
     * 自动升级
     * @param buyer 客户信息
     */
    public void upLevel(Customer buyer) {
        int curLevel = buyer.getLevel();
        List<MemberLevel> memberLevels = memberService.findAllMemberLevel();
        for (MemberLevel memberLevel : memberLevels) {
            if (curLevel < memberLevel.getLevel()) {
                if (memberLevel.getAndOr()) {     //且
                    if (buyer.getTotalPurchaseMoney() >= memberLevel.getTotalMoney() && buyer.getTotalPurchaseCount() >= memberLevel.getTotalQuantity()) {
                        curLevel = memberLevel.getLevel();
                    }
                } else {        //或
                    if (buyer.getTotalPurchaseMoney() >= memberLevel.getTotalMoney() || buyer.getTotalPurchaseCount() >= memberLevel.getTotalQuantity()) {
                        curLevel = memberLevel.getLevel();
                    }
                }
            }
        }
        if(curLevel > buyer.getLevel()){
            buyer.setLevel(curLevel);
            customerDao.update(buyer);
        }
    }

    /**
     * 获取所有客户
     * @return
     */
    public List<Customer> findAllMember(){
        return customerDao.findAllMember();
    }

    /**
     * 获取供货商
     * @return
     */
    public List<Customer> findSuppliers(String supplierName, SettlementMethodType method) {
        return customerDao.findSuppliers(CustomerType.seller, supplierName, method);
    }

    /**
     * 结算专用，he daoyuan
     * @param settlementOrder
     * @return List customer
     */
    public List<Customer> getSuppliersList(SettlementOrder settlementOrder) {
        return customerDao.getSuppliersList(settlementOrder);
    }

    /**
     * 累计采购额
     * @param buyerId
     * @param money
     */
    public void addUpPurchase(int buyerId, long money, long orderCount){
        Customer buyer = getById(buyerId);
        if (buyer != null) {
            buyer.setTotalPurchaseMoney(buyer.getTotalPurchaseMoney() + money);         //累计总采购额
            buyer.setTotalPurchaseCount(buyer.getTotalPurchaseCount() + orderCount);    //累计采购单数
            buyer.setLastPurchaseTime(new Date());                                      //更新最近采购时间
            save(buyer);

            //自动升级客户
            upLevel(buyer);
        }
    }


}
