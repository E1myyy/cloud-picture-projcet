package com.eimy.yunpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eimy.yunpicturebackend.model.dto.user.UserQueryRequest;
import com.eimy.yunpicturebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eimy.yunpicturebackend.model.vo.LoginUserVO;
import com.eimy.yunpicturebackend.model.vo.UserVO;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 路灯
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-04-20 14:40:56
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册校验
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long UserRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户密码加密
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 请求获得用户脱敏后的登录信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 请求获得用户脱敏后的登录信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登录态注销
     *
     * @param request
     * @return
     */
    boolean userLogOut(HttpServletRequest request);

    /**
     * 查询用户列表
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
