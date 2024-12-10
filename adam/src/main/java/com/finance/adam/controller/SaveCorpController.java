package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoListResponse;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoUpdateDTO;
import com.finance.adam.service.CorpInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/{userId}/saved-corps")
@RequiredArgsConstructor
public class SaveCorpController {

    private final CorpInfoService corpInfoService;

    @GetMapping
    public List<SaveCorpInfoListResponse> getSaveCorpInfoList(@AuthenticationPrincipal AccountDto accountDto) {
        String userId = accountDto.getId();
        log.info("Getting saved corporation list for user: {}", userId);

        List<SaveCorpInfoListResponse> result = corpInfoService.getSaveCorpInfoList(userId);
        log.debug("Retrieved {} saved corporations for user {}", result.size(), userId);
        return result;
    }

    @PostMapping("/{corpCode}")
    public String saveCorpInfoListWithUser(
            @PathVariable String corpCode,
            @AuthenticationPrincipal AccountDto accountDto) {
        String userId = accountDto.getId();
        log.info("Saving corporation {} for user: {}", corpCode, userId);

        corpInfoService.saveCorpInfoListWithUser(corpCode, userId);

        return "success";
    }

    @PutMapping("/{corpCode}")
    public String updateCorpInfoListWithUser(
            @PathVariable String corpCode,
            @RequestBody @Valid SaveCorpInfoUpdateDTO saveCorpInfoUpdateDTO,
            @AuthenticationPrincipal AccountDto accountDto) {
        String userId = accountDto.getId();
        log.info("Updating saved corporation {} for user: {}", corpCode, userId);
        log.debug("Update details: {}", saveCorpInfoUpdateDTO);

        corpInfoService.updateSaveCorpInfo(corpCode,saveCorpInfoUpdateDTO, userId);

        return "success";
    }

    @DeleteMapping("/{corpCode}")
    public String deleteSaveCorpInfo(
            @PathVariable String corpCode,
            @AuthenticationPrincipal AccountDto accountDto) {
        String userId = accountDto.getId();
        log.info("Deleting saved corporation {} for user: {}", corpCode, userId);
        
        corpInfoService.deleteCorpInfoListWithUser(corpCode, userId);

        return "success";
    }
}
