package com.finance.adam.datashuttle.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorpCode {

    public CorpCode(String c, String n){
        name = n;
        int addCount = 6 - c.length();
        String temp = c;
        for(int i = 0; i < addCount; i++){
            temp = "0" + temp;
        }
        code = temp;
    }

    private String code;
    private String name;
}
