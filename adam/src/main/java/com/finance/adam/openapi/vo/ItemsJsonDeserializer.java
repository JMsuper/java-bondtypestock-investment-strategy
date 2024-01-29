package com.finance.adam.openapi.vo;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

public class ItemsJsonDeserializer extends JsonDeserializer<List<KrxItemInfoVO>> {
    @Override
    public List<KrxItemInfoVO> deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JacksonException {
        KrxItemInfoVOList krxItemInfoVOList = jsonParser.readValueAs(KrxItemInfoVOList.class);
        return krxItemInfoVOList.elements;
    }

    private static class KrxItemInfoVOList{
        public List<KrxItemInfoVO> elements;
    }
}
