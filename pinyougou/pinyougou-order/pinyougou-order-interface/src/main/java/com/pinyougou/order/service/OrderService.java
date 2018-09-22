package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    /**
     * 保存订单明细支付日志到数据库中
     * @param order 订单信息
     * @return 支付日志id
     */
    String saveOrder(TbOrder order);

    /**
     * 根据支付日志id查询支付日志信息
     * @param outTradeNo 支付日志id
     * @return 支付日志信息
     */
    TbPayLog findPayLogByOutTradeNo(String outTradeNo);

    /**
     * 根据查询微信支付查询订单返回的结果修改订单状态为已支付
     * @param outTradeNo
     * @param transactionId
     */
    void updateOrderStatus(String outTradeNo, String transactionId);
}