package com.finance.adam.repository.memo.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class MemoUpdateDTO {

    @Length(max = 500)
    private String content;
}
