package com.finance.adam.repository.memo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class MemoDeleteDTO {

    @NotNull
    @Positive
    private Long memoId;
}
