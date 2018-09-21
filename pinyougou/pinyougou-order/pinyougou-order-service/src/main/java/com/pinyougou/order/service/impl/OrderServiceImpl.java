package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;


    private static final String REDIS_CART_LIST = "CART_LIST";

    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public String saveOrder(TbOrder order) {
        String outTradeNo = "";
        //1.查询redis中购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());


        if (cartList != null && cartList.size() > 0) {
            //2.对每个购物车生成对应的订单,订单编号
            Long orderId = 0L;
            String orderIds = "";
            double totalFee = 0.0;
            for (Cart cart : cartList) {
                //创建一个订单对象
                TbOrder tbOrder = new TbOrder();

                //保存订单明细
                orderId = idWorker.nextId();
                tbOrder.setOrderId(orderId);

                //保存来源
                tbOrder.setSourceType(order.getSourceType());

                //保存用户id
                tbOrder.setUserId(order.getUserId());

                //订单创建时间
                tbOrder.setCreateTime(new Date());

                //设置更新时间
                tbOrder.setUpdateTime(tbOrder.getCreateTime());

                //设置方式
                tbOrder.setPaymentType(order.getPaymentType());

                //设置收件人
                tbOrder.setReceiver(order.getReceiver());

                //设置收件地址
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());

                //设置收件手机号
                tbOrder.setReceiverMobile(order.getReceiverMobile());



                //设置商家id
                tbOrder.setSellerId(cart.getSellerId());

                //设置状态
                tbOrder.setStatus("0");

                //本笔订单的实付金额,从 每一个订单明细商品中叠加而来
                double payment = 0.0;

                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    //保存明细

                    //设置订单id
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(orderId);

                    payment += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insertSelective(orderItem);
                }
                //本笔订单的实付金额
                tbOrder.setPayment(new BigDecimal(payment));

                //记录本次交易总金额
                totalFee += payment;
                //累计订单id
                if (orderIds.length() > 0) {
                    orderIds += "," + orderId;
                } else {
                    orderIds += "," + orderId;

                }

                //保存订单
                orderMapper.insertSelective(tbOrder);
            }


        //3.如果是微信支付的话生成支付日志
        if ("1".equals(order.getPaymentType())) {
            TbPayLog payLog = new TbPayLog();

            //设置订单编号
            outTradeNo = idWorker.nextId() + "";
            payLog.setOutTradeNo(outTradeNo);

            //设置创建时间
            payLog.setCreateTime(new Date());

            //设置支付类型,在线支付
            payLog.setPayType("1");

            //设置支付状态,未支付
            payLog.setTradeState("0");

            //设置购买者
            payLog.setUserId(order.getUserId());

            //设置本次要支付的所有的订单,使用逗号隔开
            payLog.setOrderList(orderIds);

            //设置本次要支付的总金额,所有订单的总金额
            payLog.setTotalFee((long)((100 * totalFee)));

            payLogMapper.insertSelective(payLog);
        }
        //4.情况redis购物车列表
        redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());
    }
        //5.返回支付日志id,如果是货到付款返回空字符串
        return outTradeNo;
    }
}
