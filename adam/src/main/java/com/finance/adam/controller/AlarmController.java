package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.pricealarm.dto.CreatePriceAlarmDTO;
import com.finance.adam.repository.pricealarm.dto.PriceAlarmDTO;
import com.finance.adam.repository.reportalarm.dto.ReportAlarmListDTO;
import com.finance.adam.repository.reportalarm.dto.UpdateReportAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.CreateTargetPriceAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.TargetPriceAlarmDTO;
import com.finance.adam.service.AlarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    /*
     * 주가 알람 API
     */

    @GetMapping("/target-prices")
    public List<TargetPriceAlarmDTO> getTargetPriceAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Getting target price alarms for user: {}", userId);
        return alarmService.getTargetPriceAlarm(userId);
    }

    @GetMapping("/prices")
    public List<PriceAlarmDTO> getPriceAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Getting price alarms for user: {}", userId);
        return alarmService.getPriceAlarm(userId);
    }

    @PostMapping("/target-prices")
    public void createTargetPriceAlarm(@RequestBody @Valid CreateTargetPriceAlarmDTO createTargetPriceAlarmDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Creating target price alarm for user: {}", userId);
        log.debug("Target price alarm details: {}", createTargetPriceAlarmDTO);

        alarmService.createTargetPriceAlarm(userId, createTargetPriceAlarmDTO);
    }

    @PostMapping("/prices")
    public void createPriceAlarm(@RequestBody @Valid CreatePriceAlarmDTO createPriceAlarmDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Creating price alarm for user: {}", userId);
        log.debug("Price alarm details: {}", createPriceAlarmDTO);

        alarmService.createPriceAlarm(userId, createPriceAlarmDTO);
    }

    @PutMapping("/target-prices/{targetPriceId}")
    public void updateTargetPriceAlarmStatus(@PathVariable() Long targetPriceId, @RequestParam() boolean active, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Updating target price alarm status - userId: {}, alarmId: {}, active: {}", userId, targetPriceId, active);

        alarmService.updateTargetPriceAlarmStatus(userId, targetPriceId, active);
    }

    @PutMapping("/prices/{priceId}")
    public void updatePriceAlarmStatus(@PathVariable() Long priceId, @RequestParam() boolean active, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Updating price alarm status - userId: {}, alarmId: {}, active: {}", userId, priceId, active);

        alarmService.updatePriceAlarmStatus(userId, priceId, active);
    }

    @DeleteMapping("/target-prices/{targetPriceId}")
    public void deleteTargetPriceAlarm(@PathVariable() Long targetPriceId, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Deleting target price alarm - userId: {}, alarmId: {}", userId, targetPriceId);

        alarmService.deleteTargetPriceAlarm(userId, targetPriceId);
    }

    @DeleteMapping("/prices/{priceId}")
    public void deletePriceAlarm(@PathVariable() Long priceId, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Deleting price alarm - userId: {}, alarmId: {}", userId, priceId);

        alarmService.deletePriceAlarm(userId, priceId);
    }

    /*
     * 공시 알람 API
     */

    @GetMapping("/reports")
    public List<ReportAlarmListDTO> getReportAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Getting report alarms for user: {}", userId);
        return alarmService.getReportAlarmList(userId);
    }

    @PutMapping("/reports/{reportId}")
    public ReportAlarmListDTO updateReportAlarm(
            @PathVariable() Long reportId,
            @RequestBody @Valid UpdateReportAlarmDTO dto,
            @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Updating report alarm - userId: {}, reportId: {}", userId, reportId);
        log.debug("Report alarm update details: {}", dto);
        
        ReportAlarmListDTO reportAlarmListDTO = alarmService.updateReportAlarm(userId, dto);
        return reportAlarmListDTO;
    }
}
