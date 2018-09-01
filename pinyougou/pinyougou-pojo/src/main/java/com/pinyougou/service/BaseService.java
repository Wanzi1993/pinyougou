package com.pinyougou.service;

import com.pinyougou.vo.PageResult;

import java.io.Serializable;
import java.util.List;

/**
 * Date:2018-08-27
 * Author:Wanzi
 * Desc:
 */
public interface BaseService<T> {

    /**
     * 根据ID查找单个对象
     * @param id
     * @return 返回一个对象
     */
    public T findOne(Serializable id);

    /**
     * 查找所有对象
     * @return 返回所有查询到的对象
     */
    public List<T> findAll();

    /**
     * 根据条件查询
     * @param t
     * @return 所有符合条件的查询结果
     */
    public List<T> findByWhere(T t);

    /**
     * 分页查询结果
     * @param page
     * @param rows
     * @return 分页查询结果的实体类(在pojo项目中的vo包里
     */
    public PageResult findPage(Integer page, Integer rows);

    /**
     * 根据条件分页查询的结果
     * @param page
     * @param rows
     * @param t
     * @return 分页结果的实体类
     */
    public PageResult findPage(Integer page,Integer rows,T t);

    /**
     * 新增
     * @param t
     */
    public void add(T t);

    /**
     * 根据主键更新
     * @param t
     */
    public void update(T t);

    /**
     * 根据主键批量删除
     * @param ids
     */
    public void deleteByIds(Serializable[] ids);
}
