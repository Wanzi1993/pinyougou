package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

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
}