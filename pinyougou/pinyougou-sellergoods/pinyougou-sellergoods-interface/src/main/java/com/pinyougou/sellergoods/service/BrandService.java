package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * Date:2018-08-26
 * Author:Wanzi
 * Desc:
 */
public interface BrandService extends BaseService<TbBrand> {

    List<TbBrand> queryAll();

    List<TbBrand> testPage(Integer page, Integer rows);

    PageResult search(TbBrand tbBrand, Integer page, Integer rows);

    /**
     * 查询数据库中的所有品牌；并返回一个集合，集合中的数据结构如下：
     *
     * @return [{id:'1',text:'联想'},{id:'2',text:'华为'}]
     */
    List<Map<String, Object>> selectOptionList();
}
