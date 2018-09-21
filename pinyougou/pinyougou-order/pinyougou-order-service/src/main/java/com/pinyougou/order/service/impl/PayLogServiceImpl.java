package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.order.service.PayLogService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = PayLogService.class)
public class PayLogServiceImpl extends BaseServiceImpl<TbPayLog> implements PayLogService {

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbPayLog payLog) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbPayLog.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(payLog.get***())){
            criteria.andLike("***", "%" + payLog.get***() + "%");
        }*/

        List<TbPayLog> list = payLogMapper.selectByExample(example);
        PageInfo<TbPayLog> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
