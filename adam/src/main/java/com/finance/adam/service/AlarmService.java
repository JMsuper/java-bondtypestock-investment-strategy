package com.finance.adam.service;

import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.targetpricealarm.TargetPriceAlarmRepository;
import com.finance.adam.repository.targetpricealarm.domain.TargetPriceAlarm;
import com.finance.adam.repository.targetpricealarm.dto.CreateTargetPriceAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.TargetPriceAlarmDTO;
import com.finance.adam.util.AlarmAddedInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final TargetPriceAlarmRepository targetPriceAlarmRepository;
    private final SaveCorpInfoRepository saveCorpInfoRepository;
    private final UserRepository userRepository;

    public List<TargetPriceAlarmDTO> getTargetPriceAlarm(String userId) {
        Account account = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<TargetPriceAlarm> targetPriceAlarmList = targetPriceAlarmRepository.findAllByAccount(account);
        return targetPriceAlarmList.stream().map(TargetPriceAlarmDTO::from).toList();
    }

    public void createTargetPriceAlarm(String userId, CreateTargetPriceAlarmDTO createTargetPriceDTO) {
        Long saveCorpInfoId = createTargetPriceDTO.getSaveCorpInfoId();

        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByIdAndAccountId(saveCorpInfoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));

        boolean isBuy = TargetPriceAlarm.getValueFrom(createTargetPriceDTO.getBuyOrSell());

        TargetPriceAlarm targetPriceAlarm = TargetPriceAlarm.builder()
                .saveCorpInfo(saveCorpInfo)
                .isBuy(isBuy)
                .targetPrice(createTargetPriceDTO.getTargetPrice())
                .build();

        List<AlarmAddedInfo> alarmAddedInfoList = createTargetPriceDTO.getInfoIndexList().stream()
                .map(AlarmAddedInfo::valueOfFrom)
                .toList();

        targetPriceAlarm.setInfoIndexList(alarmAddedInfoList);

        targetPriceAlarmRepository.save(targetPriceAlarm);
    }

    public void deleteTargetPriceAlarm(String userId, Long targetPriceAlarmId) {
        TargetPriceAlarm targetPriceAlarm = targetPriceAlarmRepository.findById(targetPriceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.TARGET_PRICE_ALARM_NOT_FOUND));

        if(!targetPriceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)){
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        targetPriceAlarmRepository.delete(targetPriceAlarm);
    }

    public void createPriceAlarm() {

    }
}
