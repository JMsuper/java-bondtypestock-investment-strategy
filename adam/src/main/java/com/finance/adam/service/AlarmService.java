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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final TargetPriceAlarmRepository targetPriceAlarmRepository;
    private final PriceAlarmRepository priceAlarmRepository;
    private final SaveCorpInfoRepository saveCorpInfoRepository;
    private final UserRepository userRepository;
    private final ReportAlarmRepository reportAlarmRepository;

    public List<TargetPriceAlarmDTO> getTargetPriceAlarm(String userId) {
        log.info("Getting target price alarms for user: {}", userId);
        Account account = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<TargetPriceAlarm> targetPriceAlarmList = targetPriceAlarmRepository.findAllByAccount(account);
        log.debug("Found {} target price alarms", targetPriceAlarmList.size());
        return targetPriceAlarmList.stream().map(
                targetPriceAlarm -> TargetPriceAlarmDTO.from(targetPriceAlarm, targetPriceAlarm.getSaveCorpInfo().getCorpInfo())
        ).toList();
    }

    public List<PriceAlarmDTO> getPriceAlarm(String userId) {
        log.info("Getting price alarms for user: {}", userId);
        Account account = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<PriceAlarm> priceAlarmList = priceAlarmRepository.findAllByAccount(account);
        log.debug("Found {} price alarms", priceAlarmList.size());
        return priceAlarmList.stream().map(
                priceAlarm -> PriceAlarmDTO.from(priceAlarm, priceAlarm.getSaveCorpInfo().getCorpInfo())
        ).toList();
    }

    public List<ReportAlarmListDTO> getReportAlarmList(String userId) {
        log.info("Getting report alarms for user: {}", userId);
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
        log.debug("Found {} report alarms", reportAlarmListDTOList.size());
        return reportAlarmListDTOList;
    }

    public void createTargetPriceAlarm(String userId, CreateTargetPriceAlarmDTO createTargetPriceDTO) {
        log.info("Creating target price alarm for user: {}", userId);
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
        log.info("Successfully created target price alarm for stock: {}", saveCorpInfo.getCorpInfo().getName());
    }

    public void createPriceAlarm(String userId, CreatePriceAlarmDTO createPriceAlarmDTO) {
        log.info("Creating price alarm for user: {}", userId);
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
        log.info("Successfully created price alarm for stock: {}", saveCorpInfo.getCorpInfo().getName());
    }

    public void updateTargetPriceAlarmStatus(String userId, Long targetPriceAlarmId, boolean active) {
        log.info("Updating target price alarm status - userId: {}, alarmId: {}, active: {}", userId, targetPriceAlarmId, active);
        TargetPriceAlarm targetPriceAlarm = targetPriceAlarmRepository.findById(targetPriceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        if(!targetPriceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)){
            log.warn("User {} attempted to update unauthorized target price alarm {}", userId, targetPriceAlarmId);
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        targetPriceAlarm.setActive(active);
        targetPriceAlarmRepository.save(targetPriceAlarm);
        log.info("Successfully updated target price alarm status");
    }

    public void updatePriceAlarmStatus(String userId, Long priceAlarmId, boolean active) {
        log.info("Updating price alarm status - userId: {}, alarmId: {}, active: {}", userId, priceAlarmId, active);
        PriceAlarm priceAlarm = priceAlarmRepository.findById(priceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        if(!priceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)){
            log.warn("User {} attempted to update unauthorized price alarm {}", userId, priceAlarmId);
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        priceAlarm.setActive(active);
        priceAlarmRepository.save(priceAlarm);
        log.info("Successfully updated price alarm status");
    }

    public ReportAlarmListDTO updateReportAlarm(String userId, UpdateReportAlarmDTO dto) {
        log.info("Updating report alarm - userId: {}, saveCorpInfoId: {}", userId, dto.getSaveCorpInfoId());
        Long saveCorpInfoId = dto.getSaveCorpInfoId();
        List<ReportType> updateReportTypeList = dto.getReportTypeList();
        boolean active = dto.getActive();

        SaveCorpInfo saveCorpInfo = saveCorpInfoRepository.findByIdAndAccountId(saveCorpInfoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SAVE_CORP_INFO_NOT_FOUND));
        
        log.debug("Deleting existing report alarms for saveCorpInfoId: {}", saveCorpInfoId);
        reportAlarmRepository.deleteAllBySaveCorpInfo(saveCorpInfo);

        List<ReportAlarm> reportAlarmList = new ArrayList<>();
        for (ReportType reportType : updateReportTypeList) {
            ReportAlarm reportAlarm = ReportAlarm.builder()
                    .saveCorpInfo(saveCorpInfo)
                    .reportType(reportType)
                    .active(active)
                    .build();
            reportAlarmList.add(reportAlarm);
        }

        List<ReportAlarm> savedReportAlarm = reportAlarmRepository.saveAll(reportAlarmList);
        log.info("Successfully updated {} report alarms", savedReportAlarm.size());

        return ReportAlarmListDTO.builder()
                .stockName(saveCorpInfo.getCorpInfo().getName())
                .saveCorpInfoId(saveCorpInfoId)
                .reportTypeList(savedReportAlarm.stream().map(ReportAlarm::getReportType).toList())
                .active(active)
                .build();
    }

    public void deleteTargetPriceAlarm(String userId, Long targetPriceAlarmId) {
        log.info("Deleting target price alarm - userId: {}, alarmId: {}", userId, targetPriceAlarmId);
        TargetPriceAlarm targetPriceAlarm = targetPriceAlarmRepository.findById(targetPriceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        if(!targetPriceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)){
            log.warn("User {} attempted to delete unauthorized target price alarm {}", userId, targetPriceAlarmId);
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        targetPriceAlarmRepository.delete(targetPriceAlarm);
        log.info("Successfully deleted target price alarm");
    }

    public void deletePriceAlarm(String userId, Long priceAlarmId) {
        log.info("Deleting price alarm - userId: {}, alarmId: {}", userId, priceAlarmId);
        PriceAlarm priceAlarm = priceAlarmRepository.findById(priceAlarmId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALARM_NOT_FOUND));

        if (!priceAlarm.getSaveCorpInfo().getAccount().getId().equals(userId)) {
            log.warn("User {} attempted to delete unauthorized price alarm {}", userId, priceAlarmId);
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }

        priceAlarmRepository.delete(priceAlarm);
        log.info("Successfully deleted price alarm");
    }
}
