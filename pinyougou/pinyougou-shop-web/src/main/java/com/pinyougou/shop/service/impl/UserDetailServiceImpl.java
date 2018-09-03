package com.pinyougou.shop.service.impl;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2018-09-02
 * Author:Wanzi
 * Desc:
 */
public class UserDetailServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public SellerService getSellerService() {
        return sellerService;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //获取权限集合
        List<GrantedAuthority> authentications = new ArrayList<>();

        //可以根据用户到数据库中查询该用户对于的角色权限
        authentications.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //判断seller是否存在,并且通过审核
        TbSeller seller = sellerService.findOne(username);
        if (seller != null && "1".equals(seller.getStatus())){
            //说明商家存在并通过审核
            return new User(username,seller.getPassword(),authentications);
        }

        return null;
    }

}
