package com.finance.adam.service;

import com.finance.adam.exception.CustomException;
import com.finance.adam.exception.ErrorCode;
import com.finance.adam.repository.account.UserRepository;
import com.finance.adam.repository.account.domain.Account;
import com.finance.adam.repository.pricealarm.PriceAlarmRepository;
import com.finance.adam.repository.pricealarm.domain.PriceAlarm;
import com.finance.adam.repository.pricealarm.dto.CreatePriceAlarmDTO;
import com.finance.adam.repository.pricealarm.dto.PriceAlarmDTO;
import com.finance.adam.repository.reportalarm.ReportAlarmRepository;
import com.finance.adam.repository.reportalarm.domain.ReportAlarm;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.reportalarm.dto.ReportAlarmListDTO;
import com.finance.adam.repository.reportalarm.dto.UpdateReportAlarmDTO;
import com.finance.adam.repository.savecorpinfo.SaveCorpInfoRepository;
import com.finance.adam.repository.savecorpinfo.domain.SaveCorpInfo;
import com.finance.adam.repository.targetpricealarm.TargetPriceAlarmRepository;
import com.finance.adam.repository.targetpricealarm.domain.TargetPriceAlarm;
import com.finance.adam.repository.targetpricealarm.dto.CreateTargetPriceAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.TargetPriceAlarmDTO;
import com.finance.adam.util.AlarmAddedInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final TargetPriceAlarmRepository targetPriceAlarmRepository;
    private final PriceAlarmRepository priceAlarmRepository;
    private final SaveCorpInfoRepository saveCorpInfoRepository;
    private final UserRepository userRepository;
    private final ReportAlarmRepository reportAlarmRepository;

    public List<TargetPriceAlarmDTO> getTargetPriceAlarm(String userId) {
        Account account = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<TargetPriceAlarm> targetPriceAlarmList = targetPriceAlarmRepository.findAllByAccount(account);
        return targetPriceAlarmList.stream().map(
                targetPriceAlarm -> TargetPriceAlarmDTO.from(targetPriceAlarm, targetPriceAlarm.getSaveCorpInfo().getCorpInfo())
        ).toList();
    }

    public List<PriceAlarmDTO> getPriceAlarm(String userId) {
        Account account = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<PriceAlarm> priceAlarmList = priceAlarmRepository.findAllByAccount(account);
        return priceAlarmList.stream().map(
                priceAlarm -> PriceAlarmDTO.from(priceAlarm, priceAlarm.getSaveCorpInfo().getCorpInfo())
        ).toList();
    }

    public List<ReportAlarmListDTO> getReportAlarmList(String userId) {
        Account account = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<SaveCorpInfo> saveCorpInfoList = saveCorpInfoRepository.findAllByAccount(account);
        List<ReportAlarmListDTO> reportAlarmListDTOList = new ArrayList<>();

        for (SaveCorpInfo saveCorpInfo : saveCorpInfoList) {

            List<ReportAlarm> reportAlarmList = reportAlarmRepository.findAllBySaveCorpInfo(saveCorpInfo);
            reportAlarmListDTOList.add(ReportAlarmListDTO.builder()
                    .stockName(saveCorpInfo.getCorpInfo().getName())
                    .saveCorpInfoId(saveCorpInfo.getId())
                    .reportTypeList(reportAlarmList.stream().map(ReportAlarm::getReportType).toList())
                    .active(reportAlarmList.size() > 0 ? reportAlarmList.get(0).isActive() : false)
                    .build());
        }
        reportAlarmListDTOList.sort(Comparator.comparing(ReportAlarmListDTO::getSaveCorpInfoId));
        return reportAlarmListDTOList;
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

    public void createPriceAlarm(String userId, CreatePriceAlarmDTO createPriceAlarmDTO) {
        Long saveCorpInfoId = createPriceAlarmDTO.getSaveCorpInfoId();

        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByIdAndAccountId(saveCorpInfoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));

        PriceAlarm priceAlarm = PriceAlarm.builder()
                .saveCorpInfo(saveCorpInfo)
                .time(createPriceAlarmDTO.getTime())
                .build();
        priceAlarm.setWeekDayList(createPriceAlarmDTO.getWeekDayList());
        List<AlarmAddedInfo> alarmAddedInfoList = createPriceAlarmDTO.getInfoIndexList().stream()
                .map(AlarmAddedInfo::valueOfFrom)
                .toList();

        priceAlarm.setInfoIndexList(alarmAddedInfoList);
        priceAlarmRepository.save(priceAlarm);
    }

    public void updateTargetPriceAlarmStatus(String userId, Long targetPriceAlarmId, boolean active) {
        TargetPriceAlarm targetPriceAlarm = targetPriceAlarmRepository.findById(targetPriceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        if(!targetPriceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)){
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        targetPriceAlarm.setActive(active);
        targetPriceAlarmRepository.save(targetPriceAlarm);
    }

    public void updatePriceAlarmStatus(String userId, Long priceAlarmId, boolean active) {
        PriceAlarm priceAlarm = priceAlarmRepository.findById(priceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        if(!priceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)){
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        priceAlarm.setActive(active);
        priceAlarmRepository.save(priceAlarm);
    }

    public ReportAlarmListDTO updateReportAlarm(String userId, UpdateReportAlarmDTO dto) {
        Long saveCorpInfoId = dto.getSaveCorpInfoId();
        List<ReportType> updateReportTypeList = dto.getReportTypeList();
        boolean active = dto.getActive();

        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByIdAndAccountId(saveCorpInfoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));
        // 1. 기존 알람 삭제
        reportAlarmRepository.deleteAllBySaveCorpInfo(saveCorpInfo);

        // 2. 새로운 알람 추가
        List<ReportAlarm> reportAlarmList = new ArrayList<>();
        for (ReportType reportType : updateReportTypeList) {
            ReportAlarm reportAlarm = ReportAlarm.builder()
                    .saveCorpInfo(saveCorpInfo)
                    .reportType(reportType)
                    .active(active)
                    .build();
            reportAlarmList.add(reportAlarm);
        }

        // 3. 새로운 알람 저장
        List<ReportAlarm> savedReportAlarm = reportAlarmRepository.saveAll(reportAlarmList);

        // 4. DTO로 변환하여 반환
        return ReportAlarmListDTO.builder()
                .stockName(saveCorpInfo.getCorpInfo().getName())
                .saveCorpInfoId(saveCorpInfoId)
                .reportTypeList(savedReportAlarm.stream().map(ReportAlarm::getReportType).toList())
                .active(active)
                .build();
    }

    public void deleteTargetPriceAlarm(String userId, Long targetPriceAlarmId) {
        TargetPriceAlarm targetPriceAlarm = targetPriceAlarmRepository.findById(targetPriceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        if(!targetPriceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)){
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        targetPriceAlarmRepository.delete(targetPriceAlarm);
    }

    public void deletePriceAlarm(String userId, Long priceAlarmId) {
        PriceAlarm priceAlarm = priceAlarmRepository.findById(priceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        if (!priceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        priceAlarmRepository.delete(priceAlarm);
    }
}
