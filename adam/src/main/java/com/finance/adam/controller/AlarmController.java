package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.pricealarm.dto.CreatePriceAlarmDTO;
import com.finance.adam.repository.pricealarm.dto.DeletePriceAlarmDTO;
import com.finance.adam.repository.pricealarm.dto.PriceAlarmDTO;
import com.finance.adam.repository.pricealarm.dto.UpdatePriceAlarmDTO;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.repository.reportalarm.dto.ReportAlarmListDTO;
import com.finance.adam.repository.reportalarm.dto.UpdateReportAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.CreateTargetPriceAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.DeleteTargetPriceAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.TargetPriceAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.UpdateTargetPriceAlarmDTO;
import com.finance.adam.service.AlarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
public class AlarmController {

    private final AlarmService alarmService;

    /*
     * 주가 알람 API
     */

    @GetMapping("/target-price")
    public List<TargetPriceAlarmDTO> getTargetPriceAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        return alarmService.getTargetPriceAlarm(userId);
    }

    @GetMapping("/price")
    public List<PriceAlarmDTO> getPriceAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        return alarmService.getPriceAlarm(userId);
    }

    @PostMapping("/target-price")
    public void createTargetPriceAlarm(@RequestBody @Valid CreateTargetPriceAlarmDTO createTargetPriceAlarmDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.createTargetPriceAlarm(userId, createTargetPriceAlarmDTO);
    }

    @PutMapping("/target-price/status")
    public void updateTargetPriceAlarmStatus(@RequestBody @Valid UpdateTargetPriceAlarmDTO dto, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        Long targetPriceAlarmId = dto.getTargetPriceAlarmId();
        boolean active = dto.isActive();

        alarmService.updateTargetPriceAlarmStatus(userId, targetPriceAlarmId, active);
    }

    @PutMapping("/price/status")
    public void updatePriceAlarmStatus(@RequestBody @Valid UpdatePriceAlarmDTO dto, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        Long priceAlarmId = dto.getPriceAlarmId();
        boolean active = dto.isActive();

        alarmService.updatePriceAlarmStatus(userId, priceAlarmId, active);
    }

    @PostMapping("/price")
    public void createPriceAlarm(@RequestBody @Valid CreatePriceAlarmDTO createPriceAlarmDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.createPriceAlarm(userId, createPriceAlarmDTO);
    }

    @DeleteMapping("/target-price")
    public void deleteTargetPriceAlarm(@RequestBody @Valid DeleteTargetPriceAlarmDTO dto, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        Long targetPriceAlarmId = dto.getTargetPriceAlarmId();

        alarmService.deleteTargetPriceAlarm(userId, targetPriceAlarmId);
    }

    @DeleteMapping("/price")
    public void deletePriceAlarm(@RequestBody @Valid DeletePriceAlarmDTO dto, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        Long priceAlarmId = dto.getPriceAlarmId();

        alarmService.deletePriceAlarm(userId, priceAlarmId);
    }

    /*
     * 공시 알람 API
     */

    @GetMapping("/report")
    public List<ReportAlarmListDTO> getReportAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        return alarmService.getReportAlarmList(userId);
    }

    @PutMapping("/report")
    public ReportAlarmListDTO updateReportAlarm(@RequestBody @Valid UpdateReportAlarmDTO dto, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        ReportAlarmListDTO reportAlarmListDTO = alarmService.updateReportAlarm(userId, dto);
        return reportAlarmListDTO;
    }
}
