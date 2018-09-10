package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Date:2018-09-10
 * Author:Wanzi
 * Desc:
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;


    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String,Object> resultMap = new HashMap<>();

        //设置查询条件
        //SimpleQuery query = new SimpleQuery();

        //设置高亮搜索条件
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));

        query.addCriteria(criteria);

        //设置高亮域
        HighlightOptions highlightOptions = new HighlightOptions();
        //高亮域
        highlightOptions.addField("item_title");
        //高亮起始标签
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //高亮结束标签
        highlightOptions.setSimplePostfix("</em>");




        query.setHighlightOptions(highlightOptions);

        //普通查询
        //ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        //高亮查询
        HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //处理高亮查询结果
        List<HighlightEntry<TbItem>> highlighted = itemHighlightPage.getHighlighted();

        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<TbItem> entry : highlighted) {
                List<HighlightEntry.Highlight> highlights = entry.getHighlights();

                if (highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets() != null) {
                    entry.getEntity().setTitle(highlights.get(0).getSnipplets().get(0));
                }
            }
        }

        //设置返回的查询列表
        resultMap.put("rows",itemHighlightPage.getContent());
        return resultMap;
    }
}
