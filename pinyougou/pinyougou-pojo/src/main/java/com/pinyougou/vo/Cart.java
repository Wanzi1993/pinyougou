package com.pinyougou.vo;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * Date:2018-09-17
 * Author:Wanzi
 * Desc:
 */
public class Cart implements Serializable {
    //卖家id
    private String sellerId;

    //卖家名称
    private String seller;

    //购物车明细
    private List<TbOrderItem> orderItemList;

    public Cart() {
    }

    public Cart(String sellerId, String seller, List<TbOrderItem> orderItemList) {
        this.sellerId = sellerId;
        this.seller = seller;
        this.orderItemList = orderItemList;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
