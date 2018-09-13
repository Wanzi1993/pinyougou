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

        //设置多关键字查询
        if (!StringUtils.isEmpty("keywords")){
            searchMap.put("keywords",searchMap.get("keywords").toString().replaceAll(" ",""));

        }

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

        //按照分类查询
        if (!StringUtils.isEmpty(searchMap.get("category"))){
            //设置查询条件
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(categoryCriteria);
            //添加过滤条件
            query.addFilterQuery(simpleFilterQuery);
        }

        //按照品牌查询
        if (!StringUtils.isEmpty(searchMap.get("brand"))){
            //设置查询条件
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(simpleFilterQuery);
        }

        //按照规格查询
        if (searchMap.get("spec") != null){

            //将规格对象强转成map键值对
            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");
            Set<Map.Entry<String, String>> entries = specMap.entrySet();

            for (Map.Entry<String, String> entry : entries) {
                //拼接查询规格
                Criteria specCriteria = new Criteria(searchMap.get("item_spec") + entry.getKey()).is(entry.getValue());
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(simpleFilterQuery);
            }
        }
        //按照品牌查询
        if (!StringUtils.isEmpty(searchMap.get("price"))){
            
            //格式化价格
            String[] prices = searchMap.get("price").toString().split("-");


            //设置起始条件
            Criteria startCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
            SimpleFilterQuery startFilterQuery = new SimpleFilterQuery(startCriteria);
            query.addFilterQuery(startFilterQuery);

            //设置结束条件:如果不为*
            if (!"*".equals(prices[1])){
                Criteria endCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleFilterQuery endFilterQuery = new SimpleFilterQuery(endCriteria);
                query.addFilterQuery(endFilterQuery);
            }

        }

        //设置排序
        if (!StringUtils.isEmpty(searchMap.get("sortField"))&&!StringUtils.isEmpty(searchMap.get("sort"))){

            String sortOrder = searchMap.get("sort").toString();
            Sort sort = new Sort("DESC".equals(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "item_" + searchMap.get("sortField"));

            query.addSort(sort);

        }
        //设置分页信息
        //设置当前页
        int page = 1;
        if (searchMap.get("pageNo") != null){
            page = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        //设置页大小
        int rows = 20;
        if (searchMap.get("pageSize") != null) {
            rows = Integer.parseInt(searchMap.get("pageSize ").toString());
        }

        //设置起始索引号 (当前页-1)*页大小
        query.setOffset((page-1)*rows);
        //设置页大小
        query.setRows(rows);

        //普通查询
        //ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);

        //高亮查询
        HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //处理高亮查询结果
        List<HighlightEntry<TbItem>> highlighted = itemHighlightPage.getHighlighted();

        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<TbItem> entry : highlighted) {
                List<HighlightEntry.Highlight> highlights = entry.getHighlights();

                if (highlights != null && highlights.size() > 0 && highlights.size() > 0) {
                    entry.getEntity().setTitle(highlights.get(0).getSnipplets().get(0));
                }
            }
        }
        //设置分页返回结果
        resultMap.put("rows",itemHighlightPage.getContent());
        //总页数
        resultMap.put("totalPages",itemHighlightPage.getTotalPages());
        //总记录数
        resultMap.put("total",itemHighlightPage.getTotalElements());

        //设置返回的查询列表
        resultMap.put("rows",itemHighlightPage.getContent());
        return resultMap;
    }

    @Override
    public void importItemList(List<TbItem> tbItemList) {

        for (TbItem item : tbItemList) {
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        }
         solrTemplate.saveBeans(tbItemList);
         solrTemplate.commit();
    }

    @Override
    public void deleteItemByGoodsIds(List<Long> goodsIds) {
        Criteria criteria = new Criteria("item_goodsid").in(goodsIds);

        SimpleQuery query = new SimpleQuery(criteria);


        solrTemplate.delete(query);

        solrTemplate.commit();
    }
}
