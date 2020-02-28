package com.vedantu.controllers;

import com.vedantu.managers.UserManager;
import com.vedantu.models.ProductModel;
import com.vedantu.models.UserModel;
import com.vedantu.requests.LoginRequest;
import com.vedantu.requests.ProductRequest;
import com.vedantu.requests.RegisterRequest;
import com.vedantu.security.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    /*
    *field injection is not recommended
    @Autowired
    private UserManager userManager;
     */

    private UserManager userManager; // injection will be done in constructor


    @Autowired
    public UserController(UserManager userManager){
        this.userManager = userManager;
    }



    @RequestMapping(value="/createUser",  method= RequestMethod.POST)
    public boolean createUser(@RequestBody RegisterRequest user) throws Exception {
        return userManager.createUser(user);
        // return change to CreateUserResponse
    }

    @RequestMapping(value="/authenticate", method=RequestMethod.POST)
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) throws Exception {
        return userManager.authenticate(loginRequest);
    }

    @RequestMapping(value = "/updateUser", method=RequestMethod.PUT)
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public Boolean updateUser(@RequestBody RegisterRequest updateRequest) throws Exception {
        return userManager.updateUser(updateRequest);
    }

    @RequestMapping(value="/getUserById/{id}")
    public UserModel getUserById(@PathVariable("id") Long id) throws Exception {
        return userManager.getUserById(id);
    }

    @RequestMapping(value="/getUserByPhoneNumber/{phoneNumber}")
    public UserModel getUserByPhoneNumber(@PathVariable("phoneNumber") String  phoneNumber) throws Exception {
        return userManager.getUserByPhoneNumber(phoneNumber);
    }
}
