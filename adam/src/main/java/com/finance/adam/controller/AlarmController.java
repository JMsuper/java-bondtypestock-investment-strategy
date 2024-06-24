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
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return alarmService.getTargetPriceAlarm(userId);
    }

    @GetMapping("/prices")
    public List<PriceAlarmDTO> getPriceAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        return alarmService.getPriceAlarm(userId);
    }

    @PostMapping("/target-prices")
    public void createTargetPriceAlarm(@RequestBody @Valid CreateTargetPriceAlarmDTO createTargetPriceAlarmDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.createTargetPriceAlarm(userId, createTargetPriceAlarmDTO);
    }

    @PostMapping("/prices")
    public void createPriceAlarm(@RequestBody @Valid CreatePriceAlarmDTO createPriceAlarmDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.createPriceAlarm(userId, createPriceAlarmDTO);
    }

    @PutMapping("/target-prices/{targetPriceId}")
    public void updateTargetPriceAlarmStatus(@PathVariable() Long targetPriceId, @RequestParam() boolean active, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.updateTargetPriceAlarmStatus(userId, targetPriceId, active);
    }

    @PutMapping("/prices/{priceId}")
    public void updatePriceAlarmStatus(@PathVariable() Long priceId, @RequestParam() boolean active, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.updatePriceAlarmStatus(userId, priceId, active);
    }



    @DeleteMapping("/target-prices/{targetPriceId}")
    public void deleteTargetPriceAlarm(@PathVariable() Long targetPriceId, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.deleteTargetPriceAlarm(userId, targetPriceId);
    }

    @DeleteMapping("/prices/{priceId}")
    public void deletePriceAlarm(@PathVariable() Long priceId, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.deletePriceAlarm(userId, priceId);
    }

    /*
     * 공시 알람 API
     */

    @GetMapping("/reports")
    public List<ReportAlarmListDTO> getReportAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        return alarmService.getReportAlarmList(userId);
    }

    @PutMapping("/reports/{reportId}")
    public ReportAlarmListDTO updateReportAlarm(
            @PathVariable() Long reportId,
            @RequestBody @Valid UpdateReportAlarmDTO dto,
            @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        ReportAlarmListDTO reportAlarmListDTO = alarmService.updateReportAlarm(userId, dto);
        return reportAlarmListDTO;
    }
}
