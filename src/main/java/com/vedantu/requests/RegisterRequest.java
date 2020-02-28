package com.vedantu.requests;

import com.vedantu.enums.UserTypeEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

public class RegisterRequest extends AbstractFrontEndReq{

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserTypeEnum userType;
    private String password;
    private String city;
    private String state;
    private String country;
    private int pincode;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserTypeEnum getUserType() {
        return userType;
    }

    public void setUserType(UserTypeEnum userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }


    // here validation goes and adds the errors to the validationErrors list
    @Override
    public List<String> collectValidationErrors() {
        List<String> errorsList = super.collectValidationErrors(); // returns empty list
        // add errors to this list
        // and this list will be there with this object
        // and verify method can access this list and check for errors

        // firstName is mandatory
        if(this.firstName == null || this.firstName.equals("")){
            errorsList.add("firstName is mandatory");
        }

        // phoneNumber is mandatory
        if(this.phoneNumber == null || this.phoneNumber.equals("") || this.phoneNumber.replace("^0+", "").length() != 10){
            errorsList.add("phoneNumber is mandatory and should be valid one");
        }

        //  email is mandatory
        if(this.email == null || this.email.equals("")){
            errorsList.add("email is mandatory");
        }

        // userType is mandatory
        if(this.userType == null){
            errorsList.add("userType is mandatory");
        }

        // password is mandatory
        if(this.password == null || this.password.equals("") || this.password.length() < 6){
            errorsList.add("password is mandatory and it should be minimum of length 6");
        }

        // city is mandatory
        if(this.city == null || this.city.equals("")){
            errorsList.add("city is mandatory");
        }

        // pincode is mandatory
        if(this.pincode == 0){
            errorsList.add("pincode is mandatory");
        }

        return errorsList;
        // this errorsList will be checked in verify in AbstractFrontEndReq class which is super class
        // so verify method belongs to this class
        // it checks, if list contains errors, it will throw errors
        // verify method will be used in the userManager class as it takes the request objects and converts them into respective models
    }
}
