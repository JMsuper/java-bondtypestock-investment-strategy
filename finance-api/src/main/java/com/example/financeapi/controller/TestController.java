package com.example.financeapi.controller;

import com.example.financeapi.util.FinanceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private FinanceAPI financeAPI;

    @GetMapping("/hello")
     String test() throws SSLException {
        System.out.println(financeAPI.getStockPrice("005930"));
        return "hello";
    }
}
