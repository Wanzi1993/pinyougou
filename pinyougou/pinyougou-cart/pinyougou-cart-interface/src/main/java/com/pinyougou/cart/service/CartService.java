package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

/**
 * Date:2018-09-19
 * Author:Wanzi
 * Desc:
 */
public interface CartService {

    /**
     * 增减购物车购买数量
     * @param itemId 商品skuid
     * @param num 数量
     * @return 操作结果
     */
    List<Cart> addCartToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 根据用户名查询在redis中购物车列表
     * @param username
     * @return
     */
    List<Cart> findCartListInRedisByUsername(String username);

    /**
     * 保存购物车列表到redis中
     * @param cartList 购物车列表
     * @param username 用户名
     */
    void saveCartListToRedis(List<Cart> cartList, String username);

    List<Cart> mergeCartList(List<Cart> cookie_cartList, List<Cart> redis_cartList);
}
