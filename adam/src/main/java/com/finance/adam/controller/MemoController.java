package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.memo.dto.MemoCreateDTO;
import com.finance.adam.repository.memo.dto.MemoDTO;
import com.finance.adam.repository.memo.dto.MemoUpdateDTO;
import com.finance.adam.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping()
    public MemoDTO createMemo(@RequestBody @Valid MemoCreateDTO memoCreateDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Creating memo for user: {}", userId);
        log.debug("Memo create details: {}", memoCreateDTO);

        MemoDTO memoDTO = memoService.createMemo(userId, memoCreateDTO);
        return memoDTO;
    }

    @PutMapping("/{memoId}")
    public MemoDTO updateMemo(
            @PathVariable Long memoId,
            @RequestBody @Valid MemoUpdateDTO memoUpdateDTO,
            @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Updating memo - userId: {}, memoId: {}", userId, memoId);
        log.debug("Memo update details: {}", memoUpdateDTO);

        MemoDTO memoDTO = memoService.updateMemo(userId, memoId, memoUpdateDTO);
        return memoDTO;
    }

    @DeleteMapping("/{memoId}")
    public void deleteMemo(
            @PathVariable Long memoId,
            @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        log.info("Deleting memo - userId: {}, memoId: {}", userId, memoId);

        memoService.deleteMemo(userId, memoId);
    }
}
