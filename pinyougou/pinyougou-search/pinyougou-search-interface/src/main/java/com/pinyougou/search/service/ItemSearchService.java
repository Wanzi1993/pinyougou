package com.pinyougou.search.service;

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
}
