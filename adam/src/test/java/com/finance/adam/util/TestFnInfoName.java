package com.finance.adam.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFnInfoName {

    @Test
    void test(){
        String enumName = FnInfoName.nonCurrentAsset.name();
        assertEquals("NON_CURRENT_ASSETS",enumName);
    }
}
