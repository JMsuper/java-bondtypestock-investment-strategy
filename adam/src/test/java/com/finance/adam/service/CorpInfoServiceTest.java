package com.finance.adam.service;

import com.finance.adam.openapi.dart.dto.OpenDartReportExtractedDTO;
import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@SpringBootTest
@ActiveProfiles("api")
@Transactional
class CorpInfoServiceTest {

    @Autowired
    CorpInfoService corpInfoService;

    @Autowired
    SaveCorpInfoRepository saveCorpInfoRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void getSaveCorpInfoList() {
        // given
        List<Account> list = userRepository.findByEmail("tjsqls2067@naver.com");
        Account account = list.get(2);
        String userId = account.getId();

        // when
        List<SaveCorpInfoListResponse> saveCorpInfoListResponses = corpInfoService.getSaveCorpInfoList(userId);

        // then
        for(SaveCorpInfoListResponse response : saveCorpInfoListResponses){
            List<OpenDartReportExtractedDTO> reportList = response.getReportList();
            
            boolean isSorted = true;
            for (int i = 0; i < reportList.size() - 1; i++) {
                if (reportList.get(i).getRceptDt().compareTo(reportList.get(i + 1).getRceptDt()) < 0) {
                    isSorted = false;
                    break;
                }
            }
            assertTrue(isSorted);
        }
    }
}