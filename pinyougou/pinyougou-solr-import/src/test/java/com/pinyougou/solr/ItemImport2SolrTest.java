package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Date:2018-09-10
 * Author:Wanzi
 * Desc:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class ItemImport2SolrTest {

    //注入SolrTemplate对象
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void test(){
        //获取已经通过审核的商品
        TbItem param = new TbItem();
        param.setStatus("1");
        List<TbItem> itemList = itemMapper.select(param);
        
        //转换商品规格
        for (TbItem item : itemList) {
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(specMap);
            //导入商品列表到solr
            solrTemplate.saveBean(item);
        }


        solrTemplate.commit();
    }
}
