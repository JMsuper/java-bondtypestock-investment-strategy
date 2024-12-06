package com.finance.adam.repository;

import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestCorpRepository {

    @Autowired
    private CorpRepository corpRepository;

//    @Test
//    void test(){
//        List<CorpInfo> corpInfoList = corpRepository.findAllWithStockPrice();
//        System.out.println(corpInfoList);
//    }
}
