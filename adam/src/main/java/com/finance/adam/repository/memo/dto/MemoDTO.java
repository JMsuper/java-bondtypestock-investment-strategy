package com.finance.adam.repository.memo.dto;


import com.finance.adam.repository.memo.domain.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class MemoDTO {

    private Long memoId;

    private String content;

    private LocalDateTime createdAt;

    public static MemoDTO from(Memo memo) {
        return MemoDTO.builder()
                .memoId(memo.getId())
                .content(memo.getContent())
                .createdAt(memo.getCreatedAt())
                .build();
    }
}
