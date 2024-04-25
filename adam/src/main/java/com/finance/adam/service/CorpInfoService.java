package com.finance.adam.service;

import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.repository.CorpRepository;
import com.finance.adam.repository.SaveCorpInfoRepository;
import com.finance.adam.repository.UserRepository;
import com.finance.adam.repository.domain.Account;
import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.SaveCorpInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CorpInfoService {

    private final CorpRepository corpRepository;
    private final UserRepository userRepository;
    private final SaveCorpInfoRepository saveCorpInfoRepository;

    public List<KrxCorpListResponse> getSaveCorpInfoList(String userId) {
        Account user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        List<SaveCorpInfo> saveCorpInfoList = saveCorpInfoRepository.findAllByAccount(user);

        List<KrxCorpListResponse> result = saveCorpInfoList.stream().map(saveCorpInfo -> {
            CorpInfo corpInfo = saveCorpInfo.getCorpInfo();
            return KrxCorpListResponse.fromCorpInfo(corpInfo);
        }).toList();

        return result;
    }

    public void saveCorpInfoListWithUser(String corpCode, String userId) {
        CorpInfo corpInfo = corpRepository.findById(corpCode)
                .orElseThrow(() -> new CustomException(ErrorCode.CORP_NOT_FOUND));

        Account user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        int count = saveCorpInfoRepository.countByAccountId(userId);
        if (count >= 10) {
            throw new CustomException(ErrorCode.SAVE_CORP_MAX_COUNT);
        }

        saveCorpInfoRepository.findByCorpInfoCorpCodeAndAccountId(corpCode, userId)
                .ifPresent(saveCorpInfo -> {
                    throw new CustomException(ErrorCode.SAVE_CORP_INFO_DUPLICATED);
                });

        SaveCorpInfo saveCorpInfo = SaveCorpInfo.builder()
                .corpInfo(corpInfo)
                .account(user)
                .build();

        saveCorpInfoRepository.saveAndFlush(saveCorpInfo);
    }

    public void deleteCorpInfoListWithUser(String corpCode, String userId) {
        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByCorpInfoCorpCodeAndAccountId(corpCode, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));

        saveCorpInfoRepository.delete(saveCorpInfo);
    }


}
