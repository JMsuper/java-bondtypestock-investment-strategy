package com.finance.adam.controller;

import com.finance.adam.auth.dto.AccountDto;
import com.finance.adam.repository.memo.dto.MemoCreateDTO;
import com.finance.adam.repository.memo.dto.MemoDTO;
import com.finance.adam.repository.memo.dto.MemoUpdateDTO;
import com.finance.adam.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/memo")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping()
    public MemoDTO createMemo(@RequestBody @Valid MemoCreateDTO memoCreateDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        MemoDTO memoDTO = memoService.createMemo(userId, memoCreateDTO);
        return memoDTO;
    }

    @PutMapping()
    public MemoDTO updateMemo(@RequestBody @Valid MemoUpdateDTO memoUpdateDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();

        MemoDTO memoDTO = memoService.updateMemo(userId, memoUpdateDTO);
        return memoDTO;
    }

    @DeleteMapping()
    public void deleteMemo(@RequestBody @Valid MemoUpdateDTO memoUpdateDTO, @AuthenticationPrincipal AccountDto accountDto){
        String userId = accountDto.getId();
        Long memoId = memoUpdateDTO.getMemoId();

        memoService.deleteMemo(userId, memoId);
    }
}
