package com.finance.adam.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFnInfoName {

    @Test
    void test(){
        String enumName = FnInfoName.NON_CURRENT_ASSETS.toString();
        assertEquals("NON_CURRENT_ASSETS",enumName);
    }
}
