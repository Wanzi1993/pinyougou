package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Date:2018-09-21
 * Author:Wanzi
 * Desc:
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

   //注入统一下单所需的值
    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;

    @Override
    public Map<String, String> createNative(String outTradeNo, String totalFee) {

        //返回结果
        Map<String,String> returnMap = new HashMap<>();

        try {
            //1.要封装给微信支付的参数集合
            Map<String,String> paramMap = new HashMap<>();

            //微信支付分配的公众账号ID（企业号corpid即为此appId）
            paramMap.put("appid", appid);

            //微信支付分配的商户号
            paramMap.put("mch_id", partner);

            //随机字符串，长度要求在32位以内。推荐随机数生成算法
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

            //通过签名算法计算得出的签名值，详见签名生成算
            //paramMap("sign", );微信有SDK工具类生成

            //商品简单描述，该字段请按照规范传递，具体请见参数规定
            paramMap.put("body", "品优购");

            //商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
            paramMap.put("out_trade_no", outTradeNo);

            //订单总金额，单位为分，详见支付金额
            paramMap.put("total_fee",totalFee);

            //APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器
            paramMap.put("spbill_create_ip","127.0.0.1");

            //异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
            paramMap.put("notify_url",notifyurl);

            //JSAPI 公众号支付NATIVE 扫码支付APP APP支付 说明详见参数规定
            paramMap.put("trade_type","NATIVE");

            //2.将支付参数通过微信支付工具类转换为xml文件
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            System.out.println("发送到微信统一下单的参数为:" + signedXml);

            //3.创建HttpClient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

            httpClient.setHttps(true);

            httpClient.setXmlParam(signedXml);

            httpClient.post();

            //4.获取微信支付返回的数据
            String content = httpClient.getContent();

            //5.转换内容为map并返回结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            //设置返回的状态码此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
            returnMap.put("result_code", resultMap.get("result_code"));

            //设置返回的二维码,trade_type为NATIVE时有返回，用于生成二维码，展示给用户进行扫码支付
            returnMap.put("code_url", resultMap.get("code_url"));

            //设置返回的支付总费用
            returnMap.put("totalFee", totalFee);

            //设置返回的支付日志id
            returnMap.put("outTradeNo", outTradeNo);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return returnMap;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {

        try {
            //1.封装要查询的数据成map返回到controller
            Map<String,String> paramMap = new HashMap<>();

            //微信支付分配的公众账号ID（企业号corpid即为此appId）
            paramMap.put("appid",appid);

            //微信支付分配的商户号
            paramMap.put("mch_id",partner);

            //微信的订单号，建议优先使用
            //商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。二选一
            paramMap.put("out_trade_no",outTradeNo);

            //随机字符串，不长于32位。推荐随机数生成算法
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());

            //通过签名算法计算得出的签名值，详见签名生成算法
            //paramMap.put("sign",WXPayUtil.generateSignedXml());工具包生成

            //2.将参数转换为xml
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            System.out.println("发送给微信的查看订单的XML为:" + signedXml);

            //3.创建HTTPClient对象发送到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");

            httpClient.setHttps(true);

            httpClient.setXmlParam(signedXml);

            httpClient.post();

            //获取微信支付返回的数据
            String content = httpClient.getContent();

            System.out.println("微信查询订单返回的内容为： " + content);

            //将返回的内容转换为map
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
