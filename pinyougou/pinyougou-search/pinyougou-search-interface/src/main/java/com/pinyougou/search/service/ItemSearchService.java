package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * Date:2018-09-10
 * Author:Wanzi
 * Desc:
 */
public interface ItemSearchService {

    /**
     *根据搜索关键字搜索商品列表
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    Map<String, Object> search(Map<String, Object> searchMap);

    /**
     * 同步更新商品列表
     * @param tbItemList
     */
    void importItemList(List<TbItem> tbItemList);

    /**
     * 而根据商品spuids结合删除solr中的数据
     * @param goodsIds
     */
    void deleteItemByGoodsIds(List<Long> goodsIds);
}
