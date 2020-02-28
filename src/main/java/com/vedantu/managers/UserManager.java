package com.vedantu.managers;

import com.vedantu.daos.CounterDao;
import com.vedantu.daos.UserDao;
import com.vedantu.jwt.JwtUtil;
import com.vedantu.models.ProductModel;
import com.vedantu.models.UserModel;
import com.vedantu.requests.Address;
import com.vedantu.requests.LoginRequest;
import com.vedantu.requests.RegisterRequest;
import com.vedantu.security.CustomUser;
import com.vedantu.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserManager {

    private final UserDao userDao; // injection will done in constructor
    private final CounterDao counterDao; // injection will be done in constructor

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    // constructor based injection
    @Autowired
    public UserManager(UserDao userDao, CounterDao counterDao) {
        this.userDao = userDao;
        this.counterDao = counterDao;
    }


    /*
    field based injection (not recommended)
    -> @AutoWired
    -> private final UserDao userDao
    -> but this is not recommended
    -> so we go with constructor based injection
    Constructor based injection (recommended)
    ->private final UserDao userDao // declaration
    -> @Autowired
    -> public UserManager(UserDao userDao){
            this.userDao = userDao;
       }
     */


    public boolean createUser(RegisterRequest userReq) throws Exception {
        userReq.verify(); // this will check if any mandatory fields are not there

        // check if already exist with the user phoneNumber
        Query query = new Query(Criteria.where("phoneNumber").is(userReq.getPhoneNumber()));
        if(userDao.getEntity(query, UserModel.class) !=  null){
            throw new Exception("user already existed with entered phone number");
        }

        // converting RegisterRequest to UserModel to store in db
        UserModel userModel = new UserModel(userReq);
        if(userModel.getId() == null || userModel.getId() == 0L){
            // means we need to set the _id with next number
            userModel.setId(counterDao.nextSequence("users"));
        }
        return userDao.createUser(userModel);
    }


    public List<ProductModel> getProducts() throws Exception {
        Query query = new Query(Criteria.where("_id").exists(true));
        return userDao.getEntities(query, ProductModel.class);
    }

    // authenticate method
    public ResponseEntity<?> authenticate(LoginRequest loginRequest) throws Exception {
        // to check all fields in loginRequest are correct below one.
        loginRequest.verify();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword()));

        // if everything goes fine, below will execute

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getPhoneNumber());
        String jwtToken = jwtUtil.generateToken(userDetails);
        /*
        Now, jwtToken, contains phoneNumber as subject (had set in JwtUtil)
         */
        return ResponseEntity.ok(jwtToken);
    }

    // updateUser method
    public Boolean updateUser(RegisterRequest updateRequest) throws Exception {
        if(updateRequest.getPhoneNumber() == null){
            throw new Exception("phoneNumber must be there to update");
        }

        UserModel user = userDao.getUserByPhoneNumber(updateRequest.getPhoneNumber());

        if(user == null){
            throw new Exception("Invalid phoneNumber. No records with entered phoneNumber");
        }

        /* Check if entered phoneNumber is the currentUser's phoneNumber or not
         This is possible, by checking the userId with the currentLoggedInUserId
         */
        Long currentLoggedInUserId = getCurrentLoggedInUserId();
        if(!user.getId().equals(currentLoggedInUserId) && !isCurrentUserAdmin()){
            //means, not correct user and not  an admin
            throw new Exception("Tyring to unauthorized access.");
        }

        /* Everything is fine and updating the fields to fetched document from requestDocument  */
        if(updateRequest.getFirstName() != null && !updateRequest.getFirstName().equals("")){
            user.setFirstName(updateRequest.getFirstName());
        }
        if(updateRequest.getLastName() != null && !updateRequest.getLastName().equals("")){
            user.setLastName(updateRequest.getLastName());
            user.setFullName(user.getFirstName() + user.getLastName());
        }else{
            user.setFullName(user.getFirstName() + " " + user.getLastName());
        }
        if(updateRequest.getEmail() != null && !updateRequest.getEmail().equals("")){
            user.setEmail(updateRequest.getEmail());
        }
        if(updateRequest.getPassword() != null && updateRequest.getPassword().length() >= 6){
            user.setPassword(updateRequest.getPassword());
        }
        Address addr = user.getAddress();
        if(updateRequest.getCity() != null && !updateRequest.getCity().equals("")){
            addr.setCity(updateRequest.getCity());
        }
        if(updateRequest.getState() != null && !updateRequest.getState().equals("")){
            addr.setState(updateRequest.getState());
        }
        if(updateRequest.getCountry() != null && !updateRequest.getCountry().equals("")){
            addr.setCountry(updateRequest.getCountry());
        }
        if(updateRequest.getPincode() != 0){
            addr.setPincode(updateRequest.getPincode());
        }
        //setting new address
        user.setAddress(addr);


        return userDao.updateUser(user, getCurrentLoggedInUserId());
    }

    // getUserById
    public UserModel getUserById(Long id) throws Exception {
        if(id == null || id == 0L){
            throw new Exception("invalid id");
        }

        return userDao.getEntityById(id, UserModel.class);
    }

    // getUserByPhoneNumber
    public UserModel getUserByPhoneNumber(String phoneNumber) throws Exception {
        if(phoneNumber == null || phoneNumber.length() != 10){
            throw new Exception("Invalid phone number");
        }

        Query query = new Query(Criteria.where("phoneNumber").is(phoneNumber));
        return userDao.getEntity(query, UserModel.class, "users");
    }

    /* BEGIN: Methods related  to security context */
    public Long getCurrentLoggedInUserId() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof  UserDetails)){
            throw new Exception("currentLoggedInUserId not found in the SecurityContext");
        }


        CustomUser currentUser = (CustomUser)auth.getPrincipal();
        return currentUser.getUserId();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<GrantedAuthority> getCurrentUserAuthorities(){
        return new ArrayList<GrantedAuthority>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    public Boolean isCurrentUserAdmin(){
        boolean admin = false;
        for(GrantedAuthority role : getCurrentUserAuthorities()){
            System.out.println(role);
            if(role.toString().equals("ROLE_ADMIN")){
                admin = true;
                break;
            }
        }
        return admin;
    }
    /* END: Methods related to security context */
}
