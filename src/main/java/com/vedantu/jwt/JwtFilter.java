package com.vedantu.jwt;

import com.vedantu.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // this will be used whenever there is request from the user to see they have web token

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*System.out.println("in filter");*/
        // examining income request for jwt token in the header
        // if valid, it gets the userdetails from useretailservice and save it in the securitycontext

        final String authorization = request.getHeader("Authorization");
        String username= null;  // here in this project, username represents unique phoneNumber (generally, it is unique from user eg: email)
        String jwt = null;
        if(authorization != null && authorization.startsWith("Bearer ")){
            jwt  = authorization.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }/*else{
            System.out.println("no authorization header");
        }*/


        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // here username is logged in userId

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);    // username is phoneNumber
            System.out.println(userDetails.getAuthorities());
            /*
            Now, this userDetails will be the principal for the authentication object in the SecurityContext .
            So, we have userId of logged in user in the principal of the Authentication object in the SecurityContext
            -which will be used anywhere to validate or fetch any details from the db for that userId in any authenticated and
            - authorized request.
             */
            if(jwtUtil.validateToken(jwt, userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }/*else{
            System.out.println("no username or yes context");
        }*/
        filterChain.doFilter(request, response);
    }


}