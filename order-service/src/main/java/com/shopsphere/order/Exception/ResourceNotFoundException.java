package com.shopsphere.order.Exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String name){
        super(name);
    }
}
