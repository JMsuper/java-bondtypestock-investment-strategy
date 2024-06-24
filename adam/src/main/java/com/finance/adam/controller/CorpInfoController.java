package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoListResponse;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoRequestDTO;
import com.finance.adam.repository.savecorpinfo.dto.SaveCorpInfoUpdateDTO;
import com.finance.adam.service.CorpInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/saved-corps")
@RequiredArgsConstructor
public class CorpInfoController {

    private final CorpInfoService corpInfoService;

    @GetMapping
    public List<SaveCorpInfoListResponse> getSaveCorpInfoList(@AuthenticationPrincipal AccountDto accountDto) {
        String userId = accountDto.getId();

        List<SaveCorpInfoListResponse> result = corpInfoService.getSaveCorpInfoList(userId);
        return result;
    }

    @PostMapping
    public String saveCorpInfoListWithUser(@RequestBody @Valid SaveCorpInfoRequestDTO saveCorpInfoRequestDTO,
                                           @AuthenticationPrincipal AccountDto accountDto) {
        String userId = accountDto.getId();
        String corpCode = saveCorpInfoRequestDTO.getCorpCode();

        corpInfoService.saveCorpInfoListWithUser(corpCode, userId);

        return "success";
    }

    @PutMapping
    public String updateCorpInfoListWithUser(@RequestBody @Valid SaveCorpInfoUpdateDTO saveCorpInfoUpdateDTO,
                                             @AuthenticationPrincipal AccountDto accountDto) {
        String userId = accountDto.getId();

        corpInfoService.updateSaveCorpInfo(saveCorpInfoUpdateDTO, userId);

        return "success";
    }

    @DeleteMapping
    public String deleteSaveCorpInfo(@RequestBody @Valid SaveCorpInfoRequestDTO saveCorpInfoRequestDTO,
                                             @AuthenticationPrincipal AccountDto accountDto) {
        String userId = accountDto.getId();
        String corpCode = saveCorpInfoRequestDTO.getCorpCode();

        corpInfoService.deleteCorpInfoListWithUser(corpCode, userId);

        return "success";
    }
}
