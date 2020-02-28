package com.vedantu.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public  class AbstractFrontEndReq {
    // this is to find the validation errors in each request object

    // this method will be override in each request
    // that will return the list of errors
    // verify method calls this collectValidationErrors and verifies
    public List<String>  collectValidationErrors(){
        return new ArrayList<String>();
    }

    // to verify the if list of errors is empty or not
    public void verify() throws Exception{
        List<String> errorsList = this.collectValidationErrors();
        if(errorsList.size() > 0){
            throw new Exception("Errors in the request object." + errorsList.get(0));
        }
    }



}
