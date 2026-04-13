package com.shopsphere.order.DTO;

public class UserDTO {

    private Long id;
    private String name;
    private String userName;
    private String email;
    private String address;
    private String gender;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserDTO(){}

    public UserDTO(String address, String email, String gender,
                   Long id, String name, String userName) {
        this.address = address;
        this.email = email;
        this.gender = gender;
        this.id = id;
        this.name = name;
        this.userName = userName;
    }
}
