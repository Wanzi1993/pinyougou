package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void addGoods(Goods goods);

    /**
     *  根据id查询商品信息进行回显修改
     * @param id
     * @return
     */
    Goods findGoodsById(Long id);

    /**
     * 修改商品
     * @param goods
     */
    void updateGoods(Goods goods);

    void updateStatus(Long[] ids, String status);

    /**
     * 运营商后台逻辑删除商品,其实是更改显示状态
     * @param ids
     */
    void deleteGoodsByIds(Long[] ids);

    /**
     * 根据spu商品id集合查询所有的sku列表
     * @param ids
     * @param status
     * @return
     */
    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);


    /**
     * 根据商品spu id查询商品的基本、描述、sku列表等信息
     * 并跳转到商品的详情页面
     * @param goodsId 商品spu id
     * @param status 商品sku状态
     * @return mv
     */
    Goods findGoodsByIdAndStatus(Long goodsId, String status);
}