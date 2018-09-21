package com.pinyougou.order.service;

import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface PayLogService extends BaseService<TbPayLog> {

    PageResult search(Integer page, Integer rows, TbPayLog payLog);
}