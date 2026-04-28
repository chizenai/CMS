package com.cms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cms.entity.User;

public interface UserService extends IService<User> {
    User getByUsername(String username);
    boolean register(User user);
}
