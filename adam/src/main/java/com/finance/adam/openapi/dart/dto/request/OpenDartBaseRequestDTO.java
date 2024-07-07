package com.finance.adam.openapi.dart.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.finance.adam.openapi.dart.SjDivType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;


/**
 * OpenDart API 요청 기본 DTO<br/>
 * - path : API 경로
 */
@Builder
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OpenDartBaseRequestDTO {
    private String corpCode;
    private String bsnsYear;
    private String reprtCode;
    private String bgnDe;
    private String endDe;
    private String fsDiv;
    private String sjDiv;

    @Setter
    private String crtfcKey;

    public void checkParams() {
        if( corpCode != null && !checkCorpCode()){
            throw new IllegalArgumentException("corpCode must be 8 digits");
        }
        if( bsnsYear != null && !checkBsnsYear()){
            throw new IllegalArgumentException("bsnsYear must be 4 digits");
        }
        if( reprtCode != null && !checkReprtCode()){
            throw new IllegalArgumentException("reprtCode must be 11011, 11012, 11013, 11014");
        }
        if( bgnDe != null && !checkBgnDe()){
            throw new IllegalArgumentException("bgnDe must be 8 digits");
        }
        if( endDe != null && !checkEndDe()){
            throw new IllegalArgumentException("endDe must be 8 digits");
        }
        if( fsDiv != null && !checkFsDiv()){
            throw new IllegalArgumentException("fsDiv must be CFS or OFS");
        }
        if( sjDiv != null && !checkSjDiv()){
            throw new IllegalArgumentException("sjDiv must be one of SjDivType");
        }
    }

    // 매개변수 유효성 검사
    private static final Pattern corpCodePattern = Pattern.compile("^([0-9]{8})(,[0-9]{8})*$");
    private static final Pattern bsnsYearPattern = Pattern.compile("^[0-9]{4}$");
    private static final Pattern reprtCodePattern = Pattern.compile("^1101[1-4]$");
    private static final Pattern datePattern = Pattern.compile("^[0-9]{8}$");
    private static final Pattern fsDivPattern = Pattern.compile("^(CFS|OFS)$");

    private boolean checkCorpCode() {
        return corpCodePattern.matcher(corpCode).matches();
    }

    private boolean checkBsnsYear() {
        return bsnsYearPattern.matcher(bsnsYear).matches();
    }

    private boolean checkReprtCode() {
        return reprtCodePattern.matcher(reprtCode).matches();
    }

    private boolean checkBgnDe() {
        return datePattern.matcher(bgnDe).matches();
    }

    private boolean checkEndDe() {
        return datePattern.matcher(endDe).matches();
    }

    private boolean checkFsDiv() {
        return fsDivPattern.matcher(fsDiv).matches();
    }

    private boolean checkSjDiv() {
        try{
            SjDivType.valueOf(sjDiv);
            return true;
        }catch (IllegalArgumentException e){
            return false;
        }
    }
}
