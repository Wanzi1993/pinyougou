package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.jms.*;
import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    //搜索对象
    //@Reference
    //private ItemSearchService itemSearchService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue solrItemQueue;

    @Autowired
    private ActiveMQQueue solrItemDeleteQueue;

    @Autowired
    private ActiveMQTopic itemTopic;

    @Autowired
    private ActiveMQTopic itemDeleteTopic;


    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 保存商品的基本,描述,sku列表
     *
     * @param goods
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //设置商家
            final String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            goods.getGoods().setAuditStatus("0");//未提交审核
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 根据id查询商品信息进行回显修改
     *
     * @param id
     * @return
     */
    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    /**
     * 修改商品
     *
     * @param goods
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            //首先判断是否为同一个商家的商品,校验商家
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

            if (!oldGoods.getSellerId().equals(sellerId) || !sellerId.equals(goods.getGoods().getSellerId())) {
                Result.fail("非法操作");
            }
            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);

            //数据同步删除
            //itemSearchService.deleteItemByGoodsIds(Arrays.asList(ids));
            sendMQMsg(solrItemDeleteQueue,ids);
            sendMQMsg(itemDeleteTopic,ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 发现MQ消息
     * @param destination 模式:队列,主题
     * @param ids 商品idspu集合
     */
    private void sendMQMsg(Destination destination, Long[] ids) {
        try {
            jmsTemplate.send(destination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage objectMessage = session.createObjectMessage();

                    objectMessage.setObject(ids);
                    return objectMessage;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分页查询列表
     *
     * @param goods 查询条件
     * @param page  页号
     * @param rows  每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return goodsService.search(page, rows, goods);
    }

    /**
     * 根据商品id集合更新对应的商品的状态
     *
     * @param ids    商品id集合
     * @param status 商品的状态
     * @return 操作结果
     */

    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);

            //同步更新审核通过的商品数据
            if ("2".equals(status)) {
                List<TbItem> tbItemList = goodsService.findItemListByGoodsIdsAndStatus(ids, "1");
                //itemSearchService.importItemList(tbItemList);

                //使用MQ同步搜索信息,发送审核通过的商品MQ消息
                jmsTemplate.send(solrItemQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        //创建一个文本消息
                        TextMessage textMessage = session.createTextMessage();

                        textMessage.setText(JSON.toJSONString(tbItemList));
                        return textMessage;
                    }
                });
            }
            sendMQMsg(itemTopic,ids);
            return Result.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新失败");
    }

}
