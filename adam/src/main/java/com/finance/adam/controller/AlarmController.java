package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.targetpricealarm.dto.CreateTargetPriceAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.DeleteTargetPriceAlarmDTO;
import com.finance.adam.repository.targetpricealarm.dto.TargetPriceAlarmDTO;
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

    @GetMapping("/target-price")
    public List<TargetPriceAlarmDTO> getTargetPriceAlarm(@AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        return alarmService.getTargetPriceAlarm(userId);
    }

    @GetMapping("/price")
    public void getPriceAlarm(){

    }

    @PostMapping("/target-price")
    public void createTargetPriceAlarm(@RequestBody @Valid CreateTargetPriceAlarmDTO createTargetPriceAlarmDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        alarmService.createTargetPriceAlarm(userId, createTargetPriceAlarmDTO);
    }

    @PostMapping("/price")
    public void createPriceAlarm(){

    }

    @DeleteMapping("/target-price")
    public void deleteTargetPriceAlarm(@RequestBody @Valid DeleteTargetPriceAlarmDTO dto, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        Long targetPriceAlarmId = dto.getTargetPriceAlarmId();

        alarmService.deleteTargetPriceAlarm(userId, targetPriceAlarmId);
    }

    @DeleteMapping("/price")
    public void deletePriceAlarm(){

    }
}
