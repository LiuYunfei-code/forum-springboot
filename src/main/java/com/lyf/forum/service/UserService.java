package com.lyf.forum.service;

import com.lyf.forum.mapper.UserMapper;
import com.lyf.forum.model.User;
import com.lyf.forum.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample userExample=new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> dbUsers=userMapper.selectByExample(userExample);
        if (dbUsers.size()==0){
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(System.currentTimeMillis());
            userMapper.insert(user);
        }else {
            // 更新
            User dbUser=dbUsers.get(0);
            dbUser.setAvatarUrl(user.getAvatarUrl());
            dbUser.setName(user.getName());
            dbUser.setToken(user.getToken());
            dbUser.setGmtModified(System.currentTimeMillis());
            userMapper.updateByExampleSelective(dbUser,userExample);

        }
    }
}
