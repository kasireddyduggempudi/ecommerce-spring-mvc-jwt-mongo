package com.vedantu.models;

import com.vedantu.enums.UserTypeEnum;
import com.vedantu.requests.Address;
import com.vedantu.requests.RegisterRequest;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;

@Document(collection = "users")
public class UserModel extends  AbstractMongoEntity {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String email;
    private UserTypeEnum userType;
    private Long userAmount; // useful for customer to buy products. generally will update by admin by entering id and amount to customer
    private String password;
    private Address address;

    public UserModel(){
        super();
    }

    public UserModel(RegisterRequest userReq){
        // here we gets the verified request object => means mandatory fields are already verified
        // here we take request from manager, to convert registerRequest to userModel while adding user
        // But userAmount will be default set to 0. Only admin can add amount to a customer. will done in updateUserAmount by admin
        this.firstName = userReq.getFirstName();
        if(userReq.getLastName() != null && userReq.getLastName() != ""){
            this.lastName = userReq.getLastName();
            this.fullName = this.firstName + " " + this.lastName;
        }else{
            this.fullName = this.firstName;
        }
        this.phoneNumber = userReq.getPhoneNumber();
        this.email = userReq.getEmail();
        this.userType = userReq.getUserType();
        this.password = userReq.getPassword();
        //this.userAmount = userReq.getUserAmount();
        this.userAmount  = 0L; // Amount will be only added by admin via updating not by user
        Address addr = new Address();
        addr.setCity(userReq.getCity());
        addr.setPincode(userReq.getPincode());
        if(userReq.getState() != null && userReq.getState() != ""){
            addr.setState(userReq.getState());
        }
        if(userReq.getCountry() != null && userReq.getCountry() != ""){
            addr.setCountry(userReq.getCountry());
        }
        this.setAddress(addr);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Long getUserAmount(){ return userAmount;}

    public void setUserAmount(Long userAmount){ this.userAmount = userAmount;}

    public String getPassword(){
        return  password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    // setting default createdBy and createdTime
    public void setDefaultEntityProperties(Long createdBy){
        // we are extending AbstractMongoEntity
        // so we can access methods, but not properties since private

        // basically this is called before saving or updating
        // so we need to check if this object has already id
        // if id is there, just set the lastUpdatedBy and lastUpdatedTime

        if(this.getCreatedTime() == null){
            // means first time. this object saving
            this.setCreatedById(createdBy);
            this.setCreatedTime(System.currentTimeMillis());
        }
        // setting lastUpdatedTime and lastUpdatedBy
        this.setLastUpdatedTime(System.currentTimeMillis());
        this.setLastUpdatedById(createdBy);
    }
}
