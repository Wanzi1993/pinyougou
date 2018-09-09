package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //在redis中内容对应的key
    private static final String REDIS_CONTENT="content";

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> contentList = null;

        try {
            //先存缓存中查找
            contentList = (List<TbContent>)redisTemplate.boundHashOps(REDIS_CONTENT).get(categoryId);

            if (contentList != null) {
                return contentList;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();

        //id符合的可悲查询
        criteria.andEqualTo("categoryId",categoryId);
        //启用状态的才可以查询
        criteria.andEqualTo("status","1");
        //启用降序排序
        example.orderBy("sortOrder").desc();

        contentList = contentMapper.selectByExample(example);

        try {
            //设置存入缓存
            redisTemplate.boundHashOps(REDIS_CONTENT).put(categoryId,contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentList;
    }

    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);

        //同步缓存中数据
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 将分类id对应的redis数据删除
     * @param categoryId
     */
    private void updateContentInRedisByCategoryId(Long categoryId) {
        redisTemplate.boundHashOps(REDIS_CONTENT).delete(categoryId);
    }

    @Override
    public void update(TbContent tbContent) {
        super.update(tbContent);

        //查询原来这个内容对应的分类id
        TbContent oldContent = findOne(tbContent.getId());

        if (!oldContent.getCategoryId().equals(tbContent.getCategoryId())){
            //修改内容的时候,以及修改过内容分类id,所以要删除旧的分类对应的内容
            updateContentInRedisByCategoryId(oldContent.getCategoryId());
        }
        //更新当前对应的分类redis数据
        updateContentInRedisByCategoryId(tbContent.getCategoryId());
    }

    @Override
    public void deleteByIds(Serializable[] ids) {

        //查询所有内容
        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        List<TbContent> contentList = contentMapper.selectByExample(example);

        for (TbContent tbContent : contentList) {
            updateContentInRedisByCategoryId(tbContent.getCategoryId());
        }
        super.deleteByIds(ids);
    }
}
