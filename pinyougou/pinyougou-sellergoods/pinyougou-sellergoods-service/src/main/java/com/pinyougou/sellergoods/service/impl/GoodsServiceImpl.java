package com.pinyougou.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Transactional
@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //隐藏被删除的商品
        criteria.andNotEqualTo("isDelete","1");
        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId",goods.getSellerId() );
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus", goods.getAuditStatus());
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
        //1.保存基本信息
        add(goods.getGoods());
        //2.保存描述信息,在mybatis中,如果保存成功,主键可以回填到保存时候的那个对象中
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
        //3.保存sku
        saveItemList(goods);

    }

    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getGoods().getIsEnableSpec())){
            if (goods.getItemList() != null && goods.getItemList().size() > 0){

                for (TbItem item : goods.getItemList()) {

                    //item对象包含多个属性,需要单独设置
                    //sku商品名称应该为spu商品标题加所有规格拼成字符串
                    String title = goods.getGoods().getGoodsName();
                    Map<String,Object> map = JSON.parseObject(item.getSpec());
                    Set<Map.Entry<String, Object>> entries = map.entrySet();
                    for (Map.Entry entry : entries ){
                        title += " " + entry.getValue();
                    }
                    //设置标题
                    item.setTitle(title);
                    setItemValue(item,goods);
                    //保存sku进map
                    itemMapper.insert(item);
                }
            }
        }else {
             //不启用规格
            TbItem item = new TbItem();
            item.setPrice(goods.getGoods().getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setStatus("0");

            item.setTitle(goods.getGoods().getGoodsName());

            setItemValue(item,goods);
            itemMapper.insert(item);
        }

    }

    private void setItemValue(TbItem item, Goods goods) {

        //设置品牌
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(tbBrand.getName());

        //商品分类中文名称
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(tbItemCat.getName());

        //商品分类id
        item.setCategoryid(tbItemCat.getId());


        //创建时间
        item.setCreateTime(new Date());

        //设置商品id
        item.setGoodsId(goods.getGoods().getId());

        //设置商品图片,设置商品的第一张图片
        if (!StringUtils.isEmpty(goods.getGoodsDesc().getItemImages())) {
            //将图片json字符串转换为json对象
            List<Map> images = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);

            item.setImage(images.get(0).toString());
        }
        //设置商家
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(tbSeller.getName());
        item.setSellerId(tbSeller.getSellerId());

        //设置创建时间
        item.setUpdateTime(item.getCreateTime());

    }

    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();

        //1.查询商品基本信息,封装进goods对象
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);

        //2.查询商品描述信息,封装进goods对象
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);

        //按条件查询商品规格,封装进goods对象
        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId",id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setItemList(tbItems);

        return goods;
    }

    @Override
    public void updateGoods(Goods goods) {
        //先修改商品基本信息
        goods.getGoods().setAuditStatus("0");
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());

        //修改商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //删除商品SKU信息
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);

        //更新商品SKU信息
        saveItemList(goods);

    }

    @Override
    public void deleteGoodsByIds(Long[] ids) {
        //更改商品状态
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsDelete("1");

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));

        goodsMapper.updateByExampleSelective(tbGoods,example);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        //更新状态为送审状态
        TbGoods tbGoods = new TbGoods();
        tbGoods.setAuditStatus(status);

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(tbGoods,example);

        //如果审核通过,则更新SKU状态为可卖
        if ("2".equals(status)){
            TbItem tbItem = new TbItem();
            tbItem.setStatus("1");

            Example example1 = new Example(TbItem.class);

            example1.createCriteria().andIn("id",Arrays.asList(ids));
            itemMapper.updateByExampleSelective(tbItem,example1);
        }

        if ("1".equals(status)){
            TbGoods tbGoods1 = new TbGoods();
            tbGoods1.setIsMarketable("1");

            Example example2 = new Example(TbItem.class);
            example2.createCriteria().andIn("id",Arrays.asList(ids));
            goodsMapper.updateByExampleSelective(tbGoods1,example2);

        }
        if ("0".equals(status)){
            TbGoods tbGoods2 = new TbGoods();
            tbGoods2.setIsMarketable("0");

            Example example3 = new Example(TbItem.class);
            example3.createCriteria().andIn("id",Arrays.asList(ids));
            goodsMapper.updateByExampleSelective(tbGoods2,example3);

        }


    }
}
