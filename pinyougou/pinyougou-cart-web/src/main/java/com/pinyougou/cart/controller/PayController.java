package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Date:2018-09-21
 * Author:Wanzi
 * Desc:
 */
@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference(timeout = 10000)
    private WeixinPayService weixinPayService;

    /**
     * 调用支付系统的统一下单地址返回支付二维码等信息
     * @param outTradeNo 支付日志id
     * @return 支付二维码等信息
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo){
        try {
            //查找支付日志信息
            TbPayLog payLog = orderService.findPayLogByOutTradeNo(outTradeNo);

            if (payLog != null){
                //如果不为空就到支付系统进行提交订单并返回支付地址
                String totalFee = payLog.getTotalFee().toString();
                return weixinPayService.createNative(outTradeNo, totalFee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    /**
     * 根据支付日志 id 查询订单支付状态
     * @param outTradeNo 支付日志 id
     * @return 支付结果
     */
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        Result result = Result.fail("支付失败");

        int count = 0;
        try {

            while(true){
                //到微信支付查询支付状态
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);

                if (resultMap == null) {
                    break;
                }
                if("SUCCESS".equals(resultMap.get("trade_state"))){
                    result = Result.ok("支付成功");

                    //支付成功后需要修改订单的支付状态
                    String transactionId = resultMap.get("transaction_id");
                    orderService.updateOrderStatus(outTradeNo,transactionId);
                    break;
                }

                //每隔3秒查询一次
                Thread.sleep(3000);
                count++;
                //3分钟内每3秒查询一次,过了三分钟回到二维码超时页面,重新生成二维码
                if (count > 3){
                    result = Result.fail("二维码超时");
                    break;
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

}
