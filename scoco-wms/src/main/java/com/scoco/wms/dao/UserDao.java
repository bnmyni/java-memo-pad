package com.scoco.wms.dao;

import com.scoco.wms.entity.User;
import com.scoco.wms.fo.LoginUserFo;

public interface UserDao {

    User login(LoginUserFo fo);
}
