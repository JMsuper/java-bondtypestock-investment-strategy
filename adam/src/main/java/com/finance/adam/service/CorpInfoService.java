package com.finance.adam.service;

import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoListResponse;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoUpdateDTO;
import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.vo.OpenDartReportExtractedDTO;
import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.financeinfo.domain.FinanceInfo;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.stockprice.domain.StockPrice;
import com.finance.adam.util.FinanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorpInfoService {

    @Value("${finance.setting.target-rate}")
    private float DEFAULT_TARGET_RATE;

    private final CorpRepository corpRepository;
    private final UserRepository userRepository;
    private final SaveCorpInfoRepository saveCorpInfoRepository;
    private final FinanceDataService financeDataService;
    private final FinanceCalculator financeCalculator;
    private final OpenDartAPI openDartAPI;


    public List<SaveCorpInfoListResponse> getSaveCorpInfoList(String userId) {
        Account user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        List<SaveCorpInfo> saveCorpInfoList = saveCorpInfoRepository.findAllByAccount(user);

        List<SaveCorpInfoListResponse> result = saveCorpInfoList.stream().map(saveCorpInfo -> {
            CorpInfo corpInfo = saveCorpInfo.getCorpInfo();

            List<FinanceInfo> financeInfoList = corpInfo.getFinanceInfos();
            StockPrice stockPrice = corpInfo.getStockPrice();

            List<OpenDartReportExtractedDTO> reportList = openDartAPI.getRecentReportList(corpInfo.getCorpCode(), 5)
                    .stream().map((report) -> OpenDartReportExtractedDTO.from(report))
                    .toList();

            FinanceInfo financeInfo = financeInfoList.stream()
                    .filter( f -> f.getYear() == 2023)
                    .findFirst()
                    .orElse(null);

            // 재무정보가 없는 경우
            if(financeInfo == null){
                log.error("재무정보가 존재하지 않는 기업이 저장되어 있어서는 안됩니다. corpCode: {}", corpInfo.getCorpCode());
                return SaveCorpInfoListResponse.fromSaveCorpInfo(saveCorpInfo, reportList);
            }

            Long bps = financeDataService.getBPS(financeInfo, stockPrice);

            // 10년 후 예상 ROE 가 없는 경우, targetPrice 와 expectedRate 계산 불가능
            if(saveCorpInfo.getAfterTenYearsAverageROE() == null){
                return SaveCorpInfoListResponse.fromSaveCorpInfo(saveCorpInfo,reportList,bps);
            }

            Long afterTenYearsBPS = financeCalculator.calculateAfterTenYearBPS(
                    bps,
                    saveCorpInfo.getAfterTenYearsAverageROE());
            float expectedRate = financeCalculator.calculateExpectedRate(
                    stockPrice.getOpeningPrice(),
                    afterTenYearsBPS
            );
            int targetPrice = financeCalculator.calculateTargetPrice(
                    saveCorpInfo.getTargetRate(),
                    afterTenYearsBPS
            );

            return SaveCorpInfoListResponse.fromSaveCorpInfo(saveCorpInfo, reportList,bps, targetPrice, expectedRate);

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

        float targetRate = DEFAULT_TARGET_RATE;
        float afterTenYearsAverageROE = 0.0f;

        // 과거 3년 ROE 평균을 10년 후 예상 ROE 로 설정
        // 단, 3년 ROE 를 계산할 수 없는 경우 10년 후 ROE 는 null 로 설정
        Optional<Float> expectedROE = financeDataService.getThreeYearAverageROE(corpInfo.getFinanceInfos());
        SaveCorpInfo saveCorpInfo;
        if(expectedROE.isPresent()) {
            afterTenYearsAverageROE = expectedROE.get();
            saveCorpInfo = SaveCorpInfo.builder()
                    .corpInfo(corpInfo)
                    .account(user)
                    .targetRate(targetRate)
                    .afterTenYearsAverageROE(afterTenYearsAverageROE)
                    .build();
        }else{
            saveCorpInfo = SaveCorpInfo.builder()
                    .corpInfo(corpInfo)
                    .account(user)
                    .targetRate(targetRate)
                    .build();
        }

        saveCorpInfoRepository.saveAndFlush(saveCorpInfo);
    }

    public void updateSaveCorpInfo(String corpCode,SaveCorpInfoUpdateDTO saveCorpInfoUpdateDTO, String userId) {
        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByCorpInfoCorpCodeAndAccountId(corpCode, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));

        saveCorpInfo.setTargetRate(saveCorpInfoUpdateDTO.getTargetRate() / 100f);
        saveCorpInfo.setAfterTenYearsAverageROE(saveCorpInfoUpdateDTO.getExpectedROE());

        saveCorpInfoRepository.saveAndFlush(saveCorpInfo);
    }

    public void deleteCorpInfoListWithUser(String corpCode, String userId) {
        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByCorpInfoCorpCodeAndAccountId(corpCode, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));

        saveCorpInfoRepository.delete(saveCorpInfo);
    }

}
