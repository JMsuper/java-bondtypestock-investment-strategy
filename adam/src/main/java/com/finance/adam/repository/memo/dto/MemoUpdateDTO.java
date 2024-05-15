package com.finance.adam.repository.memo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class MemoUpdateDTO {

    @NotNull
    @Positive
    private Long memoId;

    @Length(max = 500)
    private String content;
}
