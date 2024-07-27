package com.finance.adam.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class CustomModelMapperTest {

    @Test
    void main(){
        LinkedHashMap<String, String> snakeCaseMap = new LinkedHashMap<>();
        snakeCaseMap.put("first_name", "John");
        snakeCaseMap.put("last_name", "Doe");
        User2 user2 = new User2();

        CustomModelMapper.convert(snakeCaseMap,user2,User2.class);
        System.out.println(user2);
    }

}

class User2{
    String firstName;
    String lastName;

    @Override
    public String toString() {
        return firstName + " " + lastName;
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
}