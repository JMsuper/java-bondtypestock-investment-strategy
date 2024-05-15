package com.finance.adam.repository.memo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class MemoCreateDTO {

    @NotNull
    @Positive
    private Long saveCorpInfoId;

    @Length(max = 500)
    private String content;
}
