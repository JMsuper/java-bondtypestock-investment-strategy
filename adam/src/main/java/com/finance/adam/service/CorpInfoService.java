package com.finance.adam.service;

import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoListResponse;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoUpdateDTO;
import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.dto.OpenDartReportExtractedDTO;
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

import java.util.Comparator;
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
        log.info("Getting saved corporation info list for user: {}", userId);
        Account user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        List<SaveCorpInfo> saveCorpInfoList = saveCorpInfoRepository.findAllByAccount(user);
        log.debug("Found {} saved corporations for user", saveCorpInfoList.size());

        return saveCorpInfoList.stream().map(saveCorpInfo -> {
            return calculateSaveCorpInfoResponse(saveCorpInfo);
        }).toList();
    }

    public SaveCorpInfoListResponse calculateSaveCorpInfoResponse(SaveCorpInfo saveCorpInfo) {
        CorpInfo corpInfo = saveCorpInfo.getCorpInfo();
        log.debug("Processing corporation: {}", corpInfo.getCorpCode());

        List<FinanceInfo> financeInfoList = corpInfo.getFinanceInfos();
        StockPrice stockPrice = corpInfo.getStockPrice();

        List<OpenDartReportExtractedDTO> reportList = openDartAPI.getRecentReportListFive(corpInfo.getCorpCode())
                .stream()
                .map(OpenDartReportExtractedDTO::from)
                .sorted(Comparator.comparing(OpenDartReportExtractedDTO::getRceptDt).reversed())
                .toList();

        log.debug("Retrieved {} recent reports for corporation {}", reportList.size(), corpInfo.getCorpCode());

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
        log.debug("Calculated BPS for corporation {}: {}", corpInfo.getCorpCode(), bps);

        // 10년 후 예상 ROE 가 없는 경우, targetPrice 와 expectedRate 계산 불가능
        if(saveCorpInfo.getAfterTenYearsAverageROE() == null){
            log.debug("No 10-year ROE projection available for corporation {}", corpInfo.getCorpCode());
            return SaveCorpInfoListResponse.fromSaveCorpInfo(saveCorpInfo,reportList,bps);
        }

        long afterTenYearsBPS = financeCalculator.calculateAfterTenYearBPS(
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
        log.debug("Calculated projections for corporation {}: 10Y BPS={}, expectedRate={}, targetPrice={}",
                corpInfo.getCorpCode(), afterTenYearsBPS, expectedRate, targetPrice);

        return SaveCorpInfoListResponse.fromSaveCorpInfo(saveCorpInfo, reportList,bps, targetPrice, expectedRate);
    }

    public void saveCorpInfoListWithUser(String corpCode, String userId) throws CustomException{
        log.info("Saving corporation {} for user {}", corpCode, userId);
        CorpInfo corpInfo = corpRepository.findById(corpCode)
                .orElseThrow(() -> new CustomException(ErrorCode.CORP_NOT_FOUND));

        Account user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        int count = saveCorpInfoRepository.countByAccountId(userId);
        log.debug("Current saved corporation count for user {}: {}", userId, count);
        if (count >= 10) {
            log.warn("User {} attempted to exceed maximum saved corporation limit", userId);
            throw new CustomException(ErrorCode.SAVE_CORP_MAX_COUNT);
        }

        saveCorpInfoRepository.findByCorpInfoCorpCodeAndAccountId(corpCode, userId)
                .ifPresent(saveCorpInfo -> {
                    log.warn("User {} attempted to save duplicate corporation {}", userId, corpCode);
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
            log.debug("Using 3-year average ROE {} as 10-year projection for corporation {}", afterTenYearsAverageROE, corpCode);
            saveCorpInfo = SaveCorpInfo.builder()
                    .corpInfo(corpInfo)
                    .account(user)
                    .targetRate(targetRate)
                    .afterTenYearsAverageROE(afterTenYearsAverageROE)
                    .build();
        }else{
            log.debug("No 3-year ROE available for corporation {}, skipping 10-year projection", corpCode);
            saveCorpInfo = SaveCorpInfo.builder()
                    .corpInfo(corpInfo)
                    .account(user)
                    .targetRate(targetRate)
                    .build();
        }

        saveCorpInfoRepository.saveAndFlush(saveCorpInfo);
        log.info("Successfully saved corporation {} for user {}", corpCode, userId);
    }

    public void updateSaveCorpInfo(String corpCode,
                                   SaveCorpInfoUpdateDTO saveCorpInfoUpdateDTO,
                                   String userId) throws CustomException{
        log.info("Updating saved corporation {} for user {}", corpCode, userId);
        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByCorpInfoCorpCodeAndAccountId(corpCode, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));

        float newTargetRate = saveCorpInfoUpdateDTO.getTargetRate() / 100f;
        float newExpectedROE = saveCorpInfoUpdateDTO.getExpectedROE();
        log.debug("Updating corporation {} with new target rate: {}, new expected ROE: {}", 
                corpCode, newTargetRate, newExpectedROE);

        saveCorpInfo.setTargetRate(newTargetRate);
        saveCorpInfo.setAfterTenYearsAverageROE(newExpectedROE);

        saveCorpInfoRepository.saveAndFlush(saveCorpInfo);
        log.info("Successfully updated corporation {} for user {}", corpCode, userId);
    }

    public void deleteCorpInfoListWithUser(String corpCode,
                                           String userId) throws CustomException{
        log.info("Deleting saved corporation {} for user {}", corpCode, userId);
        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByCorpInfoCorpCodeAndAccountId(corpCode, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));

        saveCorpInfoRepository.delete(saveCorpInfo);
        log.info("Successfully deleted corporation {} for user {}", corpCode, userId);
    }

}
