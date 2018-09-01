package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Date:2018-08-26
 * Author:Wanzi
 * Desc:
 */
public interface BrandMapper extends Mapper<TbBrand> {
    List<TbBrand> queryAll();

    List<Map<String, Object>> selectOptionList();
}
