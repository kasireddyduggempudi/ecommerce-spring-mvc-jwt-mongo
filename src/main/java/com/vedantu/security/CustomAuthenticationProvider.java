package com.vedantu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // checking if principal or credentials are invalid
        if(authentication.getPrincipal() == null || authentication.getPrincipal().toString().isEmpty() || authentication.getPrincipal() == null || authentication.getPrincipal().toString().isEmpty() ){
            throw new AuthenticationCredentialsNotFoundException("Invalid authentication object");
        }

        // fetching userDetails from the customUserDetailsService class with phoneNumber
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authentication.getPrincipal().toString());

        // checking if password is equal or not
        if(!(userDetails.getPassword().equals(authentication.getCredentials().toString()))){
            throw  new AuthenticationCredentialsNotFoundException("Invalid Credentials");
        }

        /*
        returning authentication object
        this will be used by the manager and will set the AuthenticationManager to set Anonymous User in security context
        => Here, UserDetails object, contains phoneNumber as username, password, authorities list, userId which
            represents the unique _id of logged in user.
         */
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        /* Authentication Manager will check call this to know if this provider
         is supporting authentication
         -> Here true says, supporting.
         */
        return true;
    }
}
