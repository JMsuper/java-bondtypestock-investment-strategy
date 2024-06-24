package com.finance.adam.repository.savecorpinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaveCorpInfoUpdateDTO {

    @Range(min = 0, max = 100)
    private Float targetRate;

    @Range(min = -1, max = 1)
    private Float expectedROE;
}
