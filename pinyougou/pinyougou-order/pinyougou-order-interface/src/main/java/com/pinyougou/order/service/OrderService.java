package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
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
}