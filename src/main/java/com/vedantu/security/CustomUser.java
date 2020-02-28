package com.vedantu.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/*
This is to add more fields to the existed User object of spring framework;
This is useful in adding extra data to authentication object in security context;
Actually, User(username, password, authorities);
This is taken by, provider and sets its as principal in authentication object.
So, we are adding more fields to the User Object which will be principal in the authentication object
 */
public class CustomUser extends User {
    private Long userId; // this is extra field. this is _id of users collection

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Long userId) {
        super(username, password, authorities);
        this.userId = userId;
    }
    // this will represent the logged in user's _id


    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }
}
