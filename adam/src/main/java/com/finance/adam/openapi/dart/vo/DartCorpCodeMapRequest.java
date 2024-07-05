package com.finance.adam.openapi.dart.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DartCorpCodeMapRequest {
        private String crtfcKey;

        public DartCorpCodeMapRequest(String crtfcKey) {
            this.crtfcKey = crtfcKey;
        }
}
