package com.vedantu.requests;

import java.util.List;

public class LoginRequest extends  AbstractFrontEndReq{
    private String phoneNumber;
    private String password;

    public LoginRequest(){
        super();
    }

    public LoginRequest(String phoneNumber, String password){
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public List<String> collectValidationErrors() {
        List<String> errorsList = super.collectValidationErrors();

        // phoneNumber is mandatory
        if(this.phoneNumber == null || this.phoneNumber.equals("") || this.phoneNumber.replace("^0+", "").length() != 10){
            errorsList.add("phoneNumber is mandatory and should be valid one");
        }

        // password is mandatory
        if(this.password == null || this.password.equals("") || this.password.length() < 6){
            errorsList.add("password is mandatory and it should be minimum of length 6");
        }
        return errorsList;
    }
}

