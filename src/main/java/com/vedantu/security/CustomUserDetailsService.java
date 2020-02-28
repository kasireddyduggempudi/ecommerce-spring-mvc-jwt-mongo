package com.vedantu.security;

import com.vedantu.daos.UserDao;
import com.vedantu.enums.UserTypeEnum;
import com.vedantu.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    /* This is the default method by which many providers use to get UserDetails to verify login credentials */

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException{
        UserModel userModel = null;
        try{
            userModel = userDao.getUserByPhoneNumber(phoneNumber);
        }catch(Exception e){
            throw new UsernameNotFoundException("Error in fetching UserModel from dao in customuserdetailservice");
        }

        if(userModel == null){
            throw new UsernameNotFoundException(phoneNumber);
        }

        // lets send phone number, password, authorities as UserDetails Object
        ArrayList<GrantedAuthority> authoritiesList = new ArrayList<GrantedAuthority>();
        SimpleGrantedAuthority authority;
        if(userModel.getUserType().equals(UserTypeEnum.MERCHANT)){
            authority = new SimpleGrantedAuthority("ROLE_MERCHANT");
            authoritiesList.add(authority);
        }else if(userModel.getUserType().equals(UserTypeEnum.CUSTOMER)){
            authority = new SimpleGrantedAuthority("ROLE_CUSTOMER");
            authoritiesList.add(authority);
        }else if(userModel.getUserType().equals(UserTypeEnum.ADMIN)){
            authority = new SimpleGrantedAuthority("ROLE_ADMIN");
            authoritiesList.add(authority);
        }

        // note her, CustomUser is the subclass of User which is subclass or implemented class of UserDetails
        // So, we are adding userId to UserDetails object which will be principal in the SecurityContext's authentication object
        return new CustomUser(phoneNumber, userModel.getPassword(), authoritiesList, userModel.getId());
    }
}
